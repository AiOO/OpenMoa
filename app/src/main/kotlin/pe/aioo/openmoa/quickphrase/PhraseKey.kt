package pe.aioo.openmoa.quickphrase

import android.content.Context
import pe.aioo.openmoa.settings.SettingsPreferences

interface PhraseKey {
    val name: String
    val displayName: String
    val defaultPhrase: String
    val prefKey: String

    fun getPhrase(context: Context): String {
        val value = context.getSharedPreferences(SettingsPreferences.PREFS_NAME, Context.MODE_PRIVATE)
            .getString(prefKey, null)
        return if (value.isNullOrEmpty()) defaultPhrase else value
    }

    fun setPhrase(context: Context, phrase: String) {
        context.getSharedPreferences(SettingsPreferences.PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(prefKey, phrase)
            .apply()
    }
}
