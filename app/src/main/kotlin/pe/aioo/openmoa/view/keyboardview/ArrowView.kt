package pe.aioo.openmoa.view.keyboardview

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import pe.aioo.openmoa.R
import pe.aioo.openmoa.config.KeyboardSkin
import pe.aioo.openmoa.databinding.ArrowViewBinding
import pe.aioo.openmoa.view.keytouchlistener.EnterKeyTouchListener
import pe.aioo.openmoa.view.keytouchlistener.FunctionalKeyTouchListener
import pe.aioo.openmoa.view.keytouchlistener.LanguageKeyTouchListener
import pe.aioo.openmoa.view.message.SpecialKey
import pe.aioo.openmoa.view.keytouchlistener.RepeatKeyTouchListener
import pe.aioo.openmoa.view.keytouchlistener.SimpleKeyTouchListener
import pe.aioo.openmoa.settings.SettingsPreferences
import pe.aioo.openmoa.view.keytouchlistener.SpaceKeyTouchListener
import pe.aioo.openmoa.view.message.SpecialKeyMessage
import pe.aioo.openmoa.view.message.StringKeyMessage
import pe.aioo.openmoa.view.skin.SkinApplier

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
    private var currentSkin: KeyboardSkin = KeyboardSkin.DEFAULT
    private var enterKeyListener: EnterKeyTouchListener? = null
    private var languageKeyListener: LanguageKeyTouchListener? = null

    private fun init() {
        inflate(context, R.layout.arrow_view, this)
        binding = ArrowViewBinding.bind(this)
        setOnTouchListeners()
        currentSkin = SettingsPreferences.getKeyboardSkin(context)
        SkinApplier.apply(this, currentSkin)
    }

    fun refreshOneHandMode() {
        val isReduced = SettingsPreferences.getOneHandMode(context).isReduced
        binding.copyAllKey.setText(
            if (isReduced) R.string.key_copy_all_two_line else R.string.key_copy_all
        )
        binding.selectAllKey.setText(
            if (isReduced) R.string.key_select_all_two_line else R.string.key_select_all
        )
        binding.cutAllKey.setText(
            if (isReduced) R.string.key_cut_all_two_line else R.string.key_cut_all
        )
    }

    fun setSelectingOrToggleSelecting(selecting: Boolean? = null) {
        isSelecting = selecting ?: !isSelecting
        val color = if (isSelecting) {
            SkinApplier.fgAccentColor(context, currentSkin)
        } else {
            SkinApplier.fgColor(context, currentSkin)
        }
        listOf(
            binding.areaSelectKey, binding.homeKey, binding.endKey,
            binding.upKey, binding.downKey, binding.leftKey, binding.rightKey,
        ).forEach { it.setTextColor(color) }
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
            languageKeyListener?.cancel()
            languageKeyListener = LanguageKeyTouchListener(context)
            languageKey.setOnTouchListener(languageKeyListener)
            hanjaNumberPunctuationKey.setOnTouchListener(
                SimpleKeyTouchListener(
                    context, SpecialKeyMessage(SpecialKey.HANJA_NUMBER_PUNCTUATION)
                )
            )
            spaceKey.setOnTouchListener(SpaceKeyTouchListener(context))
            enterKeyListener?.cancel()
            enterKeyListener = EnterKeyTouchListener(context)
            enterKey.setOnTouchListener(enterKeyListener)
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        enterKeyListener?.cancel()
        languageKeyListener?.cancel()
    }

}