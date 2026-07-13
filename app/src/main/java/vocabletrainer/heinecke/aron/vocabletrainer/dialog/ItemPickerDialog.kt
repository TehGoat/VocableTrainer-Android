package vocabletrainer.heinecke.aron.vocabletrainer.dialog

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.SparseArray
import androidx.annotation.ArrayRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import vocabletrainer.heinecke.aron.vocabletrainer.R

/**
 * Item picker dialog for Array Resources<br></br>
 * Allows override of specific values for dynamic entries
 */
class ItemPickerDialog : DialogFragment() {
    private var handler: ItemPickerHandler? = null
    private var overrides: SparseArray<String?>? = null

    /**
     * Interface for handlers of ItemPickerDialog on select
     */
    interface ItemPickerHandler {
        fun onItemPickerSelected(position: Int)
    }

    /**
     * Set handler for selection
     * @param handler
     */
    fun setItemPickerHandler(handler: ItemPickerHandler) {
        this.handler = handler
    }

    /**
     * Allows to override certain values<br></br>
     * Has to be called ****before showing the dialog!
     * @param id
     * @param value
     */
    fun overrideEntry(id: Int, value: String?) {
        if (overrides == null) overrides = SparseArray<String?>()
        overrides!!.put(id, value)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val itemArray = requireArguments().getInt(P_ITEMS)

        val dialog = AlertDialog.Builder(requireContext(), R.style.CustomDialog)
        dialog.setTitle(requireArguments().getInt(P_TITLE))
        val values = resources.getStringArray(itemArray)
        if (overrides != null) {
            for (i in values.indices) {
                val override = overrides!!.get(i)
                if (override != null) values[i] = override
            }
        }
        dialog.setItems(
            values
        ) { dialog1: DialogInterface?, which: Int ->
            dialog1!!.dismiss()
            handler!!.onItemPickerSelected(which)
        }
        return dialog.create()
    }

    companion object {
        private const val P_TITLE = "title"
        private const val P_ITEMS = "items"

        /**
         * Creates a new instance
         */
        @JvmStatic
        fun newInstance(@ArrayRes itemArray: Int, @StringRes title: Int): ItemPickerDialog {
            val dialog = ItemPickerDialog()
            val args = Bundle()
            args.putInt(P_ITEMS, itemArray)
            args.putInt(P_TITLE, title)
            dialog.setArguments(args)
            return dialog
        }
    }
}
