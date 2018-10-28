package io.goldstone.blockchain.common.component.overlay

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.blinnnk.animation.addTouchRippleAnimation
import com.blinnnk.animation.updateAlphaAnimation
import com.blinnnk.extension.addCorner
import com.blinnnk.extension.setAlignParentRight
import com.blinnnk.extension.setCenterInParent
import com.blinnnk.uikit.RippleMode
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.R
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.value.*
import io.goldstone.blockchain.common.value.ScreenSize
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.sdk25.coroutines.onLayoutChange

/**
 * @date 2018/6/5 1:50 AM
 * @author KaySaith
 */
open class ContentScrollOverlayView(context: Context) : RelativeLayout(context) {
	
	var recoveryBackEvent: Runnable? = null
	private var container: RelativeLayout
	private lateinit var contentLayout: LinearLayout
	private lateinit var titleView: TextView
	private lateinit var closeButton: ImageView
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
			verticalLayout {
				// Header
				relativeLayout {
					backgroundColor = GrayScale.whiteGray
					lparams(matchParent, headerHeight)
					titleView = textView {
						textSize = fontSize(14)
						textColor = GrayScale.black
						typeface = GoldStoneFont.heavy(context)
						layoutParams = LinearLayout.LayoutParams(matchParent, headerHeight)
						gravity = Gravity.CENTER
					}
					closeButton = imageView(R.drawable.close_icon) {
						layoutParams = LinearLayout.LayoutParams(headerHeight, headerHeight)
						scaleType = ImageView.ScaleType.CENTER_INSIDE
						setColorFilter(GrayScale.midGray)
						x -= 5.uiPX()
						addTouchRippleAnimation(Color.TRANSPARENT, Spectrum.green, RippleMode.Round)
						onClick {
							remove()
						}
					}
					closeButton.setAlignParentRight()
				}
				scrollView {
					lparams(matchParent, wrapContent)
					contentLayout = verticalLayout {
						id = ContainerID.contentOverlay
						bottomPadding = 15.uiPX()
						layoutParams = LinearLayout.LayoutParams(matchParent, wrapContent)
					}
				}
				// 设定最大高度
				onLayoutChange { _, _, top, _, bottom, _, _, _, _ ->
					if (bottom - top > ScreenSize.fullHeight * 0.9) {
						layoutParams.height = (ScreenSize.fullHeight * 0.9).toInt()
					}
				}
			}
			addCorner(CornerSize.small.toInt(), Spectrum.white)
		}
		container.updateAlphaAnimation(1f)
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
	
	open fun remove() {
		(parent as? ViewGroup)?.apply {
			findViewById<ContentScrollOverlayView>(ElementID.contentScrollview)?.let {
				removeView(it)
			}
		}
	}
	
	fun addContent(hold: ViewGroup.() -> Unit) {
		hold(contentLayout)
	}
}