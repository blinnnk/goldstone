package io.goldstone.blockchain.module.home.rammarket.module.ramtrade.model

import com.blinnnk.extension.safeGet
import com.google.gson.annotations.SerializedName
import org.json.JSONObject

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
) {
	constructor(jsonObject: JSONObject): this(
		jsonObject.safeGet("account"),
		jsonObject.safeGet("price").toDouble(),
		jsonObject.safeGet("tx_id"),
		jsonObject.safeGet("time").toLong(),
		jsonObject.safeGet("type").toInt(),
		jsonObject.safeGet("quantity").toDouble()
	)
}