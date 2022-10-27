package pe.aioo.openmoa

import android.content.Context
import android.content.Intent
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import androidx.core.content.res.ResourcesCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import kotlin.math.*

class KeyTouchListener(
    context: Context,
    private val key: String,
) : OnTouchListener {

    private val broadcastManager = LocalBroadcastManager.getInstance(context)
    private val backgrounds = listOf(
        ResourcesCompat.getDrawable(
            context.resources,
            R.drawable.key_background_pressed,
            context.theme,
        ),
        ResourcesCompat.getDrawable(
            context.resources,
            R.drawable.key_background,
            context.theme,
        ),
    )

    private var startX: Float = 0f
    private var startY: Float = 0f

    override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {
        when (motionEvent.action) {
            MotionEvent.ACTION_DOWN -> {
                view.background = backgrounds[0]
                startX = motionEvent.x
                startY = motionEvent.y
            }
            MotionEvent.ACTION_UP -> {
                val endX = motionEvent.x
                val endY = motionEvent.y
                val distance = sqrt((endX - startX).pow(2) + (endY - startY).pow(2))
                val degree = (atan2(endY - startY, endX - startX) * 180f) / PI
                sendKey(key)
                if (distance > 25f) {
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
                }
                view.background = backgrounds[1]
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