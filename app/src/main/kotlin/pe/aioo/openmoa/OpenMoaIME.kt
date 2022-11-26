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
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import pe.aioo.openmoa.databinding.OpenMoaImeBinding
import pe.aioo.openmoa.hangul.HangulAssembler
import pe.aioo.openmoa.view.keyboardview.*
import pe.aioo.openmoa.view.keyboardview.qwerty.QuertyView
import pe.aioo.openmoa.view.message.SpecialKey
import java.io.Serializable
import kotlin.math.roundToInt

class OpenMoaIME : InputMethodService() {

    private lateinit var binding: OpenMoaImeBinding
    private lateinit var broadcastReceiver: BroadcastReceiver
    private lateinit var keyboardViews: Map<IMEMode, View>
    private val hangulAssembler = HangulAssembler()
    private var imeMode = IMEMode.IME_KO
    private var composingText = ""

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
        return if (extra is T) extra else null
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
                                        IMEMode.IME_KO_PHONE -> IMEMode.IME_KO
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
                                        IMEMode.IME_KO_PHONE -> IMEMode.IME_KO_PUNCTUATION
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
                                        IMEMode.IME_KO_PHONE -> IMEMode.IME_KO_ARROW
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
                            SpecialKey.EMOJI -> Unit
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
                            currentInputConnection.commitText(key, 1)
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
                is ArrowView -> it.setSelectingOrToggleSelecting(false)
            }
            binding.keyboardFrameLayout.setKeyboardView(it)
        }
        imeMode = mode
    }

    private fun setShiftAutomatically() {
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
            IMEMode.IME_KO_PHONE -> setKeyboard(IMEMode.IME_KO)
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
        window.window?.apply {
            navigationBarColor =
                ContextCompat.getColor(this@OpenMoaIME, R.color.keyboard_background)
            when (resources.configuration.uiMode.and(Configuration.UI_MODE_NIGHT_MASK)) {
                Configuration.UI_MODE_NIGHT_YES -> {
                    insetsController?.apply {
                        setSystemBarsAppearance(0, APPEARANCE_LIGHT_NAVIGATION_BARS)
                    }
                }
                Configuration.UI_MODE_NIGHT_NO, Configuration.UI_MODE_NIGHT_UNDEFINED -> {
                    insetsController?.apply {
                        setSystemBarsAppearance(
                            APPEARANCE_LIGHT_NAVIGATION_BARS, APPEARANCE_LIGHT_NAVIGATION_BARS
                        )
                    }
                }
            }
        }
        val punctuationView = PunctuationView(this)
        val numberView = NumberView(this)
        val arrowView = ArrowView(this)
        val phoneView = PhoneView(this)
        keyboardViews = mapOf(
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
        )
        val view = layoutInflater.inflate(R.layout.open_moa_ime, null)
        binding = OpenMoaImeBinding.bind(view)
        setKeyboard(imeMode)
        return view
    }

    override fun onStartInputView(info: EditorInfo?, restarting: Boolean) {
        super.onStartInputView(info, restarting)
        finishComposing()
        when ((info?.inputType ?: 0) and InputType.TYPE_MASK_CLASS) {
            InputType.TYPE_CLASS_NUMBER -> {
                setKeyboard(
                    when(imeMode) {
                        IMEMode.IME_KO,
                        IMEMode.IME_KO_PUNCTUATION,
                        IMEMode.IME_KO_NUMBER,
                        IMEMode.IME_KO_ARROW,
                        IMEMode.IME_KO_PHONE -> IMEMode.IME_KO_NUMBER
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
                        IMEMode.IME_KO_PHONE -> IMEMode.IME_KO_PHONE
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
        val presentationSpecs = List(10) {
            InlinePresentationSpec.Builder(
                Size(toDp(32), getHeight()), Size(toDp(640), getHeight())
            ).setStyle(styleBundle).build()
        }
        return InlineSuggestionsRequest.Builder(presentationSpecs)
            .setMaxSuggestionCount(10)
            .build()
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
        binding.suggestionStripStartLayout.removeAllViews()
        binding.suggestionStripScrollableLayout.removeAllViews()
        binding.suggestionStripEndLayout.removeAllViews()
        binding.suggestionStripLayout.visibility =
            if (response.inlineSuggestions.isEmpty()) View.GONE else View.VISIBLE
        response.inlineSuggestions.map { inlineSuggestion ->
            val size = Size(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
            )
            inlineSuggestion.inflate(this, size, mainExecutor) { view ->
                if (inlineSuggestion.info.isPinned) {
                    if (binding.suggestionStripStartLayout.childCount == 0) {
                        binding.suggestionStripStartLayout.addView(view)
                    } else {
                        binding.suggestionStripEndLayout.addView(view)
                    }
                } else {
                    binding.suggestionStripScrollableLayout.addView(view)
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