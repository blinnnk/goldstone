package io.goldstone.blockchain.module.home.quotation.tradermemory.ramtrend.view

import android.content.Context
import android.view.Gravity
import android.view.ViewGroup
import android.widget.*
import com.blinnnk.extension.setMargins
import com.blinnnk.uikit.ScreenSize
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.Language.EOSRAMText
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.value.*
import org.jetbrains.anko.*

/**
 * @date: 2018/9/21.
 * @author: yanglihai
 * @description:
 */
class RAMInformationHeader(context: Context) : LinearLayout(context) {
	
	private val viewWidth = ScreenSize.Width - 20.uiPX()
	
	lateinit var currentPrice: TextView
	
	lateinit var trendcyPercent: TextView
	
	lateinit var startPrice: TextView
	
	lateinit var highPrice: TextView
	
	lateinit var lowPrice: TextView
	
	lateinit var ramMax: TextView
	
	lateinit var ramTotalReserved: TextView
	
	lateinit var ramPercent: TextView
	
	lateinit var percentProgressBar: ProgressBar
	
	init {
		orientation = LinearLayout.VERTICAL
		layoutParams = ViewGroup.LayoutParams(matchParent, wrapContent)
		padding = 10.uiPX()
		verticalLayout {
			textView {
				text = EOSRAMText.currentPrice
				textSize = fontSize(10)
				textColor = GrayScale.midGray
				typeface = GoldStoneFont.heavy(context)
			}
			linearLayout {
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
				startPrice = textView {
					text = EOSRAMText.openPrice("")
					textSize = fontSize(10)
					textColor = GrayScale.midGray
					typeface = GoldStoneFont.heavy(context)
					singleLine = true
				}.lparams(viewWidth/3, wrapContent)
				highPrice = textView {
					text = EOSRAMText.highPrice("")
					textSize = fontSize(10)
					textColor = GrayScale.midGray
					typeface = GoldStoneFont.heavy(context)
					singleLine = true
				}.lparams(viewWidth/3, wrapContent)
				lowPrice = textView {
					text = EOSRAMText.lowPrice("")
					textSize = fontSize(10)
					textColor = GrayScale.midGray
					typeface = GoldStoneFont.heavy(context)
					singleLine = true
				}.lparams(viewWidth/3, wrapContent)
			}.lparams(matchParent, wrapContent)
		}
		
		verticalLayout {
			topPadding = 10.uiPX()
			linearLayout {
				textView {
					text = EOSRAMText.ramUtilization
					textSize = fontSize(12)
					textColor = GrayScale.black
				}.lparams(viewWidth / 3, wrapContent)
				ramTotalReserved = textView {
					text = EOSRAMText.ramAccupyAmount("")
					textSize = fontSize(12)
					textColor = GrayScale.black
				}.lparams(viewWidth / 3, wrapContent)
				ramMax = textView {
					text = EOSRAMText.ramTotalAmount("")
					textSize = fontSize(12)
					textColor = GrayScale.black
				}.lparams(viewWidth / 3, wrapContent)
			}
			
			linearLayout {
				percentProgressBar = horizontalProgressBar {
					max = 100
				}.lparams(viewWidth - 80.uiPX(), wrapContent)
				ramPercent = textView {
					gravity = Gravity.CENTER
					text = "%"
				}.lparams(matchParent, wrapContent) {
					setMargins<LinearLayout.LayoutParams> { leftMargin = 10.uiPX() }
				}
			}
		}
		
	}
	
}