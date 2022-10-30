package pe.aioo.openmoa.view

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import pe.aioo.openmoa.R
import pe.aioo.openmoa.SpecialKey
import pe.aioo.openmoa.databinding.OpenMoaViewBinding

class OpenMoaView : ConstraintLayout {

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
        inflate(context, R.layout.open_moa_view, this)
        setOnTouchListeners(OpenMoaViewBinding.bind(this))
    }

    private fun setOnTouchListeners(binding: OpenMoaViewBinding) {
        binding.tildeKey.setOnTouchListener(SimpleKeyTouchListener(context, "~"))
        binding.ssangbieupKey.setOnTouchListener(JaumKeyTouchListener(context, "ㅃ"))
        binding.ssangjieutKey.setOnTouchListener(JaumKeyTouchListener(context, "ㅉ"))
        binding.ssangdigeutKey.setOnTouchListener(JaumKeyTouchListener(context, "ㄸ"))
        binding.ssanggiyeokKey.setOnTouchListener(JaumKeyTouchListener(context, "ㄲ"))
        binding.ssangsiotKey.setOnTouchListener(JaumKeyTouchListener(context, "ㅆ"))
        binding.emojiKey.setOnTouchListener(
            SimpleKeyTouchListener(context, SpecialKey.EMOJI.value)
        )
        binding.caretKey.setOnTouchListener(SimpleKeyTouchListener(context, "^"))
        binding.bieupKey.setOnTouchListener(JaumKeyTouchListener(context, "ㅂ"))
        binding.jieutKey.setOnTouchListener(JaumKeyTouchListener(context, "ㅈ"))
        binding.digeutKey.setOnTouchListener(JaumKeyTouchListener(context, "ㄷ"))
        binding.giyeokKey.setOnTouchListener(JaumKeyTouchListener(context, "ㄱ"))
        binding.siotKey.setOnTouchListener(JaumKeyTouchListener(context, "ㅅ"))
        binding.backspaceKey.setOnTouchListener(
            RepeatKeyTouchListener(context, SpecialKey.BACKSPACE.value)
        )
        binding.semicolonKey.setOnTouchListener(SimpleKeyTouchListener(context, ";"))
        binding.mieumKey.setOnTouchListener(JaumKeyTouchListener(context, "ㅁ"))
        binding.nieunKey.setOnTouchListener(JaumKeyTouchListener(context, "ㄴ"))
        binding.ieungKey.setOnTouchListener(JaumKeyTouchListener(context, "ㅇ"))
        binding.rieulKey.setOnTouchListener(JaumKeyTouchListener(context, "ㄹ"))
        binding.hieutKey.setOnTouchListener(JaumKeyTouchListener(context, "ㅎ"))
        binding.iKey.setOnTouchListener(SimpleKeyTouchListener(context, "ㅣ"))
        binding.asteriskKey.setOnTouchListener(SimpleKeyTouchListener(context, "*"))
        binding.kieukKey.setOnTouchListener(JaumKeyTouchListener(context, "ㅋ"))
        binding.tieutKey.setOnTouchListener(JaumKeyTouchListener(context, "ㅌ"))
        binding.chieutKey.setOnTouchListener(JaumKeyTouchListener(context, "ㅊ"))
        binding.pieupKey.setOnTouchListener(JaumKeyTouchListener(context, "ㅍ"))
        binding.euKey.setOnTouchListener(SimpleKeyTouchListener(context, "ㅡ"))
        binding.araeaKey.setOnTouchListener(SimpleKeyTouchListener(context, "ㆍ"))
        binding.languageKey.setOnTouchListener(
            SimpleKeyTouchListener(context, SpecialKey.LANGUAGE.value)
        )
        binding.hanjaNumberPunctuationKey.setOnTouchListener(
            SimpleKeyTouchListener(context, SpecialKey.HANJA_NUMBER_PUNCTUATION.value)
        )
        binding.spaceKey.setOnTouchListener(SimpleKeyTouchListener(context, " "))
        binding.commaQuestionDotExclamationKey.setOnTouchListener(
            SimpleKeyTouchListener(context, ".")
        )
        binding.enterKey.setOnTouchListener(
            SimpleKeyTouchListener(context, SpecialKey.ENTER.value)
        )
    }

}