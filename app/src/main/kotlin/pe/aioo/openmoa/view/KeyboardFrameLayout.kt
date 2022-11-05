package pe.aioo.openmoa.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import pe.aioo.openmoa.R
import pe.aioo.openmoa.databinding.KeyboardFrameLayoutBinding

class KeyboardFrameLayout : FrameLayout {

    constructor(context: Context) : super(context) {
        init()
    }
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }
    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        init()
    }

    private lateinit var binding: KeyboardFrameLayoutBinding

    private fun init() {
        inflate(context, R.layout.keyboard_frame_layout, this)
        binding = KeyboardFrameLayoutBinding.bind(this)
    }

    fun setKeyboardView(view: View) {
        view.parent?.let {
            if (it is ViewGroup) {
                it.removeView(view)
            }
        }
        binding.keyboardLayout.removeAllViews()
        binding.keyboardLayout.addView(view)
    }

}