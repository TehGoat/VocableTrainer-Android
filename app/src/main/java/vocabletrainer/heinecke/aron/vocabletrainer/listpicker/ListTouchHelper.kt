package vocabletrainer.heinecke.aron.vocabletrainer.listpicker

import android.graphics.Canvas
import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import vocabletrainer.heinecke.aron.vocabletrainer.listpicker.ListRecyclerAdapter.VListViewHolder

/**
 * VList touch helper for recyclerview
 */
class ListTouchHelper
/**
 * Create new list touch helper with specified swipe listener
 * @param swipeListener
 */(private val swipeListener: SwipeListener) :
    ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
    /**
     * Swipe listener
     */
    interface SwipeListener {
        /**
         * Called on swipe
         * @param viewHolder
         * @param position adapter position
         */
        fun onSwiped(viewHolder: VListViewHolder?, position: Int)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        swipeListener.onSwiped(viewHolder as VListViewHolder?, viewHolder.getAdapterPosition())
    }

    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        if (viewHolder != null) {
            val foregroundView: View = (viewHolder as VListViewHolder).viewForeground

            getDefaultUIUtil().onSelected(foregroundView)
        }
    }



    override fun onChildDrawOver(
        c: Canvas, recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float,
        actionState: Int, isCurrentlyActive: Boolean
    ) {
        val foregroundView: View = (viewHolder as VListViewHolder).viewForeground
        getDefaultUIUtil().onDrawOver(
            c, recyclerView, foregroundView, dX, dY,
            actionState, isCurrentlyActive
        )
    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        val foregroundView: View = (viewHolder as VListViewHolder).viewForeground
        getDefaultUIUtil().clearView(foregroundView)
    }

    override fun onChildDraw(
        c: Canvas, recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float,
        actionState: Int, isCurrentlyActive: Boolean
    ) {
        val foregroundView: View = (viewHolder as VListViewHolder).viewForeground

        getDefaultUIUtil().onDraw(
            c, recyclerView, foregroundView, dX, dY,
            actionState, isCurrentlyActive
        )
    }
}
