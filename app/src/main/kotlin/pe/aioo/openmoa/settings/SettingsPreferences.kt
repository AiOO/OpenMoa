package pe.aioo.openmoa.settings

import android.content.Context
import pe.aioo.openmoa.config.EnterLongPressAction
import pe.aioo.openmoa.config.HangulInputMode
import pe.aioo.openmoa.config.KeyboardSkin
import pe.aioo.openmoa.config.KeypadHeight
import pe.aioo.openmoa.config.LongPressTime
import pe.aioo.openmoa.config.OneHandMode
import pe.aioo.openmoa.config.SpaceLongPressAction
import pe.aioo.openmoa.quickphrase.UserCharKey

object SettingsPreferences {

    const val PREFS_NAME = "openmoa_settings"
    const val KEY_HANGUL_INPUT_MODE = "hangul_input_mode"
    const val KEY_KEYPAD_HEIGHT = "keypad_height"
    const val KEY_ONE_HAND_MODE = "one_hand_mode"
    const val KEY_KEY_PREVIEW = "key_preview_enabled"
    const val KEY_LONG_PRESS_TIME = "long_press_time"
    const val KEY_SPACE_LONG_PRESS_ACTION = "space_long_press_action"
    const val KEY_AUTO_SPACE_PERIOD = "auto_space_period"
    const val KEY_KEYBOARD_SKIN = "keyboard_skin"
    const val KEY_AUTO_CAPITALIZE_ENGLISH = "auto_capitalize_english"
    const val KEY_ENTER_LONG_PRESS_ACTION = "enter_long_press_action"

    val ALL_KEYS = setOf(
        KEY_HANGUL_INPUT_MODE,
        KEY_KEYPAD_HEIGHT,
        KEY_ONE_HAND_MODE,
        KEY_KEY_PREVIEW,
        KEY_LONG_PRESS_TIME,
        KEY_SPACE_LONG_PRESS_ACTION,
        KEY_AUTO_SPACE_PERIOD,
        KEY_KEYBOARD_SKIN,
        KEY_AUTO_CAPITALIZE_ENGLISH,
        KEY_ENTER_LONG_PRESS_ACTION,
    ) + UserCharKey.values().map { it.prefKey }.toSet()

    fun getKeyPreviewEnabled(context: Context): Boolean {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getBoolean(KEY_KEY_PREVIEW, true)
    }

    fun setKeyPreviewEnabled(context: Context, enabled: Boolean) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_KEY_PREVIEW, enabled)
            .apply()
    }

    fun getHangulInputMode(context: Context): HangulInputMode {
        val value = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(KEY_HANGUL_INPUT_MODE, null)
        return HangulInputMode.fromString(value)
    }

    fun getKeypadHeight(context: Context): KeypadHeight {
        val value = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(KEY_KEYPAD_HEIGHT, null)
        return KeypadHeight.fromString(value)
    }

    fun getLongPressTime(context: Context): LongPressTime {
        val value = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(KEY_LONG_PRESS_TIME, null)
        return LongPressTime.fromString(value)
    }

    fun getOneHandMode(context: Context): OneHandMode {
        val value = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(KEY_ONE_HAND_MODE, null)
        return OneHandMode.fromString(value)
    }

    fun getSpaceLongPressAction(context: Context): SpaceLongPressAction {
        val value = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(KEY_SPACE_LONG_PRESS_ACTION, null)
        return SpaceLongPressAction.fromString(value)
    }

    fun getKeyboardSkin(context: Context): KeyboardSkin {
        val value = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(KEY_KEYBOARD_SKIN, null)
        return KeyboardSkin.fromString(value)
    }

    fun getAutoSpacePeriod(context: Context): Boolean {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getBoolean(KEY_AUTO_SPACE_PERIOD, false)
    }

    fun setAutoSpacePeriod(context: Context, enabled: Boolean) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_AUTO_SPACE_PERIOD, enabled)
            .apply()
    }

    fun getAutoCapitalizeEnglish(context: Context): Boolean {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getBoolean(KEY_AUTO_CAPITALIZE_ENGLISH, true)
    }

    fun setAutoCapitalizeEnglish(context: Context, enabled: Boolean) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_AUTO_CAPITALIZE_ENGLISH, enabled)
            .apply()
    }

    fun getEnterLongPressAction(context: Context): EnterLongPressAction {
        val value = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(KEY_ENTER_LONG_PRESS_ACTION, null)
        return EnterLongPressAction.fromString(value)
    }

    fun save(context: Context, key: String, value: String) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(key, value)
            .apply()
    }
}
