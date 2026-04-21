package pe.aioo.openmoa.config

import pe.aioo.openmoa.R

enum class EnterLongPressAction(val labelResId: Int) {
    ARROW(R.string.settings_enter_long_press_arrow),
    IME_PICKER(R.string.settings_enter_long_press_ime_picker),
    SWITCH_LANGUAGE(R.string.settings_enter_long_press_switch_language),
    NONE(R.string.settings_enter_long_press_none);

    companion object {
        fun fromString(value: String?): EnterLongPressAction =
            values().find { it.name == value } ?: ARROW
    }
}
