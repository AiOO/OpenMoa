package pe.aioo.openmoa.config

import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import pe.aioo.openmoa.R

enum class KeyboardSkin(
    @StringRes val labelResId: Int,
    @ColorRes val keyboardBgColorRes: Int,
    @ColorRes val keyBgColorRes: Int,
    @ColorRes val keyBgPressedColorRes: Int,
    @ColorRes val keyFgColorRes: Int,
    @ColorRes val keyFgMutedColorRes: Int,
    @ColorRes val keyFgAccentColorRes: Int,
) {
    WHITE(
        R.string.settings_keyboard_skin_white,
        R.color.skin_white_keyboard_bg,
        R.color.skin_white_key_bg,
        R.color.skin_white_key_bg_pressed,
        R.color.skin_white_key_fg,
        R.color.skin_white_key_fg_muted,
        R.color.skin_white_key_fg_accent,
    ),
    DARK_GRAY(
        R.string.settings_keyboard_skin_dark_gray,
        R.color.skin_dark_gray_keyboard_bg,
        R.color.skin_dark_gray_key_bg,
        R.color.skin_dark_gray_key_bg_pressed,
        R.color.skin_dark_gray_key_fg,
        R.color.skin_dark_gray_key_fg_muted,
        R.color.skin_dark_gray_key_fg_accent,
    ),
    BLACK(
        R.string.settings_keyboard_skin_black,
        R.color.skin_black_keyboard_bg,
        R.color.skin_black_key_bg,
        R.color.skin_black_key_bg_pressed,
        R.color.skin_black_key_fg,
        R.color.skin_black_key_fg_muted,
        R.color.skin_black_key_fg_accent,
    ),
    BLUE(
        R.string.settings_keyboard_skin_blue,
        R.color.skin_blue_keyboard_bg,
        R.color.skin_blue_key_bg,
        R.color.skin_blue_key_bg_pressed,
        R.color.skin_blue_key_fg,
        R.color.skin_blue_key_fg_muted,
        R.color.skin_blue_key_fg_accent,
    ),
    GREEN(
        R.string.settings_keyboard_skin_green,
        R.color.skin_green_keyboard_bg,
        R.color.skin_green_key_bg,
        R.color.skin_green_key_bg_pressed,
        R.color.skin_green_key_fg,
        R.color.skin_green_key_fg_muted,
        R.color.skin_green_key_fg_accent,
    );

    companion object {
        val DEFAULT = WHITE

        fun fromString(value: String?): KeyboardSkin =
            values().firstOrNull { it.name == value } ?: DEFAULT
    }
}
