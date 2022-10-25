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

class OpenMoaIME : InputMethodService() {

    private lateinit var broadcastReceiver: BroadcastReceiver

    override fun onCreate() {
        super.onCreate()
        broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val key = intent.getStringExtra(EXTRA_NAME) ?: return
                if (key.length == 1) {
                    currentInputConnection.commitText(key, 1)
                } else {
                    when (key) {
                        SpecialKey.BACKSPACE.value -> currentInputConnection.deleteSurroundingText(
                            1, 0
                        )
                        SpecialKey.ENTER.value -> currentInputConnection.performEditorAction(
                            EditorInfo.IME_ACTION_GO
                        )
                    }
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