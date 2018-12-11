package io.goldstone.blockchain.module.home.rammarket.module.ramtrade.model

import com.blinnnk.extension.*
import org.json.JSONObject

/**
 * @date: 2018/11/1.
 * @author: yanglihai
 * @description: 买入卖出的model
 */
class TradingInfoModel(
	var account: String,
	val price: Double,
	val time: Long,
	val type: Int,
	val quantity: Double,
	val id: Int // 用于分页
) {
	constructor(jsonObject: JSONObject) : this(
		jsonObject.safeGet("account"),
		jsonObject.safeGet("price").toDoubleOrZero(),
		jsonObject.safeGet("time").toLongOrZero(),
		jsonObject.safeGet("type").toIntOrZero(),
		jsonObject.safeGet("quantity").toDoubleOrZero(),
		jsonObject.safeGet("id").toIntOrZero()
	)
	
	constructor() : this(
		"",
		0.0,
		0,
		0,
		0.0,
		0
	)
}