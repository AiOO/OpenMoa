package pe.aioo.openmoa.quickphrase

enum class QwertyLongKey(
    val letter: String,
    override val defaultPhrase: String,
    override val prefKey: String,
) : PhraseKey {
    Z("z", "/", "qwerty_long_key_z"),
    X("x", "'", "qwerty_long_key_x"),
    C("c", "\"", "qwerty_long_key_c"),
    V("v", ".", "qwerty_long_key_v"),
    B("b", ",", "qwerty_long_key_b"),
    N("n", "?", "qwerty_long_key_n"),
    M("m", "!", "qwerty_long_key_m");

    override val displayName get() = letter
}
