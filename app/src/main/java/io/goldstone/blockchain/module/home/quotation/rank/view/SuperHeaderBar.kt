package io.goldstone.blockchain.module.home.quotation.rank.view

import android.content.Context
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import com.blinnnk.extension.addCorner
import com.blinnnk.extension.setMargins
import com.blinnnk.uikit.ScreenSize
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.value.*
import org.jetbrains.anko.*

/**
 * @date: 2018/8/17.
 * @author: yanglihai
 * @description: 头部悬浮的view
 */
class SuperHeaderBar(context: Context) : LinearLayout(context) {
	
	private val cellWidth = ScreenSize.Width
	
	private val itemWidth = cellWidth / 10
	
	private val textViewIndex: TextView by lazy {
		TextView(context).apply {
			layoutParams = LayoutParams(itemWidth, matchParent)
			typeface = GoldStoneFont.heavy(context)
			textSize = fontSize(17)
			textColor = Spectrum.darkBlue
			gravity = Gravity.CENTER
			text = "Rank"
		}
	}
	private val textViewSymbol: TextView by lazy {
		TextView(context).apply {
			layoutParams = LayoutParams(itemWidth * 3, wrapContent)
			typeface = GoldStoneFont.heavy(context)
			textSize = fontSize(15)
			textColor = Spectrum.darkBlue
			gravity = Gravity.CENTER
			text = "Name(Symbol)"
		}
	}
	private val textViewPrice: TextView by lazy {
		TextView(context).apply {
			layoutParams = LayoutParams(itemWidth, matchParent)
			textSize = fontSize(15)
			textColor = Spectrum.darkBlue
			gravity = Gravity.CENTER
			text = "Price\n(USD)"
		}
	}
	private val textViewChange: TextView by lazy {
		TextView(context).apply {
			layoutParams = LayoutParams(itemWidth * 2, matchParent)
			textSize = fontSize(15)
			textColor = Spectrum.darkBlue
			gravity = Gravity.CENTER
			text = "Change(%) \n 24(h)"
		}
	}
	private val textViewCapValue: TextView by lazy {
		TextView(context).apply {
			layoutParams = LayoutParams(itemWidth * 3, matchParent)
			textSize = fontSize(15)
			textColor = Spectrum.darkBlue
			gravity = Gravity.CENTER
			text = "MarketCap \n Volume"
		}
	}
	
	init {
		
		layoutParams = MarginLayoutParams(matchParent, 50.uiPX())
		backgroundColor = Spectrum.white
		orientation = LinearLayout.VERTICAL
		
		linearLayout {
			orientation = LinearLayout.HORIZONTAL
			layoutParams = LayoutParams(matchParent, matchParent)
			setMargins<LayoutParams> {
				leftMargin = 5.uiPX()
				rightMargin = 5.uiPX()
				topMargin = 10.uiPX()
			}
			addCorner(CornerSize.default.toInt(), Spectrum.white)
			setPadding(0, 0, 5.uiPX(), 0)
			
			addView(textViewIndex)
			addView(textViewSymbol)
			addView(textViewPrice)
			addView(textViewChange)
			addView(textViewCapValue)
		}
		
	}
	
}