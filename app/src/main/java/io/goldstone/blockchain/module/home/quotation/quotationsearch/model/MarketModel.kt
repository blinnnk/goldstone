package io.goldstone.blockchain.module.home.quotation.quotationsearch.model

/**
 * @date: 2018/8/29.
 * @author: yanglihai
 * @description:
 */
data class MarketModel(
	var id: Int,
	var url: String,
	var name: String,
	var status: Int // 1 选中，0 未选中
) {
	
	constructor(marketSetTable: MarketSetTable) : this(
		marketSetTable.id,
		marketSetTable.url,
		marketSetTable.name,
		marketSetTable.status
	)
	
}