package pe.aioo.openmoa.quickphrase

enum class UserCharKey(
    val symbol: String,
    override val defaultPhrase: String,
    override val prefKey: String,
) : PhraseKey {
    TILDE("~", "~", "user_char_tilde"),
    CARET("^", "^", "user_char_caret"),
    SEMICOLON(";", ";", "user_char_semicolon"),
    ASTERISK("*", "*", "user_char_asterisk"),
    EXCLAMATION("!", "!", "user_char_exclamation"),
    QUESTION("?", "?", "user_char_question"),
    DOT(".", ".", "user_char_dot");

    override val displayName get() = symbol
}
