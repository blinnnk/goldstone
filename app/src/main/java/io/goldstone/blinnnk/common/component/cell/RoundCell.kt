package io.goldstone.blinnnk.common.component.cell

import android.content.Context
import android.view.Gravity
import android.view.ViewManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.blinnnk.extension.alignParentRight
import com.blinnnk.extension.into
import com.blinnnk.uikit.uiPX
import io.goldstone.blinnnk.R
import io.goldstone.blinnnk.common.component.GSCard
import io.goldstone.blinnnk.common.utils.GoldStoneFont
import io.goldstone.blinnnk.common.value.GrayScale
import io.goldstone.blinnnk.common.value.ScreenSize
import io.goldstone.blinnnk.common.value.fontSize
import org.jetbrains.anko.*
import org.jetbrains.anko.custom.ankoView

/**
 * @date 2018/7/12 2:29 PM
 * @author KaySaith
 */
class RoundCell(context: Context) : GSCard(context) {
	var container: RelativeLayout
	private val cellHeight = 40.uiPX()
	private val titleView = TextView(context).apply {
		textSize = fontSize(14)
		typeface = GoldStoneFont.heavy(context)
		textColor = GrayScale.black
		gravity = Gravity.CENTER_VERTICAL
		layoutParams = RelativeLayout.LayoutParams(matchParent, cellHeight)
		leftPadding = 25.uiPX()
	}
	private val arrowIcon = ImageView(context).apply {
		scaleX = 0.75f
		scaleY = 0.75f
		setColorFilter(GrayScale.midGray)
		imageResource = R.drawable.arrow_icon
		layoutParams = RelativeLayout.LayoutParams(cellHeight, cellHeight)
	}
	private val subtitleView = TextView(context).apply {
		textSize = fontSize(12)
		textColor = GrayScale.midGray
		typeface = GoldStoneFont.heavy(context)
		gravity = Gravity.CENTER_VERTICAL or Gravity.END
		layoutParams = RelativeLayout.LayoutParams(matchParent, cellHeight)
	}

	init {
		layoutParams = LinearLayout.LayoutParams(ScreenSize.widthWithPadding, wrapContent)
		setCardBackgroundColor(GrayScale.whiteGray)
		container = relativeLayout {
			lparams(matchParent, matchParent)
			layoutParams = RelativeLayout.LayoutParams(matchParent, cellHeight)
			subtitleView.into(this)
			titleView.into(this)
			arrowIcon.into(this)
			arrowIcon.alignParentRight()
			subtitleView.alignParentRight()
			subtitleView.x -= 40.uiPX()
		}
	}

	fun setTitles(title: String, subTitle: String) {
		titleView.text = title
		subtitleView.text = subTitle
	}
}

fun ViewManager.roundCell() = roundCell {}
inline fun ViewManager.roundCell(init: RoundCell.() -> Unit) = ankoView({ RoundCell(it) }, 0, init)