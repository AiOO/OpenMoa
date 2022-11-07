package pe.aioo.openmoa.view.keyboardview

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import pe.aioo.openmoa.R
import pe.aioo.openmoa.databinding.ArrowViewBinding
import pe.aioo.openmoa.view.message.SpecialKey
import pe.aioo.openmoa.view.keytouchlistener.RepeatKeyTouchListener
import pe.aioo.openmoa.view.keytouchlistener.SimpleKeyTouchListener
import pe.aioo.openmoa.view.message.SpecialKeyMessage
import pe.aioo.openmoa.view.message.StringKeyMessage

class ArrowView : ConstraintLayout {

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

    private lateinit var binding: ArrowViewBinding

    private fun init() {
        inflate(context, R.layout.arrow_view, this)
        binding = ArrowViewBinding.bind(this)
        setOnTouchListeners()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setOnTouchListeners() {
        binding.apply {
            backspaceKey.setOnTouchListener(
                RepeatKeyTouchListener(context, SpecialKeyMessage(SpecialKey.BACKSPACE))
            )
            languageKey.setOnTouchListener(
                SimpleKeyTouchListener(context, SpecialKeyMessage(SpecialKey.LANGUAGE))
            )
            hanjaNumberPunctuationKey.setOnTouchListener(
                SimpleKeyTouchListener(
                    context, SpecialKeyMessage(SpecialKey.HANJA_NUMBER_PUNCTUATION)
                )
            )
            spaceKey.setOnTouchListener(SimpleKeyTouchListener(context, StringKeyMessage(" ")))
            enterKey.setOnTouchListener(
                SimpleKeyTouchListener(context, SpecialKeyMessage(SpecialKey.ENTER))
            )
        }
    }

}