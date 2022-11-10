package pe.aioo.openmoa.view.keyboardview

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import pe.aioo.openmoa.R
import pe.aioo.openmoa.databinding.ArrowViewBinding
import pe.aioo.openmoa.view.keytouchlistener.FunctionalKeyTouchListener
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
    private var isSelecting = false

    private fun init() {
        inflate(context, R.layout.arrow_view, this)
        binding = ArrowViewBinding.bind(this)
        setOnTouchListeners()
    }

    fun setSelectingOrToggleSelecting(selecting: Boolean? = null) {
        isSelecting = selecting ?: !isSelecting
        listOf(
            binding.areaSelectKey, binding.homeKey, binding.endKey,
            binding.upKey, binding.downKey, binding.leftKey, binding.rightKey,
        ).map {
            it.setTextColor(
                ContextCompat.getColor(
                    context,
                    if (isSelecting) R.color.key_foreground_locked else R.color.key_foreground
                )
            )
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setOnTouchListeners() {
        binding.apply {
            copyAllKey.setOnTouchListener(
                SimpleKeyTouchListener(context, SpecialKeyMessage(SpecialKey.COPY_ALL))
            )
            copyKey.setOnTouchListener(
                SimpleKeyTouchListener(context, SpecialKeyMessage(SpecialKey.COPY))
            )
            upKey.setOnTouchListener(
                FunctionalKeyTouchListener(context) {
                    SpecialKeyMessage(
                        if (isSelecting) SpecialKey.SELECT_ARROW_UP else SpecialKey.ARROW_UP
                    )
                }
            )
            cutKey.setOnTouchListener(
                SimpleKeyTouchListener(context, SpecialKeyMessage(SpecialKey.CUT))
            )
            cutAllKey.setOnTouchListener(
                SimpleKeyTouchListener(context, SpecialKeyMessage(SpecialKey.CUT_ALL))
            )
            homeKey.setOnTouchListener(
                FunctionalKeyTouchListener(context) {
                    SpecialKeyMessage(
                        if (isSelecting) SpecialKey.SELECT_HOME else SpecialKey.HOME
                    )
                }
            )
            selectAllKey.setOnTouchListener(
                SimpleKeyTouchListener(context, SpecialKeyMessage(SpecialKey.SELECT_ALL))
            )
            leftKey.setOnTouchListener(
                FunctionalKeyTouchListener(context) {
                    SpecialKeyMessage(
                        if (isSelecting) SpecialKey.SELECT_ARROW_LEFT else SpecialKey.ARROW_LEFT
                    )
                }
            )
            areaSelectKey.setOnTouchListener(
                FunctionalKeyTouchListener(context) {
                    setSelectingOrToggleSelecting()
                    null
                }
            )
            rightKey.setOnTouchListener(
                FunctionalKeyTouchListener(context) {
                    SpecialKeyMessage(
                        if (isSelecting) SpecialKey.SELECT_ARROW_RIGHT else SpecialKey.ARROW_RIGHT
                    )
                }
            )
            endKey.setOnTouchListener(
                FunctionalKeyTouchListener(context) {
                    SpecialKeyMessage(
                        if (isSelecting) SpecialKey.SELECT_END else SpecialKey.END
                    )
                }
            )
            deleteKey.setOnTouchListener(
                SimpleKeyTouchListener(context, SpecialKeyMessage(SpecialKey.DELETE))
            )
            downKey.setOnTouchListener(
                FunctionalKeyTouchListener(context) {
                    SpecialKeyMessage(
                        if (isSelecting) SpecialKey.SELECT_ARROW_DOWN else SpecialKey.ARROW_DOWN
                    )
                }
            )
            pasteKey.setOnTouchListener(
                SimpleKeyTouchListener(context, SpecialKeyMessage(SpecialKey.PASTE))
            )
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