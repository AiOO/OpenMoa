package pe.aioo.openmoa.view.keytouchlistener

import android.annotation.SuppressLint
import android.content.Context
import android.view.MotionEvent
import android.view.View
import pe.aioo.openmoa.view.message.BaseKeyMessage

class FunctionalKeyTouchListener(
    context: Context,
    private val triggerWhenActionUp: Boolean = true,
    private val func: () -> BaseKeyMessage?,
) : BaseKeyTouchListener(context) {

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {
        when (motionEvent.action) {
            if (triggerWhenActionUp) MotionEvent.ACTION_UP else MotionEvent.ACTION_DOWN -> {
                func()?.let {
                    sendKeyMessage(it)
                }
            }
        }
        return super.onTouch(view, motionEvent)
    }

}