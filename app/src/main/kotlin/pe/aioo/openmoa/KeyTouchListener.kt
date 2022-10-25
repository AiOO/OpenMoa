package pe.aioo.openmoa

import android.content.Intent
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.atan2

class KeyTouchListener(
    private val broadcastManager: LocalBroadcastManager, private val key: String
) : OnTouchListener {

    private var startX: Float = 0f
    private var startY: Float = 0f

    override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {
        when (motionEvent.action) {
            MotionEvent.ACTION_DOWN -> {
                startX = motionEvent.x
                startY = motionEvent.y
            }
            MotionEvent.ACTION_UP -> {
                val endX = motionEvent.x
                val endY = motionEvent.y
                val degree = (atan2(endY - startY, endX - startX) * 180f) / PI
                sendKey(key)
                if (abs(degree) < 22.5f) {
                    sendKey("ㅏ")
                } else if (abs(degree) < 67.5f) {
                    sendKey(if (degree > 0) "ㅡ" else "ㅣ")
                } else if (abs(degree) < 112.5f) {
                    sendKey(if (degree > 0) "ㅜ" else "ㅗ")
                } else if (abs(degree) < 157.5f) {
                    sendKey(if (degree > 0) "ㅡ" else "ㅣ")
                } else {
                    sendKey("ㅓ")
                }
                view.performClick()
            }
        }
        return true;
    }

    private fun sendKey(key: String) {
        broadcastManager.sendBroadcast(
            Intent(OpenMoaIME.INTENT_ACTION).apply {
                putExtra(OpenMoaIME.EXTRA_NAME, key)
            }
        )
    }

}