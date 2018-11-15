package io.goldstone.blockchain.module.home.rammarket.module.ramtrade.presenter

import com.blinnnk.extension.isNull
import io.goldstone.blockchain.common.Language.EOSRAMExchangeText
import io.goldstone.blockchain.common.sharedpreference.*
import io.goldstone.blockchain.crypto.utils.formatCount
import io.goldstone.blockchain.kernel.database.GoldStoneDataBase
import io.goldstone.blockchain.kernel.network.common.GoldStoneAPI
import io.goldstone.blockchain.module.common.tokendetail.eosactivation.accountselection.model.EOSAccountTable
import io.goldstone.blockchain.module.home.rammarket.module.ramtrade.model.TradingInfoModel
import io.goldstone.blockchain.module.home.rammarket.presenter.RAMMarketDetailPresenter
import org.jetbrains.anko.*

/**
 * @date: 2018/11/1.
 * @author: yanglihai
 * @description:
 */
fun RAMMarketDetailPresenter.recentTransactions() {
	doAsync {
		GoldStoneAPI.getEOSRAMRecentTransactions { data, error ->
			if (!data.isNull() && error.isNone()) {
				recentTransactionModel = data
				GoldStoneAPI.context.runOnUiThread {
					recentTransactionModel?.apply {
						if (buyList.size > 5)  {
							val tempList = arrayListOf<TradingInfoModel>().apply {
								addAll(buyList.subList(0, 5))
							}
							buyList.clear()
							buyList.addAll(tempList)
						}
						if (sellList.size > 5)  {
							val tempList = arrayListOf<TradingInfoModel>().apply {
								addAll(sellList.subList(0, 5))
							}
							sellList.clear()
							sellList.addAll(tempList)
						}
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


fun RAMMarketDetailPresenter.setAcountInfoFromDatabase() {
	val account = SharedAddress.getCurrentEOSAccount()
	doAsync {
		GoldStoneDataBase.database.eosAccountDao().getAccount(account.accountName)?.let { localData ->
			val ramBalance = ((localData.ramQuota -localData. ramUsed).toDouble() / 1024.0).formatCount(4)
			GoldStoneAPI.context.runOnUiThread {
				fragment.setRAMBalance(ramBalance, if (localData.balance.isEmpty()) "0.0" else localData.balance)
			}
		}
	}
}

fun RAMMarketDetailPresenter.tradeRAM() {
	if (SharedAddress.getCurrentEOSAccount().accountName.equals("default")) {
		fragment.context?.alert(EOSRAMExchangeText.eosNoAccount)
		return
	}
	if (SharedValue.isTestEnvironment()) {
		fragment.context?.alert(EOSRAMExchangeText.testNetTradeDisableMessage)
		return
	}
	
	
}
















