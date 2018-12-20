package io.goldstone.blockchain.module.home.rammarket.presenter

import com.blinnnk.extension.*
import io.goldstone.blockchain.common.Language.EOSRAMExchangeText
import io.goldstone.blockchain.common.sharedpreference.*
import io.goldstone.blockchain.common.thread.launchUI
import io.goldstone.blockchain.common.utils.GoldStoneWebSocket
import io.goldstone.blockchain.common.value.GrayScale
import io.goldstone.blockchain.module.home.quotation.markettokendetail.model.CandleChartModel
import io.goldstone.blockchain.module.home.rammarket.contract.RAMMarketDetailContract
import io.goldstone.blockchain.module.home.rammarket.model.EOSRAMChartType
import io.goldstone.blockchain.module.home.rammarket.model.RAMInformationModel
import io.goldstone.blockchain.module.home.rammarket.module.ramprice.presenter.*
import io.goldstone.blockchain.module.home.rammarket.module.ramtrade.model.RecentTransactionModel
import io.goldstone.blockchain.module.home.rammarket.module.ramtrade.model.TradingInfoModel
import io.goldstone.blockchain.module.home.rammarket.module.ramtrade.presenter.recentTransactions
import io.goldstone.blockchain.module.home.rammarket.module.ramtrade.presenter.setAccountInfoFromDatabase
import org.json.JSONObject
import java.math.BigDecimal

/**
 * @date: 2018/10/29.
 * @author: yanglihai
 * @description: 头部的price展示presenter
 */
class RAMMarketDetailPresenter(val gsView: RAMMarketDetailContract.GSView)
 : RAMMarketDetailContract.GSPresenter {
	
	val currentAccount = SharedAddress.getCurrentEOSAccount()
	val currentChainID = SharedChain.getEOSCurrent().chainID.id
	val isTestEnvironment = SharedValue.isTestEnvironment()
	val currentTransactionLimitSize = 5 // 最多只展示五个
	
	override fun start() {
		setAccountInfoFromDatabase()
		getTodayPrice()
		updateRAMCandleData(EOSRAMChartType.Minute)
		recentTransactions()
		if (isTestEnvironment) {
			gsView.showRAMExchangeTips(EOSRAMExchangeText.ramTradeOnlyMainNet)
		}
	}
	
	var candleDataMap: HashMap<String, ArrayList<CandleChartModel>> = hashMapOf()
	var recentTransactionModel: RecentTransactionModel? = null
	var ramInformationModel: RAMInformationModel = RAMInformationModel()
	private var ramPriceSocket: GoldStoneWebSocket? = null
	private fun getPriceSocket(): GoldStoneWebSocket {
		return object : GoldStoneWebSocket("{\"t\": \"unsub_eos_ram_service\"}") {
			override fun onOpened() {
				sendMessage("{\"t\":\"sub_eos_ram_service\"}")
			}
			
			override fun getServerBack(content: JSONObject, isDisconnected: Boolean) {
				if (isDisconnected) {
					gsView.showSocketDisconnectedPercentColor(GrayScale.midGray)
					return
				}
				parseSocketResult(content)
			}
		}
	}
	
	private fun parseSocketResult(content: JSONObject) {
		val type = content.safeGet("t")
		if (type == "eos_ram_price") {
			// 返回的价格信息
			val price = content.getString("price")
			val current = BigDecimal.valueOf(price.toDoubleOrNull().orElse(0.0)).divide(BigDecimal(1), 8, BigDecimal.ROUND_HALF_UP)
			ramInformationModel.currentPrice = current.toDouble()
			launchUI {
				updateCurrentPriceUI()
			}
		} else if (type == "eos_ram_new_tx") {
			val model = TradingInfoModel(content)
			recentTransactionModel?.apply {
				if (model.type == 0) {
					sellList.removeAt(0)
					sellList.add(model)
				} else {
					buyList.removeAt(0)
					buyList.add(model)
				}
				launchUI {
					gsView.notifyTradingViewData()
				}
			}
			
		}
	}
	
	fun onFragmentResume() {
		if (ramPriceSocket == null) {
			ramPriceSocket = getPriceSocket().apply {
				runSocket()
			}
		} else {
			ramPriceSocket!!.isSocketConnected(). isFalse  {
				ramPriceSocket!!.runSocket()
			}
		}
	}
	
	fun onFragmentPause() {
		ramPriceSocket?.apply {
			isSocketConnected() isTrue {
				closeSocket()
			}
		}
		ramPriceSocket = null
	}
	
}