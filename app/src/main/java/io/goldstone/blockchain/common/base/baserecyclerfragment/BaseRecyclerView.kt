package io.goldstone.blockchain.common.base.baserecyclerfragment

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.MotionEvent
import android.widget.LinearLayout
import com.blinnnk.extension.isNull
import com.blinnnk.extension.orZero
import com.blinnnk.util.TinyNumberUtils
import io.goldstone.blockchain.common.utils.LogUtil
import io.goldstone.blockchain.common.utils.load
import io.goldstone.blockchain.common.utils.then
import io.goldstone.blockchain.common.value.Spectrum
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
		itemAnimator?.changeDuration = 0
	}

	fun <T> addSwipeEvent(
		icon: Int,
		iconPaddingSize: Int,
		direction: Int, // ItemTouchHelper.LEFT 格式
		callback: (position: Int, itemView: T?) -> Unit
	) {
		object : ItemTouchHelper.SimpleCallback(ItemTouchHelper.ACTION_STATE_DRAG, direction) {
			override fun onMove(
				recyclerView: RecyclerView,
				viewHolder: ViewHolder,
				targetHolder: ViewHolder
			): Boolean {
				return false
			}

			var background: Drawable? = null
			var markIcon: Drawable? = null
			var initiated: Boolean = false
			private fun init() {
				val cellColor =
					if (direction == ItemTouchHelper.LEFT) Spectrum.red else Spectrum.green
				background = ColorDrawable(cellColor)
				markIcon = ContextCompat.getDrawable(context, icon)
				markIcon?.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP)
				initiated = true
			}

			override fun onChildDraw(
				canvas: Canvas,
				recyclerView: RecyclerView,
				viewHolder: RecyclerView.ViewHolder,
				directionX: Float,
				directionY: Float,
				actionState: Int,
				isCurrentlyActive: Boolean
			) {
				val isMovingToLeft = direction == ItemTouchHelper.LEFT
				val itemView = viewHolder.itemView
				val boundsStart = if (isMovingToLeft) itemView.right else itemView.left
				// not interested in those
				if (viewHolder.adapterPosition == -1) return
				if (!initiated) init()
				// draw red background
				background?.setBounds(boundsStart + directionX.toInt(), itemView.top, boundsStart, itemView.bottom)
				background?.draw(canvas)
				val start = (boundsStart + directionX).toInt()
				val modulus = if (isMovingToLeft) -1 else 1
				val end = start - (itemView.bottom - itemView.top) * modulus
				val markLeft = if (isMovingToLeft) start else end
				val markRight = if (isMovingToLeft) end else start
				markIcon?.setBounds(
					markLeft + iconPaddingSize,
					itemView.top + iconPaddingSize,
					markRight - iconPaddingSize,
					itemView.bottom - iconPaddingSize
				)
				markIcon?.draw(canvas)
				super.onChildDraw(canvas, recyclerView, viewHolder, directionX, directionY, actionState, isCurrentlyActive)
			}

			override fun onSwiped(viewHolder: RecyclerView.ViewHolder, swipeDir: Int) {
				//Remove swiped item from list and notify the RecyclerView
				val position = viewHolder.adapterPosition
				callback(position, viewHolder.itemView as? T)
			}
		}.let { ItemTouchHelper(it).attachToRecyclerView(this) }
	}

	override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
		if (event.action == MotionEvent.ACTION_DOWN && this.scrollState == RecyclerView.SCROLL_STATE_IDLE) {
			this.stopScroll()
		}
		return super.onInterceptTouchEvent(event)
	}


	inline fun <T> addDragEventAndReordering(
		adapterDataSet: ArrayList<T>,
		crossinline hold: (fromPosition: Int, toPosition: Int) -> Unit
	) {
		var fromPosition: Int? = null
		var toPosition: Int? = null
		val itemMove = ItemTouchHelper(object : ItemTouchHelper.Callback() {
			override fun getMovementFlags(
				recyclerView: RecyclerView,
				viewHolder: ViewHolder
			): Int {
				fromPosition = viewHolder.adapterPosition
				return makeFlag(
					ItemTouchHelper.ACTION_STATE_DRAG,
					ItemTouchHelper.DOWN or ItemTouchHelper.UP or ItemTouchHelper.START or ItemTouchHelper.END
				)
			}

			override fun onMove(
				recyclerView: RecyclerView,
				viewHolder: ViewHolder,
				target: ViewHolder
			): Boolean {
				Collections.swap(
					adapterDataSet,
					viewHolder.adapterPosition.orZero(),
					target.adapterPosition.orZero()
				)
				// and notify the adapter that its dataSet has changed
				recyclerView.adapter?.notifyItemMoved(
					viewHolder.adapterPosition.orZero(), target.adapterPosition.orZero()
				)
				return true
			}

			override fun onSwiped(
				viewHolder: ViewHolder,
				direction: Int
			) {

			}

			override fun onSelectedChanged(
				viewHolder: ViewHolder?,
				actionState: Int
			) {
				super.onSelectedChanged(viewHolder, actionState)
				if (actionState == 0) {
					if (TinyNumberUtils.allFalse(fromPosition.isNull(), toPosition.isNull())) {
						hold(fromPosition!!, toPosition!!)
					}
				}
			}

			override fun onMoved(
				recyclerView: RecyclerView,
				viewHolder: ViewHolder,
				fromPos: Int,
				target: ViewHolder,
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
		crossinline block: (T) -> Unit
	) {
		load {
			findViewHolderForAdapterPosition(position)?.itemView
		} then {
			try {
				(it as? T)?.let(block)
			} catch (error: Exception) {
				LogUtil.error("getItemAtAdapterPosition", error)
			}
		}
	}
}

