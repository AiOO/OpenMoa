package pe.aioo.openmoa.view.keyboardview

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import pe.aioo.openmoa.R
import pe.aioo.openmoa.view.message.SpecialKey
import pe.aioo.openmoa.databinding.OpenMoaViewBinding
import pe.aioo.openmoa.view.keytouchlistener.CrossKeyTouchListener
import pe.aioo.openmoa.view.keytouchlistener.JaumKeyTouchListener
import pe.aioo.openmoa.view.keytouchlistener.RepeatKeyTouchListener
import pe.aioo.openmoa.view.keytouchlistener.SimpleKeyTouchListener
import pe.aioo.openmoa.view.message.SpecialKeyMessage
import pe.aioo.openmoa.view.message.StringKeyMessage

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

    @SuppressLint("ClickableViewAccessibility")
    private fun setOnTouchListeners(binding: OpenMoaViewBinding) {
        binding.apply {
            tildeKey.setOnTouchListener(SimpleKeyTouchListener(context, StringKeyMessage("~")))
            ssangbieupKey.setOnTouchListener(JaumKeyTouchListener(context, "ㅃ"))
            ssangjieutKey.setOnTouchListener(JaumKeyTouchListener(context, "ㅉ"))
            ssangdigeutKey.setOnTouchListener(JaumKeyTouchListener(context, "ㄸ"))
            ssanggiyeokKey.setOnTouchListener(JaumKeyTouchListener(context, "ㄲ"))
            ssangsiotKey.setOnTouchListener(JaumKeyTouchListener(context, "ㅆ"))
            emojiKey.setOnTouchListener(
                SimpleKeyTouchListener(context, SpecialKeyMessage(SpecialKey.EMOJI))
            )
            caretKey.setOnTouchListener(SimpleKeyTouchListener(context, StringKeyMessage("^")))
            bieupKey.setOnTouchListener(JaumKeyTouchListener(context, "ㅂ"))
            jieutKey.setOnTouchListener(JaumKeyTouchListener(context, "ㅈ"))
            digeutKey.setOnTouchListener(JaumKeyTouchListener(context, "ㄷ"))
            giyeokKey.setOnTouchListener(JaumKeyTouchListener(context, "ㄱ"))
            siotKey.setOnTouchListener(JaumKeyTouchListener(context, "ㅅ"))
            backspaceKey.setOnTouchListener(
                RepeatKeyTouchListener(context, SpecialKeyMessage(SpecialKey.BACKSPACE))
            )
            semicolonKey.setOnTouchListener(
                SimpleKeyTouchListener(context, StringKeyMessage(";"))
            )
            mieumKey.setOnTouchListener(JaumKeyTouchListener(context, "ㅁ"))
            nieunKey.setOnTouchListener(JaumKeyTouchListener(context, "ㄴ"))
            ieungKey.setOnTouchListener(JaumKeyTouchListener(context, "ㅇ"))
            rieulKey.setOnTouchListener(JaumKeyTouchListener(context, "ㄹ"))
            hieutKey.setOnTouchListener(JaumKeyTouchListener(context, "ㅎ"))
            iKey.setOnTouchListener(SimpleKeyTouchListener(context, StringKeyMessage("ㅣ")))
            asteriskKey.setOnTouchListener(
                SimpleKeyTouchListener(context, StringKeyMessage("*"))
            )
            kieukKey.setOnTouchListener(JaumKeyTouchListener(context, "ㅋ"))
            tieutKey.setOnTouchListener(JaumKeyTouchListener(context, "ㅌ"))
            chieutKey.setOnTouchListener(JaumKeyTouchListener(context, "ㅊ"))
            pieupKey.setOnTouchListener(JaumKeyTouchListener(context, "ㅍ"))
            euKey.setOnTouchListener(SimpleKeyTouchListener(context, StringKeyMessage("ㅡ")))
            araeaKey.setOnTouchListener(SimpleKeyTouchListener(context, StringKeyMessage("ㆍ")))
            languageKey.setOnTouchListener(
                SimpleKeyTouchListener(context, SpecialKeyMessage(SpecialKey.LANGUAGE))
            )
            hanjaNumberPunctuationKey.setOnTouchListener(
                SimpleKeyTouchListener(
                    context, SpecialKeyMessage(SpecialKey.HANJA_NUMBER_PUNCTUATION)
                )
            )
            spaceKey.setOnTouchListener(SimpleKeyTouchListener(context, StringKeyMessage(" ")))
            commaQuestionDotExclamationKey.setOnTouchListener(
                CrossKeyTouchListener(
                    context,
                    listOf(
                        StringKeyMessage(","),
                        StringKeyMessage("!"),
                        StringKeyMessage("."),
                        StringKeyMessage("?"),
                    ),
                )
            )
            enterKey.setOnTouchListener(
                SimpleKeyTouchListener(context, SpecialKeyMessage(SpecialKey.ENTER))
            )
        }
    }

}