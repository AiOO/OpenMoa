package pe.aioo.openmoa

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import android.inputmethodservice.InputMethodService
import android.text.InputType
import android.view.KeyEvent
import android.view.View
import android.view.WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS
import android.view.inputmethod.EditorInfo
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import pe.aioo.openmoa.databinding.OpenMoaImeBinding
import pe.aioo.openmoa.hangul.HangulAssembler
import pe.aioo.openmoa.view.keyboardview.NumberView
import pe.aioo.openmoa.view.keyboardview.OpenMoaView
import pe.aioo.openmoa.view.keyboardview.PunctuationView
import pe.aioo.openmoa.view.keyboardview.QuertyView
import pe.aioo.openmoa.view.misc.SpecialKey

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

    override fun onCreate() {
        super.onCreate()
        broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val key = intent.getStringExtra(EXTRA_NAME) ?: return
                if (key.length > 1) {
                    // Process for special key
                    when (key) {
                        SpecialKey.BACKSPACE.value -> {
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
                                    currentInputConnection.deleteSurroundingText(1, 0)
                                } else {
                                    composingText = composingText.substring(
                                        0, composingText.lastIndex
                                    )
                                }
                            }
                        }
                        SpecialKey.ENTER.value -> {
                            finishComposing()
                            val action = currentInputEditorInfo.imeOptions and (
                                EditorInfo.IME_MASK_ACTION or EditorInfo.IME_FLAG_NO_ENTER_ACTION
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
                        SpecialKey.LANGUAGE.value -> {
                            setKeyboard(
                                when (imeMode) {
                                    IMEMode.IME_KO -> IMEMode.IME_EN
                                    IMEMode.IME_EN -> IMEMode.IME_KO
                                    IMEMode.IME_KO_PUNCTUATION, IMEMode.IME_KO_NUMBER ->
                                        IMEMode.IME_KO
                                    IMEMode.IME_EN_PUNCTUATION, IMEMode.IME_EN_NUMBER ->
                                        IMEMode.IME_EN
                                }
                            )
                        }
                        SpecialKey.HANJA_NUMBER_PUNCTUATION.value -> {
                            setKeyboard(
                                when (imeMode) {
                                    IMEMode.IME_KO -> IMEMode.IME_KO_PUNCTUATION
                                    IMEMode.IME_EN -> IMEMode.IME_EN_PUNCTUATION
                                    IMEMode.IME_KO_PUNCTUATION -> IMEMode.IME_KO_NUMBER
                                    IMEMode.IME_EN_PUNCTUATION -> IMEMode.IME_EN_NUMBER
                                    IMEMode.IME_KO_NUMBER -> IMEMode.IME_KO
                                    IMEMode.IME_EN_NUMBER -> IMEMode.IME_EN
                                }
                            )
                        }
                    }
                } else if (key.matches(Regex("^[A-Za-z]$"))) {
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
                currentInputConnection.setComposingText(composingText, 1)
                setShiftAutomatically()
            }
        }
        LocalBroadcastManager.getInstance(this).registerReceiver(
            broadcastReceiver, IntentFilter(INTENT_ACTION)
        )
        val punctuationView = PunctuationView(this)
        val numberView = NumberView(this)
        keyboardViews = mapOf(
            IMEMode.IME_KO to OpenMoaView(this),
            IMEMode.IME_EN to QuertyView(this),
            IMEMode.IME_KO_PUNCTUATION to punctuationView,
            IMEMode.IME_EN_PUNCTUATION to punctuationView,
            IMEMode.IME_KO_NUMBER to numberView,
            IMEMode.IME_EN_NUMBER to numberView,
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
        keyboardViews[imeMode].let {
            if (it is QuertyView) {
                it.setShiftEnabledAutomatically(
                    currentInputConnection.getCursorCapsMode(currentInputEditorInfo.inputType) != 0
                )
            }
        }
    }

    private fun returnFromPunctuationOrNumberKeyboard() {
        when (imeMode) {
            IMEMode.IME_KO_PUNCTUATION, IMEMode.IME_KO_NUMBER -> setKeyboard(IMEMode.IME_KO)
            IMEMode.IME_EN_PUNCTUATION, IMEMode.IME_EN_NUMBER -> setKeyboard(IMEMode.IME_EN)
            else -> Unit
        }
    }

    @SuppressLint("InflateParams")
    override fun onCreateInputView(): View {
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
            returnFromPunctuationOrNumberKeyboard()
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