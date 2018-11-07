package io.goldstone.blockchain.module.home.rammarket.module.ramtrade.view

import android.content.Context
import android.widget.LinearLayout
import com.blinnnk.extension.into
import com.blinnnk.extension.setMargins
import com.blinnnk.uikit.ScreenSize
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.value.GrayScale
import org.jetbrains.anko.*

/**
 * @date: 2018/10/31.
 * @author: yanglihai
 * @description:
 */
class TradingView(context: Context): LinearLayout(context) {
	val tradingDashboardView by lazy { TradingDashboardView(context) }
	val recentTradingListView  by lazy { RecentTradingListView(context) }
	
	init {
		orientation = LinearLayout.VERTICAL
		leftPadding = 20.uiPX()
		rightPadding = 20.uiPX()
		view {
			layoutParams = LinearLayout.LayoutParams(matchParent, 1.uiPX())
			backgroundColor = GrayScale.lightGray
			setMargins<LinearLayout.LayoutParams> {
				topMargin = 16.uiPX()
				bottomMargin = 16.uiPX()
				
			}
		}
	  linearLayout {
			tradingDashboardView.apply {
				layoutParams = LinearLayout.LayoutParams(ScreenSize.Width / 2, wrapContent)
			}.into(this)
			recentTradingListView.apply {
				layoutParams = LinearLayout.LayoutParams(matchParent, 332.uiPX())
				setMargins<LinearLayout.LayoutParams> {
					topMargin = 6.uiPX()
					leftMargin = 15.uiPX()
				}
			}.into(this)
		}
	}
}