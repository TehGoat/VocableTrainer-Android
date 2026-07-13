package vocabletrainer.heinecke.aron.vocabletrainer.listpicker

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import vocabletrainer.heinecke.aron.vocabletrainer.R
import vocabletrainer.heinecke.aron.vocabletrainer.activity.MainActivity
import vocabletrainer.heinecke.aron.vocabletrainer.dialog.ItemPickerDialog
import vocabletrainer.heinecke.aron.vocabletrainer.dialog.ItemPickerDialog.Companion.newInstance
import vocabletrainer.heinecke.aron.vocabletrainer.dialog.ItemPickerDialog.ItemPickerHandler
import vocabletrainer.heinecke.aron.vocabletrainer.editor.EditorActivity
import vocabletrainer.heinecke.aron.vocabletrainer.fragment.BaseFragment
import vocabletrainer.heinecke.aron.vocabletrainer.lib.Database
import vocabletrainer.heinecke.aron.vocabletrainer.lib.comparator.GenTableComparator
import vocabletrainer.heinecke.aron.vocabletrainer.lib.storage.VList
import vocabletrainer.heinecke.aron.vocabletrainer.lib.view_model.ListPickerViewModel
import vocabletrainer.heinecke.aron.vocabletrainer.listpicker.ListRecyclerAdapter.ItemClickListener
import vocabletrainer.heinecke.aron.vocabletrainer.listpicker.ListRecyclerAdapter.VListViewHolder
import vocabletrainer.heinecke.aron.vocabletrainer.listpicker.ListTouchHelper.SwipeListener
import androidx.core.content.edit

/**
 * List selector fragment<br></br>
 * This can be used externally in other fragments<br></br>
 * Requires a toolbar
 */
class ListPickerFragment : BaseFragment(), ItemClickListener, SwipeListener, ItemPickerHandler {
    private lateinit var view: View
    private var multiSelect = false
    private val showOkButton = false
    private var recyclerView: RecyclerView? = null
    private var adapter: ListRecyclerAdapter? = null
    private var selectOnly = false
    private var sort_type = 0
    private var compName: GenTableComparator? = null
    private var compA: GenTableComparator? = null
    private var compB: GenTableComparator? = null
    private var cComp: GenTableComparator? = null
    private var listener: FinishListener? = null
    private var listPickerViewModel: ListPickerViewModel? = null
    private var sortingDialog: ItemPickerDialog? = null
    private var bNewList: FloatingActionButton? = null

    override fun onItemClick(view: View?, position: Int) {
        if (!multiSelect) {
            val list = adapter!!.getItemAt(position)
            if (list?.id != Database.ID_RESERVED_SKIP) {
                val result = ArrayList<VList?>(1)
                result.add(list)
                listener!!.selectionUpdate(result)
            }
        }
        Log.d(TAG, "item licked at: " + position)
    }

    override fun onSwiped(viewHolder: VListViewHolder?, position: Int) {
        if (position < adapter!!.getItemCount()) {
            val entry = adapter!!.getItemAt(position)!!
            val snackbar: Snackbar = Snackbar
                .make(recyclerView!!, R.string.List_Deleted_Message, Snackbar.LENGTH_LONG)
                .setAction(R.string.GEN_Undo, View.OnClickListener { view: View? ->
                    adapter!!.restoreEntry(entry, position)
                    recyclerView!!.scrollToPosition(position)
                })
                .addCallback(object : Snackbar.Callback() {
                    override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                        when (event) {
                            DISMISS_EVENT_CONSECUTIVE, DISMISS_EVENT_TIMEOUT, DISMISS_EVENT_MANUAL, DISMISS_EVENT_SWIPE -> {
                                Log.d(TAG, "deleting list")
                                val db = Database(getContext())
                                db.deleteList(entry)
                            }
                        }
                    }
                })
            snackbar.show()
            adapter!!.removeEntry(entry)
        }
    }

    override fun onItemPickerSelected(position: Int) {
        sort_type = position
        updateComp()
        adapter!!.updateSorting(cComp!!)
        sortingDialog = null
    }

    /**
     * Interface for list picker finish
     */
    interface FinishListener {
        /**
         * Called when ok button is pressed or list picker goes invisible
         * @param selected Selected lists<br></br>
         * Contains one element if multiSelect is disabled
         */
        fun selectionUpdate(selected: ArrayList<VList?>)

        /**
         * Called when list picker got canceled.
         */
        fun cancel()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState != null) {
            sortingDialog = aCActivity!!.getSupportFragmentManager()
                .getFragment(savedInstanceState, P_KEY_SORTING_DIALOG) as ItemPickerDialog?
            Log.d(TAG, "sortingDialog: " + sortingDialog)
            if (sortingDialog != null) sortingDialog!!.setItemPickerHandler(this)
        }

        listPickerViewModel = ViewModelProvider(requireActivity())[ListPickerViewModel::class.java]

        listPickerViewModel!!.listsHandle.observe(this, Observer { lists: MutableList<VList>? ->
            if (lists != null) {
                // effectively nothing happens if lists == current
                adapter!!.submitList(lists, cComp)
                Log.d(TAG, "retrieved data size:" + lists.size)
            }
        })

        // check to not override checked items on viewport change (export)
        if (listPickerViewModel!!.isDataInvalidated) {
            loadTables()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = null
        if (context is FinishListener) {
            listener = context as FinishListener
        } else {
            Log.d(TAG, context.toString() + " does not implement FinishListener")
        }
    }

    override fun onResume() {
        super.onResume()
        if (listPickerViewModel!!.isDataInvalidated) {
            // effectively submitList doesn't do anything if list==list
            listPickerViewModel!!.loadLists(getContext())
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == CODE_NEW_LIST) {
            listPickerViewModel!!.loadLists(getContext())
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    @SuppressLint("RestrictedApi")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, "onCreateView " + (savedInstanceState != null))
        view = inflater.inflate(R.layout.fragment_list_selector, container, false)

        var bundle = arguments
        if (savedInstanceState != null) {
            bundle = savedInstanceState
        } else if (bundle == null) {
            bundle = Bundle()
        }
        // default values required for {@link ExImportActivity.ViewPagerAdapter.class} solution, using no arguments
        multiSelect = bundle.getBoolean(K_MULTISELECT, true)
        selectOnly = bundle.getBoolean(K_SELECT_ONLY, false)

        bNewList = view.findViewById(R.id.bListNew)
        bNewList!!.visibility = if (selectOnly) View.GONE else View.VISIBLE
        bNewList!!.setOnClickListener {
            val myIntent = Intent(getActivity(), EditorActivity::class.java)
            myIntent.putExtra(EditorActivity.PARAM_NEW_TABLE, true)
            startActivityForResult(myIntent, CODE_NEW_LIST)
        }

        val ab = aCActivity!!.supportActionBar
        ab?.setDisplayHomeAsUpEnabled(true)

        setHasOptionsMenu(true)

        // lambdas without lambdas
        compName = GenTableComparator(
            arrayOf(
                GenTableComparator.retName,
                GenTableComparator.retA, GenTableComparator.retB
            ),
            Database.ID_RESERVED_SKIP
        )
        compA = GenTableComparator(
            arrayOf(
                GenTableComparator.retA,
                GenTableComparator.retB, GenTableComparator.retName
            ),
            Database.ID_RESERVED_SKIP
        )
        compB = GenTableComparator(
            arrayOf(
                GenTableComparator.retB,
                GenTableComparator.retA, GenTableComparator.retName
            ),
            Database.ID_RESERVED_SKIP
        )

        val settings = requireActivity().getSharedPreferences(MainActivity.PREFS_NAME, 0)
        sort_type = settings.getInt(P_KEY_LA_SORT, 0)
        updateComp()

        initRecyclerView()
        return view
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        val selectAll = menu.findItem(R.id.lMenu_select_all)
        // FIX pre-v21 devices, on rotation called twice, one time without selectAll
        if (selectAll != null) {
            selectAll.isVisible = multiSelect
        }
        super.onPrepareOptionsMenu(menu)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.list, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val itemId = item.itemId
        if (itemId == R.id.lMenu_sort) {
            sortingDialog = newInstance(R.array.sort_lists, R.string.GEN_Sort)
            sortingDialog!!.setItemPickerHandler(this)
            sortingDialog!!.show(aCActivity!!.supportFragmentManager, P_KEY_SORTING_DIALOG)
            return true
        } else if (itemId == R.id.lMenu_select_all) {
            listPickerViewModel!!.isSelectAll = !listPickerViewModel!!.isSelectAll
            adapter!!.selectAll(listPickerViewModel!!.isSelectAll)
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    /**
     * Update sorting type
     */
    private fun updateComp() {
        when (sort_type) {
            1 -> cComp = compA
            2 -> cComp = compB
            0 -> cComp = compName
            else -> {
                cComp = compName
                sort_type = 0
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Log.d(TAG, "onSaveInstanceState")
        outState.putBoolean(K_MULTISELECT, multiSelect)
        outState.putBoolean(K_SELECT_ONLY, selectOnly)
        if (sortingDialog != null && sortingDialog!!.isAdded) {
            aCActivity!!.supportFragmentManager
                .putFragment(outState, P_KEY_SORTING_DIALOG, sortingDialog!!)
        }
    }

    /**
     * Load lists from db
     */
    private fun loadTables() {
        Log.d(TAG, "loading lists")
        listPickerViewModel!!.loadLists(context)
    }

    /**
     * Setup list view
     */
    private fun initRecyclerView() {
        recyclerView = view.findViewById(R.id.listViewRecyclerView)

        val lists = ArrayList<VList>()
        adapter = ListRecyclerAdapter(lists, multiSelect, context)
        adapter!!.setItemClickListener(this)
        bNewList!!.isEnabled = !selectOnly

        val manager = LinearLayoutManager(activity)
        recyclerView!!.setLayoutManager(manager)

        recyclerView!!.addItemDecoration(
            DividerItemDecoration(
                context,
                LinearLayoutManager.VERTICAL
            )
        )
        recyclerView!!.setAdapter(adapter)

        if (!selectOnly) {
            val touchHelper = ListTouchHelper(this)
            ItemTouchHelper(touchHelper).attachToRecyclerView(recyclerView)

            aCActivity?.supportActionBar?.setTitle(R.string.Lists_Title)
        }
    }

    override fun onStop() {
        super.onStop()
        // Save values
        val settings = requireActivity().getSharedPreferences(MainActivity.PREFS_NAME, 0)
        settings.edit {
            putInt(P_KEY_LA_SORT, sort_type)
        }
    }

    companion object {
        const val TAG: String = "ListPickerFragment"
        private const val P_KEY_LA_SORT = "LA_sorting"
        private const val P_KEY_SORTING_DIALOG = "sorting_dialog_list"
        private const val K_PRESELECT = "preselect"
        private const val K_MULTISELECT = "multiSelect"
        const val K_SELECT_ONLY: String = "select_only"
        private const val CODE_NEW_LIST = 1001

        /**
         * Create new ListPickerFragment instance
         * @param multiSelect Multi select enabled
         * @param selectOnly Whether only selection is allowed, no delete / creation
         * @param selected List of pre-selected VList
         * @return ListPickerFragment
         */
        fun newInstance(
            multiSelect: Boolean, selectOnly: Boolean,
            selected: ArrayList<VList?>?
        ): ListPickerFragment {
            val lpf = ListPickerFragment()
            val args = Bundle()
            args.putBoolean(K_SELECT_ONLY, selectOnly)
            args.putBoolean(K_MULTISELECT, multiSelect)
            args.putParcelableArrayList(K_PRESELECT, selected)
            lpf.setArguments(args)
            return lpf
        }
    }
}
