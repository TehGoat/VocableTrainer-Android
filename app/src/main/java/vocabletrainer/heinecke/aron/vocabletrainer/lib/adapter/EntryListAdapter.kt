package vocabletrainer.heinecke.aron.vocabletrainer.lib.adapter

import android.app.Activity
import android.graphics.Typeface
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import vocabletrainer.heinecke.aron.vocabletrainer.R
import vocabletrainer.heinecke.aron.vocabletrainer.lib.Database
import vocabletrainer.heinecke.aron.vocabletrainer.lib.storage.VEntry
import vocabletrainer.heinecke.aron.vocabletrainer.lib.storage.VEntry.Companion.spacer
import vocabletrainer.heinecke.aron.vocabletrainer.lib.storage.VList
import java.util.Collections

/**
 * BaseAdapter for entry list views
 */
class EntryListAdapter(activity: Activity, private var dataItems: MutableList<VEntry>) :
    BaseAdapter() {
    private val inflater: LayoutInflater
    private var header: VEntry = spacer(
        activity.getString(R.string.Editor_Hint_Column_A),
        activity.getString(R.string.Editor_Hint_Column_B),
        activity.getString(R.string.Editor_Hint_Tip),
        Database.ID_RESERVED_SKIP
    )

    /**
     * Creates a new entry list adapter
     */
    init {
        // don't re-add header double on instance restore
        if (dataItems.isEmpty() || !dataItems[0].equalsId(header)) {
            dataItems.add(0, header)
        }

        inflater = activity.layoutInflater
    }

    /**
     * Set table data (Column Names)
     *
     * @param tbl
     */
    fun setTableData(tbl: VList) {
        header.aMeanings[0] = tbl.nameA
        header.bMeanings[0] = tbl.nameB
        this.notifyDataSetChanged()
        Log.d("EntryListAdapter", "setTableData")
    }

    override fun getCount(): Int {
        return dataItems.size
    }

    override fun getItem(position: Int): Any {
        return dataItems[position]
        // -1 required as onItemClicked counts from 1 but the list starts a 0
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var convertView = convertView
        val holder: ViewHolder
        val item = dataItems[position]

        if (convertView == null) {
            holder = ViewHolder()

            convertView = inflater.inflate(R.layout.entry_list_view, parent, false)

            holder.colA = convertView!!.findViewById(R.id.entryFirstText)
            holder.colB = convertView.findViewById(R.id.entrySecondText)
            holder.colTipp = convertView.findViewById(R.id.entryThirdText)
            if (holder.colA!!.typeface == null) {
                holder.originTypeface = Typeface.NORMAL
            } else {
                holder.originTypeface = holder.colA!!.typeface.style
            }

            convertView.tag = holder
            convertView.setTag(R.id.entryFirstText, holder.colA)
            convertView.setTag(R.id.entrySecondText, holder.colB)
            convertView.setTag(R.id.entryThirdText, holder.colTipp)
        } else {
            holder = convertView.tag as ViewHolder
        }

        val bold = item.id == Database.ID_RESERVED_SKIP
        val typeFace: Int = if (bold) {
            Typeface.BOLD
        }  else {
            holder.originTypeface
        }

        holder.colA!!.setTypeface(null, typeFace)
        holder.colB!!.setTypeface(null, typeFace)
        holder.colTipp!!.setTypeface(null, typeFace)

        holder.colA!!.text = item.aString
        holder.colB!!.text = item.bString
        holder.colTipp!!.text = item.tip

        return convertView
    }

    /**
     * Update sorting
     * @param comp Comparator to use for sorting
     */
    fun updateSorting(comp: Comparator<VEntry?>?) {
        Collections.sort(dataItems, comp)
        this.notifyDataSetChanged()
    }

    /**
     * Set entry as deleted
     *
     * @param entry
     */
    fun remove(entry: VEntry?) {
        dataItems.remove(entry)
        notifyDataSetChanged()
    }

    /**
     * Add a new VEntry to the view<br></br>
     * Does not update the view
     *
     * @param entry
     */
    fun addEntryUnrendered(entry: VEntry?) {
        dataItems.add(entry!!)
    }

    /**
     * Add an VEntry to the view at selected position.<br></br>
     * Does update the view rendering
     *
     * @param entry    new VEntry
     * @param position Position at which it should be inserted
     */
    fun addEntryRendered(entry: VEntry?, position: Int) {
        dataItems.add(position, entry!!)
        this.notifyDataSetChanged()
    }

    /**
     * View Holder, storing data for re-use
     */
    private class ViewHolder {
        var colA: TextView? = null
        var colB: TextView? = null
        var colTipp: TextView? = null
        var originTypeface: Int = 0
    }
}
