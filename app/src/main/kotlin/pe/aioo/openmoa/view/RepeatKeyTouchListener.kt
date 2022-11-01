package pe.aioo.openmoa.view

import android.annotation.SuppressLint
import android.content.Context
import android.view.MotionEvent
import android.view.View
import java.util.Timer

class RepeatKeyTouchListener(
    context: Context,
    private val key: String,
) : BaseKeyTouchListener(context) {

    private var elapsed = 0L
    private lateinit var timer: Timer

    private fun startTimer() {
        elapsed = 0L
        sendKey(key)
        timer = kotlin.concurrent.timer(period = config.longPressRepeatTime) {
            elapsed += config.longPressRepeatTime
            if (elapsed >= config.longPressThresholdTime) {
                sendKey(key)
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
            MotionEvent.ACTION_UP -> {
                endTimer()
            }
        }
        return super.onTouch(view, motionEvent)
    }

}