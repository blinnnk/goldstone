package io.goldstone.blockchain.module.home.rammarket.module.ramtrade.presenter

import com.blinnnk.extension.isNull
import io.goldstone.blockchain.common.Language.EOSRAMExchangeText
import io.goldstone.blockchain.common.error.GoldStoneError
import io.goldstone.blockchain.common.sharedpreference.SharedAddress
import io.goldstone.blockchain.common.sharedpreference.SharedValue
import io.goldstone.blockchain.common.utils.alert
import io.goldstone.blockchain.crypto.eos.account.EOSAccount
import io.goldstone.blockchain.crypto.eos.base.EOSResponse
import io.goldstone.blockchain.crypto.utils.formatCount
import io.goldstone.blockchain.kernel.database.GoldStoneDataBase
import io.goldstone.blockchain.kernel.network.common.GoldStoneAPI
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

/**
 * 内存交易
 * 买内存：amount 单位是 EOS
 * 卖内存：amount 单位是 byte
 */
fun RAMMarketDetailPresenter.tradeRAM(
	amount: Double,
	stakeType: StakeType,
	callback: (response: EOSResponse?, GoldStoneError) -> Unit
) {
	val accountName = SharedAddress.getCurrentEOSAccount().accountName
	if (accountName == "default" || accountName.isEmpty()) {
		fragment.context?.alert(EOSRAMExchangeText.eosNoAccount)
		return
	}
	if (SharedValue.isTestEnvironment()) {
		fragment.context?.alert(EOSRAMExchangeText.testNetTradeDisableMessage)
		return
	}
	doAsync {
		val eosAcountTable = GoldStoneDataBase.database.eosAccountDao().getAccount(accountName)
		if (eosAcountTable != null) {
			eosAcountTable.let { localData ->
				if (stakeType == StakeType.BuyRam) {
					if (amount > localData.balance.toDouble()) {
						GoldStoneAPI.context.runOnUiThread {
							fragment.context.alert(EOSRAMExchangeText.noEnoughEOS)
						}
						return@doAsync
					}
				} else if (stakeType == StakeType.SellRam) {
					if (BigDecimal(amount).toBigInteger() > (localData.ramQuota - localData.ramUsed)) {
						GoldStoneAPI.context.runOnUiThread {
							fragment.context.alert(EOSRAMExchangeText.noEnoughRAM)
						}
						return@doAsync
					}
				}
				
				fragment.context?.apply {
					BaseTradingPresenter.tradingRam( this, EOSAccount(accountName), amount, stakeType, callback)
				}
			}
		} else {
			GoldStoneAPI.context.runOnUiThread {
				fragment.context.alert("数据库错误")
			}
		}
	}
	
}
















