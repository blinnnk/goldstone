package io.goldstone.blockchain.module.home.quotation.tradermemory.largesinglerecord.view

import android.content.Context
import com.blinnnk.base.HoneyBaseAdapter
import io.goldstone.blockchain.module.home.quotation.quotationsearch.model.QuotationSelectionTable
import io.goldstone.blockchain.module.home.quotation.tradermemory.largesinglerecord.model.TraderMemoryLargeSingleRecordModel
import io.goldstone.blockchain.module.home.quotation.tradermemory.salesrecord.model.TraderMemorySalesRecordModel
import io.goldstone.blockchain.module.home.quotation.tradermemory.salesrecord.view.TraderMemorySalesRecordCell

/**
 * @date 18/09/2018 6:36 PM
 * @author wcx
 */

class TraderMemoryLargeSingleRecordAdapter(
	override val dataSet: ArrayList<TraderMemoryLargeSingleRecordModel>,
	private val hold: (TraderMemoryLargeSingleRecordCell) -> Unit
) : HoneyBaseAdapter<TraderMemoryLargeSingleRecordModel, TraderMemoryLargeSingleRecordCell>() {

	override fun generateCell(context: Context) = TraderMemoryLargeSingleRecordCell(context)

	override fun TraderMemoryLargeSingleRecordCell.bindCell(data: TraderMemoryLargeSingleRecordModel, position: Int) {
		hold(this)
	}

}