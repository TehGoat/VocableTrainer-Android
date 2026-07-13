package vocabletrainer.heinecke.aron.vocabletrainer.editor

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import android.widget.TextView.OnEditorActionListener
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import vocabletrainer.heinecke.aron.vocabletrainer.R
import vocabletrainer.heinecke.aron.vocabletrainer.lib.storage.VList
import java.util.concurrent.Callable

/**
 * Dialog for list metadata editing
 */
class VListEditorDialog : DialogFragment() {
    private var okAction: Callable<Void?>? = null
    private var cancelAction: Callable<Void?>? = null
    private var newList = false
    private var list: VList? = null
    private var iName: EditText? = null
    private var iColA: EditText? = null
    private var iColB: EditText? = null

    /**
     * Set ok action to run afterwards
     * @param okAction
     */
    fun setOkAction(okAction: Callable<Void?>?) {
        this.okAction = okAction
    }

    /**
     * Set cancel action to run afterwards
     * @param cancelAction
     */
    fun setCancelAction(cancelAction: Callable<Void?>?) {
        this.cancelAction = cancelAction
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // we have to override this one, because we're inflating + using the builder, apparently
        setStyle(STYLE_NORMAL, R.style.CustomDialog)
        Log.d(TAG, "onCreate")
        if (savedInstanceState != null) {
            newList = requireArguments().getBoolean(PARAM_NEW)
        } else if (arguments != null) {
            newList = requireArguments().getBoolean(PARAM_NEW)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Log.d(TAG, "saving..")
        outState.putBoolean(PARAM_NEW, newList)
        outState.putString(KEY_COL_A, iColA!!.text.toString())
        outState.putString(KEY_COL_B, iColB!!.text.toString())
        outState.putString(KEY_Name, iName!!.text.toString())
    }

    private val listProvider: ListEditorDataProvider?
        /**
         * Get ListEditorDataProvider<br></br>
         * Allows provider to be a targetFragment, parentFragment or the activity
         * @return
         */
        get() {
            return if (targetFragment is ListEditorDataProvider) {
                targetFragment as ListEditorDataProvider?
            } else if (parentFragment is ListEditorDataProvider) {
                parentFragment as ListEditorDataProvider
            } else if (activity is ListEditorDataProvider) {
                activity as ListEditorDataProvider?
            } else {
                throw IllegalStateException("No VList provider found!")
            }
        }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        list = this.listProvider!!.getList()
        val alertDialog = AlertDialog.Builder(requireActivity(), R.style.CustomDialog)
        val view = View.inflate(activity, R.layout.dialog_list, null)
        // and we have to use this one, together with onCreate, because of the inflation here
        // to override dialog buttons + inflated view
        view.context.theme.applyStyle(R.style.CustomDialog, true)
        alertDialog.setTitle(if (newList) R.string.Editor_Diag_table_Title_New else R.string.Editor_Diag_table_Title_Edit)
        alertDialog.setView(view)
        iName = view.findViewById(R.id.tListName)
        iColA = view.findViewById(R.id.tListColumnA)
        iColB = view.findViewById(R.id.tListColumnB)

        iName!!.setText(list!!.name)
        iName!!.setSingleLine()
        iColA!!.setText(list!!.nameA)
        iColA!!.setSingleLine()
        iColB!!.setSingleLine()
        iColB!!.setText(list!!.nameB)
        iColB!!.setOnEditorActionListener(OnEditorActionListener { _: TextView?, actionID: Int, _: KeyEvent? ->
            if (actionID == EditorInfo.IME_ACTION_DONE) {
                okAction()
                dismiss()
                return@OnEditorActionListener true
            }
            false
        })
        if (newList) {
            iName!!.setSelectAllOnFocus(true)
            iColA!!.setSelectAllOnFocus(true)
            iColB!!.setSelectAllOnFocus(true)
        }

        if (savedInstanceState != null) {
            iName!!.setText(savedInstanceState.getString(KEY_Name))
            iColA!!.setText(savedInstanceState.getString(KEY_COL_A))
            iColB!!.setText(savedInstanceState.getString(KEY_COL_B))
        }

        alertDialog.setPositiveButton(
            R.string.GEN_OK
        ) { _: DialogInterface?, _: Int ->
            okAction()
        }
        alertDialog.setNegativeButton(
            R.string.Editor_Diag_table_btn_Canel
        ) { _: DialogInterface?, _: Int -> callCancelAction() }

        return alertDialog.create()
    }

    /**
     * Action to perform on ok, doesn't close the dialog by itself
     */
    private fun okAction() {
        if (iColA!!.text.isEmpty() || iColB!!.length() == 0 || iName!!.text.isEmpty()) {
            Log.d(TAG, "empty insert")
        }

        list!!.nameA = iColA!!.text.toString()
        list!!.nameB = iColB!!.text.toString()
        list!!.name = iName!!.text.toString()
        if (okAction != null) {
            try {
                okAction!!.call()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
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
                cancelAction!!.call()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Interface for dialog caller
     */
    interface ListEditorDataProvider {
        fun getList(): VList
    }

    companion object {
        const val TAG: String = "VListEditorDialog"
        private const val PARAM_NEW = "is_new"
        private const val KEY_COL_A = "colA"
        private const val KEY_COL_B = "colB"
        private const val KEY_Name = "name"

        /**
         * Creates a new instance<br></br>
         * see [.getListProvider] for VList provider requirements
         * @param isNew true if a new list is created
         * @return VListEditorDialog
         */
        @JvmStatic
        fun newInstance(isNew: Boolean): VListEditorDialog {
            val dialog = VListEditorDialog()

            val bundle = Bundle()
            bundle.putBoolean(PARAM_NEW, isNew)
            dialog.setArguments(bundle)

            return dialog
        }
    }
}
