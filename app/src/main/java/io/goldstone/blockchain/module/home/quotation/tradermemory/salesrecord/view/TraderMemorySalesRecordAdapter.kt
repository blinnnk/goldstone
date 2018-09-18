package io.goldstone.blockchain.module.home.quotation.tradermemory.salesrecord.view

import android.content.Context
import com.blinnnk.base.HoneyBaseAdapter
import io.goldstone.blockchain.module.home.quotation.quotationsearch.model.QuotationSelectionTable
import io.goldstone.blockchain.module.home.quotation.tradermemory.salesrecord.model.TraderMemorySalesRecordModel

/**
 * @date 18/09/2018 6:36 PM
 * @author wcx
 */

class TraderMemorySalesRecordAdapter(
	override val dataSet: ArrayList<TraderMemorySalesRecordModel>,
	private val hold: (TraderMemorySalesRecordCell) -> Unit
) : HoneyBaseAdapter<TraderMemorySalesRecordModel, TraderMemorySalesRecordCell>() {

	override fun generateCell(context: Context) = TraderMemorySalesRecordCell(context)

	override fun TraderMemorySalesRecordCell.bindCell(data: TraderMemorySalesRecordModel, position: Int) {
		hold(this)
	}

}