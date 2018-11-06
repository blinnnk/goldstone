package io.goldstone.blockchain.module.home.rammarket.presenter

import com.blinnnk.extension.*
import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.common.utils.GoldStoneWebSocket
import io.goldstone.blockchain.common.utils.isEmptyThen
import io.goldstone.blockchain.common.value.GrayScale
import io.goldstone.blockchain.kernel.network.common.GoldStoneAPI
import io.goldstone.blockchain.module.home.quotation.markettokendetail.model.CandleChartModel
import io.goldstone.blockchain.module.home.rammarket.model.*
import io.goldstone.blockchain.module.home.rammarket.module.ramprice.presenter.*
import io.goldstone.blockchain.module.home.rammarket.module.ramtrade.model.RecentTransactionModel
import io.goldstone.blockchain.module.home.rammarket.module.ramtrade.model.TradingInfoModel
import io.goldstone.blockchain.module.home.rammarket.module.ramtrade.presenter.recentTransactions
import io.goldstone.blockchain.module.home.rammarket.module.ramtrade.presenter.setAcountInfoFromDatabase
import io.goldstone.blockchain.module.home.rammarket.view.RAMMarketDetailFragment
import org.jetbrains.anko.runOnUiThread
import org.json.JSONObject
import java.math.BigDecimal

/**
 * @date: 2018/10/29.
 * @author: yanglihai
 * @description: 头部的price展示presenter
 */
class RAMMarketDetailPresenter(override val fragment: RAMMarketDetailFragment)
 : BasePresenter<RAMMarketDetailFragment>() {
	var candleDataMap: HashMap<String, ArrayList<CandleChartModel>> = hashMapOf()
	var recentTransactionModel: RecentTransactionModel? = null
	var ramInformationModel: RAMInformationModel = RAMInformationModel()
	private val ramPriceSocket by lazy {
		object : GoldStoneWebSocket("{\"t\": \"unsub_eos_ram_service\"}") {
			override fun onOpened() {
				sendMessage("{\"t\":\"sub_eos_ram_service\"}")
			}
			
			override fun getServerBack(content: JSONObject, isDisconnected: Boolean) {
				if (isDisconnected) {
					fragment.setSocketDisconnectedPercentColor(GrayScale.midGray)
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
			GoldStoneAPI.context.runOnUiThread {
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
				GoldStoneAPI.context.runOnUiThread {
					fragment.notifyTradingViewData()
				}
			}
			
		}
	}
	
	override fun onFragmentCreate() {
		super.onFragmentCreate()
		fragment.context?.apply {
			setAcountInfoFromDatabase()
		}
	}
	
	override fun onFragmentCreateView() {
		super.onFragmentCreateView()
		getTodayPrice()
		updateRAMCandleData(EOSRAMChartType.Minute)
		recentTransactions()
	}
	
	override fun onFragmentResume() {
		super.onFragmentResume()
		ramPriceSocket.isSocketConnected() isFalse {
			ramPriceSocket.runSocket()
		}
	}
	
	override fun onFragmentPause() {
		super.onFragmentPause()
		ramPriceSocket.isSocketConnected() isTrue {
			ramPriceSocket.closeSocket()
		}
	}
	
	override fun onFragmentDestroy() {
		super.onFragmentDestroy()
		saveCandleDataToDatabase()
	}
	
	
	
	
}