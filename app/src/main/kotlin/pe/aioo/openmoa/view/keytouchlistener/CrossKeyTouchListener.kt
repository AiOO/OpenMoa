package pe.aioo.openmoa.view.keytouchlistener

import android.annotation.SuppressLint
import android.content.Context
import android.view.MotionEvent
import android.view.View
import pe.aioo.openmoa.view.message.StringKeyMessage
import pe.aioo.openmoa.view.preview.KeyPreviewController
import kotlin.math.*

// keyList 순서: [0]=위/왼, [1]=오른쪽, [2]=기본(탭), [3]=왼쪽/아래
class CrossKeyTouchListener(
    context: Context,
    private val keyList: List<StringKeyMessage>,
    private val previewController: KeyPreviewController? = null,
) : BaseKeyTouchListener(context) {

    private var startX: Float = 0f
    private var startY: Float = 0f

    private fun resolveKey(currentX: Float, currentY: Float): StringKeyMessage {
        val distance = sqrt((currentX - startX).pow(2) + (currentY - startY).pow(2))
        if (distance <= config.gestureThreshold) return keyList[2]
        val degree = (atan2(currentY - startY, currentX - startX) * 180f) / PI
        return when {
            abs(degree) < 45f -> keyList[1]
            abs(degree) < 135f -> if (degree > 0) keyList[2] else keyList[0]
            else -> keyList[3]
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {
        when (motionEvent.action) {
            MotionEvent.ACTION_DOWN -> {
                startX = motionEvent.x
                startY = motionEvent.y
                previewController?.show(view, keyList[2].key)
            }
            MotionEvent.ACTION_MOVE -> {
                previewController?.update(view, resolveKey(motionEvent.x, motionEvent.y).key)
            }
            MotionEvent.ACTION_UP -> {
                previewController?.hide()
                sendKeyMessage(resolveKey(motionEvent.x, motionEvent.y))
            }
            MotionEvent.ACTION_CANCEL -> {
                previewController?.hide()
            }
        }
        return super.onTouch(view, motionEvent)
    }

}