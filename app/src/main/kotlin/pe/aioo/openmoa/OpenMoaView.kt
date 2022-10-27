package pe.aioo.openmoa

import android.content.Context
import android.content.Intent
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.localbroadcastmanager.content.LocalBroadcastManager
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
        binding.ssangbieupKey.setOnTouchListener(KeyTouchListener(context, "ㅃ"))
        binding.ssangjieutKey.setOnTouchListener(KeyTouchListener(context, "ㅉ"))
        binding.ssangdigeutKey.setOnTouchListener(KeyTouchListener(context, "ㄸ"))
        binding.ssanggiyeokKey.setOnTouchListener(KeyTouchListener(context, "ㄲ"))
        binding.ssangsiotKey.setOnTouchListener(KeyTouchListener(context, "ㅆ"))
        binding.emojiKey.setOnClickListener { sendKey(SpecialKey.EMOJI.value) }
        binding.caretKey.setOnClickListener { sendKey("^") }
        binding.bieupKey.setOnTouchListener(KeyTouchListener(context, "ㅂ"))
        binding.jieutKey.setOnTouchListener(KeyTouchListener(context, "ㅈ"))
        binding.digeutKey.setOnTouchListener(KeyTouchListener(context, "ㄷ"))
        binding.giyeokKey.setOnTouchListener(KeyTouchListener(context, "ㄱ"))
        binding.siotKey.setOnTouchListener(KeyTouchListener(context, "ㅅ"))
        binding.backspaceKey.setOnClickListener { sendKey(SpecialKey.BACKSPACE.value) }
        binding.semicolonKey.setOnClickListener { sendKey(";") }
        binding.mieumKey.setOnTouchListener(KeyTouchListener(context, "ㅁ"))
        binding.nieunKey.setOnTouchListener(KeyTouchListener(context, "ㄴ"))
        binding.ieungKey.setOnTouchListener(KeyTouchListener(context, "ㅇ"))
        binding.rieulKey.setOnTouchListener(KeyTouchListener(context, "ㄹ"))
        binding.hieutKey.setOnTouchListener(KeyTouchListener(context, "ㅎ"))
        binding.iKey.setOnClickListener { sendKey("ㅣ") }
        binding.asteriskKey.setOnClickListener { sendKey("*") }
        binding.kieukKey.setOnTouchListener(KeyTouchListener(context, "ㅋ"))
        binding.tieutKey.setOnTouchListener(KeyTouchListener(context, "ㅌ"))
        binding.chieutKey.setOnTouchListener(KeyTouchListener(context, "ㅊ"))
        binding.pieupKey.setOnTouchListener(KeyTouchListener(context, "ㅍ"))
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