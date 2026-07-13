package vocabletrainer.heinecke.aron.vocabletrainer.listpicker

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnLongClickListener
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import vocabletrainer.heinecke.aron.vocabletrainer.R
import vocabletrainer.heinecke.aron.vocabletrainer.lib.storage.VList
import java.text.DateFormat
import java.util.Collections

/**
 * Recycler adapter for VList recycler with optional multi selection
 * @author Aron Heinecke
 */
class ListRecyclerAdapter(data: MutableList<VList>, multiSelect: Boolean, context: Context?) :
    ListAdapter<VList, ListRecyclerAdapter.VListViewHolder>(
        DIFF_CALLBACK
    ) {
    /**
     * Item click listener used for recycler
     */
    interface ItemClickListener {
        fun onItemClick(view: View?, position: Int)
    }

    /**
     * Item long click listener used for recycler
     */
    interface ItemLongClickListener {
        fun onItemLongClick(view: View?, postion: Int)
    }

    private var data: MutableList<VList>?
    private val multiselect: Boolean
    private val dateFormat: DateFormat
    private var itemLongClickListener: ItemLongClickListener? = null
    private var itemClickListener: ItemClickListener? = null

    fun setItemLongClickListener(itemLongClickListener: ItemLongClickListener?) {
        this.itemLongClickListener = itemLongClickListener
    }

    fun setItemClickListener(itemClickListener: ItemClickListener?) {
        this.itemClickListener = itemClickListener
    }

    /**
     * New VList recycler adapter
     * @param data initial data
     * @param multiSelect enable checkbox mode
     * @param context context for date formatting etc
     */
    init {
        this.data = data
        this.multiselect = multiSelect
        this.dateFormat = android.text.format.DateFormat.getDateFormat(context)
    }

    /**
     * Set all elements as select
     * @param select
     */
    fun selectAll(select: Boolean) {
        for (entry in data!!) {
            entry.isSelected = select
        }
        notifyDataSetChanged()
    }

    /**
     * Submit data with sorting
     * @param newData
     * @param comparator
     */
    fun submitList(newData: MutableList<VList>, comparator: Comparator<VList?>?) {
        if (comparator != null) Collections.sort<VList?>(newData, comparator)
        this.submitList(newData)
    }

    override fun submitList(newData: MutableList<VList>?) {
        // don't rely on this being checked by super.submitList
        // essential for selection persistence on viewport change (re-trigger of LiveData)
        if (newData === data) {
            return
        }
        this.data = newData
        super.submitList(newData)
    }

    /**
     * Remove entry<br></br>
     * Does not actually delete the item from the Database
     * @param entry
     */
    fun removeEntry(entry: VList?) {
        val pos = data!!.indexOf(entry!!)
        data!!.remove(entry)
        this.notifyItemRemoved(pos)
    }

    /**
     * Restore entry in list
     * @param entry
     * @param position
     */
    fun restoreEntry(entry: VList?, position: Int) {
        data!!.add(position, entry!!)
        notifyItemInserted(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VListViewHolder {
        val view: View = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.list_recycler_item, parent, false)
        return VListViewHolder(view, itemLongClickListener, itemClickListener, multiselect)
    }

    override fun onBindViewHolder(holder: VListViewHolder, position: Int) {
        val entry = data!!.get(position)
        holder.checkBox.setVisibility(if (multiselect) View.VISIBLE else View.GONE)
        holder.checkBox.setChecked(entry.isSelected)
        holder.colB.setText(entry.nameB)
        holder.colA.setText(entry.nameA)
        holder.name.setText(entry.name)
        holder.created.setText(dateFormat.format(entry.created))
        if (multiselect) {
            holder.viewForeground.setOnClickListener(View.OnClickListener { v: View? ->
                entry.isSelected = !entry.isSelected
                this.notifyItemChanged(position)
            })
        }
    }

    /**
     * Update sorting, forcing a redraw
     * @param comparator
     */
    fun updateSorting(comparator: Comparator<VList?>) {
        Collections.sort<VList?>(data, comparator)
        // required, submitList wouldn't trigger when same list is used
        this.notifyDataSetChanged()
    }

    /**
     * Get item at position
     * @param pos
     * @return
     */
    fun getItemAt(pos: Int): VList? {
        return data!!.get(pos)
    }

    override fun getItemCount(): Int {
        return if (data == null) 0 else data!!.size
    }

    /**
     * VList view holder with click & long click capabilities
     */
    class VListViewHolder(
        itemView: View,
        private val itemLongClickListener: ItemLongClickListener?,
        private val itemClickListener: ItemClickListener?,
        multiselect: Boolean,
    ) : RecyclerView.ViewHolder(itemView), View.OnClickListener, OnLongClickListener {
        val checkBox: CheckBox
        val colA: TextView
        val colB: TextView
        val name: TextView
        val created: TextView
        val viewBackground: RelativeLayout?
        val viewForeground: ConstraintLayout

        init {
            checkBox = itemView.findViewById(R.id.chkListEntrySelect)
            colA = itemView.findViewById(R.id.tListEntryColA)
            colB = itemView.findViewById(R.id.tListEntryColB)
            name = itemView.findViewById(R.id.tListEntryName)
            created = itemView.findViewById(R.id.tListEntryCreated)
            viewBackground = itemView.findViewById(R.id.view_background)
            viewForeground = itemView.findViewById(R.id.view_foreground)
            if (!multiselect) {
                viewForeground.setOnClickListener(this)
                viewForeground.setOnLongClickListener(if (itemLongClickListener != null) this else null)
            }
        }

        override fun onClick(v: View?) {
            itemClickListener?.onItemClick(v, getAdapterPosition())
        }

        override fun onLongClick(v: View?): Boolean {
            itemLongClickListener?.onItemLongClick(
                v,
                absoluteAdapterPosition
            )
            return true
        }
    }

    companion object {
        @Suppress("unused")
        private const val TAG = "ListRecyclerAdapter"

        private val DIFF_CALLBACK: DiffUtil.ItemCallback<VList?> =
            object : DiffUtil.ItemCallback<VList?>() {
                override fun areItemsTheSame(oldItem: VList, newItem: VList): Boolean {
                    return oldItem.id == newItem.id
                }

                override fun areContentsTheSame(oldItem: VList, newItem: VList): Boolean {
                    return oldItem.name == newItem.name
                            && oldItem.nameA == newItem.nameA
                            && oldItem.nameB == newItem.nameB
                }
            }
    }
}
