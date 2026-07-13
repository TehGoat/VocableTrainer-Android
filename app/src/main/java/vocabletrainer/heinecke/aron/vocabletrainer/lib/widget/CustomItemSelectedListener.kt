package vocabletrainer.heinecke.aron.vocabletrainer.lib.widget

import android.view.View
import android.widget.AdapterView

/**
 * ItemSelectedListener which allows for disabling the first call (initial item set)
 */
abstract class CustomItemSelectedListener : AdapterView.OnItemSelectedListener {
    private var firstSelect = true
    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        if (firstSelect) {
            firstSelect = false
        } else {
            itemSelected(parent, view, position, id)
        }
    }

    /**
     * Set firstSelect as undone, disabling the next itemSelect call from triggering itemSelected
     */
    fun disableNextEvent() {
        this.firstSelect = true
    }

    /**
     * Called on item select
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    abstract fun itemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long)

    override fun onNothingSelected(parent: AdapterView<*>?) {}
}
