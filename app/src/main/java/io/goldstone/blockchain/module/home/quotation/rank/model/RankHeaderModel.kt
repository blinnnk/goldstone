package io.goldstone.blockchain.module.home.quotation.rank.model

import com.google.gson.annotations.SerializedName

/**
 * @date: 2018/8/15.
 * @author: yanglihai
 * @description: 头部信息的model
 */
data class RankHeaderModel(
	@SerializedName("total_volume_24h")
	var totalVolume24h: String,
	var code: String,
	@SerializedName("total_market_cap")
	var totalMarketCap: String,
	@SerializedName("bitcoin_percentage_of_market_cap")
	var BtcPercentage: String
)