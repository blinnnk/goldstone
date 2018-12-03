package io.goldstone.blockchain.common.base.baserecyclerfragment

import android.graphics.Rect
import android.support.v7.widget.RecyclerView
import android.view.View
import com.blinnnk.uikit.uiPX


/**
 * @author KaySaith
 * @date  2018/12/01
 */
class BaseDecoration : RecyclerView.ItemDecoration() {

	override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
		super.getItemOffsets(outRect, view, parent, state)
		val position = parent.getChildAdapterPosition(view)
		when (position) {
			0 -> outRect.left = 10.uiPX()
			else -> outRect.left = 3.uiPX()
		}
	}
}