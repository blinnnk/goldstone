package io.goldstone.blockchain.module.home.rammarket.module.ramquotation.ramoccupyrank.model

/**
 * @date: 2018/11/5.
 * @author: yanglihai
 * @description:
 */
class RAMRankModel(
	val account: String,
	val used: String,
	val ram: String,
	val percent: String,
	val rank: Int
) {
	constructor() : this(
		"",
		"",
		"",
		"",
		0
	)
}