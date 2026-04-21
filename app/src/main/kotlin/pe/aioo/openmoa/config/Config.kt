package pe.aioo.openmoa.config

import android.content.Context
import pe.aioo.openmoa.settings.SettingsPreferences

class Config(private val context: Context) {
    val longPressRepeatTime: Long = 50L
    val longPressThresholdTime: Long
        get() = SettingsPreferences.getLongPressTime(context).millis
    val gestureThreshold: Float = 50f
    val hapticFeedback: Boolean = true
    val maxSuggestionCount: Int = 10
    val keyPreviewEnabled: Boolean
        get() = SettingsPreferences.getKeyPreviewEnabled(context)
    val autoCapitalizeEnglish: Boolean
        get() = SettingsPreferences.getAutoCapitalizeEnglish(context)
}
