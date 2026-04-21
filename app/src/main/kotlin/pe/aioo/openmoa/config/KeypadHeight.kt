package pe.aioo.openmoa.config

import pe.aioo.openmoa.R

enum class KeypadHeight(val labelResId: Int, val heightScale: Float) {
    NORMAL(R.string.settings_keypad_height_normal, 1.0f),
    LOW(R.string.settings_keypad_height_low, 0.85f),
    VERY_LOW(R.string.settings_keypad_height_very_low, 0.70f);

    companion object {
        fun fromString(value: String?): KeypadHeight =
            values().find { it.name == value } ?: LOW
    }
}
