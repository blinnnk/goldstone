package io.goldstone.blockchain.module.home.quotation.quotation.model

import com.google.gson.annotations.SerializedName
import org.json.JSONObject

/**
 * @date 2018/4/28 3:56 PM
 * @author KaySaith
 */

data class CurrencyPriceInfoModel(
	@SerializedName("pair") val pair: String,
	@SerializedName("price") val price: String,
	@SerializedName("percent") val percent: String
) {
	constructor(data: JSONObject) : this(
		data.get("pair").toString(),
		data.get("price").toString(),
		data.get("percent").toString()
	)
}