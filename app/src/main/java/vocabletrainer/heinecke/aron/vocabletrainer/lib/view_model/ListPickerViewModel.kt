package vocabletrainer.heinecke.aron.vocabletrainer.lib.view_model

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import vocabletrainer.heinecke.aron.vocabletrainer.lib.Database
import vocabletrainer.heinecke.aron.vocabletrainer.lib.storage.VList

/**
 * ViewModel for list picker, selector
 */
class ListPickerViewModel : ViewModel() {
    /**
     * Returns list data handle
     * @return
     */
    val listsHandle: MutableLiveData<MutableList<VList>?>

    /**
     * Returns loading indicator handle
     * @return
     */
    val loadingHandle: MutableLiveData<Boolean?>
    private val cancelLoading: MutableLiveData<Boolean>? = null

    /**
     * Returns whether data is marked as invalidated (updated in DB)
     * @return
     */
    var isDataInvalidated: Boolean
        private set
    private var loaderTask: Job? = null
    /**
     * Get select all
     * Used to remember select all swap state
     * @return
     */
    /**
     * Set select all
     * Used to remember select all swap state
     * @param selectAll
     */
    var isSelectAll: Boolean

    init {
        this.listsHandle = MutableLiveData<MutableList<VList>?>()
        this.loadingHandle = MutableLiveData<Boolean?>()
        this.isDataInvalidated = true
        this.isSelectAll = false
    }

    val selectedLists: ArrayList<VList>
        /**
         * Returns checked lists
         * @return
         */
        get() {
            val result: java.util.ArrayList<VList>
            if (listsHandle.getValue() != null) {
                result = java.util.ArrayList<VList>(listsHandle.getValue()!!.size / 2)
                for (entry in listsHandle.getValue()!!) {
                    if (entry.isSelected) result.add(entry)
                }
            } else {
                result = java.util.ArrayList<VList>(0)
            }
            return result
        }

    /**
     * Set data as invalidated
     */
    fun setDataInvalidated() {
        Log.d(TAG, "invalidating data")
        this.isDataInvalidated = true
    }

    /**
     * Load lists
     * @param context for DB
     */
    fun loadLists(context: Context?) {
        if (loaderTask != null && loaderTask!!.isActive) {
            Log.w(TAG, "prevented parallel loader")
            return
        }
        loadingHandle.value = true
        loaderTask =  viewModelScope.launch(Dispatchers.IO) {
            val db = Database(context)

            val lists = db.getLists(cancelLoading)

            listsHandle.postValue(lists.toMutableList())
            loadingHandle.postValue(false)
        }
        loaderTask!!.start()
    }

    companion object {
        private const val TAG = "ListPickerViewModel"
    }
}
