package pe.aioo.openmoa.hangul

import com.github.kimkevin.hangulparser.HangulParser
import com.github.kimkevin.hangulparser.HangulParserException

class HangulAssembler {
    private val jamoList = arrayListOf<String>()

    private fun assembleLastJongseongIfCan(jamo: String): Boolean {
        if (!jamo.matches(JAEUM_REGEX) || jamoList.size != 3) {
            return false
        }
        val lastJamo = jamoList.removeLast()
        jamoList.add(
            when (lastJamo) {
                "ㄱ" -> {
                    when (jamo) {
                        "ㅅ" ->  "ㄳ"
                        else -> lastJamo
                    }
                }
                "ㄴ" -> {
                    when (jamo) {
                        "ㅈ" -> "ㄵ"
                        "ㅎ" -> "ㄶ"
                        else -> lastJamo
                    }
                }
                "ㄹ" -> {
                    when (jamo) {
                        "ㄱ" -> "ㄺ"
                        "ㅁ" -> "ㄻ"
                        "ㅂ" -> "ㄼ"
                        "ㅅ" -> "ㄽ"
                        "ㅌ" -> "ㄾ"
                        "ㅍ" -> "ㄿ"
                        "ㅎ" -> "ㅀ"
                        else -> lastJamo
                    }
                }
                "ㅂ" -> {
                    when (jamo) {
                        "ㅅ" -> "ㅄ"
                        else -> lastJamo
                    }
                }
                else -> lastJamo
            }
        )
        return jamoList.last() != lastJamo
    }

    private fun disassembleLastJongseongIfCan() {
        val lastJamo = jamoList.removeLast()
        jamoList.addAll(
            when (lastJamo) {
                "ㄳ" -> listOf("ㄱ", "ㅅ")
                "ㄵ" -> listOf("ㄴ", "ㅈ")
                "ㄶ" -> listOf("ㄴ", "ㅎ")
                "ㄺ" -> listOf("ㄹ", "ㄱ")
                "ㄻ" -> listOf("ㄹ", "ㅁ")
                "ㄼ" -> listOf("ㄹ", "ㅂ")
                "ㄽ" -> listOf("ㄹ", "ㅅ")
                "ㄾ" -> listOf("ㄹ", "ㅌ")
                "ㄿ" -> listOf("ㄹ", "ㅍ")
                "ㅀ" -> listOf("ㄹ", "ㅎ")
                "ㅄ" -> listOf("ㅂ", "ㅅ")
                else -> listOf(lastJamo)
            }
        )
    }

    private fun assembleLastMoeumIfCan(jamo: String): Boolean {
        if (!jamoList.last().matches(MOEUM_REGEX)) {
            return false
        }
        val lastJamo = jamoList.removeLast()
        jamoList.add(
            when (lastJamo) {
                "ㅏ" -> when (jamo) {
                    "ㅣ" -> "ㅐ"
                    "ㆍ" -> "ㅑ"
                    else -> lastJamo
                }
                "ㅐ" -> when (jamo) {
                    "ㆍ" -> "ㅒ"
                    else -> lastJamo
                }
                "ㅑ" -> when (jamo) {
                    "ㅣ" -> "ㅒ"
                    else -> lastJamo
                }
                "ㅓ" -> when (jamo) {
                    "ㅣ" -> "ㅔ"
                    "ㆍ" -> "ㅕ"
                    else -> lastJamo
                }
                "ㅔ" -> when (jamo) {
                    "ㆍ" -> "ㅖ"
                    else -> lastJamo
                }
                "ㅕ" -> when (jamo) {
                    "ㅣ" -> "ㅖ"
                    else -> lastJamo
                }
                "ㅗ" -> when (jamo) {
                    "ㅣ" -> "ㅚ"
                    "ㆍ" -> "ㅛ"
                    else -> lastJamo
                }
                "ㅘ" -> when (jamo) {
                    "ㅣ" -> "ㅙ"
                    else -> lastJamo
                }
                "ㅚ" -> when (jamo) {
                    "ㆍ" -> "ㅘ"
                    else -> lastJamo
                }
                "ㅜ" -> when (jamo) {
                    "ㅣ" -> "ㅟ"
                    "ㆍ" -> "ㅠ"
                    else -> lastJamo
                }
                "ㅝ" -> when (jamo) {
                    "ㅣ" ->  "ㅞ"
                    else -> lastJamo
                }
                "ㅠ" -> when (jamo) {
                    "ㅣ" ->  "ㅝ"
                    else -> lastJamo
                }
                "ㅡ" -> when (jamo) {
                    "ㅣ" -> "ㅢ"
                    "ㆍ" -> "ㅜ"
                    else -> lastJamo
                }
                "ㅣ" -> when (jamo) {
                    "ㆍ" -> "ㅏ"
                    else -> lastJamo
                }
                "ㆍ" -> when (jamo) {
                    "ㅡ" -> "ㅗ"
                    "ㅣ" -> "ㅓ"
                    "ㆍ" -> "ᆢ"
                    else -> lastJamo
                }
                "ᆢ" -> when (jamo) {
                    "ㅡ" -> "ㅛ"
                    "ㅣ" -> "ㅕ"
                    else -> lastJamo
                }
                else -> lastJamo
            }
        )
        return jamoList.last() != lastJamo
    }

    private fun resolveJamoList(): String? {
        try {
            val assembled = HangulParser.assemble(jamoList)
            if (assembled.length > 1) {
                for (i in 0 until HangulParser.disassemble(assembled.substring(0, 1)).size) {
                    jamoList.removeFirst()
                }
                return assembled.substring(0, 1)
            }
            return null
        } catch (e: HangulParserException) {
            val prevJamoList = jamoList.subList(0, jamoList.size - 1)
            val resolved = try {
                HangulParser.assemble(prevJamoList)
            } catch (_: HangulParserException) {
                prevJamoList.joinToString("")
            }
            for (i in 0 until prevJamoList.size) {
                jamoList.removeFirst()
            }
            return resolved
        }
    }

    fun appendJamo(jamo: String): String? {
        if (jamoList.isEmpty()) {
            jamoList.add(jamo)
            return null
        }
        if (jamo.matches(MOEUM_REGEX)) {
            disassembleLastJongseongIfCan()
        }
        if (assembleLastMoeumIfCan(jamo)) {
            return null
        }
        if (assembleLastJongseongIfCan(jamo)) {
            return null
        }
        if (jamo.matches(ARAEA_REGEX) && jamoList.last().matches(JAEUM_REGEX)) {
            val resolved = resolveJamoList()
            jamoList.add(jamo)
            return resolved
        }
        jamoList.add(jamo)
        return resolveJamoList()
    }

    fun getUnresolved(): String? {
        return if (jamoList.isEmpty()) {
            null
        } else {
            try {
                HangulParser.assemble(jamoList)
            } catch (_: HangulParserException) {
                jamoList.joinToString("")
            }
        }
    }

    fun removeLastJamo() {
        jamoList.removeLastOrNull()
    }

    fun clear() {
        jamoList.clear()
    }

    companion object {
        val JAMO_REGEX = Regex("^[ㄱ-ㅎㅏ-ㅣㆍ]$")
        private val JAEUM_REGEX = Regex("^[ㄱ-ㅎ]$")
        private val MOEUM_REGEX = Regex("^[ㅏ-ㅣㆍᆢ]$")
        private val ARAEA_REGEX = Regex("^[ㆍᆢ]$")
    }
}