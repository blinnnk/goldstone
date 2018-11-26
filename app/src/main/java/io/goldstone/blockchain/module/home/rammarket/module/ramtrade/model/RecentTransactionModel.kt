package io.goldstone.blockchain.module.home.rammarket.module.ramtrade.model

import com.google.gson.annotations.SerializedName

/**
 * @date: 2018/11/1.
 * @author: yanglihai
 * @description:
 */
class RecentTransactionModel(
	val price: String,
	@SerializedName("sell_list")
	val sellList: ArrayList<TradingInfoModel>,
	@SerializedName("buy_list")
	val buyList: ArrayList<TradingInfoModel>
)