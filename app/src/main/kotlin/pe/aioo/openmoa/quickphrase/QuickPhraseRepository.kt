package pe.aioo.openmoa.quickphrase

import android.content.Context
import pe.aioo.openmoa.settings.SettingsPreferences

object QuickPhraseRepository {

    fun getPhrase(context: Context, key: QuickPhraseKey): String {
        val value = context.getSharedPreferences(SettingsPreferences.PREFS_NAME, Context.MODE_PRIVATE)
            .getString(key.prefKey, null)
        return if (value.isNullOrEmpty()) key.defaultPhrase else value
    }

    fun setPhrase(context: Context, key: QuickPhraseKey, phrase: String) {
        context.getSharedPreferences(SettingsPreferences.PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(key.prefKey, phrase)
            .apply()
    }

    fun getFirstChar(context: Context, key: QuickPhraseKey): String {
        val phrase = getPhrase(context, key)
        return phrase.firstOrNull()?.toString() ?: ""
    }
}
