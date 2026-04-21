package pe.aioo.openmoa

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.Icon
import android.inputmethodservice.InputMethodService
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.view.inputmethod.InputMethodManager
import android.text.InputType
import android.util.Size
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.ExtractedTextRequest
import android.view.inputmethod.InlineSuggestionsRequest
import android.view.inputmethod.InlineSuggestionsResponse
import android.widget.inline.InlinePresentationSpec
import androidx.autofill.inline.UiVersions
import androidx.autofill.inline.common.ImageViewStyle
import androidx.autofill.inline.common.TextViewStyle
import androidx.autofill.inline.common.ViewStyle
import androidx.autofill.inline.v1.InlineSuggestionUi
import androidx.core.content.ContextCompat
import androidx.core.view.isEmpty
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import pe.aioo.openmoa.config.Config
import pe.aioo.openmoa.config.HangulInputMode
import pe.aioo.openmoa.config.KeyboardSkin
import pe.aioo.openmoa.config.OneHandMode
import pe.aioo.openmoa.databinding.OpenMoaImeBinding
import pe.aioo.openmoa.hangul.HangulAssembler
import pe.aioo.openmoa.settings.SettingsActivity
import pe.aioo.openmoa.settings.SettingsPreferences
import pe.aioo.openmoa.view.keyboardview.*
import pe.aioo.openmoa.view.keyboardview.qwerty.QuertyView
import pe.aioo.openmoa.view.message.SpecialKey
import pe.aioo.openmoa.view.skin.SkinApplier
import java.io.Serializable
import kotlin.math.roundToInt

class OpenMoaIME : InputMethodService(), KoinComponent {

    private lateinit var binding: OpenMoaImeBinding
    private lateinit var broadcastReceiver: BroadcastReceiver
    private lateinit var keyboardViews: MutableMap<IMEMode, View>
    private val config: Config by inject()
    private val hangulAssembler = HangulAssembler()
    private var imeMode = IMEMode.IME_KO
    private var previousImeMode = IMEMode.IME_KO
    private var composingText = ""
    private var lastSpaceTime = 0L
    private var lastAppliedSkin: KeyboardSkin = KeyboardSkin.DEFAULT

    private fun finishComposing() {
        currentInputConnection?.finishComposingText()
        hangulAssembler.clear()
        composingText = ""
    }

    private inline fun <reified T : Serializable> getKeyFromIntent(intent: Intent): T? {
        val extra = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra(EXTRA_NAME, Serializable::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getSerializableExtra(EXTRA_NAME)
        }
        return extra as? T
    }

    private fun sendKeyDownUpEvent(keyCode: Int, metaState: Int = 0, withShift: Boolean = false) {
        var eventTime = SystemClock.uptimeMillis()
        if (withShift) {
            currentInputConnection.sendKeyEvent(
                KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_SHIFT_LEFT)
            )
        }
        currentInputConnection.sendKeyEvent(
            KeyEvent(
                eventTime,
                eventTime,
                KeyEvent.ACTION_DOWN,
                keyCode,
                0,
                metaState,
                0,
                0,
                KeyEvent.FLAG_SOFT_KEYBOARD or KeyEvent.FLAG_KEEP_TOUCH_MODE,
            )
        )
        eventTime = SystemClock.uptimeMillis()
        currentInputConnection.sendKeyEvent(
            KeyEvent(
                eventTime,
                eventTime,
                KeyEvent.ACTION_UP,
                keyCode,
                0,
                0,
                0,
                0,
                KeyEvent.FLAG_SOFT_KEYBOARD or KeyEvent.FLAG_KEEP_TOUCH_MODE,
            )
        )
        if (withShift) {
            currentInputConnection.sendKeyEvent(
                KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_SHIFT_LEFT)
            )
        }
    }

    private fun isTextEmpty(): Boolean {
        val text = currentInputConnection.getExtractedText(ExtractedTextRequest(), 0)
        return (text?.text ?: "") == ""
    }

    override fun onCreate() {
        super.onCreate()
        broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val key = getKeyFromIntent<String>(intent)
                    ?: getKeyFromIntent<SpecialKey>(intent)
                    ?: return
                val beforeComposingText = composingText
                when (key) {
                    is SpecialKey -> {
                        // Process for special key
                        when (key) {
                            SpecialKey.BACKSPACE -> {
                                val unresolved = hangulAssembler.getUnresolved()
                                if (unresolved != null) {
                                    composingText = composingText.substring(
                                        0, composingText.length - unresolved.length
                                    )
                                    hangulAssembler.removeLastJamo()
                                    hangulAssembler.getUnresolved()?.let {
                                        composingText += it
                                    }
                                } else {
                                    if (composingText.isEmpty()) {
                                        sendKeyDownUpEvent(KeyEvent.KEYCODE_DEL)
                                    } else {
                                        composingText = composingText.substring(
                                            0, composingText.lastIndex
                                        )
                                    }
                                }
                            }
                            SpecialKey.ENTER -> {
                                finishComposing()
                                val action = currentInputEditorInfo.imeOptions and (
                                    EditorInfo.IME_MASK_ACTION or
                                    EditorInfo.IME_FLAG_NO_ENTER_ACTION
                                )
                                when (action) {
                                    EditorInfo.IME_ACTION_GO,
                                    EditorInfo.IME_ACTION_NEXT,
                                    EditorInfo.IME_ACTION_SEARCH,
                                    EditorInfo.IME_ACTION_SEND,
                                    EditorInfo.IME_ACTION_DONE -> {
                                        currentInputConnection.performEditorAction(action)
                                    }
                                    else -> {
                                        sendKeyDownUpEvent(KeyEvent.KEYCODE_ENTER)
                                    }
                                }
                            }
                            SpecialKey.LANGUAGE -> {
                                setKeyboard(
                                    when (imeMode) {
                                        IMEMode.IME_KO -> IMEMode.IME_EN
                                        IMEMode.IME_EN -> IMEMode.IME_KO
                                        IMEMode.IME_KO_PUNCTUATION,
                                        IMEMode.IME_KO_NUMBER,
                                        IMEMode.IME_KO_ARROW,
                                        IMEMode.IME_KO_PHONE,
                                        IMEMode.IME_EMOJI -> IMEMode.IME_KO
                                        IMEMode.IME_EN_PUNCTUATION,
                                        IMEMode.IME_EN_NUMBER,
                                        IMEMode.IME_EN_ARROW,
                                        IMEMode.IME_EN_PHONE -> IMEMode.IME_EN
                                    }
                                )
                            }
                            SpecialKey.HANJA_NUMBER_PUNCTUATION -> {
                                setKeyboard(
                                    when (imeMode) {
                                        IMEMode.IME_KO,
                                        IMEMode.IME_KO_NUMBER,
                                        IMEMode.IME_KO_ARROW,
                                        IMEMode.IME_KO_PHONE,
                                        IMEMode.IME_EMOJI -> IMEMode.IME_KO_PUNCTUATION
                                        IMEMode.IME_EN,
                                        IMEMode.IME_EN_NUMBER,
                                        IMEMode.IME_EN_ARROW,
                                        IMEMode.IME_EN_PHONE -> IMEMode.IME_EN_PUNCTUATION
                                        IMEMode.IME_KO_PUNCTUATION -> IMEMode.IME_KO_NUMBER
                                        IMEMode.IME_EN_PUNCTUATION -> IMEMode.IME_EN_NUMBER
                                    }
                                )
                            }
                            SpecialKey.ARROW -> {
                                setKeyboard(
                                    when (imeMode) {
                                        IMEMode.IME_KO,
                                        IMEMode.IME_KO_NUMBER,
                                        IMEMode.IME_KO_PUNCTUATION,
                                        IMEMode.IME_KO_ARROW,
                                        IMEMode.IME_KO_PHONE,
                                        IMEMode.IME_EMOJI -> IMEMode.IME_KO_ARROW
                                        IMEMode.IME_EN,
                                        IMEMode.IME_EN_NUMBER,
                                        IMEMode.IME_EN_PUNCTUATION,
                                        IMEMode.IME_EN_ARROW,
                                        IMEMode.IME_EN_PHONE -> IMEMode.IME_EN_ARROW
                                    }
                                )
                            }
                            SpecialKey.ARROW_UP -> {
                                if (!isTextEmpty()) {
                                    sendKeyDownUpEvent(KeyEvent.KEYCODE_DPAD_UP)
                                }
                            }
                            SpecialKey.ARROW_LEFT -> {
                                if (!isTextEmpty()) {
                                    sendKeyDownUpEvent(KeyEvent.KEYCODE_DPAD_LEFT)
                                }
                            }
                            SpecialKey.ARROW_RIGHT -> {
                                if (!isTextEmpty()) {
                                    sendKeyDownUpEvent(KeyEvent.KEYCODE_DPAD_RIGHT)
                                }
                            }
                            SpecialKey.ARROW_DOWN -> {
                                if (!isTextEmpty()) {
                                    sendKeyDownUpEvent(KeyEvent.KEYCODE_DPAD_DOWN)
                                }
                            }
                            SpecialKey.COPY_ALL -> {
                                sendKeyDownUpEvent(KeyEvent.KEYCODE_A, KeyEvent.META_CTRL_ON)
                                sendKeyDownUpEvent(KeyEvent.KEYCODE_C, KeyEvent.META_CTRL_ON)
                            }
                            SpecialKey.COPY -> {
                                sendKeyDownUpEvent(KeyEvent.KEYCODE_C, KeyEvent.META_CTRL_ON)
                            }
                            SpecialKey.CUT_ALL -> {
                                sendKeyDownUpEvent(KeyEvent.KEYCODE_A, KeyEvent.META_CTRL_ON)
                                sendKeyDownUpEvent(KeyEvent.KEYCODE_X, KeyEvent.META_CTRL_ON)
                            }
                            SpecialKey.CUT -> {
                                sendKeyDownUpEvent(KeyEvent.KEYCODE_X, KeyEvent.META_CTRL_ON)
                            }
                            SpecialKey.HOME -> {
                                sendKeyDownUpEvent(
                                    KeyEvent.KEYCODE_MOVE_HOME, KeyEvent.META_CTRL_ON
                                )
                            }
                            SpecialKey.END -> {
                                sendKeyDownUpEvent(
                                    KeyEvent.KEYCODE_MOVE_END, KeyEvent.META_CTRL_ON
                                )
                            }
                            SpecialKey.DELETE -> {
                                sendKeyDownUpEvent(KeyEvent.KEYCODE_DEL)
                            }
                            SpecialKey.PASTE -> {
                                sendKeyDownUpEvent(KeyEvent.KEYCODE_V, KeyEvent.META_CTRL_ON)
                            }
                            SpecialKey.SELECT_ALL -> {
                                sendKeyDownUpEvent(KeyEvent.KEYCODE_A, KeyEvent.META_CTRL_ON)
                            }
                            SpecialKey.SELECT_ARROW_UP -> {
                                if (!isTextEmpty()) {
                                    sendKeyDownUpEvent(KeyEvent.KEYCODE_DPAD_UP, withShift = true)
                                }
                            }
                            SpecialKey.SELECT_ARROW_LEFT -> {
                                if (!isTextEmpty()) {
                                    sendKeyDownUpEvent(KeyEvent.KEYCODE_DPAD_LEFT, withShift = true)
                                }
                            }
                            SpecialKey.SELECT_ARROW_RIGHT -> {
                                if (!isTextEmpty()) {
                                    sendKeyDownUpEvent(
                                        KeyEvent.KEYCODE_DPAD_RIGHT, withShift = true
                                    )
                                }
                            }
                            SpecialKey.SELECT_ARROW_DOWN -> {
                                if (!isTextEmpty()) {
                                    sendKeyDownUpEvent(KeyEvent.KEYCODE_DPAD_DOWN, withShift = true)
                                }
                            }
                            SpecialKey.SELECT_END -> {
                                sendKeyDownUpEvent(
                                    KeyEvent.KEYCODE_MOVE_END, KeyEvent.META_CTRL_ON, true
                                )
                            }
                            SpecialKey.SELECT_HOME -> {
                                sendKeyDownUpEvent(
                                    KeyEvent.KEYCODE_MOVE_HOME, KeyEvent.META_CTRL_ON, true
                                )
                            }
                            SpecialKey.EMOJI -> {
                                if (imeMode == IMEMode.IME_EMOJI) {
                                    setKeyboard(previousImeMode)
                                } else {
                                    previousImeMode = imeMode
                                    setKeyboard(IMEMode.IME_EMOJI)
                                }
                            }
                            SpecialKey.OPEN_SETTINGS -> {
                                startActivity(
                                    Intent(this@OpenMoaIME, SettingsActivity::class.java).apply {
                                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    }
                                )
                            }
                            SpecialKey.SHOW_IME_PICKER -> {
                                finishComposing()
                                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                                imm?.showInputMethodPicker()
                            }
                        }
                    }
                    is String -> {
                        if (key.matches(Regex("^[A-Za-z]$"))) {
                            // Process for Alphabet key
                            composingText += key
                        } else if (key.matches(HangulAssembler.JAMO_REGEX)) {
                            // Process for Jamo key
                            hangulAssembler.getUnresolved()?.let {
                                composingText = composingText.substring(
                                    0, composingText.length - it.length
                                )
                            }
                            hangulAssembler.appendJamo(key)?.let {
                                composingText += it
                            }
                            hangulAssembler.getUnresolved()?.let {
                                composingText += it
                            }
                        } else {
                            // Process for another key
                            finishComposing()
                            if (key == " ") {
                                val autoEnabled = SettingsPreferences.getAutoSpacePeriod(this@OpenMoaIME)
                                val now = SystemClock.elapsedRealtime()
                                if (autoEnabled && now - lastSpaceTime < 1000L &&
                                    currentInputConnection.getTextBeforeCursor(1, 0) == " ") {
                                    currentInputConnection.deleteSurroundingText(1, 0)
                                    currentInputConnection.commitText(". ", 1)
                                    lastSpaceTime = 0L
                                } else {
                                    currentInputConnection.commitText(key, 1)
                                    lastSpaceTime = if (autoEnabled) now else 0L
                                }
                            } else {
                                currentInputConnection.commitText(key, 1)
                                lastSpaceTime = 0L
                            }
                        }
                    }
                }
                if (beforeComposingText != composingText) {
                    currentInputConnection.setComposingText(composingText, 1)
                }
                setShiftAutomatically()
            }
        }
        LocalBroadcastManager.getInstance(this).registerReceiver(
            broadcastReceiver, IntentFilter(INTENT_ACTION)
        )
    }

    private fun setKeyboard(mode: IMEMode) {
        finishComposing()
        keyboardViews[mode]?.let {
            when (it) {
                is PunctuationView -> it.setPageOrNextPage(0)
                is PhoneView -> it.setPageOrNextPage(0)
                is ArrowView -> {
                    it.setSelectingOrToggleSelecting(false)
                    it.refreshOneHandMode()
                }
            }
            binding.keyboardFrameLayout.setKeyboardView(it)
        }
        imeMode = mode
    }

    private fun setShiftAutomatically() {
        if (!config.autoCapitalizeEnglish) return
        keyboardViews[imeMode]?.let { view ->
            if (view is QuertyView) {
                currentInputConnection?.let { inputConnection ->
                    view.setShiftEnabledAutomatically(
                        inputConnection.getCursorCapsMode(currentInputEditorInfo.inputType) != 0
                    )
                }
            }
        }
    }

    private fun returnFromNonStringKeyboard() {
        when (imeMode) {
            IMEMode.IME_KO,
            IMEMode.IME_KO_PUNCTUATION,
            IMEMode.IME_KO_NUMBER,
            IMEMode.IME_KO_ARROW,
            IMEMode.IME_KO_PHONE,
            IMEMode.IME_EMOJI -> setKeyboard(IMEMode.IME_KO)
            IMEMode.IME_EN,
            IMEMode.IME_EN_PUNCTUATION,
            IMEMode.IME_EN_NUMBER,
            IMEMode.IME_EN_ARROW,
            IMEMode.IME_EN_PHONE -> setKeyboard(IMEMode.IME_EN)
        }
    }

    @SuppressLint("InflateParams")
    override fun onCreateInputView(): View {
        super.onCreateInputView()
        lastAppliedSkin = SettingsPreferences.getKeyboardSkin(this)
        keyboardViews = buildKeyboardViews()
        val view = layoutInflater.inflate(R.layout.open_moa_ime, null)
        binding = OpenMoaImeBinding.bind(view)
        applyKeyboardLayout()
        setKeyboard(imeMode)
        return view
    }

    private fun buildKeyboardViews(): MutableMap<IMEMode, View> {
        val punctuationView = PunctuationView(this)
        val numberView = NumberView(this)
        val arrowView = ArrowView(this)
        val phoneView = PhoneView(this)
        val emojiView = EmojiView(this)
        return mutableMapOf(
            IMEMode.IME_KO to OpenMoaView(this),
            IMEMode.IME_EN to QuertyView(this),
            IMEMode.IME_KO_PUNCTUATION to punctuationView,
            IMEMode.IME_EN_PUNCTUATION to punctuationView,
            IMEMode.IME_KO_NUMBER to numberView,
            IMEMode.IME_EN_NUMBER to numberView,
            IMEMode.IME_KO_ARROW to arrowView,
            IMEMode.IME_EN_ARROW to arrowView,
            IMEMode.IME_KO_PHONE to phoneView,
            IMEMode.IME_EN_PHONE to phoneView,
            IMEMode.IME_EMOJI to emojiView,
        )
    }

    private fun refreshSkinIfNeeded() {
        val currentSkin = SettingsPreferences.getKeyboardSkin(this)
        if (currentSkin == lastAppliedSkin) return
        lastAppliedSkin = currentSkin
        keyboardViews = buildKeyboardViews()
    }

    private fun refreshOpenMoaViewIfNeeded() {
        val savedIsMoakey = SettingsPreferences.getHangulInputMode(this) == HangulInputMode.MOAKEY
        val currentView = keyboardViews[IMEMode.IME_KO] as? OpenMoaView ?: return
        if (currentView.isMoakeyMode != savedIsMoakey) {
            keyboardViews[IMEMode.IME_KO] = OpenMoaView(this)
        }
    }

    override fun onStartInputView(info: EditorInfo?, restarting: Boolean) {
        super.onStartInputView(info, restarting)
        finishComposing()
        refreshSkinIfNeeded()
        refreshOpenMoaViewIfNeeded()
        (keyboardViews[IMEMode.IME_KO] as? OpenMoaView)?.refreshQuickPhraseBadges()
        (keyboardViews[IMEMode.IME_KO] as? OpenMoaView)?.refreshUserCharLabels()
        applyKeyboardLayout()
        when ((info?.inputType ?: 0) and InputType.TYPE_MASK_CLASS) {
            InputType.TYPE_CLASS_NUMBER -> {
                setKeyboard(
                    when(imeMode) {
                        IMEMode.IME_KO,
                        IMEMode.IME_KO_PUNCTUATION,
                        IMEMode.IME_KO_NUMBER,
                        IMEMode.IME_KO_ARROW,
                        IMEMode.IME_KO_PHONE,
                        IMEMode.IME_EMOJI -> IMEMode.IME_KO_NUMBER
                        IMEMode.IME_EN,
                        IMEMode.IME_EN_PUNCTUATION,
                        IMEMode.IME_EN_NUMBER,
                        IMEMode.IME_EN_ARROW,
                        IMEMode.IME_EN_PHONE -> IMEMode.IME_EN_NUMBER
                    }
                )
            }
            InputType.TYPE_CLASS_PHONE -> {
                setKeyboard(
                    when(imeMode) {
                        IMEMode.IME_KO,
                        IMEMode.IME_KO_PUNCTUATION,
                        IMEMode.IME_KO_NUMBER,
                        IMEMode.IME_KO_ARROW,
                        IMEMode.IME_KO_PHONE,
                        IMEMode.IME_EMOJI -> IMEMode.IME_KO_PHONE
                        IMEMode.IME_EN,
                        IMEMode.IME_EN_PUNCTUATION,
                        IMEMode.IME_EN_NUMBER,
                        IMEMode.IME_EN_ARROW,
                        IMEMode.IME_EN_PHONE -> IMEMode.IME_EN_PHONE
                    }
                )
            }
            else -> {
                setShiftAutomatically()
                returnFromNonStringKeyboard()
            }
        }
    }

    override fun onUpdateSelection(
        oldSelStart: Int,
        oldSelEnd: Int,
        newSelStart: Int,
        newSelEnd: Int,
        candidatesStart: Int,
        candidatesEnd: Int
    ) {
        super.onUpdateSelection(
            oldSelStart,
            oldSelEnd,
            newSelStart,
            newSelEnd,
            candidatesStart,
            candidatesEnd
        )
        if (composingText.isNotEmpty() &&
            (newSelStart != candidatesEnd || newSelEnd != candidatesEnd)
        ) {
            finishComposing()
        }
    }

    override fun onDestroy() {
        if (this::broadcastReceiver.isInitialized) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver)
        }
        super.onDestroy()
    }

    @SuppressLint("RestrictedApi")
    override fun onCreateInlineSuggestionsRequest(uiExtras: Bundle): InlineSuggestionsRequest {
        val styleBuilder = UiVersions.newStylesBuilder()
        val foregroundColor = ContextCompat.getColor(this@OpenMoaIME, R.color.key_foreground)
        val style = InlineSuggestionUi.newStyleBuilder()
            .setSingleIconChipStyle(
                ViewStyle.Builder()
                    .setBackground(
                        Icon.createWithResource(this, R.drawable.selector_key_background)
                    )
                    .setPadding(toDp(4), toDp(4), toDp(4), toDp(4))
                    .build()
            )
            .setChipStyle(
                ViewStyle.Builder()
                    .setBackground(
                        Icon.createWithResource(this, R.drawable.selector_key_background)
                    )
                    .setPadding(toDp(4), toDp(4), toDp(4), toDp(4))
                    .build()
            )
            .setStartIconStyle(
                ImageViewStyle.Builder()
                    .setPadding(0, 0, 0, 0)
                    .setLayoutMargin(toDp(4), 0, toDp(4), 0)
                    .build()
            )
            .setEndIconStyle(
                ImageViewStyle.Builder()
                    .setPadding(0, 0, 0, 0)
                    .setLayoutMargin(toDp(4), 0, toDp(4), 0)
                    .build()
            )
            .setTitleStyle(
                TextViewStyle.Builder()
                    .setTextColor(foregroundColor)
                    .setPadding(0, 0, 0, 0)
                    .setLayoutMargin(toDp(4), 0, toDp(4), 0)
                    .setTextSize(14F)
                    .build()
            )
            .setSubtitleStyle(
                TextViewStyle.Builder()
                    .setTextColor(
                        Color.argb(
                            127,
                            Color.red(foregroundColor),
                            Color.green(foregroundColor),
                            Color.blue(foregroundColor),
                        )
                    )
                    .setPadding(0, 0, 0, 0)
                    .setLayoutMargin(toDp(4), 0, toDp(4), 0)
                    .setTextSize(14F)
                    .build()
            )
            .build()
        styleBuilder.addStyle(style)
        val styleBundle = styleBuilder.build()
        // According to the document, when this list has only one element,
        // the first element should be used repeatedly. But it doesn't work that way on 1Password.
        // So I decide on the size of this list as the number of suggestions.
        // https://developer.android.com/reference/android/view/inputmethod/InlineSuggestionsRequest.Builder#public-constructors_1
        val presentationSpecs = List(config.maxSuggestionCount) {
            InlinePresentationSpec.Builder(
                Size(toDp(32), getHeight()), Size(toDp(640), getHeight())
            ).setStyle(styleBundle).build()
        }
        return InlineSuggestionsRequest.Builder(presentationSpecs)
            .setMaxSuggestionCount(config.maxSuggestionCount)
            .build()
    }

    private fun applyKeyboardLayout() {
        val skin = SettingsPreferences.getKeyboardSkin(this)
        val bgColor = SkinApplier.keyboardBgColor(this, skin)
        binding.root.setBackgroundColor(bgColor)
        binding.keyboardContainer.setBackgroundColor(bgColor)
        window.window?.apply {
            navigationBarColor = bgColor
            val isLightBg = Color.luminance(bgColor) > 0.5f
            insetsController?.setSystemBarsAppearance(
                if (isLightBg) APPEARANCE_LIGHT_NAVIGATION_BARS else 0,
                APPEARANCE_LIGHT_NAVIGATION_BARS,
            )
        }
        val oneHandMode = SettingsPreferences.getOneHandMode(this)
        val displayWidth = resources.displayMetrics.widthPixels
        val keyboardWidth = if (oneHandMode.isReduced) {
            (displayWidth * 0.80f).toInt()
        } else {
            android.view.ViewGroup.LayoutParams.MATCH_PARENT
        }
        val params = android.widget.FrameLayout.LayoutParams(keyboardWidth, calculateKeyboardHeight()).apply {
            gravity = oneHandMode.gravity
        }
        binding.keyboardFrameLayout.layoutParams = params
    }

    private fun calculateKeyboardHeight(): Int {
        val displayHeight = resources.displayMetrics.heightPixels
        val isLandscape = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
        val baseScale = if (isLandscape) 0.50f else 0.35f
        val heightScale = SettingsPreferences.getKeypadHeight(this).heightScale
        return (displayHeight * baseScale * heightScale).toInt()
    }

    private fun getHeight(): Int {
        return toDp(32)
    }

    private fun toDp(pixel: Int): Int {
        return (pixel * resources.displayMetrics.density).roundToInt()
    }

    override fun onInlineSuggestionsResponse(response: InlineSuggestionsResponse): Boolean {
        if (!this::binding.isInitialized) {
            return false
        }
        binding.suggestionStripStartChipGroup.removeAllViews()
        binding.suggestionStripScrollableChipGroup.removeAllViews()
        binding.suggestionStripEndChipGroup.removeAllViews()
        binding.suggestionStripLayout.visibility =
            if (response.inlineSuggestions.isEmpty()) View.GONE else View.VISIBLE
        response.inlineSuggestions.forEach { inlineSuggestion ->
            val size = Size(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
            )
            inlineSuggestion.inflate(this, size, mainExecutor) { view ->
                if (inlineSuggestion.info.isPinned) {
                    if (binding.suggestionStripStartChipGroup.isEmpty()) {
                        binding.suggestionStripStartChipGroup.addView(view)
                    } else {
                        binding.suggestionStripEndChipGroup.addView(view)
                    }
                } else {
                    binding.suggestionStripScrollableChipGroup.addView(view)
                }
            }
        }
        return true
    }

    companion object {
        const val INTENT_ACTION = "keyInput"
        const val EXTRA_NAME = "key"
    }

}