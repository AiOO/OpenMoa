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
        setOnClickListeners(OpenMoaViewBinding.bind(this))
    }

    private fun setOnClickListeners(binding: OpenMoaViewBinding) {
        binding.tildeKey.setOnClickListener { sendKey("~") }
        binding.ssangbieupKey.setOnClickListener { sendKey("ㅃ") }
        binding.ssangjieutKey.setOnClickListener { sendKey("ㅉ") }
        binding.ssangdigeutKey.setOnClickListener { sendKey("ㄸ") }
        binding.ssanggiyeokKey.setOnClickListener { sendKey("ㄲ") }
        binding.ssangsiotKey.setOnClickListener { sendKey("ㅆ") }
        binding.emojiKey.setOnClickListener {}
        binding.caretKey.setOnClickListener { sendKey("^") }
        binding.bieupKey.setOnClickListener { sendKey("ㅂ") }
        binding.jieutKey.setOnClickListener { sendKey("ㅈ") }
        binding.digeutKey.setOnClickListener { sendKey("ㄷ") }
        binding.giyeokKey.setOnClickListener { sendKey("ㄱ") }
        binding.siotKey.setOnClickListener { sendKey("ㅅ") }
        binding.backspaceKey.setOnClickListener { sendKey("BS") }
        binding.semicolonKey.setOnClickListener { sendKey(";") }
        binding.mieumKey.setOnClickListener { sendKey("ㅁ") }
        binding.nieunKey.setOnClickListener { sendKey("ㄴ") }
        binding.ieungKey.setOnClickListener { sendKey("ㅇ") }
        binding.rieulKey.setOnClickListener { sendKey("ㄹ") }
        binding.hieutKey.setOnClickListener { sendKey("ㅎ") }
        binding.iKey.setOnClickListener { sendKey("ㅣ") }
        binding.asteriskKey.setOnClickListener { sendKey("*") }
        binding.kieukKey.setOnClickListener { sendKey("ㅋ") }
        binding.tieutKey.setOnClickListener { sendKey("ㅌ") }
        binding.chieutKey.setOnClickListener { sendKey("ㅊ") }
        binding.pieupKey.setOnClickListener { sendKey("ㅍ") }
        binding.euKey.setOnClickListener { sendKey("ㅡ") }
        binding.araeaKey.setOnClickListener { sendKey("ㆍ") }
        binding.languageKey.setOnClickListener {}
        binding.hanjaNumberPunctuationKey.setOnClickListener {}
        binding.spaceKey.setOnClickListener { sendKey(" ") }
        binding.commaQuestionDotExclamationKey.setOnClickListener { sendKey(".") }
        binding.enterKey.setOnClickListener { sendKey("GO") }
    }

    private fun sendKey(key: String) {
        LocalBroadcastManager.getInstance(context).sendBroadcast(
            Intent("keyInput").apply {
                putExtra("key", key)
            }
        )
    }

}