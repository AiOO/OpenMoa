package pe.aioo.openmoa

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import android.inputmethodservice.InputMethodService
import android.view.View
import android.view.WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS
import android.view.inputmethod.EditorInfo
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import pe.aioo.openmoa.hangul.HangulAssembler
import pe.aioo.openmoa.view.misc.SpecialKey

class OpenMoaIME : InputMethodService() {

    private lateinit var broadcastReceiver: BroadcastReceiver
    private val hangulAssembler = HangulAssembler()
    private var composingText = ""

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
                                    0, composingText.length - unresolved.length + 1
                                )
                                hangulAssembler.removeLastJamo()
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
                            hangulAssembler.getUnresolved()?.let {
                                composingText += it
                                hangulAssembler.clear()
                            }
                            currentInputConnection.commitText(composingText, 1)
                            composingText = ""
                            currentInputConnection.performEditorAction(
                                EditorInfo.IME_ACTION_GO
                            )
                        }
                    }
                } else if (!key.matches(HangulAssembler.JAMO_REGEX)) {
                    // Process for non-Jamo key
                    hangulAssembler.getUnresolved()?.let {
                        composingText += it
                        hangulAssembler.clear()
                    }
                    currentInputConnection.commitText(composingText, 1)
                    composingText = ""
                    currentInputConnection.commitText(key, 1)
                } else {
                    // Process for Jamo key
                    hangulAssembler.appendJamo(key)?.let {
                        composingText += it
                    }
                }
                currentInputConnection.setComposingText(
                    composingText + (hangulAssembler.getUnresolved() ?: ""), 1
                )
            }
        }
        LocalBroadcastManager.getInstance(this).registerReceiver(
            broadcastReceiver, IntentFilter(INTENT_ACTION)
        )
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
        return layoutInflater.inflate(R.layout.open_moa_ime, null)
    }

    override fun onFinishInputView(finishingInput: Boolean) {
        hangulAssembler.clear()
        super.onFinishInputView(finishingInput)
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