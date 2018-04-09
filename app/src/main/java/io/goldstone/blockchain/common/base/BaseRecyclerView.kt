package io.goldstone.blockchain.common.base

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.MotionEvent
import android.widget.LinearLayout
import com.blinnnk.extension.isTrue
import com.blinnnk.util.coroutinesTask
import org.jetbrains.anko.matchParent

/**
 * @date 23/03/2018 3:48 PM
 * @author KaySaith
 */

@Suppress("UNCHECKED_CAST")
open class BaseRecyclerView(context: Context) : RecyclerView(context) {

  init {
    layoutManager = LinearLayoutManager(context, LinearLayout.VERTICAL, false)
    layoutParams = LinearLayout.LayoutParams(matchParent, matchParent)
    itemAnimator.changeDuration = 0
  }

  override fun onInterceptTouchEvent(event: MotionEvent): Boolean {

    if (event.action == MotionEvent.ACTION_DOWN && this.scrollState == RecyclerView.SCROLL_STATE_IDLE) {
      this.stopScroll()
    }
    return super.onInterceptTouchEvent(event)
  }

  fun getAllItemsHeight(block: (Int) -> Unit) {
    coroutinesTask({
      (0 until childCount).map { getChildAt(it) }
    }) {
      val visibleItemsHeight = it.map { it.measuredHeight }.sum()
      val finalHeight =
        if (computeVerticalScrollOffset() > visibleItemsHeight)
          computeVerticalScrollOffset() + (computeVerticalScrollOffset() - visibleItemsHeight)
        else visibleItemsHeight
      block(finalHeight)
    }

  }

  inline fun <reified T> getItemViewAtAdapterPosition(position: Int, crossinline block: T.() -> Unit) {
    coroutinesTask({
      findViewHolderForAdapterPosition(position)?.itemView
    }) {
      block(it as T)
    }
  }

  inline fun <reified T : RecyclerView.LayoutManager> getManager(block: T.() -> Unit) {
    (this.layoutManager is T).isTrue { block(this.layoutManager as T) }
  }

}