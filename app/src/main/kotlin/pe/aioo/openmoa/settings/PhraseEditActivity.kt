package pe.aioo.openmoa.settings

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import pe.aioo.openmoa.R
import pe.aioo.openmoa.databinding.ActivityPhraseEditBinding
import pe.aioo.openmoa.quickphrase.PhraseKey
import pe.aioo.openmoa.quickphrase.QuickPhraseKey
import pe.aioo.openmoa.quickphrase.QwertyLongKey
import pe.aioo.openmoa.quickphrase.UserCharKey

class PhraseEditActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_TYPE = "extra_type"
        const val EXTRA_KEY = "extra_key"
        const val TYPE_KOREAN = "KOREAN"
        const val TYPE_ENGLISH = "ENGLISH"
        const val TYPE_USER_CHAR = "USER_CHAR"
    }

    private lateinit var binding: ActivityPhraseEditBinding
    private lateinit var keys: Array<out PhraseKey>
    private lateinit var type: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPhraseEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        type = intent.getStringExtra(EXTRA_TYPE) ?: TYPE_KOREAN
        val initialKeyName = intent.getStringExtra(EXTRA_KEY)

        keys = when (type) {
            TYPE_ENGLISH -> QwertyLongKey.values()
            TYPE_USER_CHAR -> UserCharKey.values()
            else -> QuickPhraseKey.values()
        }

        binding.editTitle.text = when (type) {
            TYPE_USER_CHAR -> getString(R.string.user_char_edit_title)
            else -> getString(R.string.quick_phrase_edit_title)
        }

        setupSpinner(initialKeyName)
        setupButtons()
    }

    private fun setupSpinner(initialKeyName: String?) {
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, keys.map { it.displayName })
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.keySpinner.adapter = adapter

        val initialIndex = keys.indexOfFirst { it.name == initialKeyName }.takeIf { it >= 0 } ?: 0
        binding.keySpinner.setSelection(initialIndex)

        binding.keySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                binding.contentEditText.setText(keys[position].getPhrase(this@PhraseEditActivity))
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun setupButtons() {
        binding.confirmButton.setOnClickListener {
            val key = keys[binding.keySpinner.selectedItemPosition]
            val raw = binding.contentEditText.text.toString().trim()
            val content = if (type == TYPE_USER_CHAR && raw.isNotEmpty()) {
                raw.substring(0, raw.offsetByCodePoints(0, 1))
            } else {
                raw
            }
            key.setPhrase(this, content.ifEmpty { key.defaultPhrase })
            finish()
        }
        binding.cancelButton.setOnClickListener { finish() }
        binding.resetButton.setOnClickListener {
            val key = keys[binding.keySpinner.selectedItemPosition]
            key.setPhrase(this, key.defaultPhrase)
            binding.contentEditText.setText(key.defaultPhrase)
        }
    }
}
