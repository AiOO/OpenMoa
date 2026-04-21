package pe.aioo.openmoa.view.keytouchlistener

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import pe.aioo.openmoa.view.message.BaseKeyMessage
import pe.aioo.openmoa.view.message.StringKeyMessage
import pe.aioo.openmoa.view.preview.KeyPreviewController
import pe.aioo.openmoa.view.preview.QuickPhraseMenuPopup

class QwertyKeyTouchListener(
    context: Context,
    private val previewController: KeyPreviewController? = null,
    private val longKeyProvider: () -> String,
    private val onTap: () -> BaseKeyMessage?,
    private val quickPhraseMenuPopup: QuickPhraseMenuPopup? = null,
    private val onEdit: (() -> Unit)? = null,
) : BaseKeyTouchListener(context) {

    private var isLongPressed = false
    private val handler = Handler(Looper.getMainLooper())
    private var anchorView: View? = null
    private var longPressStartX = 0f

    private val longPressRunnable = Runnable {
        val view = anchorView ?: return@Runnable
        isLongPressed = true
        if (quickPhraseMenuPopup != null) {
            previewController?.hide()
            quickPhraseMenuPopup.show(view, longKeyProvider())
        } else {
            previewController?.update(view, longKeyProvider())
        }
    }

    fun cancel() {
        handler.removeCallbacks(longPressRunnable)
        quickPhraseMenuPopup?.dismiss()
        anchorView = null
        isLongPressed = false
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {
        when (motionEvent.action) {
            MotionEvent.ACTION_DOWN -> {
                anchorView = view
                isLongPressed = false
                longPressStartX = motionEvent.x
                (view as? TextView)?.text?.toString()?.let { previewController?.show(view, it) }
                handler.postDelayed(longPressRunnable, config.longPressThresholdTime)
            }
            MotionEvent.ACTION_MOVE -> {
                if (isLongPressed && quickPhraseMenuPopup != null) {
                    val dx = motionEvent.x - longPressStartX
                    quickPhraseMenuPopup.updateSelectionByDelta(dx, view.width)
                }
            }
            MotionEvent.ACTION_UP -> {
                handler.removeCallbacks(longPressRunnable)
                previewController?.hide()
                if (isLongPressed) {
                    isLongPressed = false
                    if (quickPhraseMenuPopup != null) {
                        when (quickPhraseMenuPopup.confirmAndDismiss()) {
                            QuickPhraseMenuPopup.MenuItem.PHRASE_PREVIEW -> {
                                sendKeyMessage(StringKeyMessage(longKeyProvider()))
                            }
                            QuickPhraseMenuPopup.MenuItem.EDIT -> onEdit?.invoke()
                            else -> Unit
                        }
                    } else {
                        sendKeyMessage(StringKeyMessage(longKeyProvider()))
                    }
                } else {
                    onTap()?.let { sendKeyMessage(it) }
                }
            }
            MotionEvent.ACTION_CANCEL -> {
                handler.removeCallbacks(longPressRunnable)
                previewController?.hide()
                quickPhraseMenuPopup?.dismiss()
                isLongPressed = false
            }
        }
        return super.onTouch(view, motionEvent)
    }
}
