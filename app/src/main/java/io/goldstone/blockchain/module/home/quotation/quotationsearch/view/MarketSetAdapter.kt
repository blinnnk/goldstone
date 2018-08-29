package io.goldstone.blockchain.module.home.quotation.quotationsearch.view

import android.content.Context
import com.blinnnk.base.HoneyBaseAdapter
import io.goldstone.blockchain.module.home.quotation.quotationsearch.model.MarketSetTable

/**
 * @date: 2018/8/29.
 * @author: yanglihai
 * @description:
 */
class MarketSetAdapter(
	override val dataSet: ArrayList<MarketSetTable>,
	private val  hold: (MarketSetCell)-> Unit
	) : HoneyBaseAdapter<MarketSetTable, MarketSetCell>() {
	
	override fun generateCell(context: Context): MarketSetCell {
		return MarketSetCell(context)
	}
	
	override fun MarketSetCell.bindCell(
		data: MarketSetTable,
		position: Int
	) {
		marketSetTable = data
		hold(this)
	}
}