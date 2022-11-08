package pe.aioo.openmoa.view.keytouchlistener

import android.content.Context
import android.content.Intent
import android.view.HapticFeedbackConstants
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import pe.aioo.openmoa.OpenMoaIME
import pe.aioo.openmoa.R
import pe.aioo.openmoa.config.Config
import pe.aioo.openmoa.view.message.BaseKeyMessage
import pe.aioo.openmoa.view.message.SpecialKeyMessage
import pe.aioo.openmoa.view.message.StringKeyMessage

open class BaseKeyTouchListener(context: Context) : OnTouchListener, KoinComponent {

    protected val config: Config by inject()

    private val broadcastManager = LocalBroadcastManager.getInstance(context)
    private val backgrounds = listOf(
        ContextCompat.getDrawable(context, R.drawable.key_background_pressed),
        ContextCompat.getDrawable(context, R.drawable.key_background),
    )

    override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {
        when (motionEvent.action) {
            MotionEvent.ACTION_DOWN -> {
                view.background = backgrounds[0]
                if (config.hapticFeedback) {
                    view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_PRESS)
                }
            }
            MotionEvent.ACTION_CANCEL -> {
                view.background = backgrounds[1]
            }
            MotionEvent.ACTION_UP -> {
                view.background = backgrounds[1]
                view.performClick()
            }
        }
        return true
    }

    protected fun sendKeyMessage(keyMessage: BaseKeyMessage) {
        broadcastManager.sendBroadcast(
            Intent(OpenMoaIME.INTENT_ACTION).apply {
                putExtra(OpenMoaIME.EXTRA_NAME, when (keyMessage) {
                    is StringKeyMessage -> keyMessage.key
                    is SpecialKeyMessage -> keyMessage.key
                    else -> ""
                })
            }
        )
    }

}