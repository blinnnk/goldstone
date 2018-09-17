package io.goldstone.blockchain.common.component.overlay

import android.content.Context
import android.graphics.Color
import android.view.*
import android.widget.*
import com.blinnnk.animation.addTouchRippleAnimation
import com.blinnnk.animation.updateAlphaAnimation
import com.blinnnk.extension.*
import com.blinnnk.extension.addCorner
import com.blinnnk.extension.setAlignParentBottom
import com.blinnnk.extension.setCenterInParent
import com.blinnnk.uikit.RippleMode
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.R
import io.goldstone.blockchain.common.component.button.RoundButton
import io.goldstone.blockchain.common.language.CommonText
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.utils.click
import io.goldstone.blockchain.common.value.*
import io.goldstone.blockchain.common.value.ScreenSize
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.sdk25.coroutines.onLayoutChange
import javax.security.auth.callback.Callback

/**
 * @date 2018/6/5 1:50 AM
 * @author KaySaith
 */
class ContentScrollOverlayView(context: Context) : RelativeLayout(context) {
	
	var recoveryBackEvent: Runnable? = null
	private var container: RelativeLayout
	private lateinit var contentLayout: LinearLayout
	private lateinit var titleView: TextView
	private lateinit var closeButton: ImageView
	private lateinit var scrollViewContent: ScrollView
	val maxWidth = 300.uiPX()
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
				id = ElementID.overlayContainer
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
				scrollViewContent = scrollView {
					lparams(matchParent, wrapContent)
					contentLayout = verticalLayout {
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
			findViewById<ContentScrollOverlayView>(ElementID.contentScrollview)?.let {
				removeView(it)
			}
		}
	}
	
	fun addContent(hold: ViewGroup.() -> Unit) {
		hold(contentLayout)
	}
	
	fun showConfirmButton(view: View) {
		scrollViewContent.bottomPadding = 60.uiPX()
		container.apply {
			relativeLayout {
				gravity = Gravity.CENTER
				lparams(matchParent, 60.uiPX()) {
					addRule(RelativeLayout.ALIGN_BOTTOM, ElementID.overlayContainer)
				}
				addView(view, LayoutParams(matchParent, matchParent))
			}
		}
	}
}