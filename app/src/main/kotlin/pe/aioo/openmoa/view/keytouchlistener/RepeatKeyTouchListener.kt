package pe.aioo.openmoa.view.keytouchlistener

import android.annotation.SuppressLint
import android.content.Context
import android.view.MotionEvent
import android.view.View
import pe.aioo.openmoa.view.message.BaseKeyMessage
import java.util.Timer

class RepeatKeyTouchListener(
    context: Context,
    private val key: BaseKeyMessage,
) : BaseKeyTouchListener(context) {

    private var elapsed = 0L
    private lateinit var timer: Timer

    private fun startTimer() {
        elapsed = 0L
        sendKeyMessage(key)
        timer = kotlin.concurrent.timer(period = config.longPressRepeatTime) {
            elapsed += config.longPressRepeatTime
            if (elapsed >= config.longPressThresholdTime) {
                sendKeyMessage(key)
            }
        }
    }

    private fun endTimer() {
        timer.cancel()
        elapsed = 0
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {
        when (motionEvent.action) {
            MotionEvent.ACTION_DOWN -> {
                startTimer()
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                endTimer()
            }
        }
        return super.onTouch(view, motionEvent)
    }

}