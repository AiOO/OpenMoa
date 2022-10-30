package pe.aioo.openmoa.view

import android.content.Context
import android.view.MotionEvent
import android.view.View

class SimpleKeyTouchListener(
    context: Context,
    private val key: String,
) : BaseKeyTouchListener(context) {

    override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {
        when (motionEvent.action) {
            MotionEvent.ACTION_UP -> {
                sendKey(key)
            }
        }
        super.onTouch(view, motionEvent)
        return true
    }

}