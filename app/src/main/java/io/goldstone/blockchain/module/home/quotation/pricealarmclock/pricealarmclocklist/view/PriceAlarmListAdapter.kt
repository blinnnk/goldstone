package io.goldstone.blockchain.module.home.quotation.pricealarmclock.pricealarmclocklist.view

import android.content.Context
import android.view.View
import com.blinnnk.base.HoneyBaseAdapterWithHeaderAndFooter
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.module.home.quotation.pricealarmclock.pricealarmclocklist.model.PriceAlarmTable

/**
 * @data 08/08/2018 11:49 AM
 * @author wcx
 * @description 价格闹钟列表适配器
 */
class PriceAlarmListAdapter(
	override var dataSet: ArrayList<PriceAlarmTable>,
	private val callback: PriceAlarmListCell.() -> Unit
) : HoneyBaseAdapterWithHeaderAndFooter<PriceAlarmTable, View, PriceAlarmListCell, View>() {

	override fun generateFooter(context: Context) = View(context)
	override fun generateHeader(context: Context) = View(context).apply { layoutParams.height = 15.uiPX() }

	override fun getItemCount(): Int {
		return dataSet.size
	}

	override fun generateCell(context: Context) = PriceAlarmListCell(context)

	override fun PriceAlarmListCell.bindCell(
		data: PriceAlarmTable,
		position: Int
	) {
		model = data
		callback(this)
	}
}