package pe.aioo.openmoa.view.keyboardview

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import pe.aioo.openmoa.OpenMoaIME
import pe.aioo.openmoa.R
import pe.aioo.openmoa.view.misc.SpecialKey
import pe.aioo.openmoa.databinding.QuertyViewBinding
import pe.aioo.openmoa.view.keytouchlistener.CrossKeyTouchListener
import pe.aioo.openmoa.view.keytouchlistener.FunctionalKeyTouchListener
import pe.aioo.openmoa.view.keytouchlistener.RepeatKeyTouchListener
import pe.aioo.openmoa.view.keytouchlistener.SimpleKeyTouchListener

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

    private var isShiftEnabled = false
    private val broadcastManager = LocalBroadcastManager.getInstance(context)

    private fun init() {
        inflate(context, R.layout.querty_view, this)
        setOnTouchListeners(QuertyViewBinding.bind(this))
    }

    private fun setShiftEnabled(binding: QuertyViewBinding, isEnabled: Boolean) {
        if (isShiftEnabled == isEnabled) {
            return
        }
        listOf(
            binding.qKey, binding.wKey, binding.eKey, binding.rKey, binding.tKey, binding.yKey,
            binding.uKey, binding.iKey, binding.oKey, binding.pKey, binding.aKey, binding.sKey,
            binding.dKey, binding.fKey, binding.gKey, binding.hKey, binding.jKey, binding.kKey,
            binding.lKey, binding.zKey, binding.xKey, binding.cKey, binding.vKey, binding.bKey,
            binding.nKey, binding.mKey,
        ).map {
            it.apply {
                text = if (isEnabled) text.toString().uppercase() else text.toString().lowercase()
            }
        }
        binding.shiftKey.apply {
            text = if(isEnabled) "⬆︎" else "⇧"
        }
        isShiftEnabled = isEnabled
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setOnTouchListeners(binding: QuertyViewBinding) {
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
                    sendKey(text.toString())
                    if (isShiftEnabled) {
                        setShiftEnabled(binding, false)
                    }
                })
            }
        }
        binding.shiftKey.setOnTouchListener(
            FunctionalKeyTouchListener(context, false) {
                setShiftEnabled(binding, !isShiftEnabled)
            }
        )
        binding.backspaceKey.setOnTouchListener(
            RepeatKeyTouchListener(context, SpecialKey.BACKSPACE.value)
        )
        binding.languageKey.setOnTouchListener(
            SimpleKeyTouchListener(context, SpecialKey.LANGUAGE.value)
        )
        binding.hanjaNumberPunctuationKey.setOnTouchListener(
            SimpleKeyTouchListener(context, SpecialKey.HANJA_NUMBER_PUNCTUATION.value)
        )
        binding.spaceKey.setOnTouchListener(SimpleKeyTouchListener(context, " "))
        binding.commaQuestionDotExclamationKey.setOnTouchListener(
            CrossKeyTouchListener(context, listOf(",", "!", ".", "?"))
        )
        binding.enterKey.setOnTouchListener(
            SimpleKeyTouchListener(context, SpecialKey.ENTER.value)
        )
    }

    private fun sendKey(key: String) {
        broadcastManager.sendBroadcast(
            Intent(OpenMoaIME.INTENT_ACTION).apply {
                putExtra(OpenMoaIME.EXTRA_NAME, key)
            }
        )
    }

}