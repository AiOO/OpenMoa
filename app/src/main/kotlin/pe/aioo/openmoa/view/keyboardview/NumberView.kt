package pe.aioo.openmoa.view.keyboardview

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import pe.aioo.openmoa.R
import pe.aioo.openmoa.databinding.NumberViewBinding
import pe.aioo.openmoa.view.misc.SpecialKey
import pe.aioo.openmoa.view.keytouchlistener.RepeatKeyTouchListener
import pe.aioo.openmoa.view.keytouchlistener.SimpleKeyTouchListener

class NumberView : ConstraintLayout {

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

    private lateinit var binding: NumberViewBinding

    private fun init() {
        inflate(context, R.layout.number_view, this)
        binding = NumberViewBinding.bind(this)
        setOnTouchListeners()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setOnTouchListeners() {
        binding.plusKey.setOnTouchListener(SimpleKeyTouchListener(context, "+"))
        binding.oneKey.setOnTouchListener(SimpleKeyTouchListener(context, "1"))
        binding.twoKey.setOnTouchListener(SimpleKeyTouchListener(context, "2"))
        binding.threeKey.setOnTouchListener(SimpleKeyTouchListener(context, "3"))
        binding.minusKey.setOnTouchListener(SimpleKeyTouchListener(context, "-"))
        binding.asteriskKey.setOnTouchListener(SimpleKeyTouchListener(context, "*"))
        binding.fourKey.setOnTouchListener(SimpleKeyTouchListener(context, "4"))
        binding.fiveKey.setOnTouchListener(SimpleKeyTouchListener(context, "5"))
        binding.sixKey.setOnTouchListener(SimpleKeyTouchListener(context, "6"))
        binding.dotKey.setOnTouchListener(SimpleKeyTouchListener(context, "."))
        binding.slashKey.setOnTouchListener(SimpleKeyTouchListener(context, "/"))
        binding.sevenKey.setOnTouchListener(SimpleKeyTouchListener(context, "7"))
        binding.eightKey.setOnTouchListener(SimpleKeyTouchListener(context, "8"))
        binding.nineKey.setOnTouchListener(SimpleKeyTouchListener(context, "9"))
        binding.backspaceKey.setOnTouchListener(
            RepeatKeyTouchListener(context, SpecialKey.BACKSPACE.value)
        )
        binding.languageKey.setOnTouchListener(
            SimpleKeyTouchListener(context, SpecialKey.LANGUAGE.value)
        )
        binding.hanjaNumberPunctuationKey.setOnTouchListener(
            SimpleKeyTouchListener(context, SpecialKey.HANJA_NUMBER_PUNCTUATION.value)
        )
        binding.zeroKey.setOnTouchListener(SimpleKeyTouchListener(context, "0"))
        binding.spaceKey.setOnTouchListener(SimpleKeyTouchListener(context, " "))
        binding.enterKey.setOnTouchListener(
            SimpleKeyTouchListener(context, SpecialKey.ENTER.value)
        )
    }

}