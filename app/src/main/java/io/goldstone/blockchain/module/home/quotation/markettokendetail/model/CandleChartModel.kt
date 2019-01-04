package io.goldstone.blockchain.module.home.quotation.markettokendetail.model

import com.blinnnk.extension.safeGet
import com.google.gson.annotations.SerializedName
import org.json.JSONArray
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
		data.safeGet("high"),
		data.safeGet("low"),
		data.safeGet("close"),
		data.safeGet("open"),
		data.safeGet("time")
	)

	companion object {
	    fun convertData(jsonString: String): List<CandleChartModel> {
				val jsonArray = JSONArray(jsonString)
				var finalData = listOf<CandleChartModel>()
				// 把数据转换成需要的格式
				(0 until jsonArray.length()).forEach {
					val jsonModel = JSONObject(jsonArray[it]?.toString())
					if (jsonModel.toString().contains("open")) {
						finalData += CandleChartModel(jsonModel)
					}
				}
				return finalData
			}
	}
}