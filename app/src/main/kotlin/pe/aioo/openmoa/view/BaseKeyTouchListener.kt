package pe.aioo.openmoa.view

import android.content.Context
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import androidx.core.content.res.ResourcesCompat
import pe.aioo.openmoa.R

open class BaseKeyTouchListener(context: Context) : OnTouchListener {

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
            }
            MotionEvent.ACTION_UP -> {
                view.background = backgrounds[1]
                view.performClick()
            }
        }
        return true
    }
}