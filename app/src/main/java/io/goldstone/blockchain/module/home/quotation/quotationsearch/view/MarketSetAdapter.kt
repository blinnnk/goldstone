package io.goldstone.blockchain.module.home.quotation.quotationsearch.view

import android.content.Context
import com.blinnnk.base.HoneyBaseAdapter
import io.goldstone.blockchain.module.home.quotation.quotationsearch.model.ExchangeTable

/**
 * @date: 2018/8/29.
 * @author: yanglihai
 * @description:
 */
class MarketSetAdapter(
	override val dataSet: ArrayList<ExchangeTable>,
	private val  hold: (ExchangeCell)-> Unit
	) : HoneyBaseAdapter<ExchangeTable, ExchangeCell>() {
	
	override fun generateCell(context: Context): ExchangeCell {
		return ExchangeCell(context)
	}
	
	override fun ExchangeCell.bindCell(
		data: ExchangeTable,
		position: Int
	) {
		model = data
		hold(this)
	}
}