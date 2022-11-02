package pe.aioo.openmoa.view

import android.annotation.SuppressLint
import android.content.Context
import android.view.MotionEvent
import android.view.View
import pe.aioo.openmoa.hangul.MoeumGestureProcessor
import kotlin.math.*

class JaumKeyTouchListener(
    context: Context,
    private val key: String,
) : BaseKeyTouchListener(context) {

    private var startX: Float = 0f
    private var startY: Float = 0f
    private val moeumGestureProcessor = MoeumGestureProcessor()

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {
        when (motionEvent.action) {
            MotionEvent.ACTION_DOWN -> {
                startX = motionEvent.x
                startY = motionEvent.y
                moeumGestureProcessor.clear()
            }
            MotionEvent.ACTION_MOVE -> {
                val currentX = motionEvent.x
                val currentY = motionEvent.y
                val distance = sqrt(
                    (currentX - startX).pow(2) + (currentY - startY).pow(2)
                )
                if (distance > config.gestureThreshold) {
                    val degree = (atan2(currentY - startY, currentX - startX) * 180f) / PI
                    startX = currentX
                    startY = currentY
                    if (0.001f <= abs(degree) && abs(degree) < 22.5f) {
                        moeumGestureProcessor.appendMoeum("ㅏ")
                    } else if (abs(degree) < 67.5f) {
                        moeumGestureProcessor.appendMoeum(if (degree > 0) "ㅡR" else "ㅣR")
                    } else if (abs(degree) < 112.5f) {
                        moeumGestureProcessor.appendMoeum(if (degree > 0) "ㅜ" else "ㅗ")
                    } else if (abs(degree) < 157.5f) {
                        moeumGestureProcessor.appendMoeum(if (degree > 0) "ㅡL" else "ㅣL")
                    } else if (abs(degree) <= 179.999f) {
                        moeumGestureProcessor.appendMoeum("ㅓ")
                    }
                }
            }
            MotionEvent.ACTION_UP -> {
                sendKey(key)
                moeumGestureProcessor.resolveMoeumList()?.let {
                    sendKey(it)
                }
            }
        }
        return super.onTouch(view, motionEvent)
    }

}