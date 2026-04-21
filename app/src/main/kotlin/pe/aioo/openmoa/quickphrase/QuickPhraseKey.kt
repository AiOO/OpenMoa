package pe.aioo.openmoa.quickphrase

enum class QuickPhraseKey(
    val jaum: String,
    override val defaultPhrase: String,
    override val prefKey: String,
) : PhraseKey {
    KIEUK("ㅋ", "안녕하세요", "quick_phrase_kieuk"),
    TIEUT("ㅌ", "감사합니다", "quick_phrase_tieut"),
    CHIEUT("ㅊ", "어디세요?", "quick_phrase_chieut"),
    PIEUP("ㅍ", "연락바랍니다", "quick_phrase_pieup"),
    SSANGBIEUP("ㅃ", "잠시만요", "quick_phrase_ssangbieup"),
    SSANGJIEUT("ㅉ", "알겠습니다", "quick_phrase_ssangjieut"),
    SSANGDIGEUT("ㄸ", "수고하세요", "quick_phrase_ssangdigeut"),
    SSANGGIYEOK("ㄲ", "죄송합니다", "quick_phrase_ssanggiyeok"),
    SSANGSIOT("ㅆ", "확인해드릴게요", "quick_phrase_ssangsiot");

    override val displayName get() = jaum
}
