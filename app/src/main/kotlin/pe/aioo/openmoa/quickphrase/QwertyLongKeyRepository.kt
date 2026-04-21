package pe.aioo.openmoa.quickphrase

import android.content.Context
import pe.aioo.openmoa.settings.SettingsPreferences

object QwertyLongKeyRepository {

    fun getPhrase(context: Context, key: QwertyLongKey): String {
        val value = context.getSharedPreferences(SettingsPreferences.PREFS_NAME, Context.MODE_PRIVATE)
            .getString(key.prefKey, null)
        return if (value.isNullOrEmpty()) key.defaultPhrase else value
    }

    fun setPhrase(context: Context, key: QwertyLongKey, phrase: String) {
        context.getSharedPreferences(SettingsPreferences.PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(key.prefKey, phrase)
            .apply()
    }
}
