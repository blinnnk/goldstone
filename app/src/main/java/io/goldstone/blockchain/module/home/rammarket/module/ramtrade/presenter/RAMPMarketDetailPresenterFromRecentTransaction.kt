package io.goldstone.blockchain.module.home.rammarket.module.ramtrade.presenter

import android.content.Context
import android.support.annotation.WorkerThread
import com.blinnnk.extension.*
import io.goldstone.blockchain.common.Language.EOSRAMExchangeText
import io.goldstone.blockchain.common.error.GoldStoneError
import io.goldstone.blockchain.crypto.eos.base.EOSResponse
import io.goldstone.blockchain.crypto.utils.formatCount
import io.goldstone.blockchain.kernel.database.GoldStoneDataBase
import io.goldstone.blockchain.kernel.network.common.GoldStoneAPI
import io.goldstone.blockchain.kernel.network.eos.EOSAPI
import io.goldstone.blockchain.module.common.tokendetail.eosresourcetrading.common.basetradingfragment.presenter.BaseTradingPresenter
import io.goldstone.blockchain.module.common.tokendetail.eosresourcetrading.common.basetradingfragment.view.StakeType
import io.goldstone.blockchain.module.home.rammarket.module.ramtrade.model.TradingInfoModel
import io.goldstone.blockchain.module.home.rammarket.presenter.RAMMarketDetailPresenter
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.runOnUiThread
import java.math.BigDecimal

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
						ramMarketDetailView.setTradingViewData(buyList, sellList)
					}
				}
			} else {
				GoldStoneAPI.context.runOnUiThread {
					ramMarketDetailView.showError(error)
				}
			}
		}
	}
}


fun RAMMarketDetailPresenter.setAcountInfoFromDatabase() {
	doAsync {
		GoldStoneDataBase.database.eosAccountDao().getAccount(currentAccount.name, currentChainID)?.let { localData ->
			val ramBalance = ((localData.ramQuota -localData. ramUsed).toDouble() / 1024.0).formatCount(4)
			GoldStoneAPI.context.runOnUiThread {
				ramMarketDetailView.setRAMBalance(ramBalance, if (localData.balance.isEmpty()) "0.0" else localData.balance)
			}
		}
	}
}

/**
 * 买交易
 * 买内存：amount 单位是 EOS
 */
fun RAMMarketDetailPresenter.buyRAM(
	context: Context,
	amount: Double,
	callback: (response: EOSResponse?, GoldStoneError) -> Unit
) {
	doAsync {
		val eosAcountTable = GoldStoneDataBase.database.eosAccountDao().getAccount(currentAccount.name, currentChainID)
		if (eosAcountTable != null) {
			eosAcountTable.let { localData ->
				if (amount > localData.balance.toDoubleOrZero()) {
					GoldStoneAPI.context.runOnUiThread {
						callback(null, GoldStoneError(EOSRAMExchangeText.noEnoughEOS))
					}
					return@doAsync
				}
				BaseTradingPresenter.buyRam( context, currentAccount, amount, callback)
			}
		} else {
			GoldStoneAPI.context.runOnUiThread {
				callback(null, GoldStoneError("数据库错误"))
			}
		}
	}
}

/**
 * 卖交易
 * 卖内存：amount 单位是 byte
 */
fun RAMMarketDetailPresenter.sellRAM(
	context: Context,
	amount: Long,
	callback: (response: EOSResponse?, GoldStoneError) -> Unit
) {
	
	doAsync {
		val eosAcountTable = GoldStoneDataBase.database.eosAccountDao().getAccount(currentAccount.name, currentChainID)
		if (eosAcountTable != null) {
			eosAcountTable.let { localData ->
				if (BigDecimal(amount).toBigInteger() > (localData.ramQuota - localData.ramUsed)) {
					GoldStoneAPI.context.runOnUiThread {
						callback(null, GoldStoneError(EOSRAMExchangeText.noEnoughEOS))
					}
					return@doAsync
				}
				BaseTradingPresenter.sellRAM(context, amount, callback)
			}
		} else {
			GoldStoneAPI.context.runOnUiThread {
				callback(null, GoldStoneError("数据库错误"))
			}
		}
	}
}

/**
 * 买卖内存
 */
fun RAMMarketDetailPresenter.tradeRAM(
	context: Context,
	amount: Double,
	staketype: StakeType,
	callback: (response: EOSResponse?, GoldStoneError) -> Unit
) {
	
	if (!currentAccount.isValid()) {
		callback(null, GoldStoneError(EOSRAMExchangeText.eosNoAccount))
		return
	}
	if (isTestEnvironment) {
		callback(null, GoldStoneError(EOSRAMExchangeText.testNetTradeDisableMessage))
		return
	}
	
	if (staketype.isSellRam()) {
		sellRAM(context, amount.toLong(), callback)
	} else {
		buyRAM(context, amount, callback)
	}
}

fun RAMMarketDetailPresenter.updateAccountData(@WorkerThread callback: () -> Unit) {
	EOSAPI.getAccountInfo(currentAccount) { newData, error ->
		if (newData.isNotNull() && error.isNone()) {
			// 新数据标记为老数据的 `主键` 值
			GoldStoneDataBase.database.eosAccountDao().update(newData)
			callback()
		}
	}
}
















