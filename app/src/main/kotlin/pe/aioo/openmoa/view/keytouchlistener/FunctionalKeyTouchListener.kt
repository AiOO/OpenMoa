package pe.aioo.openmoa.view.keytouchlistener

import android.annotation.SuppressLint
import android.content.Context
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import pe.aioo.openmoa.view.message.BaseKeyMessage
import pe.aioo.openmoa.view.preview.KeyPreviewController

class FunctionalKeyTouchListener(
    context: Context,
    private val triggerWhenActionUp: Boolean = true,
    private val previewController: KeyPreviewController? = null,
    private val func: () -> BaseKeyMessage?,
) : BaseKeyTouchListener(context) {

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {
        when (motionEvent.action) {
            MotionEvent.ACTION_DOWN -> {
                (view as? TextView)?.text?.toString()?.let { previewController?.show(view, it) }
                if (!triggerWhenActionUp) func()?.let { sendKeyMessage(it) }
            }
            MotionEvent.ACTION_UP -> {
                previewController?.hide()
                if (triggerWhenActionUp) func()?.let { sendKeyMessage(it) }
            }
            MotionEvent.ACTION_CANCEL -> {
                previewController?.hide()
            }
        }
        return super.onTouch(view, motionEvent)
    }

}