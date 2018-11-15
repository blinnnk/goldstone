package io.goldstone.blockchain.module.home.rammarket.module.ramtrade.view

import android.content.Context
import android.widget.LinearLayout
import com.blinnnk.extension.*
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.value.GrayScale
import io.goldstone.blockchain.module.home.rammarket.model.RAMMarketPadding
import io.goldstone.blockchain.module.home.rammarket.module.ramtrade.presenter.tradeRAM
import io.goldstone.blockchain.module.home.rammarket.view.RAMMarketDetailFragment
import io.goldstone.blockchain.module.home.rammarket.view.RAMMarketOverlayFragment
import org.jetbrains.anko.*

/**
 * @date: 2018/10/31.
 * @author: yanglihai
 * @description:
 */
class TradingView(context: Context, val fragment: RAMMarketDetailFragment): LinearLayout(context) {
	val tradingDashboardView by lazy {
		TradingDashboardView(context).apply {
			setShowHistoryEvent(Runnable {
				fragment.getParentFragment<RAMMarketOverlayFragment> {
					presenter.showTransactionHistoryFragment()
				}
			})
			
			setConfirmEvent(Runnable {
				fragment.presenter.tradeRAM()
			})
		}
	}
	val recentTradingListView  by lazy {
		RecentTradingListView(context) {
			fragment.getParentFragment<RAMMarketOverlayFragment> {
				presenter.showTransactionHistoryFragment(account)
			}
		}
	}
	
	init {
		orientation = LinearLayout.VERTICAL
		rightPadding = RAMMarketPadding
		view {
			layoutParams = LinearLayout.LayoutParams(matchParent, 1)
			backgroundColor = GrayScale.lightGray
			setMargins<LinearLayout.LayoutParams> {
				topMargin = 10.uiPX()
				leftMargin = RAMMarketPadding
			}
		}
	  linearLayout {
			tradingDashboardView.into(this)
			recentTradingListView.apply {
				layoutParams = LinearLayout.LayoutParams(matchParent, 290.uiPX())
				setMargins<LinearLayout.LayoutParams> {
					topMargin = 7.uiPX()
					leftMargin = 10.uiPX()
				}
			}.into(this)
		}
	}
}