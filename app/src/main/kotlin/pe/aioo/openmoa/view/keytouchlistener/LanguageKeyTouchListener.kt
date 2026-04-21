package pe.aioo.openmoa.view.keytouchlistener

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.view.View
import pe.aioo.openmoa.view.message.SpecialKey
import pe.aioo.openmoa.view.message.SpecialKeyMessage
import java.util.Timer

class LanguageKeyTouchListener(context: Context) : BaseKeyTouchListener(context) {

    @Volatile
    private var longPressTriggered = false
    private var timer: Timer? = null
    private val mainHandler = Handler(Looper.getMainLooper())

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {
        when (motionEvent.action) {
            MotionEvent.ACTION_DOWN -> {
                longPressTriggered = false
                var elapsed = 0L
                timer = kotlin.concurrent.timer(period = config.longPressRepeatTime) {
                    elapsed += config.longPressRepeatTime
                    if (elapsed >= LONG_PRESS_THRESHOLD_MS) {
                        longPressTriggered = true
                        cancel()
                        mainHandler.post {
                            sendKeyMessage(SpecialKeyMessage(SpecialKey.OPEN_SETTINGS))
                        }
                    }
                }
            }
            MotionEvent.ACTION_UP -> {
                timer?.cancel()
                timer = null
                if (!longPressTriggered) {
                    sendKeyMessage(SpecialKeyMessage(SpecialKey.LANGUAGE))
                }
                longPressTriggered = false
            }
            MotionEvent.ACTION_CANCEL -> {
                timer?.cancel()
                timer = null
                longPressTriggered = false
            }
        }
        return super.onTouch(view, motionEvent)
    }

    fun cancel() {
        timer?.cancel()
        timer = null
        longPressTriggered = false
    }

    companion object {
        private const val LONG_PRESS_THRESHOLD_MS = 300L
    }
}
