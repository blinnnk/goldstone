package io.goldstone.blockchain.module.home.quotation.tradermemory.ramtrend.view

import android.content.Context
import android.view.ViewGroup
import android.widget.*
import org.jetbrains.anko.*

/**
 * @date: 2018/9/21.
 * @author: yanglihai
 * @description:
 */
class RAMInformationHeader(context: Context) : LinearLayout(context) {
	
	lateinit var currentPrice: TextView
	
	lateinit var trendcyPercent: TextView
	
	lateinit var startPrice: TextView
	
	lateinit var highPrice : TextView
	
	lateinit var lowPrice : TextView
	
	init {
		orientation = LinearLayout.VERTICAL
		layoutParams = ViewGroup.LayoutParams(matchParent, wrapContent)
		linearLayout {
			verticalLayout {
				textView {
					text = "当前价"
				}
				linearLayout {
					currentPrice = textView { text = "当前价"}
					trendcyPercent = textView { text = "涨幅"}
				}
				textView {
					text = "EOS/KB"
				}
			}
			
			verticalLayout {
				startPrice = textView { text = "开盘价"}
				highPrice = textView { text = "最高"}
				lowPrice = textView { text = "最低"}
			}
		}
		
		verticalLayout {
			linearLayout {
				textView { text = "RAM内存占用率" }
				textView { text = "占用23GB"}
				textView { text = "总量56GB" }
			}
			
			linearLayout {
				horizontalProgressBar {  }
				textView { text = "100%" }
			}
		}
		
	}
	
}