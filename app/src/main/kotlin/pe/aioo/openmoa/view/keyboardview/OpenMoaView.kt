package pe.aioo.openmoa.view.keyboardview

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.AttributeSet
import android.view.HapticFeedbackConstants
import android.view.MotionEvent
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import pe.aioo.openmoa.OpenMoaIME
import pe.aioo.openmoa.R
import pe.aioo.openmoa.config.Config
import pe.aioo.openmoa.config.HangulInputMode
import pe.aioo.openmoa.settings.SettingsPreferences
import pe.aioo.openmoa.view.message.SpecialKey
import pe.aioo.openmoa.databinding.OpenMoaViewBinding
import pe.aioo.openmoa.databinding.OpenMoaViewMoakeyBinding
import pe.aioo.openmoa.view.keytouchlistener.CrossKeyTouchListener
import pe.aioo.openmoa.view.keytouchlistener.EnterKeyTouchListener
import pe.aioo.openmoa.view.keytouchlistener.JaumKeyTouchListener
import pe.aioo.openmoa.view.keytouchlistener.LanguageKeyTouchListener
import pe.aioo.openmoa.view.keytouchlistener.RepeatKeyTouchListener
import pe.aioo.openmoa.view.keytouchlistener.SimpleKeyTouchListener
import pe.aioo.openmoa.view.keytouchlistener.SpaceKeyTouchListener
import pe.aioo.openmoa.view.keytouchlistener.UserCharKeyTouchListener
import pe.aioo.openmoa.quickphrase.UserCharKey
import pe.aioo.openmoa.view.message.SpecialKeyMessage
import pe.aioo.openmoa.view.message.StringKeyMessage
import pe.aioo.openmoa.quickphrase.QuickPhraseKey
import pe.aioo.openmoa.quickphrase.QuickPhraseRepository
import pe.aioo.openmoa.view.preview.KeyPreviewController
import pe.aioo.openmoa.view.preview.QuickPhraseMenuPopup
import pe.aioo.openmoa.config.KeyboardSkin
import pe.aioo.openmoa.view.skin.SkinApplier

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
    internal var isMoakeyMode = false
        private set
    private var twoHandBinding: OpenMoaViewBinding? = null
    private var moakeyBinding: OpenMoaViewMoakeyBinding? = null
    private var touchedMoeum: String? = null
    private var moeumKeyBgPressed: android.graphics.drawable.Drawable? = null
    private var moeumKeyBgNormal: android.graphics.drawable.Drawable? = null
    private lateinit var previewController: KeyPreviewController
    private var enterKeyListener: EnterKeyTouchListener? = null

    private fun init() {
        val skin = SettingsPreferences.getKeyboardSkin(context)
        previewController = KeyPreviewController({ config.keyPreviewEnabled }, skin)
        val mode = SettingsPreferences.getHangulInputMode(context)
        isMoakeyMode = mode == HangulInputMode.MOAKEY
        if (isMoakeyMode) {
            inflate(context, R.layout.open_moa_view_moakey, this)
            moakeyBinding = OpenMoaViewMoakeyBinding.bind(this)
            setMoakeyTouchListeners()
        } else {
            inflate(context, R.layout.open_moa_view, this)
            twoHandBinding = OpenMoaViewBinding.bind(this)
            setTwoHandTouchListeners()
        }
        updateQuickPhraseBadges()
        updateUserCharLabels()
        SkinApplier.apply(this, skin)
        moeumKeyBgPressed = SkinApplier.buildKeyDrawable(context, skin, pressed = true)
        moeumKeyBgNormal = SkinApplier.buildKeyDrawable(context, skin, pressed = false)
        if (SettingsPreferences.getOneHandMode(context).isReduced) {
            twoHandBinding?.emojiKey?.setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, 16f)
            moakeyBinding?.emojiKey?.setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, 16f)
        }
    }

    fun refreshQuickPhraseBadges() {
        updateQuickPhraseBadges()
    }

    fun refreshUserCharLabels() {
        updateUserCharLabels()
    }

    private fun updateQuickPhraseBadges() {
        twoHandBinding?.apply {
            ssangbieupBadge.text = QuickPhraseRepository.getFirstChar(context, QuickPhraseKey.SSANGBIEUP)
            ssangjieutBadge.text = QuickPhraseRepository.getFirstChar(context, QuickPhraseKey.SSANGJIEUT)
            ssangdigeutBadge.text = QuickPhraseRepository.getFirstChar(context, QuickPhraseKey.SSANGDIGEUT)
            ssanggiyeokBadge.text = QuickPhraseRepository.getFirstChar(context, QuickPhraseKey.SSANGGIYEOK)
            ssangsiotBadge.text = QuickPhraseRepository.getFirstChar(context, QuickPhraseKey.SSANGSIOT)
            kieukBadge.text = QuickPhraseRepository.getFirstChar(context, QuickPhraseKey.KIEUK)
            tieutBadge.text = QuickPhraseRepository.getFirstChar(context, QuickPhraseKey.TIEUT)
            chieutBadge.text = QuickPhraseRepository.getFirstChar(context, QuickPhraseKey.CHIEUT)
            pieupBadge.text = QuickPhraseRepository.getFirstChar(context, QuickPhraseKey.PIEUP)
        }
        moakeyBinding?.apply {
            ssangbieupBadge.text = QuickPhraseRepository.getFirstChar(context, QuickPhraseKey.SSANGBIEUP)
            ssangjieutBadge.text = QuickPhraseRepository.getFirstChar(context, QuickPhraseKey.SSANGJIEUT)
            ssangdigeutBadge.text = QuickPhraseRepository.getFirstChar(context, QuickPhraseKey.SSANGDIGEUT)
            ssanggiyeokBadge.text = QuickPhraseRepository.getFirstChar(context, QuickPhraseKey.SSANGGIYEOK)
            ssangsiotBadge.text = QuickPhraseRepository.getFirstChar(context, QuickPhraseKey.SSANGSIOT)
            kieukBadge.text = QuickPhraseRepository.getFirstChar(context, QuickPhraseKey.KIEUK)
            tieutBadge.text = QuickPhraseRepository.getFirstChar(context, QuickPhraseKey.TIEUT)
            chieutBadge.text = QuickPhraseRepository.getFirstChar(context, QuickPhraseKey.CHIEUT)
            pieupBadge.text = QuickPhraseRepository.getFirstChar(context, QuickPhraseKey.PIEUP)
        }
    }

    private fun updateUserCharLabels() {
        twoHandBinding?.apply {
            tildeKey.text = UserCharKey.TILDE.getPhrase(context)
            caretKey.text = UserCharKey.CARET.getPhrase(context)
            semicolonKey.text = UserCharKey.SEMICOLON.getPhrase(context)
            asteriskKey.text = UserCharKey.ASTERISK.getPhrase(context)
        }
        moakeyBinding?.apply {
            tildeKey.text = UserCharKey.TILDE.getPhrase(context)
            caretKey.text = UserCharKey.CARET.getPhrase(context)
            semicolonKey.text = UserCharKey.SEMICOLON.getPhrase(context)
            asteriskKey.text = UserCharKey.ASTERISK.getPhrase(context)
            exclamationKey.text = UserCharKey.EXCLAMATION.getPhrase(context)
            questionKey.text = UserCharKey.QUESTION.getPhrase(context)
            dotKey.text = UserCharKey.DOT.getPhrase(context)
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        previewController.cancel()
        enterKeyListener?.cancel()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setTwoHandTouchListeners() {
        val b = twoHandBinding ?: return
        val quickPhraseMenuPopup = QuickPhraseMenuPopup(context)
        b.apply {
            tildeKey.setOnTouchListener(UserCharKeyTouchListener(context, UserCharKey.TILDE, previewController))
            ssangbieupKey.setOnTouchListener(JaumKeyTouchListener(context, "ㅃ", previewController, QuickPhraseKey.SSANGBIEUP, quickPhraseMenuPopup))
            ssangjieutKey.setOnTouchListener(JaumKeyTouchListener(context, "ㅉ", previewController, QuickPhraseKey.SSANGJIEUT, quickPhraseMenuPopup))
            ssangdigeutKey.setOnTouchListener(JaumKeyTouchListener(context, "ㄸ", previewController, QuickPhraseKey.SSANGDIGEUT, quickPhraseMenuPopup))
            ssanggiyeokKey.setOnTouchListener(JaumKeyTouchListener(context, "ㄲ", previewController, QuickPhraseKey.SSANGGIYEOK, quickPhraseMenuPopup))
            ssangsiotKey.setOnTouchListener(JaumKeyTouchListener(context, "ㅆ", previewController, QuickPhraseKey.SSANGSIOT, quickPhraseMenuPopup))
            emojiKey.setOnTouchListener(
                SimpleKeyTouchListener(context, SpecialKeyMessage(SpecialKey.EMOJI))
            )
            caretKey.setOnTouchListener(UserCharKeyTouchListener(context, UserCharKey.CARET, previewController))
            bieupKey.setOnTouchListener(JaumKeyTouchListener(context, "ㅂ", previewController, numberChar = "1"))
            jieutKey.setOnTouchListener(JaumKeyTouchListener(context, "ㅈ", previewController, numberChar = "2"))
            digeutKey.setOnTouchListener(JaumKeyTouchListener(context, "ㄷ", previewController, numberChar = "3"))
            giyeokKey.setOnTouchListener(JaumKeyTouchListener(context, "ㄱ", previewController, numberChar = "4"))
            siotKey.setOnTouchListener(JaumKeyTouchListener(context, "ㅅ", previewController, numberChar = "5"))
            backspaceKey.setOnTouchListener(
                RepeatKeyTouchListener(context, SpecialKeyMessage(SpecialKey.BACKSPACE))
            )
            semicolonKey.setOnTouchListener(
                UserCharKeyTouchListener(context, UserCharKey.SEMICOLON, previewController)
            )
            mieumKey.setOnTouchListener(JaumKeyTouchListener(context, "ㅁ", previewController, numberChar = "6"))
            nieunKey.setOnTouchListener(JaumKeyTouchListener(context, "ㄴ", previewController, numberChar = "7"))
            ieungKey.setOnTouchListener(JaumKeyTouchListener(context, "ㅇ", previewController, numberChar = "8"))
            rieulKey.setOnTouchListener(JaumKeyTouchListener(context, "ㄹ", previewController, numberChar = "9"))
            hieutKey.setOnTouchListener(JaumKeyTouchListener(context, "ㅎ", previewController, numberChar = "0"))
            asteriskKey.setOnTouchListener(
                UserCharKeyTouchListener(context, UserCharKey.ASTERISK, previewController)
            )
            kieukKey.setOnTouchListener(JaumKeyTouchListener(context, "ㅋ", previewController, QuickPhraseKey.KIEUK, quickPhraseMenuPopup))
            tieutKey.setOnTouchListener(JaumKeyTouchListener(context, "ㅌ", previewController, QuickPhraseKey.TIEUT, quickPhraseMenuPopup))
            chieutKey.setOnTouchListener(JaumKeyTouchListener(context, "ㅊ", previewController, QuickPhraseKey.CHIEUT, quickPhraseMenuPopup))
            pieupKey.setOnTouchListener(JaumKeyTouchListener(context, "ㅍ", previewController, QuickPhraseKey.PIEUP, quickPhraseMenuPopup))
            languageKey.setOnTouchListener(LanguageKeyTouchListener(context))
            hanjaNumberPunctuationKey.setOnTouchListener(
                SimpleKeyTouchListener(
                    context, SpecialKeyMessage(SpecialKey.HANJA_NUMBER_PUNCTUATION)
                )
            )
            spaceKey.setOnTouchListener(SpaceKeyTouchListener(context))
            commaQuestionDotExclamationKey.setOnTouchListener(
                CrossKeyTouchListener(
                    context,
                    listOf(
                        StringKeyMessage(","),
                        StringKeyMessage("!"),
                        StringKeyMessage("."),
                        StringKeyMessage("?"),
                    ),
                    previewController,
                )
            )
            enterKeyListener = EnterKeyTouchListener(context)
            enterKey.setOnTouchListener(enterKeyListener)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setMoakeyTouchListeners() {
        val b = moakeyBinding ?: return
        val quickPhraseMenuPopup = QuickPhraseMenuPopup(context)
        b.apply {
            tildeKey.setOnTouchListener(UserCharKeyTouchListener(context, UserCharKey.TILDE, previewController))
            ssangbieupKey.setOnTouchListener(JaumKeyTouchListener(context, "ㅃ", previewController, QuickPhraseKey.SSANGBIEUP, quickPhraseMenuPopup))
            ssangjieutKey.setOnTouchListener(JaumKeyTouchListener(context, "ㅉ", previewController, QuickPhraseKey.SSANGJIEUT, quickPhraseMenuPopup))
            ssangdigeutKey.setOnTouchListener(JaumKeyTouchListener(context, "ㄸ", previewController, QuickPhraseKey.SSANGDIGEUT, quickPhraseMenuPopup))
            ssanggiyeokKey.setOnTouchListener(JaumKeyTouchListener(context, "ㄲ", previewController, QuickPhraseKey.SSANGGIYEOK, quickPhraseMenuPopup))
            ssangsiotKey.setOnTouchListener(JaumKeyTouchListener(context, "ㅆ", previewController, QuickPhraseKey.SSANGSIOT, quickPhraseMenuPopup))
            exclamationKey.setOnTouchListener(
                UserCharKeyTouchListener(context, UserCharKey.EXCLAMATION, previewController)
            )
            caretKey.setOnTouchListener(UserCharKeyTouchListener(context, UserCharKey.CARET, previewController))
            bieupKey.setOnTouchListener(JaumKeyTouchListener(context, "ㅂ", previewController, numberChar = "1"))
            jieutKey.setOnTouchListener(JaumKeyTouchListener(context, "ㅈ", previewController, numberChar = "2"))
            digeutKey.setOnTouchListener(JaumKeyTouchListener(context, "ㄷ", previewController, numberChar = "3"))
            giyeokKey.setOnTouchListener(JaumKeyTouchListener(context, "ㄱ", previewController, numberChar = "4"))
            siotKey.setOnTouchListener(JaumKeyTouchListener(context, "ㅅ", previewController, numberChar = "5"))
            questionKey.setOnTouchListener(
                UserCharKeyTouchListener(context, UserCharKey.QUESTION, previewController)
            )
            semicolonKey.setOnTouchListener(
                UserCharKeyTouchListener(context, UserCharKey.SEMICOLON, previewController)
            )
            mieumKey.setOnTouchListener(JaumKeyTouchListener(context, "ㅁ", previewController, numberChar = "6"))
            nieunKey.setOnTouchListener(JaumKeyTouchListener(context, "ㄴ", previewController, numberChar = "7"))
            ieungKey.setOnTouchListener(JaumKeyTouchListener(context, "ㅇ", previewController, numberChar = "8"))
            rieulKey.setOnTouchListener(JaumKeyTouchListener(context, "ㄹ", previewController, numberChar = "9"))
            hieutKey.setOnTouchListener(JaumKeyTouchListener(context, "ㅎ", previewController, numberChar = "0"))
            dotKey.setOnTouchListener(UserCharKeyTouchListener(context, UserCharKey.DOT, previewController))
            asteriskKey.setOnTouchListener(
                UserCharKeyTouchListener(context, UserCharKey.ASTERISK, previewController)
            )
            kieukKey.setOnTouchListener(JaumKeyTouchListener(context, "ㅋ", previewController, QuickPhraseKey.KIEUK, quickPhraseMenuPopup))
            tieutKey.setOnTouchListener(JaumKeyTouchListener(context, "ㅌ", previewController, QuickPhraseKey.TIEUT, quickPhraseMenuPopup))
            chieutKey.setOnTouchListener(JaumKeyTouchListener(context, "ㅊ", previewController, QuickPhraseKey.CHIEUT, quickPhraseMenuPopup))
            pieupKey.setOnTouchListener(JaumKeyTouchListener(context, "ㅍ", previewController, QuickPhraseKey.PIEUP, quickPhraseMenuPopup))
            backspaceKey.setOnTouchListener(
                RepeatKeyTouchListener(context, SpecialKeyMessage(SpecialKey.BACKSPACE))
            )
            emojiKey.setOnTouchListener(
                SimpleKeyTouchListener(context, SpecialKeyMessage(SpecialKey.EMOJI))
            )
            languageKey.setOnTouchListener(LanguageKeyTouchListener(context))
            hanjaNumberPunctuationKey.setOnTouchListener(
                SimpleKeyTouchListener(
                    context, SpecialKeyMessage(SpecialKey.HANJA_NUMBER_PUNCTUATION)
                )
            )
            spaceKey.setOnTouchListener(SpaceKeyTouchListener(context))
            moeumKey.setOnTouchListener(
                CrossKeyTouchListener(
                    context,
                    listOf(
                        StringKeyMessage("ᆢ"),
                        StringKeyMessage("ㅡ"),
                        StringKeyMessage("ㆍ"),
                        StringKeyMessage("ㅣ"),
                    ),
                    previewController,
                )
            )
            enterKeyListener = EnterKeyTouchListener(context)
            enterKey.setOnTouchListener(enterKeyListener)
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (!isMoakeyMode) {
            val b = twoHandBinding ?: return super.dispatchTouchEvent(ev)
            touchedMoeum.let { moeum ->
                when (ev.action) {
                    MotionEvent.ACTION_DOWN,
                    MotionEvent.ACTION_MOVE -> {
                        if (ev.action == MotionEvent.ACTION_DOWN ||
                            (ev.action == MotionEvent.ACTION_MOVE && touchedMoeum != null)
                        ) {
                            b.iKey.apply {
                                if (ev.x in x..x + width && ev.y in y..y + height) {
                                    if (moeum != "ㅣ") {
                                        background = moeumKeyBgPressed
                                        b.euKey.background = moeumKeyBgNormal
                                        b.araeaKey.background = moeumKeyBgNormal
                                        if (config.hapticFeedback) {
                                            performHapticFeedback(
                                                HapticFeedbackConstants.KEYBOARD_PRESS
                                            )
                                        }
                                        if (moeum != null) {
                                            sendKeyMessage(StringKeyMessage(moeum))
                                        }
                                    }
                                    touchedMoeum = "ㅣ"
                                    return true
                                }
                            }
                            b.euKey.apply {
                                if (ev.x in x..x + width && ev.y in y..y + height) {
                                    if (moeum != "ㅡ") {
                                        background = moeumKeyBgPressed
                                        b.iKey.background = moeumKeyBgNormal
                                        b.araeaKey.background = moeumKeyBgNormal
                                        if (config.hapticFeedback) {
                                            performHapticFeedback(
                                                HapticFeedbackConstants.KEYBOARD_PRESS
                                            )
                                        }
                                        if (moeum != null) {
                                            sendKeyMessage(StringKeyMessage(moeum))
                                        }
                                    }
                                    touchedMoeum = "ㅡ"
                                    return true
                                }
                            }
                            b.araeaKey.apply {
                                if (ev.x in x..x + width && ev.y in y..y + height) {
                                    if (moeum != "ㆍ") {
                                        background = moeumKeyBgPressed
                                        b.iKey.background = moeumKeyBgNormal
                                        b.euKey.background = moeumKeyBgNormal
                                        if (config.hapticFeedback) {
                                            performHapticFeedback(
                                                HapticFeedbackConstants.KEYBOARD_PRESS
                                            )
                                        }
                                        if (moeum != null) {
                                            sendKeyMessage(StringKeyMessage(moeum))
                                        }
                                    }
                                    touchedMoeum = "ㆍ"
                                    return true
                                }
                            }
                        }
                        Unit
                    }
                    MotionEvent.ACTION_UP -> {
                        if (moeum != null) {
                            b.iKey.background = moeumKeyBgNormal
                            b.euKey.background = moeumKeyBgNormal
                            b.araeaKey.background = moeumKeyBgNormal
                            sendKeyMessage(StringKeyMessage(moeum))
                            touchedMoeum = null
                            return true
                        }
                        Unit
                    }
                    else -> Unit
                }
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
