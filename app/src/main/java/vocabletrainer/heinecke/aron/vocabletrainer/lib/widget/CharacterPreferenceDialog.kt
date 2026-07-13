package vocabletrainer.heinecke.aron.vocabletrainer.lib.widget

import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.appcompat.app.AlertDialog
import androidx.preference.EditTextPreference
import androidx.preference.Preference
import androidx.preference.PreferenceDialogFragmentCompat
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import vocabletrainer.heinecke.aron.vocabletrainer.R
import vocabletrainer.heinecke.aron.vocabletrainer.eximport.GenericSpinnerEntry

/**
 * Character Preference Dialog
 */
class CharacterPreferenceDialog : PreferenceDialogFragmentCompat() {
    var charInput: TextInputEditText? = null
    var charInputLayout: TextInputLayout? = null
    var spPreset: Spinner? = null
    var adapter: ArrayAdapter<GenericSpinnerEntry<Char?>?>? = null
    private var listener: CustomItemSelectedListener? = null
    private var okButton: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CustomDialog)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        okButton = dialog.findViewById(AlertDialog.BUTTON_POSITIVE)
        return dialog
    }

    override fun onBindDialogView(view: View) {
        super.onBindDialogView(view)
        view.context.theme.applyStyle(R.style.CustomDialog, true)
        PLACEHOLDER = getString(R.string.Placeholder)
        val preference = getPreference()

        charInput = view.findViewById(R.id.tCharInput)
        charInputLayout = view.findViewById(R.id.tCharInputLayout)
        charInputLayout!!.isCounterEnabled = true
        charInputLayout!!.counterMaxLength = MAX_LENGTH
        val hint = requireArguments().getString(ARG_TITLE, PLACEHOLDER)
        charInputLayout!!.hint = hint
        charInput!!.setSingleLine()
        charInput!!.hint = hint
        charInput!!.setText((preference as EditTextPreference).text)
        spPreset = view.findViewById(R.id.spPreset)

        adapter = ArrayAdapter<GenericSpinnerEntry<Char?>?>(
            requireContext(),
            android.R.layout.simple_spinner_item
        )
        adapter!!.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        adapter!!.add(GenericSpinnerEntry(null, getString(R.string.Character_Custom)))
        adapter!!.add(GenericSpinnerEntry('\t', getString(R.string.Character_Tab)))
        adapter!!.add(GenericSpinnerEntry('\r', getString(R.string.Character_Line_Feed)))
        adapter!!.add(GenericSpinnerEntry(',', getString(R.string.Character_Comma)))
        adapter!!.add(GenericSpinnerEntry(';', getString(R.string.Character_Semicolon)))
        adapter!!.add(GenericSpinnerEntry('"', getString(R.string.Character_Quotations_Mark)))
        adapter!!.add(GenericSpinnerEntry('\\', getString(R.string.Character_Backslash)))
        spPreset!!.adapter = adapter

        listener = object : CustomItemSelectedListener() {
            override fun itemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selected = adapter!!.getItem(position)!!.getObject()
                if (selected != null) {
                    charInput!!.setText(selected.toString())
                }
            }
        }

        spPreset!!.onItemSelectedListener = listener

        charInput!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable) {
                updateSpinner()
                val length = s.toString().toCharArray().size
                if (length == MAX_LENGTH) {
                    charInputLayout!!.error = null
                } else {
                    charInputLayout!!.error = getString(R.string.Character_Incorrect_Amount)
                }
            }
        })
    }

    private fun updateSpinner() {
        when (val input = charInput!!.getText().toString()) {
            "\t" -> spPreset!!.setSelection(1)
            "\r" -> spPreset!!.setSelection(2)
            "," -> spPreset!!.setSelection(3)
            ";" -> spPreset!!.setSelection(4)
            "\"" -> spPreset!!.setSelection(5)
            "\\" -> spPreset!!.setSelection(6)
            else -> {
                spPreset!!.setSelection(0)
                if (input.length == MAX_LENGTH) {
                    adapter!!.getItem(0)!!.updateObject(input[0])
                }
            }
        }
    }

    override fun onDialogClosed(positiveResult: Boolean) {
        if (positiveResult && charInputLayout!!.error == null) {
            val preference = getPreference()
            if (preference is EditTextPreference) {
                val textPreference =
                    preference
                // This allows the client to ignore the user value.
                if (textPreference.callChangeListener(charInput!!.getText().toString())) {
                    textPreference.setText(charInput!!.getText().toString())
                }
            }
        }
    }

    companion object {
        private const val MAX_LENGTH = 1
        private const val ARG_TITLE = "title"
        private var PLACEHOLDER: String? = null

        @JvmStatic
        fun newInstance(preference: Preference): CharacterPreferenceDialog {
            val fragment = CharacterPreferenceDialog()
            val b = Bundle(1)
            b.putString(ARG_KEY, preference.key)
            b.putString(ARG_TITLE, preference.title.toString())
            fragment.setArguments(b)
            return fragment
        }
    }
}
