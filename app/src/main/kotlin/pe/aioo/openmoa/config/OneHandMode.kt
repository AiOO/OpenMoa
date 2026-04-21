package pe.aioo.openmoa.config

import android.view.Gravity
import pe.aioo.openmoa.R

enum class OneHandMode(val labelResId: Int, val gravity: Int) {
    NONE(R.string.settings_one_hand_mode_none, Gravity.START),
    LEFT(R.string.settings_one_hand_mode_left, Gravity.START),
    RIGHT(R.string.settings_one_hand_mode_right, Gravity.END),
    CENTER(R.string.settings_one_hand_mode_center, Gravity.CENTER_HORIZONTAL);

    val isReduced: Boolean get() = this != NONE

    companion object {
        fun fromString(value: String?): OneHandMode =
            values().find { it.name == value } ?: NONE
    }
}
