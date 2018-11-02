package io.goldstone.blockchain.module.home.rammarket.module.ramtrade.presenter

import com.blinnnk.extension.isNull
import io.goldstone.blockchain.common.sharedpreference.SharedAddress
import io.goldstone.blockchain.crypto.utils.formatCount
import io.goldstone.blockchain.kernel.network.common.GoldStoneAPI
import io.goldstone.blockchain.module.common.tokendetail.eosactivation.accountselection.model.EOSAccountTable
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


fun RAMPMarketDetailPresenter.setAcountInfoFromDatabase() {
	val account = SharedAddress.getCurrentEOSAccount()
	EOSAccountTable.getAccountByName(account.accountName) { localData ->
		localData?.apply {
			val ramBalance = ((ramQuota - ramUsed).toDouble() / 1024.toDouble()).formatCount(4)
			GoldStoneAPI.context.runOnUiThread {
				fragment.setRAMBalance(ramBalance, if (balance.isEmpty()) "0.0" else balance)
			}
		}
	}
}

