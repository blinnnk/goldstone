package io.goldstone.blockchain.module.home.rammarket.module.ramtrade.presenter

import com.blinnnk.extension.isNull
import io.goldstone.blockchain.kernel.network.common.GoldStoneAPI
import io.goldstone.blockchain.module.home.rammarket.presenter.RAMPMarketDetailPresenter
import org.jetbrains.anko.*

/**
 * @date: 2018/11/1.
 * @author: yanglihai
 * @description:
 */

fun RAMPMarketDetailPresenter.recentTransactions() {
	doAsync {
		GoldStoneAPI.getEOSRAMRecentTransactions { data, error ->
			if (!data.isNull() && error.isNone()) {
				recentTransactionModel = data
				GoldStoneAPI.context.runOnUiThread {
					recentTransactionModel?.apply {
						fragment.setTradingViewData(buyList, sellList)
					}
				}
			} else {
				GoldStoneAPI.context.runOnUiThread {
					fragment.context?.alert(error.message)
				}
			}
		}
	}
}