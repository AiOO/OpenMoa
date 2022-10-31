package pe.aioo.openmoa.view

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import pe.aioo.openmoa.R
import pe.aioo.openmoa.view.misc.SpecialKey
import pe.aioo.openmoa.databinding.QuertyViewBinding

class QuertyView : ConstraintLayout {

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

    private fun init() {
        inflate(context, R.layout.querty_view, this)
        setOnTouchListeners(QuertyViewBinding.bind(this))
    }

    private fun setOnTouchListeners(binding: QuertyViewBinding) {
        binding.oneKey.setOnTouchListener(SimpleKeyTouchListener(context, "1"))
        binding.twoKey.setOnTouchListener(SimpleKeyTouchListener(context, "2"))
        binding.threeKey.setOnTouchListener(SimpleKeyTouchListener(context, "3"))
        binding.fourKey.setOnTouchListener(SimpleKeyTouchListener(context, "4"))
        binding.fiveKey.setOnTouchListener(SimpleKeyTouchListener(context, "5"))
        binding.sixKey.setOnTouchListener(SimpleKeyTouchListener(context, "6"))
        binding.sevenKey.setOnTouchListener(SimpleKeyTouchListener(context, "7"))
        binding.eightKey.setOnTouchListener(SimpleKeyTouchListener(context, "8"))
        binding.nineKey.setOnTouchListener(SimpleKeyTouchListener(context, "9"))
        binding.zeroKey.setOnTouchListener(SimpleKeyTouchListener(context, "0"))
        binding.qKey.setOnTouchListener(SimpleKeyTouchListener(context, "q"))
        binding.wKey.setOnTouchListener(SimpleKeyTouchListener(context, "w"))
        binding.eKey.setOnTouchListener(SimpleKeyTouchListener(context, "e"))
        binding.rKey.setOnTouchListener(SimpleKeyTouchListener(context, "r"))
        binding.tKey.setOnTouchListener(SimpleKeyTouchListener(context, "t"))
        binding.yKey.setOnTouchListener(SimpleKeyTouchListener(context, "y"))
        binding.uKey.setOnTouchListener(SimpleKeyTouchListener(context, "u"))
        binding.iKey.setOnTouchListener(SimpleKeyTouchListener(context, "i"))
        binding.oKey.setOnTouchListener(SimpleKeyTouchListener(context, "o"))
        binding.pKey.setOnTouchListener(SimpleKeyTouchListener(context, "p"))
        binding.aKey.setOnTouchListener(SimpleKeyTouchListener(context, "a"))
        binding.sKey.setOnTouchListener(SimpleKeyTouchListener(context, "s"))
        binding.dKey.setOnTouchListener(SimpleKeyTouchListener(context, "d"))
        binding.fKey.setOnTouchListener(SimpleKeyTouchListener(context, "f"))
        binding.gKey.setOnTouchListener(SimpleKeyTouchListener(context, "g"))
        binding.hKey.setOnTouchListener(SimpleKeyTouchListener(context, "h"))
        binding.jKey.setOnTouchListener(SimpleKeyTouchListener(context, "j"))
        binding.kKey.setOnTouchListener(SimpleKeyTouchListener(context, "k"))
        binding.lKey.setOnTouchListener(SimpleKeyTouchListener(context, "l"))
        binding.shiftKey.setOnTouchListener(
            SimpleKeyTouchListener(context, SpecialKey.SHIFT.value)
        )
        binding.zKey.setOnTouchListener(SimpleKeyTouchListener(context, "z"))
        binding.xKey.setOnTouchListener(SimpleKeyTouchListener(context, "x"))
        binding.cKey.setOnTouchListener(SimpleKeyTouchListener(context, "c"))
        binding.vKey.setOnTouchListener(SimpleKeyTouchListener(context, "v"))
        binding.bKey.setOnTouchListener(SimpleKeyTouchListener(context, "b"))
        binding.nKey.setOnTouchListener(SimpleKeyTouchListener(context, "n"))
        binding.mKey.setOnTouchListener(SimpleKeyTouchListener(context, "m"))
        binding.backspaceKey.setOnTouchListener(
            RepeatKeyTouchListener(context, SpecialKey.BACKSPACE.value)
        )
        binding.languageKey.setOnTouchListener(
            SimpleKeyTouchListener(context, SpecialKey.LANGUAGE.value)
        )
        binding.hanjaNumberPunctuationKey.setOnTouchListener(
            SimpleKeyTouchListener(context, SpecialKey.HANJA_NUMBER_PUNCTUATION.value)
        )
        binding.spaceKey.setOnTouchListener(SimpleKeyTouchListener(context, " "))
        binding.commaQuestionDotExclamationKey.setOnTouchListener(
            CrossKeyTouchListener(context, listOf(",", "!", ".", "?"))
        )
        binding.enterKey.setOnTouchListener(
            SimpleKeyTouchListener(context, SpecialKey.ENTER.value)
        )
    }

}