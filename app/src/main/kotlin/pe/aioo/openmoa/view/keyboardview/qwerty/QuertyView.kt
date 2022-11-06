package pe.aioo.openmoa.view.keyboardview.qwerty

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import pe.aioo.openmoa.R
import pe.aioo.openmoa.view.message.SpecialKey
import pe.aioo.openmoa.databinding.QuertyViewBinding
import pe.aioo.openmoa.view.keytouchlistener.CrossKeyTouchListener
import pe.aioo.openmoa.view.keytouchlistener.FunctionalKeyTouchListener
import pe.aioo.openmoa.view.keytouchlistener.RepeatKeyTouchListener
import pe.aioo.openmoa.view.keytouchlistener.SimpleKeyTouchListener
import pe.aioo.openmoa.view.message.SpecialKeyMessage
import pe.aioo.openmoa.view.message.StringKeyMessage

class QuertyView : ConstraintLayout {

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

    private var shiftKeyStatus = ShiftKeyStatus.DISABLED
    private lateinit var binding: QuertyViewBinding

    private fun init() {
        inflate(context, R.layout.querty_view, this)
        binding = QuertyViewBinding.bind(this)
        setShiftStatus(ShiftKeyStatus.DISABLED, true)
        setOnTouchListeners()
    }

    private fun setShiftStatus(status: ShiftKeyStatus, isInitialize: Boolean = false) {
        if (shiftKeyStatus == status && !isInitialize) {
            return
        }
        val prevShiftEnabled = shiftKeyStatus != ShiftKeyStatus.DISABLED
        val isShiftEnabled = status != ShiftKeyStatus.DISABLED
        if (prevShiftEnabled != isShiftEnabled || isInitialize) {
            listOf(
                binding.qKey, binding.wKey, binding.eKey, binding.rKey, binding.tKey, binding.yKey,
                binding.uKey, binding.iKey, binding.oKey, binding.pKey, binding.aKey, binding.sKey,
                binding.dKey, binding.fKey, binding.gKey, binding.hKey, binding.jKey, binding.kKey,
                binding.lKey, binding.zKey, binding.xKey, binding.cKey, binding.vKey, binding.bKey,
                binding.nKey, binding.mKey,
            ).mapIndexed { index, view ->
                view.text = KEY_LIST[if (isShiftEnabled) 1 else 0][index]
            }
            binding.shiftKey.setTextColor(
                ContextCompat.getColor(
                    context,
                    if (isShiftEnabled) {
                        R.color.shift_key_foreground_locked
                    } else {
                        R.color.key_foreground
                    },
                )
            )
        }
        binding.shiftKey.text = if (status == ShiftKeyStatus.LOCKED) "⬆︎" else "⇧"
        shiftKeyStatus = status
    }

    fun setShiftEnabledAutomatically(isEnabled: Boolean) {
        if (shiftKeyStatus != ShiftKeyStatus.LOCKED) {
            setShiftStatus(if (isEnabled) ShiftKeyStatus.AUTO_ENABLED else ShiftKeyStatus.DISABLED)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setOnTouchListeners() {
        listOf(
            binding.oneKey, binding.twoKey, binding.threeKey, binding.fourKey, binding.fiveKey,
            binding.sixKey, binding.sevenKey, binding.eightKey, binding.nineKey, binding.zeroKey,
            binding.qKey, binding.wKey, binding.eKey, binding.rKey, binding.tKey, binding.yKey,
            binding.uKey, binding.iKey, binding.oKey, binding.pKey, binding.aKey, binding.sKey,
            binding.dKey, binding.fKey, binding.gKey, binding.hKey, binding.jKey, binding.kKey,
            binding.lKey, binding.zKey, binding.xKey, binding.cKey, binding.vKey, binding.bKey,
            binding.nKey, binding.mKey,
        ).map {
            it.apply {
                setOnTouchListener(FunctionalKeyTouchListener(context) {
                    val key = text.toString()
                    setShiftStatus(
                        when (shiftKeyStatus) {
                            ShiftKeyStatus.ENABLED -> ShiftKeyStatus.DISABLED
                            else -> shiftKeyStatus
                        }
                    )
                    StringKeyMessage(key)
                })
            }
        }
        binding.apply {
            shiftKey.setOnTouchListener(
                FunctionalKeyTouchListener(context, false) {
                    setShiftStatus(
                        when (shiftKeyStatus) {
                            ShiftKeyStatus.DISABLED -> ShiftKeyStatus.ENABLED
                            ShiftKeyStatus.ENABLED -> ShiftKeyStatus.LOCKED
                            ShiftKeyStatus.AUTO_ENABLED,
                            ShiftKeyStatus.LOCKED -> ShiftKeyStatus.DISABLED
                        }
                    )
                    null
                }
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

    companion object {
        private val KEY_LIST = listOf(
            listOf(
                "q", "w", "e", "r", "t", "y", "u", "i", "o", "p",
                "a", "s", "d", "f", "g", "h", "j", "k", "l",
                "z", "x", "c", "v", "b", "n", "m",
            ),
            listOf(
                "Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P",
                "A", "S", "D", "F", "G", "H", "J", "K", "L",
                "Z", "X", "C", "V", "B", "N", "M",
            ),
        )
    }

}