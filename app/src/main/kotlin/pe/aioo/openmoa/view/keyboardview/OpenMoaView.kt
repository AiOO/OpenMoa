package pe.aioo.openmoa.view.keyboardview

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.AttributeSet
import android.view.HapticFeedbackConstants
import android.view.MotionEvent
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import pe.aioo.openmoa.OpenMoaIME
import pe.aioo.openmoa.R
import pe.aioo.openmoa.config.Config
import pe.aioo.openmoa.view.message.SpecialKey
import pe.aioo.openmoa.databinding.OpenMoaViewBinding
import pe.aioo.openmoa.view.keytouchlistener.CrossKeyTouchListener
import pe.aioo.openmoa.view.keytouchlistener.JaumKeyTouchListener
import pe.aioo.openmoa.view.keytouchlistener.RepeatKeyTouchListener
import pe.aioo.openmoa.view.keytouchlistener.SimpleKeyTouchListener
import pe.aioo.openmoa.view.message.SpecialKeyMessage
import pe.aioo.openmoa.view.message.StringKeyMessage

class OpenMoaView : ConstraintLayout, KoinComponent {

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

    private val broadcastManager = LocalBroadcastManager.getInstance(context)
    private lateinit var binding: OpenMoaViewBinding
    private var touchedMoeum: String? = null
    private val backgrounds = listOf(
        ContextCompat.getDrawable(context, R.drawable.key_background_pressed),
        ContextCompat.getDrawable(context, R.drawable.key_background),
    )

    private fun init() {
        inflate(context, R.layout.open_moa_view, this)
        binding = OpenMoaViewBinding.bind(this)
        setOnTouchListeners()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setOnTouchListeners() {
        binding.apply {
            tildeKey.setOnTouchListener(SimpleKeyTouchListener(context, StringKeyMessage("~")))
            ssangbieupKey.setOnTouchListener(JaumKeyTouchListener(context, "???"))
            ssangjieutKey.setOnTouchListener(JaumKeyTouchListener(context, "???"))
            ssangdigeutKey.setOnTouchListener(JaumKeyTouchListener(context, "???"))
            ssanggiyeokKey.setOnTouchListener(JaumKeyTouchListener(context, "???"))
            ssangsiotKey.setOnTouchListener(JaumKeyTouchListener(context, "???"))
            emojiKey.setOnTouchListener(
                SimpleKeyTouchListener(context, SpecialKeyMessage(SpecialKey.EMOJI))
            )
            caretKey.setOnTouchListener(SimpleKeyTouchListener(context, StringKeyMessage("^")))
            bieupKey.setOnTouchListener(JaumKeyTouchListener(context, "???"))
            jieutKey.setOnTouchListener(JaumKeyTouchListener(context, "???"))
            digeutKey.setOnTouchListener(JaumKeyTouchListener(context, "???"))
            giyeokKey.setOnTouchListener(JaumKeyTouchListener(context, "???"))
            siotKey.setOnTouchListener(JaumKeyTouchListener(context, "???"))
            backspaceKey.setOnTouchListener(
                RepeatKeyTouchListener(context, SpecialKeyMessage(SpecialKey.BACKSPACE))
            )
            semicolonKey.setOnTouchListener(
                SimpleKeyTouchListener(context, StringKeyMessage(";"))
            )
            mieumKey.setOnTouchListener(JaumKeyTouchListener(context, "???"))
            nieunKey.setOnTouchListener(JaumKeyTouchListener(context, "???"))
            ieungKey.setOnTouchListener(JaumKeyTouchListener(context, "???"))
            rieulKey.setOnTouchListener(JaumKeyTouchListener(context, "???"))
            hieutKey.setOnTouchListener(JaumKeyTouchListener(context, "???"))
            asteriskKey.setOnTouchListener(
                SimpleKeyTouchListener(context, StringKeyMessage("*"))
            )
            kieukKey.setOnTouchListener(JaumKeyTouchListener(context, "???"))
            tieutKey.setOnTouchListener(JaumKeyTouchListener(context, "???"))
            chieutKey.setOnTouchListener(JaumKeyTouchListener(context, "???"))
            pieupKey.setOnTouchListener(JaumKeyTouchListener(context, "???"))
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

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        touchedMoeum.let { moeum ->
            when (ev.action) {
                MotionEvent.ACTION_DOWN,
                MotionEvent.ACTION_MOVE -> {
                    if (ev.action == MotionEvent.ACTION_DOWN ||
                        (ev.action == MotionEvent.ACTION_MOVE && touchedMoeum != null)
                    ) {
                        binding.iKey.apply {
                            if (ev.x in x..x + width && ev.y in y..y + height) {
                                if (moeum != "???") {
                                    background = backgrounds[0]
                                    binding.euKey.background = backgrounds[1]
                                    binding.araeaKey.background = backgrounds[1]
                                    if (config.hapticFeedback) {
                                        performHapticFeedback(
                                            HapticFeedbackConstants.KEYBOARD_PRESS
                                        )
                                    }
                                    if (moeum != null) {
                                        sendKeyMessage(StringKeyMessage(moeum))
                                    }
                                }
                                touchedMoeum = "???"
                                return true
                            }
                        }
                        binding.euKey.apply {
                            if (ev.x in x..x + width && ev.y in y..y + height) {
                                if (moeum != "???") {
                                    background = backgrounds[0]
                                    binding.iKey.background = backgrounds[1]
                                    binding.araeaKey.background = backgrounds[1]
                                    if (config.hapticFeedback) {
                                        performHapticFeedback(
                                            HapticFeedbackConstants.KEYBOARD_PRESS
                                        )
                                    }
                                    if (moeum != null) {
                                        sendKeyMessage(StringKeyMessage(moeum))
                                    }
                                }
                                touchedMoeum = "???"
                                return true
                            }
                        }
                        binding.araeaKey.apply {
                            if (ev.x in x..x + width && ev.y in y..y + height) {
                                if (moeum != "???") {
                                    background = backgrounds[0]
                                    binding.iKey.background = backgrounds[1]
                                    binding.euKey.background = backgrounds[1]
                                    if (config.hapticFeedback) {
                                        performHapticFeedback(
                                            HapticFeedbackConstants.KEYBOARD_PRESS
                                        )
                                    }
                                    if (moeum != null) {
                                        sendKeyMessage(StringKeyMessage(moeum))
                                    }
                                }
                                touchedMoeum = "???"
                                return true
                            }
                        }
                    }
                    Unit
                }
                MotionEvent.ACTION_UP -> {
                    if (moeum != null) {
                        binding.iKey.background = backgrounds[1]
                        binding.euKey.background = backgrounds[1]
                        binding.araeaKey.background = backgrounds[1]
                        sendKeyMessage(StringKeyMessage(moeum))
                        touchedMoeum = null
                        return true
                    }
                    Unit
                }
                else -> Unit
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    private fun sendKeyMessage(keyMessage: StringKeyMessage) {
        broadcastManager.sendBroadcast(
            Intent(OpenMoaIME.INTENT_ACTION).apply {
                putExtra(OpenMoaIME.EXTRA_NAME, keyMessage.key)
            }
        )
    }

}