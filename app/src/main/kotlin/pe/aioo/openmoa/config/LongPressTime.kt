package pe.aioo.openmoa.config

import pe.aioo.openmoa.R

enum class LongPressTime(val labelResId: Int, val millis: Long) {
    MS_200(R.string.settings_long_press_time_200, 200L),
    MS_300(R.string.settings_long_press_time_300, 300L),
    MS_500(R.string.settings_long_press_time_500, 500L),
    MS_1000(R.string.settings_long_press_time_1000, 1000L),
    MS_1500(R.string.settings_long_press_time_1500, 1500L);

    companion object {
        fun fromString(value: String?): LongPressTime =
            values().find { it.name == value } ?: MS_500
    }
}
