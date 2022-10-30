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

class OpenMoaIME : InputMethodService() {

    private lateinit var broadcastReceiver: BroadcastReceiver
    private val hangulAssembler = HangulAssembler()

    override fun onCreate() {
        super.onCreate()
        broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val key = intent.getStringExtra(EXTRA_NAME) ?: return
                // Process for special key
                if (key.length > 1) {
                    when (key) {
                        SpecialKey.BACKSPACE.value -> {
                            if (hangulAssembler.getUnresolved() != null) {
                                hangulAssembler.removeLastJamo()
                                currentInputConnection.setComposingText(
                                    hangulAssembler.getUnresolved() ?: "", 1
                                )
                            } else {
                                currentInputConnection.deleteSurroundingText(1, 0)
                            }
                        }
                        SpecialKey.ENTER.value -> {
                            hangulAssembler.getUnresolved()?.let {
                                currentInputConnection.commitText(it, 1)
                                hangulAssembler.clear()
                            }
                            currentInputConnection.performEditorAction(
                                EditorInfo.IME_ACTION_GO
                            )
                        }
                    }
                    return
                }
                // Process for non-Jamo key
                if (!key.matches(HangulAssembler.JAMO_REGEX)) {
                    hangulAssembler.getUnresolved()?.let {
                        currentInputConnection.commitText(it, 1)
                        hangulAssembler.clear()
                    }
                    currentInputConnection.commitText(key, 1)
                    return
                }
                // Process for Jamo key
                hangulAssembler.appendJamo(key)?.let {
                    currentInputConnection.commitText(it, 1)
                }
                hangulAssembler.getUnresolved()?.let {
                    currentInputConnection.setComposingText(it, 1)
                }
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

    override fun onWindowHidden() {
        hangulAssembler.clear()
        super.onWindowHidden()
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