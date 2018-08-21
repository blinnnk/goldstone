package io.goldstone.blockchain.module.home.quotation.markettokendetail.model

import com.blinnnk.extension.safeGet
import com.google.gson.annotations.SerializedName
import org.json.JSONObject

/**
 * @date: 2018/8/8.
 * @author: yanglihai
 * @description: 服务器返回的蜡烛图item的类
 */
class CandleChartModel (
	@SerializedName("high")
	val high: String,
	@SerializedName("low")
	val low: String,
	@SerializedName("close")
	val close: String,
	@SerializedName("open")
	val open: String,
	@SerializedName("time")
	val time: String
) {
	constructor(data: JSONObject) : this(
		data.safeGet("high"), data.safeGet("low"),
			data.safeGet("close"), data.safeGet("open"),
		data.safeGet("time")
	
	)
}