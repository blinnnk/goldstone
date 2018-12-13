package io.goldstone.blockchain.module.home.quotation.rank.view

import android.content.Context
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.blinnnk.uikit.ScreenSize
import com.blinnnk.uikit.uiPX
import org.jetbrains.anko.*

/**
 * @date: 2018-12-12.
 * @author: yangLiHai
 * @description:
 */
class CoinRankHeader(context: Context): LinearLayout(context) {
	private val marketCap = TextView(context)
	private val volume24h = TextView(context)
	private val btcDominance = TextView(context)
	
	init {
		layoutParams = ViewGroup.LayoutParams(matchParent, 100.uiPX())
		
	  verticalLayout {
			gravity = Gravity.CENTER
			layoutParams = LayoutParams(ScreenSize.Width / 3, matchParent)
			textView {
				text = "market cap"
			}
			addView(marketCap)
			marketCap.text = "marketCap"
		}
		
		verticalLayout {
			gravity = Gravity.CENTER
			layoutParams = LayoutParams(ScreenSize.Width / 3, matchParent)
			textView {
				text = "volume 24h"
			}
			addView(volume24h)
			volume24h.text = "valume"
		}
		
		verticalLayout {
			gravity = Gravity.CENTER
			layoutParams = LayoutParams(ScreenSize.Width / 3, matchParent)
			textView {
				text = "btc dominance"
			}
			addView(btcDominance)
			btcDominance.text = "btcDominance"
		}
		
		
	}
	
}