package io.goldstone.blockchain.common.component.cell

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.blinnnk.animation.addTouchRippleAnimation
import com.blinnnk.extension.*
import com.blinnnk.uikit.RippleMode
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.R
import io.goldstone.blockchain.common.component.cell.GraySquareCellWithButtons.Companion.CellType.Default
import io.goldstone.blockchain.common.component.cell.GraySquareCellWithButtons.Companion.CellType.Normal
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.value.*
import io.goldstone.blockchain.common.value.ScreenSize
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk25.coroutines.onClick

/**
 * @date 2018/7/11 1:20 AM
 * @author KaySaith
 */
open class GraySquareCellWithButtons(context: Context) : RelativeLayout(context) {

	private var cellHeight = 45.uiPX()
	protected val title = TextView(context).apply {
		textSize = fontSize(12)
		typeface = GoldStoneFont.heavy(context)
		textColor = GrayScale.black
		layoutParams = RelativeLayout.LayoutParams(matchParent, matchParent)
		x += 20.uiPX()
		gravity = Gravity.CENTER_VERTICAL
	}

	protected val subtitle = TextView(context).apply {
		visibility = View.GONE
		textSize = fontSize(12)
		typeface = GoldStoneFont.black(context)
		textColor = GrayScale.black
		layoutParams = RelativeLayout.LayoutParams(matchParent, matchParent)
		gravity = Gravity.CENTER_VERTICAL
	}

	protected val description by lazy {
		TextView(context).apply {
			textSize = fontSize(11)
			typeface = GoldStoneFont.medium(context)
			setTypeface(typeface, Typeface.ITALIC)
			layoutParams = RelativeLayout.LayoutParams(matchParent, matchParent)
			gravity = Gravity.CENTER_VERTICAL
			visibility = View.GONE
		}
	}

	val copyButton by lazy {
		ImageView(context).apply {
			imageResource = R.drawable.copy_icon
			scaleType = ImageView.ScaleType.CENTER_INSIDE
			setColorFilter(GrayScale.midGray)
			layoutParams = RelativeLayout.LayoutParams(cellHeight, matchParent)
			addTouchRippleAnimation(Color.TRANSPARENT, Spectrum.green, RippleMode.Round)
		}
	}
	val moreButton by lazy {
		ImageView(context).apply {
			imageResource = R.drawable.more_icon
			scaleType = ImageView.ScaleType.CENTER_INSIDE
			setColorFilter(GrayScale.midGray)
			layoutParams = RelativeLayout.LayoutParams(cellHeight, matchParent)
			addTouchRippleAnimation(Color.TRANSPARENT, Spectrum.green, RippleMode.Round)
		}
	}
	private lateinit var lineView: View
	private var container: RelativeLayout

	init {
		container = relativeLayout {
			lparams(ScreenSize.widthWithPadding, cellHeight)
			setCenterInParent()
			addCorner(CornerSize.cell, GrayScale.whiteGray)
			lineView = View(context).apply {
				layoutParams = RelativeLayout.LayoutParams(6.uiPX(), matchParent)
			}
			addView(lineView)
			addView(title)
			addView(subtitle)
			addView(description)
			addView(copyButton)
			copyButton.setAlignParentRight()
			copyButton.x -= 30.uiPX()
			addView(moreButton)
			moreButton.setAlignParentRight()
		}
		layoutParams = RelativeLayout.LayoutParams(matchParent, cellHeight + 7.uiPX())
	}

	fun <T : CharSequence> setTitle(text: T) {
		title.text = text
		val paddingSize = 25.uiPX() + text.measureTextWidth(13.uiPX().toFloat()).toInt()
		subtitle.leftPadding = paddingSize
		description.leftPadding = paddingSize
	}

	fun showOnlyCopyButton(action: () -> Unit) {
		moreButton.visibility = View.GONE
		copyButton.x += 30.uiPX()
		copyButton.onClick {
			copyButton.preventDuplicateClicks()
			action()
		}
		updateStyle(Normal, true)
	}

	fun showDescriptionTitle(text: String, color: Int = GrayScale.midGray) {
		description.visibility = View.VISIBLE
		description.text = text
		description.textColor = color
		subtitle.y -= 5.uiPX()
		description.y += 8.uiPX()
	}

	fun setSubtitle(text: String) {
		subtitle.visibility = View.VISIBLE
		subtitle.text = if (text.length > 36) text.substring(0, 36) + "..." else text
	}

	fun updateStyle(type: CellType = Normal, isGrayTitle: Boolean = false) {
		container.elevation = 4f
		title.textColor = if (isGrayTitle) GrayScale.gray else GrayScale.black
		moreButton.setColorFilter(GrayScale.gray)
		copyButton.setColorFilter(GrayScale.gray)
		when (type) {
			Normal -> lineView.backgroundColor = GrayScale.midGray
			Default -> lineView.backgroundColor = Spectrum.blue
		}
	}

	fun getTitle(): String = title.text.toString()
	fun getSubtitle(): String = subtitle.text.toString()

	companion object {
		enum class CellType {
			Normal, Default
		}
	}
}