package io.goldstone.blockchain.module.home.rammarket.module.ramquotation.ramoccupyrank.model

import com.google.gson.annotations.SerializedName

/**
 * @date: 2018/11/5.
 * @author: yanglihai
 * @description:
 */
class RAMRankModel(
	val account: String,
	val used: String,
	@SerializedName("average_price")
	val averagePrice: String,
  val ram: String,
	val percent: String,
	val rank: Int,
	val gain: Double
) {

}