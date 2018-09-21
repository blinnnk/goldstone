package io.goldstone.blockchain.common.component.container

import android.content.Context
import android.view.Gravity
import android.view.ViewGroup
import android.widget.RelativeLayout
import com.blinnnk.extension.addCircleBorder
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

open class GrayCardView(context: Context) : RelativeLayout(context) {

	protected var container = verticalLayout {
		setCenterInParent()
		topPadding = 5.uiPX()
		gravity = Gravity.CENTER_HORIZONTAL
		addCorner(CornerSize.small.toInt(), GrayScale.whiteGray)
		elevation = 4f
	}

	override fun onAttachedToWindow() {
		super.onAttachedToWindow()
		// 在这个生命周期的时候更改容器的部分参数, 让阴影能够完美的漏出来
		measure(0, 0)
		val width = measuredWidth
		val height = measuredHeight
		layoutParams.height = height + 6.uiPX()
		container.layoutParams = RelativeLayout.LayoutParams(width - 4.uiPX(), height)
		container.requestLayout()
	}

	// 为了阴影内嵌了 双层 `RelativeLayout` 所以这里重写默认的 `addView` 方法
	fun addView(childView: ViewGroup) {
		container.addView(childView)
	}

	fun getContainer(): ViewGroup = container

	override fun setBackgroundColor(color: Int) {
		container.addCorner(CornerSize.small.toInt(), color)
	}

	fun setBorder(borderColor: Int) {
		container.addCircleBorder(CornerSize.small.toInt(), 1, borderColor)
	}

}