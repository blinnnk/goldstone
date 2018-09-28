package io.goldstone.blockchain.module.home.quotation.tradermemory.ramrank.model

/**
 * @date: 2018/9/25.
 * @author: yanglihai
 * @description:
 */
class EOSRAMRankModel(
	val account: String = "",
	val used: String = "",
	val average_price: Double = 0.toDouble(),
	val ram: String = "",
	val percent: String = "",
	val rank: String = "",
	val gain: String = ""
) {
	constructor() : this("", "", 0.toDouble(), "", "", "", "")
}