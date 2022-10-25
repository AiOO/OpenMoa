package pe.aioo.openmoa

import android.annotation.SuppressLint
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
        setOnTouchListeners(
            OpenMoaViewBinding.bind(this),
            LocalBroadcastManager.getInstance(context),
        )
    }

    private fun setOnTouchListeners(
        binding: OpenMoaViewBinding,
        broadcastManager: LocalBroadcastManager,
    ) {
        binding.tildeKey.setOnClickListener { sendKey("~") }
        binding.ssangbieupKey.setOnTouchListener(KeyTouchListener(broadcastManager, "ㅃ"))
        binding.ssangjieutKey.setOnTouchListener(KeyTouchListener(broadcastManager, "ㅉ"))
        binding.ssangdigeutKey.setOnTouchListener(KeyTouchListener(broadcastManager, "ㄸ"))
        binding.ssanggiyeokKey.setOnTouchListener(KeyTouchListener(broadcastManager, "ㄲ"))
        binding.ssangsiotKey.setOnTouchListener(KeyTouchListener(broadcastManager, "ㅆ"))
        binding.emojiKey.setOnClickListener { sendKey(SpecialKey.EMOJI.value) }
        binding.caretKey.setOnClickListener { sendKey("^") }
        binding.bieupKey.setOnTouchListener(KeyTouchListener(broadcastManager, "ㅂ"))
        binding.jieutKey.setOnTouchListener(KeyTouchListener(broadcastManager, "ㅈ"))
        binding.digeutKey.setOnTouchListener(KeyTouchListener(broadcastManager, "ㄷ"))
        binding.giyeokKey.setOnTouchListener(KeyTouchListener(broadcastManager, "ㄱ"))
        binding.siotKey.setOnTouchListener(KeyTouchListener(broadcastManager, "ㅅ"))
        binding.backspaceKey.setOnClickListener { sendKey(SpecialKey.BACKSPACE.value) }
        binding.semicolonKey.setOnClickListener { sendKey(";") }
        binding.mieumKey.setOnTouchListener(KeyTouchListener(broadcastManager, "ㅁ"))
        binding.nieunKey.setOnTouchListener(KeyTouchListener(broadcastManager, "ㄴ"))
        binding.ieungKey.setOnTouchListener(KeyTouchListener(broadcastManager, "ㅇ"))
        binding.rieulKey.setOnTouchListener(KeyTouchListener(broadcastManager, "ㄹ"))
        binding.hieutKey.setOnTouchListener(KeyTouchListener(broadcastManager, "ㅎ"))
        binding.iKey.setOnClickListener { sendKey("ㅣ") }
        binding.asteriskKey.setOnClickListener { sendKey("*") }
        binding.kieukKey.setOnTouchListener(KeyTouchListener(broadcastManager, "ㅋ"))
        binding.tieutKey.setOnTouchListener(KeyTouchListener(broadcastManager, "ㅌ"))
        binding.chieutKey.setOnTouchListener(KeyTouchListener(broadcastManager, "ㅊ"))
        binding.pieupKey.setOnTouchListener(KeyTouchListener(broadcastManager, "ㅍ"))
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