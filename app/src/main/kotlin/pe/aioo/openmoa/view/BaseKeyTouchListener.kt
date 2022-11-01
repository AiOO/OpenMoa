package pe.aioo.openmoa.view

import android.content.Context
import android.content.Intent
import android.view.HapticFeedbackConstants
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import androidx.core.content.res.ResourcesCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import pe.aioo.openmoa.OpenMoaIME
import pe.aioo.openmoa.R
import pe.aioo.openmoa.config.Config

open class BaseKeyTouchListener(context: Context) : OnTouchListener, KoinComponent {

    protected val config: Config by inject()

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

    override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {
        when (motionEvent.action) {
            MotionEvent.ACTION_DOWN -> {
                view.background = backgrounds[0]
                if (config.hapticFeedback) {
                    view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_PRESS)
                }
            }
            MotionEvent.ACTION_UP -> {
                view.background = backgrounds[1]
                view.performClick()
            }
        }
        return true
    }

    protected fun sendKey(key: String) {
        broadcastManager.sendBroadcast(
            Intent(OpenMoaIME.INTENT_ACTION).apply {
                putExtra(OpenMoaIME.EXTRA_NAME, key)
            }
        )
    }

}