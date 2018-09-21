package io.goldstone.blockchain.module.home.quotation.tradermemory.ramtrend.view

import android.content.Context
import android.view.Gravity
import android.view.ViewGroup
import android.widget.*
import com.blinnnk.extension.setMargins
import com.blinnnk.uikit.ScreenSize
import com.blinnnk.uikit.uiPX
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
		linearLayout {
			verticalLayout {
				textView {
					text = "当前价"
					textSize = fontSize(12)
					textColor = GrayScale.black
				}
				linearLayout {
					currentPrice = textView {
						text = "当前价"
						textSize = fontSize(18)
						textColor = Spectrum.darkBlue
						singleLine = true
					}
					trendcyPercent = textView {
						leftPadding = 10.uiPX()
						text = "涨幅"
						singleLine = true
					}
				}
				textView {
					text = "EOS/KB"
					textSize = fontSize(12)
					textColor = GrayScale.black
				}
			}.lparams(viewWidth / 5 * 3, wrapContent)
			
			verticalLayout {
				startPrice = textView {
					text = "开盘价"
					textSize = fontSize(12)
					textColor = GrayScale.black
					singleLine = true
				}
				highPrice = textView {
					text = "最高"
					textSize = fontSize(12)
					textColor = GrayScale.black
					singleLine = true
				}
				lowPrice = textView {
					text = "最低"
					textSize = fontSize(12)
					textColor = GrayScale.black
					singleLine = true
				}
			}.lparams(matchParent, wrapContent)
		}
		
		verticalLayout {
			topPadding = 10.uiPX()
			linearLayout {
				textView {
					text = "RAM内存占用率"
					textSize = fontSize(12)
					textColor = GrayScale.black
				}.lparams(viewWidth / 3, wrapContent)
				ramTotalReserved = textView {
					text = "占用/GB"
					textSize = fontSize(12)
					textColor = GrayScale.black
				}.lparams(viewWidth / 3, wrapContent)
				ramMax = textView {
					text = "总量/GB"
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