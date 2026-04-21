package pe.aioo.openmoa.view.keyboardview.qwerty

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import pe.aioo.openmoa.R
import pe.aioo.openmoa.config.Config
import pe.aioo.openmoa.view.message.SpecialKey
import pe.aioo.openmoa.databinding.QuertyViewBinding
import pe.aioo.openmoa.quickphrase.QwertyLongKey
import pe.aioo.openmoa.quickphrase.QwertyLongKeyRepository
import pe.aioo.openmoa.view.keytouchlistener.CrossKeyTouchListener
import pe.aioo.openmoa.view.keytouchlistener.EnterKeyTouchListener
import pe.aioo.openmoa.view.keytouchlistener.FunctionalKeyTouchListener
import pe.aioo.openmoa.view.keytouchlistener.LanguageKeyTouchListener
import pe.aioo.openmoa.view.keytouchlistener.QwertyKeyTouchListener
import pe.aioo.openmoa.view.keytouchlistener.RepeatKeyTouchListener
import pe.aioo.openmoa.view.keytouchlistener.SimpleKeyTouchListener
import pe.aioo.openmoa.view.keytouchlistener.SpaceKeyTouchListener
import pe.aioo.openmoa.view.message.SpecialKeyMessage
import pe.aioo.openmoa.view.message.StringKeyMessage
import pe.aioo.openmoa.config.KeyboardSkin
import android.content.Intent
import pe.aioo.openmoa.settings.PhraseEditActivity
import pe.aioo.openmoa.settings.SettingsPreferences
import pe.aioo.openmoa.view.preview.KeyPreviewController
import pe.aioo.openmoa.view.preview.QuickPhraseMenuPopup
import pe.aioo.openmoa.view.skin.SkinApplier

class QuertyView : ConstraintLayout, KoinComponent {

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

    private var shiftKeyStatus = ShiftKeyStatus.DISABLED
    private lateinit var binding: QuertyViewBinding
    private var previewController: KeyPreviewController? = null
    private var currentSkin: KeyboardSkin = KeyboardSkin.DEFAULT
    private val configurableLongKeyListeners = mutableListOf<QwertyKeyTouchListener>()
    private var enterKeyListener: EnterKeyTouchListener? = null
    private var languageKeyListener: LanguageKeyTouchListener? = null
    private val prefs by lazy {
        context.getSharedPreferences(SettingsPreferences.PREFS_NAME, Context.MODE_PRIVATE)
    }
    private val prefKeySet = QwertyLongKey.values().map { it.prefKey }.toSet()
    private val prefChangeListener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
        if (key in prefKeySet && ::binding.isInitialized) updateConfigurableKeyHints()
    }
    private val configurableLongKeyPairs by lazy {
        listOf(
            binding.zKey to QwertyLongKey.Z, binding.xKey to QwertyLongKey.X,
            binding.cKey to QwertyLongKey.C, binding.vKey to QwertyLongKey.V,
            binding.bKey to QwertyLongKey.B, binding.nKey to QwertyLongKey.N,
            binding.mKey to QwertyLongKey.M,
        )
    }

    private fun init() {
        inflate(context, R.layout.querty_view, this)
        binding = QuertyViewBinding.bind(this)
        currentSkin = SettingsPreferences.getKeyboardSkin(context)
        previewController = KeyPreviewController({ config.keyPreviewEnabled }, currentSkin)
        setShiftStatus(ShiftKeyStatus.DISABLED, true)
        setOnTouchListeners()
        SkinApplier.apply(this, currentSkin)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        prefs.registerOnSharedPreferenceChangeListener(prefChangeListener)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        prefs.unregisterOnSharedPreferenceChangeListener(prefChangeListener)
        previewController?.cancel()
        configurableLongKeyListeners.forEach { it.cancel() }
        enterKeyListener?.cancel()
        languageKeyListener?.cancel()
    }

    override fun onWindowFocusChanged(hasWindowFocus: Boolean) {
        super.onWindowFocusChanged(hasWindowFocus)
        if (!hasWindowFocus) {
            configurableLongKeyListeners.forEach { it.cancel() }
        }
    }

    private fun updateConfigurableKeyHints() {
        configurableLongKeyPairs.forEach { (view, longKeyEnum) ->
            view.keyHint = QwertyLongKeyRepository.getPhrase(context, longKeyEnum).take(1)
        }
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
                if (isShiftEnabled) {
                    SkinApplier.fgAccentColor(context, currentSkin)
                } else {
                    SkinApplier.fgColor(context, currentSkin)
                }
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
        ).map {
            it.apply {
                setOnTouchListener(FunctionalKeyTouchListener(context, previewController = previewController) {
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
        setAlphaKeyTouchListeners()
        binding.apply {
            shiftKey.setOnTouchListener(
                FunctionalKeyTouchListener(context, triggerWhenActionUp = false) {
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
            languageKeyListener?.cancel()
            languageKeyListener = LanguageKeyTouchListener(context)
            languageKey.setOnTouchListener(languageKeyListener)
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
    private fun setAlphaKeyTouchListeners() {
        val fixedLongKeyPairs = listOf(
            binding.qKey to "#", binding.wKey to "&", binding.eKey to "%",
            binding.rKey to "+", binding.tKey to "=", binding.yKey to "_",
            binding.uKey to "\\", binding.iKey to "|", binding.oKey to "<", binding.pKey to ">",
            binding.aKey to "-", binding.sKey to "@", binding.dKey to "*",
            binding.fKey to "^", binding.gKey to ":", binding.hKey to ";",
            binding.jKey to "(", binding.kKey to ")", binding.lKey to "~",
        )
        fixedLongKeyPairs.forEach { (view, longKey) ->
            view.apply {
                keyHint = longKey
                setOnTouchListener(QwertyKeyTouchListener(
                    context,
                    previewController,
                    { longKey },
                    onTap = {
                        val key = text.toString()
                        setShiftStatus(when (shiftKeyStatus) {
                            ShiftKeyStatus.ENABLED -> ShiftKeyStatus.DISABLED
                            else -> shiftKeyStatus
                        })
                        StringKeyMessage(key)
                    }
                ))
            }
        }
        configurableLongKeyListeners.clear()
        configurableLongKeyPairs.forEach { (view, longKeyEnum) ->
            val popup = QuickPhraseMenuPopup(context)
            val listener = QwertyKeyTouchListener(
                context,
                previewController,
                { QwertyLongKeyRepository.getPhrase(context, longKeyEnum) },
                onTap = {
                    val key = view.text.toString()
                    setShiftStatus(when (shiftKeyStatus) {
                        ShiftKeyStatus.ENABLED -> ShiftKeyStatus.DISABLED
                        else -> shiftKeyStatus
                    })
                    StringKeyMessage(key)
                },
                quickPhraseMenuPopup = popup,
                onEdit = {
                    val intent = Intent(context, PhraseEditActivity::class.java).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        putExtra(PhraseEditActivity.EXTRA_TYPE, PhraseEditActivity.TYPE_ENGLISH)
                        putExtra(PhraseEditActivity.EXTRA_KEY, longKeyEnum.name)
                    }
                    context.startActivity(intent)
                }
            )
            configurableLongKeyListeners.add(listener)
            view.apply {
                keyHint = QwertyLongKeyRepository.getPhrase(context, longKeyEnum).take(1)
                setOnTouchListener(listener)
            }
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