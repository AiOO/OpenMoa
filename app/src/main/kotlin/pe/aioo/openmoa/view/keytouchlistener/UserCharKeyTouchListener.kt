package pe.aioo.openmoa.view.keytouchlistener

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.view.View
import pe.aioo.openmoa.quickphrase.UserCharKey
import pe.aioo.openmoa.settings.PhraseEditActivity
import pe.aioo.openmoa.view.message.StringKeyMessage
import pe.aioo.openmoa.view.preview.KeyPreviewController

class UserCharKeyTouchListener(
    private val context: Context,
    private val userCharKey: UserCharKey,
    private val previewController: KeyPreviewController? = null,
) : BaseKeyTouchListener(context) {

    private var isLongPressed = false
    private val handler = Handler(Looper.getMainLooper())
    private var anchorView: View? = null

    private val longPressRunnable = Runnable {
        isLongPressed = true
        previewController?.hide()
        val intent = Intent(context, PhraseEditActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            putExtra(PhraseEditActivity.EXTRA_TYPE, PhraseEditActivity.TYPE_USER_CHAR)
            putExtra(PhraseEditActivity.EXTRA_KEY, userCharKey.name)
        }
        context.startActivity(intent)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {
        when (motionEvent.action) {
            MotionEvent.ACTION_DOWN -> {
                anchorView = view
                isLongPressed = false
                previewController?.show(view, userCharKey.getPhrase(context))
                handler.postDelayed(longPressRunnable, config.longPressThresholdTime)
            }
            MotionEvent.ACTION_UP -> {
                handler.removeCallbacks(longPressRunnable)
                previewController?.hide()
                if (!isLongPressed) {
                    sendKeyMessage(StringKeyMessage(userCharKey.getPhrase(context)))
                }
            }
            MotionEvent.ACTION_CANCEL -> {
                handler.removeCallbacks(longPressRunnable)
                previewController?.hide()
                isLongPressed = false
            }
        }
        return super.onTouch(view, motionEvent)
    }
}
