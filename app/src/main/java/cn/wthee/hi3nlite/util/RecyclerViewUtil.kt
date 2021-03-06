package cn.wthee.hi3nlite.util

import androidx.recyclerview.widget.RecyclerView

object RecyclerViewUtil {
    fun isBottom(recyclerView: RecyclerView): Boolean {
        val screenHeight = recyclerView.computeVerticalScrollExtent()
        val scrollHeight = recyclerView.computeVerticalScrollOffset()
        val recyclerViewHeight = recyclerView.computeVerticalScrollRange()
        return screenHeight + scrollHeight >= recyclerViewHeight * 0.8
    }
}