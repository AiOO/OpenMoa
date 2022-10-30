package pe.aioo.openmoa.view

import android.content.Context
import android.content.Intent
import android.view.MotionEvent
import android.view.View
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import pe.aioo.openmoa.OpenMoaIME

class SimpleKeyTouchListener(
    context: Context,
    private val key: String,
) : BaseKeyTouchListener(context) {

    private val broadcastManager = LocalBroadcastManager.getInstance(context)

    override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {
        when (motionEvent.action) {
            MotionEvent.ACTION_UP -> {
                sendKey(key)
            }
        }
        super.onTouch(view, motionEvent)
        return true
    }

    private fun sendKey(key: String) {
        broadcastManager.sendBroadcast(
            Intent(OpenMoaIME.INTENT_ACTION).apply {
                putExtra(OpenMoaIME.EXTRA_NAME, key)
            }
        )
    }

}