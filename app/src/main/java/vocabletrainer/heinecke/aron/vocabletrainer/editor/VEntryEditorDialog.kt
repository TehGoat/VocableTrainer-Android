package vocabletrainer.heinecke.aron.vocabletrainer.editor

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import vocabletrainer.heinecke.aron.vocabletrainer.R
import vocabletrainer.heinecke.aron.vocabletrainer.lib.storage.VEntry
import androidx.core.view.size

/**
 * Dialog for VEntry editing
 */
class VEntryEditorDialog : DialogFragment() {
    private var focusableEditText: EditText? = null
    private var okAction: ((VEntry?) -> Unit?)? = null
    private var cancelAction: ((VEntry?) -> Unit?)? = null
    private var entry: VEntry? = null

    private var meaningsA: LinearLayout? = null
    private var meaningsB: LinearLayout? = null
    private var tHint: TextInputEditText? = null
    private var tAddition: TextInputEditText? = null
    private var tagCounter = 0

    /**
     * Set ok action to run afterwards
     * @param okAction
     */
    fun setOkAction(okAction: ((VEntry?) -> Unit?)?) {
        this.okAction = okAction
    }

    /**
     * Set cancel action to run afterwards
     * @param cancelAction
     */
    fun setCancelAction(cancelAction: ((VEntry?) -> Unit?)?) {
        this.cancelAction = cancelAction
    }

    override fun onResume() {
        super.onResume()
        if (focusableEditText != null) {
            focusableEditText!!.clearFocus()
            focusableEditText!!.requestFocus()
            val inputMethodManager =
                requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.toggleSoftInput(
                InputMethodManager.SHOW_FORCED,
                InputMethodManager.HIDE_IMPLICIT_ONLY
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // we have to override this one, because we're inflating + using the builder, apparently
        setStyle(STYLE_NORMAL, R.style.CustomDialog)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        var meaning = getMeanings(meaningsA!!)
        outState.putStringArrayList(KEY_INPUT_A, meaning)
        outState.putInt(KEY_INPUT_A_COUNT, meaning.size)
        meaning = getMeanings(meaningsB!!)
        outState.putStringArrayList(KEY_INPUT_B, meaning)
        outState.putInt(KEY_INPUT_B_COUNT, meaning.size)
        outState.putString(KEY_INPUT_HINT, tHint!!.getText().toString())
        outState.putString(KEY_INPUT_ADDITION, tAddition!!.getText().toString())
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireActivity(), R.style.CustomDialog)

        val view = View.inflate(activity, R.layout.dialog_entry, null)
        // and we have to use this one, together with onCreate, because of the inflation here
        // to override dialog buttons + inflated view
        view.context.theme.applyStyle(R.style.CustomDialog, true)
        builder.setTitle(R.string.Editor_Diag_edit_Title)
        builder.setView(view)

        meaningsA = view.findViewById(R.id.meaningsA)
        meaningsB = view.findViewById(R.id.meaningsB)
        tHint = view.findViewById(R.id.tHint)
        tAddition = view.findViewById(R.id.tAddition)

        val mLstA: MutableList<String>
        val mLstB: MutableList<String>
        val tip: String?
        val addition: String?
        val provider = activity as EditorDialogDataProvider?
        entry = provider!!.editVEntry
        if (savedInstanceState == null) {
            mLstA = entry!!.aMeanings
            mLstB = entry!!.bMeanings
            tip = entry!!.tip
            addition = entry!!.addition
        } else {
            mLstA = savedInstanceState.getStringArrayList(KEY_INPUT_A) ?: mutableListOf()
            mLstB = savedInstanceState.getStringArrayList(KEY_INPUT_B) ?: mutableListOf()
            tip = savedInstanceState.getString(KEY_INPUT_HINT)
            addition = savedInstanceState.getString(KEY_INPUT_ADDITION)
        }

        generateMeanings(mLstA, entry!!.list!!.nameA, meaningsA!!, true)
        generateMeanings(mLstB, entry!!.list!!.nameB, meaningsB!!, false)
        tHint!!.setSingleLine()
        tHint!!.setText(tip)

        tAddition!!.setSingleLine()
        tAddition!!.setText(addition)
        tAddition!!.setOnEditorActionListener { _: TextView?, actionID: Int, _: KeyEvent? ->
            if (actionID == EditorInfo.IME_ACTION_DONE) {
                okAction()
                dismiss()
                return@setOnEditorActionListener true
            }
            false
        }

        builder.setPositiveButton(
            R.string.GEN_OK
        ) { _: DialogInterface?, _: Int ->
            okAction()
        }
        builder.setNegativeButton(
            R.string.Editor_Diag_edit_btn_CANCEL
        ) { _: DialogInterface?, _: Int -> callCancelAction() }

        return builder.create()
    }

    /**
     * Action to perform on done, doesn't close dialog by itself
     */
    private fun okAction() {
        val mA: MutableList<String> = getMeanings(meaningsA!!)
        val mB: MutableList<String> = getMeanings(meaningsB!!)

        if (mA.isEmpty() || mB.isEmpty()) {
            Log.d(TAG, "empty insert")
        }

        entry!!.aMeanings = mA
        entry!!.bMeanings = mB
        entry!!.tip = tHint!!.getText().toString()
        entry!!.addition = tAddition!!.getText().toString()
        if (okAction != null) {
            try {
                okAction!!(entry)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Retrieves meanings from layout input
     * @param layout Layout to traverse
     * @return List of meanings found in layout
     */
    private fun getMeanings(layout: LinearLayout): ArrayList<String> {
        val lst = ArrayList<String>(layout.size)

        for (i in 0..<layout.size) {
            val child = layout.getChildAt(i)
            val text = child.findViewById<TextInputEditText>(R.id.meaning)
            if (text != null && text.getText()!!.isNotEmpty()) {
                lst.add(text.getText().toString())
            }
        }

        return lst
    }

    /**
     * Generate view with all meanings for specified list
     * @param meanings List of meanings to process
     * @param hint Hint for input
     * @param layout Layout to add views into
     */
    private fun generateMeanings(
        meanings: MutableList<String>,
        hint: String?,
        layout: LinearLayout,
        allowFocus: Boolean
    ) {
        val descAdd: String = getString(R.string.Editor_Meaning_Btn_Desc_Add)
        val descRemove: String = getString(R.string.Editor_Meaning_Btn_Desc_Remove)
        val addListener: View.OnClickListener = object : View.OnClickListener {
            override fun onClick(v: View?) {
                layout.addView(generateMeaning("", hint, IMG_ADD, descAdd, this, true))

                for (i in 0..<layout.size - 1) {
                    val child = layout.getChildAt(i)
                    val childBtn = child.findViewById<ImageButton>(R.id.btnMeaning)
                    childBtn.setImageResource(IMG_REMOVE)
                    childBtn.setOnClickListener(
                        DeleteAction(
                            child.tag,
                            layout
                        )
                    )
                }
            }
        }

        if (meanings.size > 0) {
            for (i in 0..<meanings.size - 1) {
                layout.addView(
                    generateMeaning(
                        meanings.get(i),
                        hint,
                        IMG_REMOVE,
                        descRemove,
                        DeleteAction(tagCounter, layout),
                        false
                    )
                )
            }
            layout.addView(
                generateMeaning(
                    meanings[meanings.size - 1],
                    hint,
                    IMG_ADD,
                    descAdd,
                    addListener,
                    false
                )
            )
        } else {
            layout.addView(generateMeaning("", hint, IMG_ADD, descAdd, addListener, allowFocus))
        }
    }

    /**
     * [View.OnClickListener] for meaning delete action
     */
    class DeleteAction
    /**
     * Create a new delete action
     * @param tag Tag of view to delete on click
     * @param group parent in which to delete
     */(private val tag: Any?, private val group: ViewGroup) : View.OnClickListener {
        override fun onClick(v: View?) {
            group.removeView(group.findViewWithTag(tag))
        }
    }

    /**
     * Generate view for meaning entry
     * @param meaning
     * @param hint
     * @param image Button image resource ID
     * @param description Button description
     * @param listener button listener
     * @return View
     */
    private fun generateMeaning(
        meaning: String?,
        hint: String?,
        image: Int,
        description: String?,
        listener: View.OnClickListener?,
        focus: Boolean
    ): View {
        val container = View.inflate(activity, R.layout.editor_meaning, null) as RelativeLayout
        container.context.theme.applyStyle(R.style.CustomDialog, true)
        tagCounter++
        val layout = container.findViewById<TextInputLayout>(R.id.wrapper_meaning)
        val text = container.findViewById<TextInputEditText>(R.id.meaning)
        val btn = container.findViewById<ImageButton>(R.id.btnMeaning)
        text.setSingleLine()

        if (focus) {
            Log.d(TAG, "setting focus element")
            focusableEditText = text
            focusableEditText!!.requestFocus()
        }

        layout.hint = hint
        text.setText(meaning)

        btn.setImageResource(image)
        btn.contentDescription = description
        btn.setOnClickListener(listener)

        return container
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        callCancelAction()
    }

    /**
     * Calls cancel action
     */
    private fun callCancelAction() {
        if (cancelAction != null) {
            try {
                cancelAction!!(entry)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Required interface for parent
     */
    interface EditorDialogDataProvider {
        val editVEntry: VEntry
    }

    companion object {
        private val IMG_ADD: Int = R.drawable.ic_add_black_24dp
        private val IMG_REMOVE: Int = R.drawable.ic_remove_black_24dp
        const val TAG: String = "VEntryEditorDialog"
        private const val KEY_INPUT_A = "inputA"
        private const val KEY_INPUT_A_COUNT = "inputACount"
        private const val KEY_INPUT_B = "inputB"
        private const val KEY_INPUT_B_COUNT = "inputBCount"
        private const val KEY_INPUT_HINT = "inputH"
        private const val KEY_INPUT_ADDITION = "inputAd"

        /**
         * Creates a new instance
         * @return VListEditorDialog
         */
        fun newInstance(): VEntryEditorDialog {
            return VEntryEditorDialog()
        }
    }
}
