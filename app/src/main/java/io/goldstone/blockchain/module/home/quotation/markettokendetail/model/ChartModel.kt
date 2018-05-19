package io.goldstone.blockchain.module.home.quotation.markettokendetail.model

import com.blinnnk.extension.safeGet
import com.google.gson.annotations.SerializedName
import org.json.JSONObject

/**
 * @date 2018/5/4 4:33 PM
 * @author KaySaith
 */

data class ChartModel(
	@SerializedName("price")
	val price: String,
	@SerializedName("time")
	val timestamp: String
) {
	constructor(data: JSONObject) : this(
		data.safeGet("price"), data.safeGet("time")
	)
}