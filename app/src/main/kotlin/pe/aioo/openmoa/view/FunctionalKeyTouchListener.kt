package pe.aioo.openmoa.view

import android.annotation.SuppressLint
import android.content.Context
import android.view.MotionEvent
import android.view.View

class FunctionalKeyTouchListener(
    context: Context,
    private val triggerWhenActionUp: Boolean = true,
    private val func: () -> Unit,
) : BaseKeyTouchListener(context) {

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {
        when (motionEvent.action) {
            if (triggerWhenActionUp) MotionEvent.ACTION_UP else MotionEvent.ACTION_DOWN -> func()
        }
        return super.onTouch(view, motionEvent)
    }

}