package io.goldstone.blockchain.module.home.rammarket.module.ramtrade.presenter

import android.content.Context
import android.support.annotation.WorkerThread
import com.blinnnk.extension.*
import io.goldstone.blockchain.common.Language.EOSRAMExchangeText
import io.goldstone.blockchain.common.error.GoldStoneError
import io.goldstone.blockchain.common.thread.launchUI
import io.goldstone.blockchain.crypto.eos.base.EOSResponse
import io.goldstone.blockchain.crypto.utils.formatCount
import io.goldstone.blockchain.kernel.network.common.GoldStoneAPI
import io.goldstone.blockchain.kernel.network.eos.EOSAPI
import io.goldstone.blockchain.module.common.tokendetail.eosactivation.accountselection.model.EOSAccountTable
import io.goldstone.blockchain.module.common.tokendetail.eosresourcetrading.common.basetradingfragment.presenter.BaseTradingPresenter
import io.goldstone.blockchain.module.common.tokendetail.eosresourcetrading.common.basetradingfragment.view.StakeType
import io.goldstone.blockchain.module.home.rammarket.module.ramtrade.model.TradingInfoModel
import io.goldstone.blockchain.module.home.rammarket.presenter.RAMMarketDetailPresenter
import kotlinx.coroutines.*

/**
 * @date: 2018/11/1.
 * @author: yanglihai
 * @description:
 */
fun RAMMarketDetailPresenter.recentTransactions() {
	GlobalScope.launch(Dispatchers.Default) {
		GoldStoneAPI.getEOSRAMRecentTransactions { data, error ->
			if (!data.isNull() && error.isNone()) {
				recentTransactionModel = data
				launchUI {
					recentTransactionModel?.apply {
						if (buyList.size > currentTransactionLimitSize)  {
							val tempList = arrayListOf<TradingInfoModel>().apply {
								addAll(buyList.subList(0, 5))
							}
							buyList.clear()
							buyList.addAll(tempList)
						}
						if (sellList.size > currentTransactionLimitSize)  {
							val tempList = arrayListOf<TradingInfoModel>().apply {
								addAll(sellList.subList(0, 5))
							}
							sellList.clear()
							sellList.addAll(tempList)
						}
						ramMarketDetailView.showTradingViewData(buyList, sellList)
					}
				}
			} else {
				launchUI {
					ramMarketDetailView.showError(error)
				}
			}
		}
	}
}


fun RAMMarketDetailPresenter.setAcountInfoFromDatabase() {
	GlobalScope.launch(Dispatchers.Default) {
		EOSAccountTable.dao.getAccount(currentAccount.name, currentChainID)?.let { localData ->
			val ramBalance = ((localData.ramQuota -localData. ramUsed).toDouble() / 1024.0).formatCount(4)
			launchUI {
				ramMarketDetailView.showRAMBalance(ramBalance, if (localData.balance.isEmpty()) "0.0" else localData.balance)
			}
		}
	}
}

/**
 * 买内存：amount 单位是 EOS
 * 卖内存：amount 单位是 byte
 */
fun RAMMarketDetailPresenter.tradeRAM(
	context: Context,
	amount: Double,
	stakeType: StakeType,
	callback: (response: EOSResponse?, GoldStoneError) -> Unit
) {
	if (isTestEnvironment) {
		callback(null, GoldStoneError(EOSRAMExchangeText.testNetTradeDisableMessage))
		return
	}
	
	GlobalScope.launch(Dispatchers.Default) {
		if (stakeType.isSellRam()) {
			BaseTradingPresenter.sellRAM(context, amount.toLong(), callback)
		} else {
			BaseTradingPresenter.buyRam( context, currentAccount, amount, callback)
		}
	}
	
}

fun RAMMarketDetailPresenter.updateAccountData(@WorkerThread callback: () -> Unit) {
	EOSAPI.getAccountInfo(currentAccount) { newData, error ->
		if (newData.isNotNull() && error.isNone()) {
			// 新数据标记为老数据的 `主键` 值
			EOSAccountTable.dao.update(newData)
			callback()
		}
	}
}
















