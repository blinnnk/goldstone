package io.goldstone.blockchain.common.component.dragbutton

import android.annotation.SuppressLint
import android.content.Context
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.blinnnk.extension.addCorner
import com.blinnnk.extension.centerInParent
import com.blinnnk.extension.setMargins
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.R
import io.goldstone.blockchain.common.value.*
import org.jetbrains.anko.*

/**
 * @date: 2018-12-10.
 * @author: yangLiHai
 * @description:
 */
class DragFloatingButton(context: Context) : RelativeLayout(context) {

	var isTouching = false
	var xDistance = 0f // 按下的点距离左侧的距离
	var yDistance = 0f // 按下的点距离上边的距离
	private lateinit var icon: ImageView
	private var container: RelativeLayout

	init {
		layoutParams = RelativeLayout.LayoutParams(90.uiPX(), 90.uiPX())
		container = relativeLayout {
			lparams(66.uiPX(), 66.uiPX())
			centerInParent()
			icon = imageView {
				scaleType = ImageView.ScaleType.CENTER_INSIDE
				setColorFilter(Spectrum.white)
				visibility = View.GONE
			}
			icon.centerInParent()
			elevation = 6.uiPX().toFloat()
		}
		container.setMargins<RelativeLayout.LayoutParams> {
			margin = 15.uiPX()
		}
		isClickable = true
	}

	@SuppressLint("ClickableViewAccessibility")
	override fun onTouchEvent(event: MotionEvent): Boolean {
		if (event.action == MotionEvent.ACTION_DOWN) {
			isTouching = true
			xDistance = event.x
			yDistance = event.y
		} else if (event.action == MotionEvent.ACTION_UP) {
			isTouching = false
		}
		return super.onTouchEvent(event)
	}

	fun showIcon(src: Int = R.drawable.menu_icon, color: Int = Spectrum.green) {
		container.addCorner(30.uiPX(), color)
		icon.visibility = View.VISIBLE
		icon.imageResource = src
	}
}
