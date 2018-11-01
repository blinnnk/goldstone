package io.goldstone.blockchain.module.home.rammarket.module.ramprice.view

import android.content.Context
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import com.blinnnk.uikit.ScreenSize
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.Language.EOSRAMExchangeText
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.value.GrayScale
import io.goldstone.blockchain.common.value.fontSize
import org.jetbrains.anko.*

/**
 * @date: 2018/10/30.
 * @author: yanglihai
 * @description:
 */
class RAMTodayPriceView(context: Context): LinearLayout(context) {
	private val viewWidth = ScreenSize.Width - 40.uiPX()
	lateinit var startPrice: TextView
	lateinit var highPrice: TextView
	lateinit var lowPrice: TextView
	
	init {
		orientation = LinearLayout.VERTICAL
		layoutParams = LayoutParams(viewWidth, wrapContent)
		linearLayout {
			topPadding = 16.uiPX()
			bottomPadding = 16.uiPX()
			startPrice = textView {
				gravity = Gravity.LEFT
				text = EOSRAMExchangeText.openPrice("")
				textSize = fontSize(10)
				textColor = GrayScale.midGray
				typeface = GoldStoneFont.heavy(context)
				singleLine = true
			}.lparams(viewWidth/3, wrapContent)
			highPrice = textView {
				gravity = Gravity.CENTER
				text = EOSRAMExchangeText.highPrice("")
				textSize = fontSize(10)
				textColor = GrayScale.midGray
				typeface = GoldStoneFont.heavy(context)
				singleLine = true
			}.lparams(viewWidth/3, wrapContent)
			lowPrice = textView {
				gravity = Gravity.RIGHT
				text = EOSRAMExchangeText.lowPrice("")
				textSize = fontSize(10)
				textColor = GrayScale.midGray
				typeface = GoldStoneFont.heavy(context)
				singleLine = true
			}.lparams(viewWidth/3, wrapContent)
		}
		
		view {
			backgroundColor = GrayScale.lightGray
			layoutParams = LinearLayout.LayoutParams(matchParent, 1.uiPX())
		}
	}
}