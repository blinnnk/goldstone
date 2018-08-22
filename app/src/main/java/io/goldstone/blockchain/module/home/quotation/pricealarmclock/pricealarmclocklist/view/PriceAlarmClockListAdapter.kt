package io.goldstone.blockchain.module.home.quotation.pricealarmclock.pricealarmclocklist.view

import android.content.Context
import com.blinnnk.base.HoneyBaseAdapter
import io.goldstone.blockchain.module.home.quotation.pricealarmclock.pricealarmclocklist.model.PriceAlarmClockTable

/**
 * @data 08/08/2018 11:49 AM
 * @author wcx
 * @description 价格闹钟列表适配器
 */
class PriceAlarmClockListAdapter(
	override var dataSet: ArrayList<PriceAlarmClockTable>,
	private val callback: PriceAlarmClockListCell.() -> Unit
) : HoneyBaseAdapter<PriceAlarmClockTable, PriceAlarmClockListCell>() {

	override fun getItemCount(): Int {
		return dataSet.size
	}

	override fun generateCell(context: Context) = PriceAlarmClockListCell(context)

	override fun PriceAlarmClockListCell.bindCell(
		data: PriceAlarmClockTable,
		position: Int
	) {
		model = data
		callback(this)
	}
}