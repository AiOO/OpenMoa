package pe.aioo.openmoa.view.keytouchlistener

import android.annotation.SuppressLint
import android.content.Context
import android.view.MotionEvent
import android.view.View
import pe.aioo.openmoa.view.message.BaseKeyMessage
import java.util.Timer

class RepeatKeyTouchListener(
    context: Context,
    private val keyProvider: () -> BaseKeyMessage?,
) : BaseKeyTouchListener(context) {

    constructor(context: Context, key: BaseKeyMessage) : this(context, { key })

    @Volatile
    private var elapsed = 0L
    private var timer: Timer? = null

    private fun startTimer() {
        endTimer()
        elapsed = 0L
        keyProvider()?.let { sendKeyMessage(it) }
        timer = kotlin.concurrent.timer(period = config.longPressRepeatTime) {
            elapsed += config.longPressRepeatTime
            if (elapsed >= config.longPressThresholdTime) {
                keyProvider()?.let { sendKeyMessage(it) }
            }
        }
    }

    fun endTimer() {
        timer?.cancel()
        timer = null
        elapsed = 0L
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