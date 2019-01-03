package io.goldstone.blockchain.module.home.quotation.quotationrank.model

import com.blinnnk.extension.safeGet
import com.google.gson.annotations.SerializedName
import org.json.JSONObject

/**
 * @date: 2019-01-02.
 * @author: yangLiHai
 * @description:
 */
class QuotationGlobalModel(
	@SerializedName("total_volume_24h")
	val totalVolume: String,
	@SerializedName("total_market_cap")
	val totalMarketCap: String,
	@SerializedName("bitcoin_percentage_of_market_cap")
	val btcPercentageMarketCap: String
) {
	constructor(jsonObject: JSONObject) : this(
		jsonObject.safeGet("total_volume_24h"),
		jsonObject.safeGet("total_market_cap"),
		jsonObject.safeGet("bitcoin_percentage_of_market_cap")
	)
}