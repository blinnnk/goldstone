package io.goldstone.blockchain.module.home.rammarket.model

import io.goldstone.blockchain.module.home.rammarket.module.ramquotation.ramoccupyrank.model.RAMRankModel
import io.goldstone.blockchain.module.home.rammarket.module.ramtrade.model.TradingInfoModel

/**
 * @date: 2018/10/30.
 * @author: yanglihai
 * @description: 价格市场的信息，有一部分缓存在内存中
 */
object RAMTradeRoomData {
	var ramInformationModel: RAMInformationModel? = null
	var bigOrderList: ArrayList<TradingInfoModel>? = null
	var ramRankList: ArrayList<RAMRankModel>? = null
}