package pe.aioo.openmoa

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import android.inputmethodservice.InputMethodService
import android.os.Build
import android.text.InputType
import android.view.KeyEvent
import android.view.View
import android.view.WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS
import android.view.inputmethod.EditorInfo
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import pe.aioo.openmoa.databinding.OpenMoaImeBinding
import pe.aioo.openmoa.hangul.HangulAssembler
import pe.aioo.openmoa.view.keyboardview.ArrowView
import pe.aioo.openmoa.view.keyboardview.NumberView
import pe.aioo.openmoa.view.keyboardview.OpenMoaView
import pe.aioo.openmoa.view.keyboardview.PunctuationView
import pe.aioo.openmoa.view.keyboardview.qwerty.QuertyView
import pe.aioo.openmoa.view.message.SpecialKey
import java.io.Serializable

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

    override fun onCreate() {
        super.onCreate()
        broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val key = getKeyFromIntent<String>(intent)
                    ?: getKeyFromIntent<SpecialKey>(intent)
                    ?: return
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
                                        currentInputConnection.deleteSurroundingText(
                                            1, 0
                                        )
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
                                        currentInputConnection.sendKeyEvent(
                                            KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER)
                                        )
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
                                        IMEMode.IME_KO_ARROW -> IMEMode.IME_KO
                                        IMEMode.IME_EN_PUNCTUATION,
                                        IMEMode.IME_EN_NUMBER,
                                        IMEMode.IME_EN_ARROW -> IMEMode.IME_EN
                                    }
                                )
                            }
                            SpecialKey.HANJA_NUMBER_PUNCTUATION -> {
                                setKeyboard(
                                    when (imeMode) {
                                        IMEMode.IME_KO,
                                        IMEMode.IME_KO_NUMBER,
                                        IMEMode.IME_KO_ARROW-> IMEMode.IME_KO_PUNCTUATION
                                        IMEMode.IME_EN,
                                        IMEMode.IME_EN_NUMBER,
                                        IMEMode.IME_EN_ARROW -> IMEMode.IME_EN_PUNCTUATION
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
                                        IMEMode.IME_KO_ARROW -> IMEMode.IME_KO_ARROW
                                        IMEMode.IME_EN,
                                        IMEMode.IME_EN_NUMBER,
                                        IMEMode.IME_EN_PUNCTUATION,
                                        IMEMode.IME_EN_ARROW -> IMEMode.IME_EN_ARROW
                                    }
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
                currentInputConnection.setComposingText(composingText, 1)
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
            if (it is PunctuationView) {
                it.setPageOrNextPage(0)
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
            IMEMode.IME_KO_PUNCTUATION,
            IMEMode.IME_KO_NUMBER,
            IMEMode.IME_KO_ARROW -> setKeyboard(IMEMode.IME_KO)
            IMEMode.IME_EN_PUNCTUATION,
            IMEMode.IME_EN_NUMBER,
            IMEMode.IME_EN_ARROW -> setKeyboard(IMEMode.IME_EN)
            else -> Unit
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
        keyboardViews = mapOf(
            IMEMode.IME_KO to OpenMoaView(this),
            IMEMode.IME_EN to QuertyView(this),
            IMEMode.IME_KO_PUNCTUATION to punctuationView,
            IMEMode.IME_EN_PUNCTUATION to punctuationView,
            IMEMode.IME_KO_NUMBER to numberView,
            IMEMode.IME_EN_NUMBER to numberView,
            IMEMode.IME_KO_ARROW to arrowView,
            IMEMode.IME_EN_ARROW to arrowView,
        )
        val view = layoutInflater.inflate(R.layout.open_moa_ime, null)
        binding = OpenMoaImeBinding.bind(view)
        setKeyboard(imeMode)
        return view
    }

    override fun onStartInputView(info: EditorInfo?, restarting: Boolean) {
        super.onStartInputView(info, restarting)
        finishComposing()
        if (info != null && info.inputType and InputType.TYPE_CLASS_NUMBER != 0) {
            setKeyboard(
                when(imeMode) {
                    IMEMode.IME_KO, IMEMode.IME_KO_PUNCTUATION -> IMEMode.IME_KO_NUMBER
                    IMEMode.IME_EN, IMEMode.IME_EN_PUNCTUATION -> IMEMode.IME_EN_NUMBER
                    else -> imeMode
                }
            )
        } else {
            setShiftAutomatically()
            returnFromNonStringKeyboard()
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

    companion object {
        const val INTENT_ACTION = "keyInput"
        const val EXTRA_NAME = "key"
    }

}