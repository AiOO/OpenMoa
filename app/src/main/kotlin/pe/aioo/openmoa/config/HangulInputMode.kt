package pe.aioo.openmoa.config

import pe.aioo.openmoa.R

enum class HangulInputMode(val labelResId: Int) {
    MOAKEY(R.string.settings_input_mode_moakey),
    TWO_HAND_MOAKEY(R.string.settings_input_mode_two_hand_moakey);

    companion object {
        fun fromString(value: String?): HangulInputMode =
            values().find { it.name == value } ?: TWO_HAND_MOAKEY
    }
}
