package pe.aioo.openmoa.view.keytouchlistener

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.view.View
import pe.aioo.openmoa.hangul.HangulPreviewComposer
import pe.aioo.openmoa.hangul.MoeumGestureProcessor
import pe.aioo.openmoa.quickphrase.QuickPhraseKey
import pe.aioo.openmoa.quickphrase.QuickPhraseRepository
import pe.aioo.openmoa.settings.PhraseEditActivity
import pe.aioo.openmoa.view.message.StringKeyMessage
import pe.aioo.openmoa.view.preview.KeyPreviewController
import pe.aioo.openmoa.view.preview.QuickPhraseMenuPopup
import kotlin.math.*

class JaumKeyTouchListener(
    private val context: Context,
    private val key: String,
    private val previewController: KeyPreviewController? = null,
    private val quickPhraseKey: QuickPhraseKey? = null,
    private val quickPhraseMenuPopup: QuickPhraseMenuPopup? = null,
    private val numberChar: String? = null,
) : BaseKeyTouchListener(context) {

    init {
        require(quickPhraseKey == null || numberChar == null) {
            "quickPhraseKey and numberChar cannot both be set"
        }
    }

    private var startX = 0f
    private var startY = 0f
    private var longPressStartX = 0f
    private var hasMoved = false
    private var isLongPressed = false
    private val moeumGestureProcessor = MoeumGestureProcessor()
    private val handler = Handler(Looper.getMainLooper())
    private var anchorView: View? = null

    private val longPressRunnable = Runnable {
        val view = anchorView ?: return@Runnable
        isLongPressed = true
        val phraseKey = quickPhraseKey
        if (phraseKey != null) {
            previewController?.hide()
            val phrase = QuickPhraseRepository.getPhrase(context, phraseKey)
            quickPhraseMenuPopup?.show(view, phrase)
        } else if (numberChar != null) {
            previewController?.update(view, numberChar)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {
        when (motionEvent.action) {
            MotionEvent.ACTION_DOWN -> {
                anchorView = view
                startX = motionEvent.x
                startY = motionEvent.y
                longPressStartX = motionEvent.x
                hasMoved = false
                isLongPressed = false
                moeumGestureProcessor.clear()
                previewController?.show(view, key)
                if (quickPhraseKey != null || numberChar != null) {
                    handler.postDelayed(longPressRunnable, config.longPressThresholdTime)
                }
            }
            MotionEvent.ACTION_MOVE -> {
                if (isLongPressed && numberChar != null) {
                    return true
                }
                val currentX = motionEvent.x
                val currentY = motionEvent.y
                val distance = sqrt((currentX - startX).pow(2) + (currentY - startY).pow(2))
                if (distance > config.gestureThreshold) {
                    if (!hasMoved) {
                        hasMoved = true
                        handler.removeCallbacks(longPressRunnable)
                    }
                    val degree = (atan2(currentY - startY, currentX - startX) * 180f) / PI
                    startX = currentX
                    startY = currentY
                    if (0.001f <= abs(degree) && abs(degree) < 22.5f) {
                        moeumGestureProcessor.appendMoeum("ㅏ")
                    } else if (abs(degree) < 67.5f) {
                        moeumGestureProcessor.appendMoeum(if (degree > 0) "ㅡR" else "ㅣR")
                    } else if (abs(degree) < 112.5f) {
                        moeumGestureProcessor.appendMoeum(if (degree > 0) "ㅜ" else "ㅗ")
                    } else if (abs(degree) < 157.5f) {
                        moeumGestureProcessor.appendMoeum(if (degree > 0) "ㅡL" else "ㅣL")
                    } else if (abs(degree) <= 179.999f) {
                        moeumGestureProcessor.appendMoeum("ㅓ")
                    }
                    val moeum = moeumGestureProcessor.resolveMoeumList()
                    previewController?.update(view, HangulPreviewComposer.compose(key, moeum))
                }
                if (isLongPressed && quickPhraseKey != null) {
                    val dx = motionEvent.x - longPressStartX
                    quickPhraseMenuPopup?.updateSelectionByDelta(dx, view.width)
                }
            }
            MotionEvent.ACTION_UP -> {
                handler.removeCallbacks(longPressRunnable)
                previewController?.hide()
                if (isLongPressed) {
                    isLongPressed = false
                    val phraseKey = quickPhraseKey
                    if (phraseKey != null) {
                        when (quickPhraseMenuPopup?.confirmAndDismiss()) {
                            QuickPhraseMenuPopup.MenuItem.PHRASE_PREVIEW -> {
                                sendKeyMessage(StringKeyMessage(QuickPhraseRepository.getPhrase(context, phraseKey)))
                            }
                            QuickPhraseMenuPopup.MenuItem.EDIT -> {
                                val intent = Intent(context, PhraseEditActivity::class.java).apply {
                                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    putExtra(PhraseEditActivity.EXTRA_TYPE, PhraseEditActivity.TYPE_KOREAN)
                                    putExtra(PhraseEditActivity.EXTRA_KEY, phraseKey.name)
                                }
                                context.startActivity(intent)
                            }
                            else -> Unit
                        }
                    } else if (numberChar != null) {
                        sendKeyMessage(StringKeyMessage(numberChar))
                    }
                } else {
                    // 탭 또는 제스처 종료: 자음 + 합성 모음 전송 (모든 자음 키 공통)
                    sendKeyMessage(StringKeyMessage(key))
                    moeumGestureProcessor.resolveMoeumList()?.let {
                        sendKeyMessage(StringKeyMessage(it))
                    }
                }
            }
            MotionEvent.ACTION_CANCEL -> {
                handler.removeCallbacks(longPressRunnable)
                previewController?.hide()
                quickPhraseMenuPopup?.dismiss()
                isLongPressed = false
            }
        }
        return super.onTouch(view, motionEvent)
    }
}
