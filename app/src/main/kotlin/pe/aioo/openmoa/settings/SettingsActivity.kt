package pe.aioo.openmoa.settings

import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONObject
import pe.aioo.openmoa.R
import pe.aioo.openmoa.config.EnterLongPressAction
import pe.aioo.openmoa.config.HangulInputMode
import pe.aioo.openmoa.config.KeyboardSkin
import pe.aioo.openmoa.config.KeypadHeight
import pe.aioo.openmoa.config.LongPressTime
import pe.aioo.openmoa.config.OneHandMode
import pe.aioo.openmoa.config.SpaceLongPressAction
import pe.aioo.openmoa.databinding.ActivitySettingsBinding
import pe.aioo.openmoa.quickphrase.QuickPhraseKey
import pe.aioo.openmoa.quickphrase.QuickPhraseRepository
import pe.aioo.openmoa.quickphrase.QwertyLongKey
import pe.aioo.openmoa.quickphrase.QwertyLongKeyRepository
import java.io.File

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupViews()
    }

    private fun setupViews() {
        refreshAllDisplays()
        binding.hangulInputModeItem.setOnClickListener { showInputModeDialog() }
        binding.keyboardSkinItem.setOnClickListener { showKeyboardSkinDialog() }
        binding.keypadHeightItem.setOnClickListener { showKeypadHeightDialog() }
        binding.oneHandModeItem.setOnClickListener { showOneHandModeDialog() }
        binding.longPressTimeItem.setOnClickListener { showLongPressTimeDialog() }
        binding.spaceLongPressActionItem.setOnClickListener { showSpaceLongPressActionDialog() }
        binding.keyPreviewItem.setOnClickListener { toggleKeyPreview() }
        binding.autoSpacePeriodItem.setOnClickListener { toggleAutoSpacePeriod() }
        binding.autoCapitalizeEnglishItem.setOnClickListener { toggleAutoCapitalizeEnglish() }
        binding.enterLongPressActionItem.setOnClickListener { showEnterLongPressActionDialog() }
        binding.quickPhraseKieukItem.setOnClickListener { showQuickPhraseEditDialog(QuickPhraseKey.KIEUK) }
        binding.quickPhraseTieutItem.setOnClickListener { showQuickPhraseEditDialog(QuickPhraseKey.TIEUT) }
        binding.quickPhraseChieutItem.setOnClickListener { showQuickPhraseEditDialog(QuickPhraseKey.CHIEUT) }
        binding.quickPhrasePieupItem.setOnClickListener { showQuickPhraseEditDialog(QuickPhraseKey.PIEUP) }
        binding.quickPhraseSsangbieupItem.setOnClickListener { showQuickPhraseEditDialog(QuickPhraseKey.SSANGBIEUP) }
        binding.quickPhraseSsangjieutItem.setOnClickListener { showQuickPhraseEditDialog(QuickPhraseKey.SSANGJIEUT) }
        binding.quickPhraseSsangdigeutItem.setOnClickListener { showQuickPhraseEditDialog(QuickPhraseKey.SSANGDIGEUT) }
        binding.quickPhraseSsanggiyeokItem.setOnClickListener { showQuickPhraseEditDialog(QuickPhraseKey.SSANGGIYEOK) }
        binding.quickPhraseSsangsiotItem.setOnClickListener { showQuickPhraseEditDialog(QuickPhraseKey.SSANGSIOT) }
        binding.qwertyLongKeyZItem.setOnClickListener { showQwertyLongKeyEditDialog(QwertyLongKey.Z) }
        binding.qwertyLongKeyXItem.setOnClickListener { showQwertyLongKeyEditDialog(QwertyLongKey.X) }
        binding.qwertyLongKeyCItem.setOnClickListener { showQwertyLongKeyEditDialog(QwertyLongKey.C) }
        binding.qwertyLongKeyVItem.setOnClickListener { showQwertyLongKeyEditDialog(QwertyLongKey.V) }
        binding.qwertyLongKeyBItem.setOnClickListener { showQwertyLongKeyEditDialog(QwertyLongKey.B) }
        binding.qwertyLongKeyNItem.setOnClickListener { showQwertyLongKeyEditDialog(QwertyLongKey.N) }
        binding.qwertyLongKeyMItem.setOnClickListener { showQwertyLongKeyEditDialog(QwertyLongKey.M) }
        binding.settingsDataExportItem.setOnClickListener { exportSettings() }
        binding.settingsDataImportItem.setOnClickListener { importSettings() }
        binding.settingsDataResetItem.setOnClickListener { showResetConfirmDialog() }
    }

    private fun updateQuickPhraseDisplays() {
        binding.quickPhraseKieukValue.text = QuickPhraseRepository.getPhrase(this, QuickPhraseKey.KIEUK)
        binding.quickPhraseTieutValue.text = QuickPhraseRepository.getPhrase(this, QuickPhraseKey.TIEUT)
        binding.quickPhraseChieutValue.text = QuickPhraseRepository.getPhrase(this, QuickPhraseKey.CHIEUT)
        binding.quickPhrasePieupValue.text = QuickPhraseRepository.getPhrase(this, QuickPhraseKey.PIEUP)
        binding.quickPhraseSsangbieupValue.text = QuickPhraseRepository.getPhrase(this, QuickPhraseKey.SSANGBIEUP)
        binding.quickPhraseSsangjieutValue.text = QuickPhraseRepository.getPhrase(this, QuickPhraseKey.SSANGJIEUT)
        binding.quickPhraseSsangdigeutValue.text = QuickPhraseRepository.getPhrase(this, QuickPhraseKey.SSANGDIGEUT)
        binding.quickPhraseSsanggiyeokValue.text = QuickPhraseRepository.getPhrase(this, QuickPhraseKey.SSANGGIYEOK)
        binding.quickPhraseSsangsiotValue.text = QuickPhraseRepository.getPhrase(this, QuickPhraseKey.SSANGSIOT)
    }

    private fun showQuickPhraseEditDialog(key: QuickPhraseKey) {
        val editText = EditText(this).apply {
            setText(QuickPhraseRepository.getPhrase(this@SettingsActivity, key))
            hint = getString(R.string.settings_quick_phrase_edit_hint)
            setSingleLine(true)
        }
        AlertDialog.Builder(this)
            .setTitle("${key.jaum} ${getString(R.string.settings_quick_phrase_edit_hint)}")
            .setView(editText)
            .setPositiveButton(R.string.settings_qwerty_long_key_save) { _, _ ->
                val input = editText.text.toString().trim()
                if (input.isNotEmpty()) {
                    QuickPhraseRepository.setPhrase(this, key, input)
                } else {
                    QuickPhraseRepository.setPhrase(this, key, key.defaultPhrase)
                }
                updateQuickPhraseDisplays()
            }
            .setNeutralButton(R.string.settings_quick_phrase_reset) { _, _ ->
                QuickPhraseRepository.setPhrase(this, key, key.defaultPhrase)
                updateQuickPhraseDisplays()
            }
            .setNegativeButton(R.string.settings_qwerty_long_key_cancel, null)
            .show()
    }

    private fun updateQwertyLongKeyDisplays() {
        binding.qwertyLongKeyZValue.text = QwertyLongKeyRepository.getPhrase(this, QwertyLongKey.Z)
        binding.qwertyLongKeyXValue.text = QwertyLongKeyRepository.getPhrase(this, QwertyLongKey.X)
        binding.qwertyLongKeyCValue.text = QwertyLongKeyRepository.getPhrase(this, QwertyLongKey.C)
        binding.qwertyLongKeyVValue.text = QwertyLongKeyRepository.getPhrase(this, QwertyLongKey.V)
        binding.qwertyLongKeyBValue.text = QwertyLongKeyRepository.getPhrase(this, QwertyLongKey.B)
        binding.qwertyLongKeyNValue.text = QwertyLongKeyRepository.getPhrase(this, QwertyLongKey.N)
        binding.qwertyLongKeyMValue.text = QwertyLongKeyRepository.getPhrase(this, QwertyLongKey.M)
    }

    private fun showQwertyLongKeyEditDialog(key: QwertyLongKey) {
        val editText = EditText(this).apply {
            setText(QwertyLongKeyRepository.getPhrase(this@SettingsActivity, key))
            hint = getString(R.string.settings_qwerty_long_key_edit_hint)
            setSingleLine(true)
        }
        AlertDialog.Builder(this)
            .setTitle("${key.letter} ${getString(R.string.settings_qwerty_long_key_edit_hint)}")
            .setView(editText)
            .setPositiveButton(R.string.settings_qwerty_long_key_save) { _, _ ->
                val input = editText.text.toString().trim()
                if (input.isNotEmpty()) {
                    QwertyLongKeyRepository.setPhrase(this, key, input)
                } else {
                    QwertyLongKeyRepository.setPhrase(this, key, key.defaultPhrase)
                }
                updateQwertyLongKeyDisplays()
            }
            .setNeutralButton(R.string.settings_qwerty_long_key_reset) { _, _ ->
                QwertyLongKeyRepository.setPhrase(this, key, key.defaultPhrase)
                updateQwertyLongKeyDisplays()
            }
            .setNegativeButton(R.string.settings_qwerty_long_key_cancel, null)
            .show()
    }

    private fun updateKeyboardSkinDisplay() {
        binding.keyboardSkinValue.text =
            getString(SettingsPreferences.getKeyboardSkin(this).labelResId)
    }

    private fun showKeyboardSkinDialog() {
        val options = KeyboardSkin.values()
        val labels = options.map { getString(it.labelResId) }.toTypedArray()
        val currentIndex = options.indexOf(SettingsPreferences.getKeyboardSkin(this))

        AlertDialog.Builder(this)
            .setTitle(R.string.settings_keyboard_skin_title)
            .setSingleChoiceItems(labels, currentIndex) { dialog, which ->
                SettingsPreferences.save(this, SettingsPreferences.KEY_KEYBOARD_SKIN, options[which].name)
                updateKeyboardSkinDisplay()
                dialog.dismiss()
            }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
    }

    private fun updateInputModeDisplay() {
        binding.hangulInputModeValue.text =
            getString(SettingsPreferences.getHangulInputMode(this).labelResId)
    }

    private fun updateKeypadHeightDisplay() {
        binding.keypadHeightValue.text =
            getString(SettingsPreferences.getKeypadHeight(this).labelResId)
    }

    private fun updateOneHandModeDisplay() {
        binding.oneHandModeValue.text =
            getString(SettingsPreferences.getOneHandMode(this).labelResId)
    }

    private fun updateLongPressTimeDisplay() {
        binding.longPressTimeValue.text =
            getString(SettingsPreferences.getLongPressTime(this).labelResId)
    }

    private fun showLongPressTimeDialog() {
        val options = LongPressTime.values()
        val labels = options.map { getString(it.labelResId) }.toTypedArray()
        val currentIndex = options.indexOf(SettingsPreferences.getLongPressTime(this))

        AlertDialog.Builder(this)
            .setTitle(R.string.settings_long_press_time_title)
            .setSingleChoiceItems(labels, currentIndex) { dialog, which ->
                SettingsPreferences.save(this, SettingsPreferences.KEY_LONG_PRESS_TIME, options[which].name)
                updateLongPressTimeDisplay()
                dialog.dismiss()
            }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
    }

    private fun updateKeyPreviewDisplay() {
        binding.keyPreviewSwitch.isChecked = SettingsPreferences.getKeyPreviewEnabled(this)
    }

    private fun toggleKeyPreview() {
        val newValue = !SettingsPreferences.getKeyPreviewEnabled(this)
        SettingsPreferences.setKeyPreviewEnabled(this, newValue)
        binding.keyPreviewSwitch.isChecked = newValue
    }

    private fun updateSpaceLongPressActionDisplay() {
        binding.spaceLongPressActionValue.text =
            getString(SettingsPreferences.getSpaceLongPressAction(this).labelResId)
    }

    private fun showSpaceLongPressActionDialog() {
        val options = SpaceLongPressAction.values()
        val labels = options.map { getString(it.labelResId) }.toTypedArray()
        val currentIndex = options.indexOf(SettingsPreferences.getSpaceLongPressAction(this))

        AlertDialog.Builder(this)
            .setTitle(R.string.settings_space_long_press_title)
            .setSingleChoiceItems(labels, currentIndex) { dialog, which ->
                SettingsPreferences.save(this, SettingsPreferences.KEY_SPACE_LONG_PRESS_ACTION, options[which].name)
                updateSpaceLongPressActionDisplay()
                dialog.dismiss()
            }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
    }

    private fun updateAutoSpacePeriodDisplay() {
        binding.autoSpacePeriodSwitch.isChecked = SettingsPreferences.getAutoSpacePeriod(this)
    }

    private fun toggleAutoSpacePeriod() {
        val newValue = !SettingsPreferences.getAutoSpacePeriod(this)
        SettingsPreferences.setAutoSpacePeriod(this, newValue)
        binding.autoSpacePeriodSwitch.isChecked = newValue
    }

    private fun updateAutoCapitalizeEnglishDisplay() {
        binding.autoCapitalizeEnglishSwitch.isChecked = SettingsPreferences.getAutoCapitalizeEnglish(this)
    }

    private fun toggleAutoCapitalizeEnglish() {
        val newValue = !SettingsPreferences.getAutoCapitalizeEnglish(this)
        SettingsPreferences.setAutoCapitalizeEnglish(this, newValue)
        binding.autoCapitalizeEnglishSwitch.isChecked = newValue
    }

    private fun updateEnterLongPressActionDisplay() {
        binding.enterLongPressActionValue.text =
            getString(SettingsPreferences.getEnterLongPressAction(this).labelResId)
    }

    private fun showEnterLongPressActionDialog() {
        val options = EnterLongPressAction.values()
        val labels = options.map { getString(it.labelResId) }.toTypedArray()
        val currentIndex = options.indexOf(SettingsPreferences.getEnterLongPressAction(this))

        AlertDialog.Builder(this)
            .setTitle(R.string.settings_enter_long_press_title)
            .setSingleChoiceItems(labels, currentIndex) { dialog, which ->
                SettingsPreferences.save(this, SettingsPreferences.KEY_ENTER_LONG_PRESS_ACTION, options[which].name)
                updateEnterLongPressActionDisplay()
                dialog.dismiss()
            }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
    }

    private fun showInputModeDialog() {
        val modes = HangulInputMode.values()
        val labels = modes.map { getString(it.labelResId) }.toTypedArray()
        val currentIndex = modes.indexOf(SettingsPreferences.getHangulInputMode(this))

        AlertDialog.Builder(this)
            .setTitle(R.string.settings_hangul_input_mode_title)
            .setSingleChoiceItems(labels, currentIndex) { dialog, which ->
                SettingsPreferences.save(this, SettingsPreferences.KEY_HANGUL_INPUT_MODE, modes[which].name)
                updateInputModeDisplay()
                dialog.dismiss()
            }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
    }

    private fun showKeypadHeightDialog() {
        val options = KeypadHeight.values()
        val labels = options.map { getString(it.labelResId) }.toTypedArray()
        val currentIndex = options.indexOf(SettingsPreferences.getKeypadHeight(this))

        AlertDialog.Builder(this)
            .setTitle(R.string.settings_keypad_height_title)
            .setSingleChoiceItems(labels, currentIndex) { dialog, which ->
                SettingsPreferences.save(this, SettingsPreferences.KEY_KEYPAD_HEIGHT, options[which].name)
                updateKeypadHeightDisplay()
                dialog.dismiss()
            }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
    }

    private fun showOneHandModeDialog() {
        val options = OneHandMode.values()
        val labels = options.map { getString(it.labelResId) }.toTypedArray()
        val currentIndex = options.indexOf(SettingsPreferences.getOneHandMode(this))

        AlertDialog.Builder(this)
            .setTitle(R.string.settings_one_hand_mode_title)
            .setSingleChoiceItems(labels, currentIndex) { dialog, which ->
                SettingsPreferences.save(this, SettingsPreferences.KEY_ONE_HAND_MODE, options[which].name)
                updateOneHandModeDisplay()
                dialog.dismiss()
            }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
    }

    private fun getSettingsFile(): File {
        val dir = getExternalFilesDir(null) ?: filesDir
        return File(dir, "openmoa_settings.json")
    }

    private fun exportSettings() {
        try {
            val prefs = getSharedPreferences(SettingsPreferences.PREFS_NAME, MODE_PRIVATE)
            val json = JSONObject()
            // prefs 파일 하나에 SettingsPreferences, QuickPhraseRepository, QwertyLongKeyRepository 가 함께 저장됨
            prefs.all.forEach { (key, value) ->
                when (value) {
                    is Boolean -> json.put(key, value)
                    is String -> json.put(key, value)
                    is Int -> json.put(key, value)
                    is Long -> json.put(key, value)
                    is Float -> json.put(key, value.toDouble())
                    // Set<String> 등 미지원 타입은 내보내지 않음
                }
            }
            getSettingsFile().writeText(json.toString(2))
            Toast.makeText(this, R.string.settings_data_export_success, Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, R.string.settings_data_export_fail, Toast.LENGTH_SHORT).show()
        }
    }

    private fun importSettings() {
        try {
            val file = getSettingsFile()
            if (!file.exists()) {
                Toast.makeText(this, R.string.settings_data_import_fail, Toast.LENGTH_SHORT).show()
                return
            }
            val json = JSONObject(file.readText())
            val allowedKeys = buildSet {
                addAll(SettingsPreferences.ALL_KEYS)
                QuickPhraseKey.values().forEach { add(it.prefKey) }
                QwertyLongKey.values().forEach { add(it.prefKey) }
            }
            val booleanKeys = setOf(
                SettingsPreferences.KEY_KEY_PREVIEW,
                SettingsPreferences.KEY_AUTO_SPACE_PERIOD,
                SettingsPreferences.KEY_AUTO_CAPITALIZE_ENGLISH,
            )
            val editor = getSharedPreferences(SettingsPreferences.PREFS_NAME, MODE_PRIVATE).edit()
            json.keys().forEach { key ->
                if (key !in allowedKeys) return@forEach
                if (key in booleanKeys) {
                    editor.putBoolean(key, json.optBoolean(key))
                } else if (json.has(key)) {
                    val value = json.optString(key)
                    if (value.isNotEmpty()) editor.putString(key, value)
                }
            }
            editor.apply()
            refreshAllDisplays()
            Toast.makeText(this, R.string.settings_data_import_success, Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, R.string.settings_data_import_fail, Toast.LENGTH_SHORT).show()
        }
    }

    private fun showResetConfirmDialog() {
        AlertDialog.Builder(this)
            .setMessage(R.string.settings_data_reset_confirm)
            .setPositiveButton(android.R.string.ok) { _, _ -> resetAllSettings() }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
    }

    private fun resetAllSettings() {
        getSharedPreferences(SettingsPreferences.PREFS_NAME, MODE_PRIVATE).edit().clear().apply()
        refreshAllDisplays()
        Toast.makeText(this, R.string.settings_data_reset_success, Toast.LENGTH_SHORT).show()
    }

    private fun refreshAllDisplays() {
        updateInputModeDisplay()
        updateKeyboardSkinDisplay()
        updateKeypadHeightDisplay()
        updateOneHandModeDisplay()
        updateLongPressTimeDisplay()
        updateSpaceLongPressActionDisplay()
        updateKeyPreviewDisplay()
        updateAutoSpacePeriodDisplay()
        updateAutoCapitalizeEnglishDisplay()
        updateEnterLongPressActionDisplay()
        updateQuickPhraseDisplays()
        updateQwertyLongKeyDisplays()
    }
}
