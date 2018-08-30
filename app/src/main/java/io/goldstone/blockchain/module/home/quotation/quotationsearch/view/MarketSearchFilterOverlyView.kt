package io.goldstone.blockchain.module.home.quotation.quotationsearch.view

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.ViewGroup
import android.widget.*
import com.blinnnk.animation.addTouchRippleAnimation
import com.blinnnk.animation.updateAlphaAnimation
import com.blinnnk.extension.*
import com.blinnnk.uikit.RippleMode
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.R
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.utils.click
import io.goldstone.blockchain.common.value.*
import io.goldstone.blockchain.common.value.ScreenSize
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.sdk25.coroutines.onLayoutChange

/**
 * @date: 2018/8/29.
 * @author: yanglihai
 * @description:
 */
class MarketSearchFilterOverlyView(context: Context) : RelativeLayout(context){
	
	var recoveryBackEvent: Runnable? = null
	private var container: RelativeLayout
	private lateinit var contentLayout: RelativeLayout
	private lateinit var titleView: TextView
	private lateinit var closeButton: ImageView
	lateinit var confirmButton: Button
	private val maxWidth = 300.uiPX()
	private val headerHeight = 50.uiPX()
	
	init {
		isClickable = true
		layoutParams = RelativeLayout.LayoutParams(matchParent, matchParent)
		id = ElementID.contentScrollview
		backgroundColor = GrayScale.Opacity5Black
		// 主容器
		container = relativeLayout {
			alpha = 0f
			lparams(maxWidth, wrapContent)
			minimumHeight = 400.uiPX()
			updateAlphaAnimation(1f)
			verticalLayout {
				// Header
				linearLayout {
					backgroundColor = GrayScale.whiteGray
					lparams(matchParent, headerHeight)
					titleView = textView {
						textSize = fontSize(14)
						textColor = GrayScale.black
						typeface = GoldStoneFont.heavy(context)
						layoutParams = LinearLayout.LayoutParams(250.uiPX(), headerHeight)
						gravity = Gravity.CENTER_VERTICAL
						leftPadding = 20.uiPX()
					}
					closeButton = imageView(R.drawable.close_icon) {
						layoutParams = LinearLayout.LayoutParams(headerHeight, headerHeight)
						scaleType = ImageView.ScaleType.CENTER_INSIDE
						setColorFilter(GrayScale.midGray)
						addTouchRippleAnimation(Color.TRANSPARENT, Spectrum.green, RippleMode.Round)
						onClick {
							remove()
						}
					}
				}
				
				relativeLayout {
					layoutParams = LinearLayout.LayoutParams(matchParent, wrapContent)
					contentLayout = relativeLayout {
						id = ElementID.miniOverlay
						layoutParams = LinearLayout.LayoutParams(matchParent, wrapContent)
						bottomPadding = 40.uiPX()
					}
					confirmButton = button {
						text = "confirm"
					}.lparams(wrapContent, 40.uiPX()) {
						addRule(RelativeLayout.ALIGN_BOTTOM, contentLayout.id)
						addRule(RelativeLayout.CENTER_HORIZONTAL)
					}
				}
				
				// 设定最大高度
				onLayoutChange { _, _, top, _, bottom, _, _, _, _ ->
					if (bottom - top > ScreenSize.fullHeight * 0.9) {
						layoutParams.height = (ScreenSize.fullHeight * 0.9).toInt()
					}
				}
			}
			addCorner(CornerSize.small, Spectrum.white)
		}
		container.setCenterInParent()
	}
	
	fun setContentPadding(
		left: Int = 20.uiPX(),
		top: Int = 10.uiPX(),
		right: Int = 20.uiPX(),
		bottom: Int = 20.uiPX()
	) {
		contentLayout.setPadding(left, top, right, bottom)
	}
	
	fun setTitle(text: String) {
		titleView.text = text
	}
	
	fun remove() {
		(parent as? ViewGroup)?.apply {
			findViewById<MarketSearchFilterOverlyView>(ElementID.contentScrollview)?.let {
				removeView(it)
			}
		}
	}
	
	fun addContent(hold: ViewGroup.() -> Unit) {
		hold(contentLayout)
	}
}