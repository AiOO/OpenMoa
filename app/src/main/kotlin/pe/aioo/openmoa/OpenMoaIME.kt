package pe.aioo.openmoa

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import android.inputmethodservice.InputMethodService
import android.view.View
import android.view.WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS
import android.view.inputmethod.EditorInfo
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.github.kimkevin.hangulparser.HangulParser
import com.github.kimkevin.hangulparser.HangulParserException

class OpenMoaIME : InputMethodService() {

    private lateinit var broadcastReceiver: BroadcastReceiver
    private val jamoList = arrayListOf<String>()

    override fun onCreate() {
        super.onCreate()
        broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val key = intent.getStringExtra(EXTRA_NAME) ?: return
                // Process for special key
                if (key.length > 1) {
                    when (key) {
                        SpecialKey.BACKSPACE.value -> {
                            if (jamoList.size > 0) {
                                jamoList.removeLast()
                                val assembled = try {
                                    HangulParser.assemble(jamoList)
                                } catch (_: HangulParserException) {
                                    jamoList.joinToString("")
                                }
                                currentInputConnection.setComposingText(assembled, 1)
                            } else {
                                currentInputConnection.deleteSurroundingText(1, 0)
                            }
                        }
                        SpecialKey.ENTER.value -> currentInputConnection.performEditorAction(
                            EditorInfo.IME_ACTION_GO
                        )
                    }
                    return
                }
                // Process for non-Hangul key
                if (!key.matches(Regex("^[ㄱ-ㅎㅏ-ㅣㆍ]$"))) {
                    if (jamoList.size > 0) {
                        val assembled = try {
                            HangulParser.assemble(jamoList)
                        } catch (_: HangulParserException) {
                            jamoList.joinToString("")
                        }
                        currentInputConnection.commitText(assembled, 1)
                        jamoList.clear()
                    }
                    currentInputConnection.commitText(key, 1)
                    return
                }
                // Process for Hangul key
                if (jamoList.isEmpty()) {
                    jamoList.add(key)
                    currentInputConnection.setComposingText(key, 1)
                    return
                }
                when (key) {
                    "ㆍ" -> {
                        when (jamoList.last()) {
                            "ㅏ" -> {
                                jamoList[jamoList.lastIndex] = "ㅑ"
                            }
                            "ㅐ" -> {
                                jamoList[jamoList.lastIndex] = "ㅒ"
                            }
                            "ㅓ" -> {
                                jamoList[jamoList.lastIndex] = "ㅕ"
                            }
                            "ㅔ" -> {
                                jamoList[jamoList.lastIndex] = "ㅖ"
                            }
                            "ㅗ" -> {
                                jamoList[jamoList.lastIndex] = "ㅛ"
                            }
                            "ㅚ" -> {
                                jamoList[jamoList.lastIndex] = "ㅘ"
                            }
                            "ㅜ" -> {
                                jamoList[jamoList.lastIndex] = "ㅠ"
                            }
                            "ㅡ" -> {
                                jamoList[jamoList.lastIndex] = "ㅜ"
                            }
                            "ㅣ" -> {
                                jamoList[jamoList.lastIndex] = "ㅏ"
                            }
                            else -> {
                                if (jamoList.last() == "ㆍ") {
                                    jamoList[jamoList.lastIndex] = "⠒"
                                    currentInputConnection.setComposingText(
                                        jamoList.joinToString(""), 1
                                    )
                                    return
                                } else if (jamoList.last().matches(Regex("^[ㄱ-ㅎ]$"))) {
                                    val prevJamoList = jamoList.subList(0, jamoList.size - 1)
                                    val assembled = try {
                                        HangulParser.assemble(prevJamoList)
                                    } catch (_: HangulParserException) {
                                        prevJamoList.joinToString("")
                                    }
                                    for (i in 0 until prevJamoList.size) {
                                        jamoList.removeFirst()
                                    }
                                    currentInputConnection.commitText(assembled, 1)
                                    jamoList.add(key)
                                    currentInputConnection.setComposingText(
                                        jamoList.joinToString(""), 1
                                    )
                                    return
                                }
                                jamoList.add(key)
                            }
                        }
                    }
                    "ㅡ" -> {
                        when (jamoList.last()) {
                            "ㆍ" -> {
                                jamoList[jamoList.lastIndex] = "ㅗ"
                            }
                            "⠒" -> {
                                jamoList[jamoList.lastIndex] = "ㅛ"
                            }
                            else -> {
                                jamoList.add(key)
                            }
                        }
                    }
                    "ㅣ" -> {
                        when (jamoList.last()) {
                            "ㆍ" -> {
                                jamoList[jamoList.lastIndex] = "ㅓ"
                            }
                            "⠒" -> {
                                jamoList[jamoList.lastIndex] = "ㅕ"
                            }
                            "ㅏ" -> {
                                jamoList[jamoList.lastIndex] = "ㅐ"
                            }
                            "ㅑ" -> {
                                jamoList[jamoList.lastIndex] = "ㅒ"
                            }
                            "ㅓ" -> {
                                jamoList[jamoList.lastIndex] = "ㅔ"
                            }
                            "ㅕ" -> {
                                jamoList[jamoList.lastIndex] = "ㅖ"
                            }
                            "ㅗ" -> {
                                jamoList[jamoList.lastIndex] = "ㅚ"
                            }
                            "ㅘ" -> {
                                jamoList[jamoList.lastIndex] = "ㅙ"
                            }
                            "ㅛ" -> {
                                jamoList[jamoList.lastIndex] = "ㅘ"
                            }
                            "ㅜ" -> {
                                jamoList[jamoList.lastIndex] = "ㅟ"
                            }
                            "ㅝ" -> {
                                jamoList[jamoList.lastIndex] = "ㅞ"
                            }
                            "ㅠ" -> {
                                jamoList[jamoList.lastIndex] = "ㅝ"
                            }
                            "ㅡ" -> {
                                jamoList[jamoList.lastIndex] = "ㅢ"
                            }
                            else -> {
                                jamoList.add(key)
                            }
                        }
                    }
                    else -> {
                        if (jamoList.size == 3) {
                            when (jamoList.last()) {
                                "ㄱ" -> {
                                    when (key) {
                                        "ㅅ" -> {
                                            jamoList[jamoList.lastIndex] = "ㄳ"
                                        }
                                        else -> jamoList.add(key)
                                    }
                                }
                                "ㄴ" -> {
                                    when (key) {
                                        "ㅈ" -> {
                                            jamoList[jamoList.lastIndex] = "ㄵ"
                                        }
                                        "ㅎ" -> {
                                            jamoList[jamoList.lastIndex] = "ㄶ"
                                        }
                                        else -> jamoList.add(key)
                                    }
                                }
                                "ㄹ" -> {
                                    when (key) {
                                        "ㄱ" -> {
                                            jamoList[jamoList.lastIndex] = "ㄺ"
                                        }
                                        "ㅁ" -> {
                                            jamoList[jamoList.lastIndex] = "ㄻ"
                                        }
                                        "ㅂ" -> {
                                            jamoList[jamoList.lastIndex] = "ㄼ"
                                        }
                                        "ㅅ" -> {
                                            jamoList[jamoList.lastIndex] = "ㄽ"
                                        }
                                        "ㅌ" -> {
                                            jamoList[jamoList.lastIndex] = "ㄾ"
                                        }
                                        "ㅍ" -> {
                                            jamoList[jamoList.lastIndex] = "ㄿ"
                                        }
                                        "ㅎ" -> {
                                            jamoList[jamoList.lastIndex] = "ㅀ"
                                        }
                                        else -> jamoList.add(key)
                                    }
                                }
                                "ㅂ" -> {
                                    when (key) {
                                        "ㅅ" -> {
                                            jamoList[jamoList.lastIndex] = "ㅄ"
                                        }
                                        else -> jamoList.add(key)
                                    }
                                }
                                else -> jamoList.add(key)
                            }
                        } else {
                            jamoList.add(key)
                        }
                    }
                }
                // Assemble jamoList
                try {
                    val assembled = HangulParser.assemble(jamoList)
                    if (assembled.length > 1) {
                        val composed = assembled.substring(0, 1)
                        currentInputConnection.commitText(composed, 1)
                        currentInputConnection.setComposingText(
                            assembled.substring(1, 2), 1
                        )
                        for (i in 0 until HangulParser.disassemble(composed).size) {
                            jamoList.removeFirst()
                        }
                        return
                    }
                    currentInputConnection.setComposingText(assembled, 1)
                } catch (e: HangulParserException) {
                    val prevJamoList = jamoList.subList(0, jamoList.size - 1)
                    val assembled = try {
                        HangulParser.assemble(prevJamoList)
                    } catch (_: HangulParserException) {
                        prevJamoList.joinToString("")
                    }
                    for (i in 0 until prevJamoList.size) {
                        jamoList.removeFirst()
                    }
                    currentInputConnection.commitText(assembled, 1)
                    currentInputConnection.setComposingText(jamoList.first(), 1)
                }
            }
        }
        LocalBroadcastManager.getInstance(this).registerReceiver(
            broadcastReceiver, IntentFilter(INTENT_ACTION)
        )
    }

    @SuppressLint("InflateParams")
    override fun onCreateInputView(): View {
        window.window?.apply {
            navigationBarColor =
                ContextCompat.getColor(this@OpenMoaIME, R.color.keyboard_background)
            when (resources.configuration.uiMode.and(Configuration.UI_MODE_NIGHT_MASK)) {
                Configuration.UI_MODE_NIGHT_YES -> {
                    insetsController?.apply {
                        setSystemBarsAppearance(0, APPEARANCE_LIGHT_NAVIGATION_BARS)
                    }
                }
                Configuration.UI_MODE_NIGHT_NO, Configuration.UI_MODE_NIGHT_UNDEFINED -> {
                    insetsController?.apply {
                        setSystemBarsAppearance(
                            APPEARANCE_LIGHT_NAVIGATION_BARS, APPEARANCE_LIGHT_NAVIGATION_BARS
                        )
                    }
                }
            }
        }
        return layoutInflater.inflate(R.layout.open_moa_ime, null)
    }

    override fun onDestroy() {
        if (this::broadcastReceiver.isInitialized) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver)
        }
        super.onDestroy()
    }

    companion object {
        const val INTENT_ACTION = "keyInput"
        const val EXTRA_NAME = "key"
    }

}