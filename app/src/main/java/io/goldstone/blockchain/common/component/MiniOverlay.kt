package io.goldstone.blockchain.common.component

import android.annotation.SuppressLint
import android.content.Context
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.blinnnk.animation.scale
import com.blinnnk.extension.addCorner
import com.blinnnk.extension.into
import com.blinnnk.extension.setAlignParentRight
import com.blinnnk.extension.setCenterInVertical
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.observing
import io.goldstone.blockchain.common.base.BaseCell
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.value.*
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk25.coroutines.onClick

@SuppressLint("ViewConstructor")
/**
 * @date 2018/7/11 1:32 PM
 * @author KaySaith
 */
class MiniOverlay(
	context: Context,
	private val hold: (cell: BaseCell, title: String) -> Unit
) : RelativeLayout(context) {
	
	private var overlayHeight = 0
	private lateinit var dashBoard: ViewGroup
	private val contentHeight = 26.uiPX()
	var model: List<Pair<Int, String>> by observing(listOf()) {
		dashBoard = verticalLayout {
			model.forEachIndexed { index, pair ->
				topPadding = 10.uiPX()
				bottomPadding = 10.uiPX()
				addCorner(5.uiPX(), Spectrum.white)
				elevation = ShadowSize.Overlay
				BaseCell(context).apply {
					layoutParams.height = 40.uiPX()
					setGrayStyle()
					imageView {
						imageResource = pair.first
						layoutParams = LinearLayout.LayoutParams(contentHeight, contentHeight)
					}.setCenterInVertical()
					textView {
						x = 30.uiPX().toFloat()
						textSize = fontSize(12)
						text = pair.second
						layoutParams = LinearLayout.LayoutParams(wrapContent, contentHeight)
						textColor = GrayScale.black
						typeface = GoldStoneFont.heavy(context)
						gravity = Gravity.CENTER_VERTICAL
					}.setCenterInVertical()
					if (index == model.lastIndex) {
						removeBottomLine()
					}
					hold(this, pair.second)
				}.into(this)
			}
		}.apply {
			overlayHeight = 40.uiPX() * model.size + 20.uiPX()
			layoutParams = RelativeLayout.LayoutParams(220.uiPX(), overlayHeight)
		}
	}
	
	init {
		id = ElementID.miniOverlay
		isClickable = true
		layoutParams = RelativeLayout.LayoutParams(matchParent, matchParent)
		onClick {
			removeSelf()
		}
	}
	
	fun getOverlayHeight() = overlayHeight
	
	fun setTopRight() {
		if (this::dashBoard.isInitialized) {
			dashBoard.scale(2)
			dashBoard.y = 20.uiPX().toFloat()
			dashBoard.x -= 20.uiPX()
			dashBoard.setAlignParentRight()
		}
	}
	
	fun setTopValue(topPosition: Float) {
		if (this::dashBoard.isInitialized) {
			dashBoard.y = topPosition
			dashBoard.x -= 20.uiPX()
			dashBoard.setAlignParentRight()
		}
	}
	
	fun setTopLeft() {
		if (this::dashBoard.isInitialized) {
			dashBoard.scale(1)
			dashBoard.y = 20.uiPX().toFloat()
			dashBoard.x = 20.uiPX().toFloat()
		}
	}
	
	fun removeSelf() {
		(parent as? ViewGroup)?.removeView(this)
	}
}