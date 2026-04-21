package pe.aioo.openmoa.view.keytouchlistener

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.view.View
import pe.aioo.openmoa.config.SpaceLongPressAction
import pe.aioo.openmoa.settings.SettingsPreferences
import pe.aioo.openmoa.view.message.SpecialKey
import pe.aioo.openmoa.view.message.SpecialKeyMessage
import pe.aioo.openmoa.view.message.StringKeyMessage

class SpaceKeyTouchListener(private val context: Context) : BaseKeyTouchListener(context) {

    private var longPressTriggered = false
    private var defaultBackground: Drawable? = null
    private var longPressRunnable: Runnable? = null
    private val handler = Handler(Looper.getMainLooper())

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {
        when (motionEvent.action) {
            MotionEvent.ACTION_DOWN -> {
                longPressTriggered = false
                defaultBackground = view.background
                val action = SettingsPreferences.getSpaceLongPressAction(context)
                val runnable = Runnable {
                    when (action) {
                        SpaceLongPressAction.IME_PICKER -> {
                            longPressTriggered = true
                            sendKeyMessage(SpecialKeyMessage(SpecialKey.SHOW_IME_PICKER))
                        }
                        SpaceLongPressAction.SWITCH_LANGUAGE -> {
                            longPressTriggered = true
                            sendKeyMessage(SpecialKeyMessage(SpecialKey.LANGUAGE))
                        }
                        SpaceLongPressAction.ARROW -> {
                            longPressTriggered = true
                            sendKeyMessage(SpecialKeyMessage(SpecialKey.ARROW))
                        }
                        SpaceLongPressAction.NONE -> Unit
                    }
                }
                longPressRunnable = runnable
                handler.postDelayed(runnable, config.longPressThresholdTime)
            }
            MotionEvent.ACTION_UP -> {
                longPressRunnable?.let { handler.removeCallbacks(it) }
                if (longPressTriggered) {
                    longPressTriggered = false
                    view.background = defaultBackground
                    return true
                }
                sendKeyMessage(StringKeyMessage(" "))
            }
            MotionEvent.ACTION_CANCEL -> {
                longPressRunnable?.let { handler.removeCallbacks(it) }
                longPressTriggered = false
            }
        }
        return super.onTouch(view, motionEvent)
    }
}
