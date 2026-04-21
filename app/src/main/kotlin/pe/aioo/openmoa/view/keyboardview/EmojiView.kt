package pe.aioo.openmoa.view.keyboardview

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import pe.aioo.openmoa.OpenMoaIME
import pe.aioo.openmoa.R
import pe.aioo.openmoa.databinding.EmojiViewBinding
import pe.aioo.openmoa.view.keytouchlistener.EnterKeyTouchListener
import pe.aioo.openmoa.view.keytouchlistener.RepeatKeyTouchListener
import pe.aioo.openmoa.view.keytouchlistener.SimpleKeyTouchListener
import pe.aioo.openmoa.view.message.SpecialKey
import pe.aioo.openmoa.view.message.SpecialKeyMessage

class EmojiView : ConstraintLayout {

    constructor(context: Context) : super(context) {
        init()
    }
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }
    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle,
    ) {
        init()
    }

    private lateinit var binding: EmojiViewBinding
    private var enterKeyListener: EnterKeyTouchListener? = null

    @SuppressLint("ClickableViewAccessibility")
    private fun init() {
        inflate(context, R.layout.emoji_view, this)
        binding = EmojiViewBinding.bind(this)

        val broadcastManager = LocalBroadcastManager.getInstance(context)
        binding.emojiPickerView.setOnEmojiPickedListener { item ->
            broadcastManager.sendBroadcast(
                Intent(OpenMoaIME.INTENT_ACTION).apply {
                    putExtra(OpenMoaIME.EXTRA_NAME, item.emoji)
                }
            )
        }

        binding.closeButton.setOnTouchListener(
            SimpleKeyTouchListener(context, SpecialKeyMessage(SpecialKey.EMOJI))
        )
        binding.backspaceKey.setOnTouchListener(
            RepeatKeyTouchListener(context, SpecialKeyMessage(SpecialKey.BACKSPACE))
        )
        enterKeyListener = EnterKeyTouchListener(context)
        binding.enterKey.setOnTouchListener(enterKeyListener)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        enterKeyListener?.cancel()
    }

}
