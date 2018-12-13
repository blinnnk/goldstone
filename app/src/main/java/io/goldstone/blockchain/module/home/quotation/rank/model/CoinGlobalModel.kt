package io.goldstone.blockchain.module.home.quotation.rank.model

import com.google.gson.annotations.SerializedName

/**
 * @date: 2018-12-12.
 * @author: yangLiHai
 * @description:
 */
class CoinGlobalModel(
	@SerializedName("total_volume_24h")
	val totalVolume: String,
	@SerializedName("total_market_cap")
	val totalMarketCap: String,
	@SerializedName("bitcoin_percentage_of_market_cap")
	val btcPercentageMarketCap: String
) 