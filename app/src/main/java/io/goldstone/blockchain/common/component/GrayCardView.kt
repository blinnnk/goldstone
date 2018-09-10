package io.goldstone.blockchain.common.component

import android.content.Context
import android.view.ViewGroup
import android.widget.RelativeLayout
import com.blinnnk.extension.addCorner
import com.blinnnk.extension.setCenterInParent
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.value.CornerSize
import io.goldstone.blockchain.common.value.GrayScale
import org.jetbrains.anko.topPadding
import org.jetbrains.anko.verticalLayout


/**
 * @author KaySaith
 * @date  2018/09/10
 */

class GrayCardView(context: Context) : RelativeLayout(context) {

	private var container = verticalLayout {
		topPadding = 5.uiPX()
		addCorner(CornerSize.cell, GrayScale.whiteGray)
		elevation = 4f
	}

	fun setCardParams(width: Int, height: Int) {
		container.layoutParams = RelativeLayout.LayoutParams(width - 4.uiPX(), height - 6.uiPX())
		layoutParams = RelativeLayout.LayoutParams(width, height)
		container.setCenterInParent()
		requestLayout()
	}

	// 为了阴影内嵌了 双层 `RelativeLayout` 所以这里重写默认的 `addView` 方法
	fun addView(childView: ViewGroup) {
		container.addView(childView)
	}

	fun getContainer(): ViewGroup = container

}