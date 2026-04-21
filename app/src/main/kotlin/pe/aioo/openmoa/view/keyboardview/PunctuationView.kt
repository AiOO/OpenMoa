package pe.aioo.openmoa.view.keyboardview

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import pe.aioo.openmoa.R
import pe.aioo.openmoa.config.Config
import pe.aioo.openmoa.databinding.PunctuationViewBinding
import pe.aioo.openmoa.view.message.SpecialKey
import pe.aioo.openmoa.view.keytouchlistener.EnterKeyTouchListener
import pe.aioo.openmoa.view.keytouchlistener.FunctionalKeyTouchListener
import pe.aioo.openmoa.view.keytouchlistener.LanguageKeyTouchListener
import pe.aioo.openmoa.view.keytouchlistener.RepeatKeyTouchListener
import pe.aioo.openmoa.view.keytouchlistener.SimpleKeyTouchListener
import pe.aioo.openmoa.settings.SettingsPreferences
import pe.aioo.openmoa.view.keytouchlistener.SpaceKeyTouchListener
import pe.aioo.openmoa.view.message.SpecialKeyMessage
import pe.aioo.openmoa.view.message.StringKeyMessage
import pe.aioo.openmoa.view.preview.KeyPreviewController
import pe.aioo.openmoa.view.skin.SkinApplier

class PunctuationView : ConstraintLayout, KoinComponent {

    private val config: Config by inject()

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

    private lateinit var binding: PunctuationViewBinding
    private var previewController: KeyPreviewController? = null
    private var enterKeyListener: EnterKeyTouchListener? = null
    private var languageKeyListener: LanguageKeyTouchListener? = null
    private var page = 0

    private fun init() {
        inflate(context, R.layout.punctuation_view, this)
        binding = PunctuationViewBinding.bind(this)
        previewController = KeyPreviewController({ config.keyPreviewEnabled })
        setPageOrNextPage(0, true)
        setOnTouchListeners()
        SkinApplier.apply(this, SettingsPreferences.getKeyboardSkin(context))
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        previewController?.cancel()
        enterKeyListener?.cancel()
        languageKeyListener?.cancel()
    }

    fun setPageOrNextPage(newPage: Int? = null, isInitialize: Boolean = false) {
        if (page == newPage && !isInitialize) {
            return
        }
        page = newPage ?: ((page + 1) % PUNCTUATION_LIST.size)
        listOf(
            binding.qKey, binding.wKey, binding.eKey, binding.rKey, binding.tKey, binding.yKey,
            binding.uKey, binding.iKey, binding.oKey, binding.pKey, binding.aKey, binding.sKey,
            binding.dKey, binding.fKey, binding.gKey, binding.hKey, binding.jKey, binding.kKey,
            binding.lKey, binding.zKey, binding.xKey, binding.cKey, binding.vKey, binding.bKey,
            binding.nKey, binding.mKey,
        ).mapIndexed { index, view ->
            view.text = PUNCTUATION_LIST[page][index]
        }
        binding.nextKey.text = resources.getString(
            R.string.key_next_format, page + 1, PUNCTUATION_LIST.size
        )
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setOnTouchListeners() {
        listOf(
            binding.qKey, binding.wKey, binding.eKey, binding.rKey, binding.tKey, binding.yKey,
            binding.uKey, binding.iKey, binding.oKey, binding.pKey, binding.aKey, binding.sKey,
            binding.dKey, binding.fKey, binding.gKey, binding.hKey, binding.jKey, binding.kKey,
            binding.lKey, binding.zKey, binding.xKey, binding.cKey, binding.vKey, binding.bKey,
            binding.nKey, binding.mKey,
        ).map {
            it.apply {
                setOnTouchListener(FunctionalKeyTouchListener(context, previewController = previewController) {
                    StringKeyMessage(text.toString())
                })
            }
        }
        binding.apply {
            nextKey.setOnTouchListener(
                FunctionalKeyTouchListener(context) {
                    setPageOrNextPage()
                    null
                }
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
            arrowKey.setOnTouchListener(
                SimpleKeyTouchListener(context, SpecialKeyMessage(SpecialKey.ARROW))
            )
            enterKeyListener?.cancel()
            enterKeyListener = EnterKeyTouchListener(context)
            enterKey.setOnTouchListener(enterKeyListener)
        }
    }

    companion object {
        private val PUNCTUATION_LIST = listOf(
            listOf(
                "1", "2", "3", "4", "5", "6", "7", "8", "9", "0",
                "-", "@", "*", "^", ":", ";", "(", ")", "~",
                "/", "'", "\"", ".", ",", "?", "!",
            ),
            listOf(
                "#", "&", "%", "+", "=", "_", "\\", "|", "<", ">",
                "{", "}", "[", "]", "$", "￡", "¥", "€", "₩",
                "¢", "`", "˚", "•", "®", "©", "¿",
            ),
            listOf(
                "♥", "♡", "◎", "♩", "♬", "♨", "♀", "♂", "☞", "☜",
                "≠", "※", "≒", "♠", "♤", "★", "☆", "♣", "♧",
                "◐", "◆", "◇", "■", "□", "×", "÷",
            ),
            listOf(
                "Ψ", "Ω", "α", "β", "γ", "δ", "ε", "ζ", "η", "θ",
                "∀", "∂", "∃", "∇", "∈", "∋", "∏", "∑", "∝",
                "∞", "∧", "∨", "∩", "∪", "∫", "∬",
            ),
            listOf(
                "←", "↑", "→", "↓", "↔", "↕", "↖", "↗", "↘", "↙",
                "∮", "∴", "∵", "≡", "≤", "≥", "≪", "≫", "⌒",
                "⊂", "⊃", "⊆", "⊇", "℃", "℉", "™",
            ),
        )
    }

}