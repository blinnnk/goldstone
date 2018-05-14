package io.goldstone.blockchain.module.home.quotation.quotation.model

import com.google.gson.annotations.SerializedName
import io.goldstone.blockchain.common.utils.safeGet
import org.json.JSONObject

/**
 * @date 2018/4/28 3:56 PM
 * @author KaySaith
 */

data class CurrencyPriceInfoModel(
	@SerializedName("pair") val pair: String,
	@SerializedName("price") val price: String,
	@SerializedName("percent") val percent: String,
	@SerializedName("quote_price") val usdtPrice: String?
) {
	constructor(data: JSONObject) : this(
		data.safeGet("pair"),
		data.safeGet("price"),
		data.safeGet("percent"),
		data.safeGet("quote_price")
	)
}