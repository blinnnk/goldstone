package io.goldstone.blockchain.module.home.quotation.quotationrank.model

import com.google.gson.annotations.SerializedName

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
)