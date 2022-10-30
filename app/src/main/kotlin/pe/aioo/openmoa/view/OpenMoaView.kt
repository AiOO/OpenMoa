package pe.aioo.openmoa.view

import android.content.Context
import android.content.Intent
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import pe.aioo.openmoa.OpenMoaIME
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
        binding.tildeKey.setOnClickListener { sendKey("~") }
        binding.ssangbieupKey.setOnTouchListener(JaumKeyTouchListener(context, "ㅃ"))
        binding.ssangjieutKey.setOnTouchListener(JaumKeyTouchListener(context, "ㅉ"))
        binding.ssangdigeutKey.setOnTouchListener(JaumKeyTouchListener(context, "ㄸ"))
        binding.ssanggiyeokKey.setOnTouchListener(JaumKeyTouchListener(context, "ㄲ"))
        binding.ssangsiotKey.setOnTouchListener(JaumKeyTouchListener(context, "ㅆ"))
        binding.emojiKey.setOnClickListener { sendKey(SpecialKey.EMOJI.value) }
        binding.caretKey.setOnClickListener { sendKey("^") }
        binding.bieupKey.setOnTouchListener(JaumKeyTouchListener(context, "ㅂ"))
        binding.jieutKey.setOnTouchListener(JaumKeyTouchListener(context, "ㅈ"))
        binding.digeutKey.setOnTouchListener(JaumKeyTouchListener(context, "ㄷ"))
        binding.giyeokKey.setOnTouchListener(JaumKeyTouchListener(context, "ㄱ"))
        binding.siotKey.setOnTouchListener(JaumKeyTouchListener(context, "ㅅ"))
        binding.backspaceKey.setOnClickListener { sendKey(SpecialKey.BACKSPACE.value) }
        binding.semicolonKey.setOnClickListener { sendKey(";") }
        binding.mieumKey.setOnTouchListener(JaumKeyTouchListener(context, "ㅁ"))
        binding.nieunKey.setOnTouchListener(JaumKeyTouchListener(context, "ㄴ"))
        binding.ieungKey.setOnTouchListener(JaumKeyTouchListener(context, "ㅇ"))
        binding.rieulKey.setOnTouchListener(JaumKeyTouchListener(context, "ㄹ"))
        binding.hieutKey.setOnTouchListener(JaumKeyTouchListener(context, "ㅎ"))
        binding.iKey.setOnClickListener { sendKey("ㅣ") }
        binding.asteriskKey.setOnClickListener { sendKey("*") }
        binding.kieukKey.setOnTouchListener(JaumKeyTouchListener(context, "ㅋ"))
        binding.tieutKey.setOnTouchListener(JaumKeyTouchListener(context, "ㅌ"))
        binding.chieutKey.setOnTouchListener(JaumKeyTouchListener(context, "ㅊ"))
        binding.pieupKey.setOnTouchListener(JaumKeyTouchListener(context, "ㅍ"))
        binding.euKey.setOnClickListener { sendKey("ㅡ") }
        binding.araeaKey.setOnClickListener { sendKey("ㆍ") }
        binding.languageKey.setOnClickListener { sendKey(SpecialKey.LANGUAGE.value) }
        binding.hanjaNumberPunctuationKey.setOnClickListener {
            sendKey(SpecialKey.HANJA_NUMBER_PUNCTUATION.value)
        }
        binding.spaceKey.setOnClickListener { sendKey(" ") }
        binding.commaQuestionDotExclamationKey.setOnClickListener { sendKey(".") }
        binding.enterKey.setOnClickListener { sendKey(SpecialKey.ENTER.value) }
    }

    private fun sendKey(key: String) {
        LocalBroadcastManager.getInstance(context).sendBroadcast(
            Intent(OpenMoaIME.INTENT_ACTION).apply {
                putExtra(OpenMoaIME.EXTRA_NAME, key)
            }
        )
    }

}