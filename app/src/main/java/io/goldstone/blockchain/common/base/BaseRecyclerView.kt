package io.goldstone.blockchain.common.base

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.MotionEvent
import android.widget.LinearLayout
import com.blinnnk.extension.orZero
import io.goldstone.blockchain.common.utils.load
import io.goldstone.blockchain.common.utils.then
import org.jetbrains.anko.matchParent
import java.util.*

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
	
	inline fun <T> addDragEventAndReordering(
		adapterDataSet: ArrayList<T>,
		crossinline hold: (fromPosition: Int?, toPosition: Int?) -> Unit
	) {
		var fromPosition: Int? = null
		var toPosition: Int? = null
		val itemMove = ItemTouchHelper(object : ItemTouchHelper.Callback() {
			override fun getMovementFlags(
				recyclerView: RecyclerView?,
				viewHolder: ViewHolder?
			): Int {
				fromPosition = viewHolder?.adapterPosition
				return makeFlag(
					ItemTouchHelper.ACTION_STATE_DRAG,
					ItemTouchHelper.DOWN or ItemTouchHelper.UP or ItemTouchHelper.START or ItemTouchHelper.END
				)
			}
			
			override fun onMove(
				recyclerView: RecyclerView?,
				viewHolder: ViewHolder?,
				target: ViewHolder?
			): Boolean {
				Collections.swap(
					adapterDataSet, viewHolder?.adapterPosition.orZero(), target?.adapterPosition.orZero()
				)
				// and notify the adapter that its dataset has changed
				recyclerView?.adapter?.notifyItemMoved(
					viewHolder?.adapterPosition.orZero(), target?.adapterPosition.orZero()
				)
				return true
			}
			
			override fun onSwiped(
				viewHolder: ViewHolder?,
				direction: Int
			) {
			}
			
			override fun onSelectedChanged(
				viewHolder: ViewHolder?,
				actionState: Int
			) {
				super.onSelectedChanged(viewHolder, actionState)
				if (actionState == 0) {
					hold(fromPosition, toPosition)
				}
			}
			
			override fun onMoved(
				recyclerView: RecyclerView?,
				viewHolder: ViewHolder?,
				fromPos: Int,
				target: ViewHolder?,
				toPos: Int,
				x: Int,
				y: Int
			) {
				super.onMoved(recyclerView, viewHolder, fromPos, target, toPos, x, y)
				toPosition = toPos
			}
		})
		itemMove.attachToRecyclerView(this)
	}
	
	inline fun <reified T> getItemAtAdapterPosition(
		position: Int,
		crossinline block: (T?) -> Unit
	) {
		load {
			findViewHolderForAdapterPosition(position)?.itemView
		} then {
			block(it as? T)
		}
	}
}

