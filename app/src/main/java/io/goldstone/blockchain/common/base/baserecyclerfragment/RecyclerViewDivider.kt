package io.goldstone.blockchain.common.base.baserecyclerfragment

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.support.v7.widget.RecyclerView
import android.text.TextPaint
import android.view.View
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.observing
import io.goldstone.blockchain.common.utils.GoldStoneFont


/**
 * @author KaySaith
 * @date  2018/11/15
 */
class RecyclerViewDivider(recyclerView: BaseRecyclerView) : RecyclerView.ItemDecoration() {

	var sessionData: List<RecyclerViewSessionModel> by observing(listOf()) {
		recyclerView.invalidate()
	}
	private var paint: Paint = TextPaint().apply {
		isAntiAlias = true
		textSize = 12.uiPX().toFloat()
		typeface = GoldStoneFont.heavy(recyclerView.context)
	}

	fun setTextColor(color: Int) {
		paint.color = color
	}

	fun setTextSize(size: Int) {
		paint.textSize = size.toFloat()
	}

	override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
		super.getItemOffsets(outRect, view, parent, state)
		val position = parent.getChildAdapterPosition(view)
		sessionData.forEach {
			if (position == it.position) outRect.top = it.height
		}
	}

	override fun onDraw(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
		val childCount = parent.childCount
		val left = parent.paddingLeft.toFloat()
		for (index in 0 until childCount - 1) {
			val view = parent.getChildAt(index)
			val position = parent.getChildAdapterPosition(view)
			sessionData.forEach {
				if (position == it.position) {
					val top = view.top - 5.uiPX().toFloat()
					canvas.drawText(it.title, left + it.horizontalPadding, top, paint)
				}
			}
		}
	}
}

data class RecyclerViewSessionModel(
	val position: Int,
	val title: String,
	val height: Int,
	val horizontalPadding: Float
)
