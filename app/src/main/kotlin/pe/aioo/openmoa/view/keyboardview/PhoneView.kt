package pe.aioo.openmoa.view.keyboardview

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import pe.aioo.openmoa.R
import pe.aioo.openmoa.databinding.PhoneViewBinding
import pe.aioo.openmoa.view.keytouchlistener.FunctionalKeyTouchListener
import pe.aioo.openmoa.view.message.SpecialKey
import pe.aioo.openmoa.view.keytouchlistener.RepeatKeyTouchListener
import pe.aioo.openmoa.view.keytouchlistener.SimpleKeyTouchListener
import pe.aioo.openmoa.view.message.SpecialKeyMessage
import pe.aioo.openmoa.view.message.StringKeyMessage

class PhoneView : ConstraintLayout {

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

    private lateinit var binding: PhoneViewBinding
    private var page = 0

    private fun init() {
        inflate(context, R.layout.phone_view, this)
        binding = PhoneViewBinding.bind(this)
        setPageOrNextPage(0, true)
        setOnTouchListeners()
    }

    fun setPageOrNextPage(newPage: Int? = null, isInitialize: Boolean = false) {
        if (page == newPage && !isInitialize) {
            return
        }
        page = newPage ?: ((page + 1) % KEY_LIST.size)
        listOf(
            binding.oneKey, binding.twoKey, binding.threeKey, binding.fourKey, binding.fiveKey,
            binding.sixKey, binding.sevenKey, binding.eightKey, binding.nineKey, binding.zeroKey,
        ).mapIndexed { index, view ->
            view.text = KEY_LIST[page][index][0]
            view.tag = KEY_LIST[page][index][1]
        }
        binding.punctuationKey.text = resources.getString(
            if (page == 0) R.string.key_punctuation else R.string.key_one_two_three
        )
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setOnTouchListeners() {
        listOf(
            binding.oneKey, binding.twoKey, binding.threeKey, binding.fourKey, binding.fiveKey,
            binding.sixKey, binding.sevenKey, binding.eightKey, binding.nineKey, binding.zeroKey,
        ).map {
            it.apply {
                setOnTouchListener(FunctionalKeyTouchListener(context) {
                    StringKeyMessage(tag as String)
                })
            }
        }
        binding.apply {
            backspaceKey.setOnTouchListener(
                RepeatKeyTouchListener(context, SpecialKeyMessage(SpecialKey.BACKSPACE))
            )
            minusKey.setOnTouchListener(SimpleKeyTouchListener(context, StringKeyMessage("-")))
            dotKey.setOnTouchListener(SimpleKeyTouchListener(context, StringKeyMessage(".")))
            punctuationKey.setOnTouchListener(
                FunctionalKeyTouchListener(context) {
                    setPageOrNextPage()
                    null
                }
            )
            spaceKey.setOnTouchListener(SimpleKeyTouchListener(context, StringKeyMessage(" ")))
            enterKey.setOnTouchListener(
                SimpleKeyTouchListener(context, SpecialKeyMessage(SpecialKey.ENTER))
            )
        }
    }

    companion object {
        private val KEY_LIST = listOf(
            listOf(
                listOf("1", "1"), listOf("2", "2"), listOf("3", "3"),
                listOf("4", "4"), listOf("5", "5"), listOf("6", "6"),
                listOf("7", "7"), listOf("8", "8"), listOf("9", "9"),
                listOf("0", "0"),
            ),
            listOf(
                listOf("(", "("), listOf("/", "/"), listOf(")", ")"),
                listOf("N", "N"), listOf("Pause", ","), listOf(",", ","),
                listOf("*", "*"), listOf("Wait", ";"), listOf("#", "#"),
                listOf("+", "+"),
            ),
        )
    }

}