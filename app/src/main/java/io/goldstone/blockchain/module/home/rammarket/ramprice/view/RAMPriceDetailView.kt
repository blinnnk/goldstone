package io.goldstone.blockchain.module.home.rammarket.ramprice.view

import android.content.Context
import android.view.Gravity
import android.view.ViewGroup
import android.widget.*
import com.blinnnk.extension.setMargins
import com.blinnnk.uikit.ScreenSize
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.Language.EOSRAMText
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.value.GrayScale
import io.goldstone.blockchain.common.value.fontSize
import org.jetbrains.anko.*

/**
 * @date: 2018/9/21.
 * @author: yanglihai
 * @description: 头部价格信息详情
 */
class RAMPriceDetailView(context: Context) : LinearLayout(context) {
	
	private val viewWidth = ScreenSize.Width - 40.uiPX()
	lateinit var currentPrice: TextView
	lateinit var trendcyPercent: TextView
	lateinit var startPrice: TextView
	lateinit var highPrice: TextView
	lateinit var lowPrice: TextView
	
	init {
		orientation = LinearLayout.VERTICAL
		layoutParams = ViewGroup.LayoutParams(matchParent, wrapContent)
		leftPadding = 20.uiPX()
		rightPadding = 20.uiPX()
		topPadding = 20.uiPX()
		verticalLayout {
			textView {
				text = EOSRAMText.currentPrice
				textSize = fontSize(10)
				textColor = GrayScale.midGray
				typeface = GoldStoneFont.heavy(context)
			}
			linearLayout {
				topPadding = 13.uiPX()
				currentPrice = textView {
					text = ""
					textSize = fontSize(26)
					typeface = GoldStoneFont.black(context)
					textColor  = GrayScale.black
					singleLine = true
				}
				textView {
					leftPadding = 10.uiPX()
					text = "EOS/KB"
					textSize = fontSize(10)
					textColor = GrayScale.black
					typeface = GoldStoneFont.book(context)
				}
				trendcyPercent = textView {
					gravity = Gravity.RIGHT
					textSize = fontSize(13)
					typeface = GoldStoneFont.heavy(context)
					text = ""
					singleLine = true
				}.lparams(matchParent, wrapContent) {
					gravity = Gravity.CENTER_VERTICAL
				}
			}.lparams(matchParent, wrapContent)
			
			linearLayout {
				topPadding = 16.uiPX()
				bottomPadding = 16.uiPX()
				startPrice = textView {
					gravity = Gravity.LEFT
					text = EOSRAMText.openPrice("")
					textSize = fontSize(10)
					textColor = GrayScale.midGray
					typeface = GoldStoneFont.heavy(context)
					singleLine = true
				}.lparams(viewWidth/3, wrapContent)
				highPrice = textView {
					gravity = Gravity.CENTER
					text = EOSRAMText.highPrice("")
					textSize = fontSize(10)
					textColor = GrayScale.midGray
					typeface = GoldStoneFont.heavy(context)
					singleLine = true
				}.lparams(viewWidth/3, wrapContent)
				lowPrice = textView {
					gravity = Gravity.RIGHT
					text = EOSRAMText.lowPrice("")
					textSize = fontSize(10)
					textColor = GrayScale.midGray
					typeface = GoldStoneFont.heavy(context)
					singleLine = true
				}.lparams(viewWidth/3, wrapContent)
			}.lparams(matchParent, wrapContent)
			
			view {
				backgroundColor = GrayScale.midGray
				layoutParams = LinearLayout.LayoutParams(matchParent, 1.uiPX())
			}
		}
		
	}
	
}