package pe.aioo.openmoa.hangul

class MoeumGestureProcessor {

    private val moeumList = arrayListOf<String>()

    fun appendMoeum(moeum: String) {
        moeumList.add(moeum)
    }

    fun clear() {
        moeumList.clear()
    }

    fun resolveMoeumList(): String? {
        var moeum: String? = null
        for (nextMoeum in moeumList) {
            moeum = when (moeum) {
                "ㅏ" -> when (nextMoeum) {
                    "ㅓ", "ㅗ", "ㅜ" -> "ㅐ"
                    else -> moeum
                }
                "ㅐ" -> when (nextMoeum) {
                    "ㅏ" -> "ㅑ"
                    else -> moeum
                }
                "ㅑ" -> when (nextMoeum) {
                    "ㅓ" -> "ㅒ"
                    else -> moeum
                }
                "ㅓ" -> when (nextMoeum) {
                    "ㅏ", "ㅗ", "ㅜ" -> "ㅔ"
                    else -> moeum
                }
                "ㅔ" -> when (nextMoeum) {
                    "ㅓ" -> "ㅕ"
                    else -> moeum
                }
                "ㅕ" -> when (nextMoeum) {
                    "ㅏ" -> "ㅖ"
                    else -> moeum
                }
                "ㅗ" -> when (nextMoeum) {
                    "ㅏ" -> "ㅘ"
                    "ㅜ" -> "ㅚ"
                    else -> moeum
                }
                "ㅘ" -> when (nextMoeum) {
                    "ㅓ" -> "ㅙ"
                    else -> moeum
                }
                "ㅚ" -> when (nextMoeum) {
                    "ㅗ" -> "ㅛ"
                    else -> moeum
                }
                "ㅜ" -> when (nextMoeum) {
                    "ㅓ" -> "ㅝ"
                    "ㅗ" -> "ㅟ"
                    else -> moeum
                }
                "ㅝ" -> when (nextMoeum) {
                    "ㅏ" -> "ㅞ"
                    else -> moeum
                }
                "ㅟ" -> when (nextMoeum) {
                    "ㅜ" -> "ㅠ"
                    else -> moeum
                }
                "ㅡL" -> when (nextMoeum) {
                    "ㅏ", "ㅜ" -> "ㅡLㅜ"
                    "ㅓ", "ㅗ" -> "ㅡLㅓ"
                    "ㅣL", "ㅣR" -> "ㅢ"
                    else -> moeum
                }
                "ㅡLㅓ" -> when (nextMoeum) {
                    "ㅓ", "ㅗ" -> "ㅓ"
                    "ㅣL", "ㅣR" -> "ㅢ"
                    else -> moeum
                }
                "ㅡLㅜ" -> when (nextMoeum) {
                    "ㅏ", "ㅜ" -> "ㅜ"
                    "ㅣL", "ㅣR" -> "ㅢ"
                    else -> moeum
                }
                "ㅡR" -> when (nextMoeum) {
                    "ㅏ", "ㅗ" -> "ㅡRㅏ"
                    "ㅓ", "ㅜ" -> "ㅡRㅜ"
                    "ㅣL", "ㅣR" -> "ㅢ"
                    else -> moeum
                }
                "ㅡRㅏ" -> when (nextMoeum) {
                    "ㅏ", "ㅗ" -> "ㅏ"
                    "ㅣL", "ㅣR" -> "ㅢ"
                    else -> moeum
                }
                "ㅡRㅜ" -> when (nextMoeum) {
                    "ㅓ", "ㅜ" -> "ㅜ"
                    "ㅣL", "ㅣR" -> "ㅢ"
                    else -> moeum
                }
                "ㅣL" -> when (nextMoeum) {
                    "ㅏ", "ㅗ" -> "ㅣLㅗ"
                    "ㅓ", "ㅜ" -> "ㅣLㅓ"
                    else -> moeum
                }
                "ㅣLㅓ" -> when (nextMoeum) {
                    "ㅓ", "ㅜ" -> "ㅓ"
                    else -> moeum
                }
                "ㅣLㅗ" -> when (nextMoeum) {
                    "ㅏ", "ㅗ" -> "ㅗ"
                    else -> moeum
                }
                "ㅣR" -> when (nextMoeum) {
                    "ㅏ", "ㅜ" -> "ㅣRㅏ"
                    "ㅓ", "ㅗ" -> "ㅣRㅗ"
                    else -> moeum
                }
                "ㅣRㅏ" -> when (nextMoeum) {
                    "ㅏ", "ㅜ" -> "ㅏ"
                    else -> moeum
                }
                "ㅣRㅗ" -> when (nextMoeum) {
                    "ㅓ", "ㅗ" -> "ㅗ"
                    else -> moeum
                }
                null -> nextMoeum
                else -> moeum
            }
        }
        return moeum?.substring(0, 1)
    }

}