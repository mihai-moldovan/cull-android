package ro.chiralinteriordesign.cull.ui.products

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager

/**
 * Created by Mihai Moldovan on 13/02/2021.
 */
class SpaceItemDecoration(private val spacing: Int) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {

        val lm = parent.layoutManager as? StaggeredGridLayoutManager ?: return
        val lp = view.layoutParams as? StaggeredGridLayoutManager.LayoutParams ?: return

        val spanCount = lm.spanCount
        val spanIndex = lp.spanIndex
        val positionLayout = lp.viewLayoutPosition
        val itemCount = lm.itemCount
        val position = parent.getChildAdapterPosition(view)

        outRect.right = spacing / 2
        outRect.left = spacing / 2
        outRect.top = spacing / 2
        outRect.bottom = spacing / 2

        if (spanIndex == 0) outRect.left = spacing

        if (position < spanCount) outRect.top = spacing

        if (spanIndex == (spanCount - 1)) outRect.right = spacing

        if (positionLayout > (itemCount - spanCount)) outRect.bottom = spacing
    }
}