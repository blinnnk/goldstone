package io.goldstone.blockchain.module.home.rammarket.contract

import io.goldstone.blockchain.module.common.contract.GoldStonePresenter
import io.goldstone.blockchain.module.common.contract.GoldStoneView
import io.goldstone.blockchain.module.home.quotation.markettokendetail.model.CandleChartModel
import io.goldstone.blockchain.module.home.rammarket.module.ramtrade.model.TradingInfoModel

/**
 * @date: 2018-11-20.
 * @author: yangLiHai
 * @description:
 */
interface RAMMarketDetailContract {
	interface GSView : GoldStoneView<GSPresenter> {
		fun setCurrentPriceAndPercent(price: Double, percent: Double)
		fun setTodayPrice(startPrice: String, highPrice: String, lowPrice: String)
		fun setSocketDisconnectedPercentColor(color: Int)
		fun updateCandleChartUI(dateType: Int, data: ArrayList<CandleChartModel>)
		fun setTradingViewData(buyList: List<TradingInfoModel>, sellList: List<TradingInfoModel>)
		fun notifyTradingViewData()
		fun setRAMBalance(ramBalance: String, eosBalance: String)
	}
	
	interface GSPresenter : GoldStonePresenter {
	}
}