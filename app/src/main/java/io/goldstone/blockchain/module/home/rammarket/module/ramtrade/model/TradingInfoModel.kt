package io.goldstone.blockchain.module.home.rammarket.module.ramtrade.model

import com.google.gson.annotations.SerializedName

/**
 * @date: 2018/11/1.
 * @author: yanglihai
 * @description: 买入卖出的model
 */
class TradingInfoModel(
	val account: String,
	val price: Double,
	@SerializedName("tx_id")
	val txID: String,
	val time: Long,
	val type: Int,
	val  quantity: Double
) {}