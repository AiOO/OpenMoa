package pe.aioo.openmoa.view.keytouchlistener

import android.annotation.SuppressLint
import android.content.Context
import android.view.MotionEvent
import android.view.View
import pe.aioo.openmoa.view.message.BaseKeyMessage
import pe.aioo.openmoa.view.message.StringKeyMessage
import pe.aioo.openmoa.view.preview.KeyPreviewController

class SimpleKeyTouchListener(
    context: Context,
    private val key: BaseKeyMessage,
    private val previewController: KeyPreviewController? = null,
) : BaseKeyTouchListener(context) {

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {
        when (motionEvent.action) {
            MotionEvent.ACTION_DOWN -> {
                (key as? StringKeyMessage)?.let { previewController?.show(view, it.key) }
            }
            MotionEvent.ACTION_UP -> {
                previewController?.hide()
                sendKeyMessage(key)
            }
            MotionEvent.ACTION_CANCEL -> {
                previewController?.hide()
            }
        }
        return super.onTouch(view, motionEvent)
    }

}