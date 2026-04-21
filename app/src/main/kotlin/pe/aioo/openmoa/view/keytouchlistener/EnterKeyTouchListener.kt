package pe.aioo.openmoa.view.keytouchlistener

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.view.View
import pe.aioo.openmoa.config.EnterLongPressAction
import pe.aioo.openmoa.settings.SettingsPreferences
import pe.aioo.openmoa.view.message.SpecialKey
import pe.aioo.openmoa.view.message.SpecialKeyMessage

class EnterKeyTouchListener(private val context: Context) : BaseKeyTouchListener(context) {

    private var longPressTriggered = false
    private var longPressRunnable: Runnable? = null
    private val handler = Handler(Looper.getMainLooper())

    fun cancel() {
        longPressRunnable?.let { handler.removeCallbacks(it) }
        longPressRunnable = null
        longPressTriggered = false
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {
        when (motionEvent.action) {
            MotionEvent.ACTION_DOWN -> {
                longPressTriggered = false
                val action = SettingsPreferences.getEnterLongPressAction(context)
                if (action == EnterLongPressAction.NONE) return super.onTouch(view, motionEvent)
                val runnable = Runnable {
                    when (action) {
                        EnterLongPressAction.ARROW -> {
                            longPressTriggered = true
                            sendKeyMessage(SpecialKeyMessage(SpecialKey.ARROW))
                        }
                        EnterLongPressAction.IME_PICKER -> {
                            longPressTriggered = true
                            sendKeyMessage(SpecialKeyMessage(SpecialKey.SHOW_IME_PICKER))
                        }
                        EnterLongPressAction.SWITCH_LANGUAGE -> {
                            longPressTriggered = true
                            sendKeyMessage(SpecialKeyMessage(SpecialKey.LANGUAGE))
                        }
                        EnterLongPressAction.NONE -> Unit
                    }
                }
                longPressRunnable = runnable
                handler.postDelayed(runnable, config.longPressThresholdTime)
            }
            MotionEvent.ACTION_UP -> {
                longPressRunnable?.let { handler.removeCallbacks(it) }
                if (longPressTriggered) {
                    longPressTriggered = false
                    return true
                }
                sendKeyMessage(SpecialKeyMessage(SpecialKey.ENTER))
            }
            MotionEvent.ACTION_CANCEL -> {
                longPressRunnable?.let { handler.removeCallbacks(it) }
                longPressRunnable = null
                longPressTriggered = false
            }
        }
        return super.onTouch(view, motionEvent)
    }
}
