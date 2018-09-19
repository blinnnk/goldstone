package io.goldstone.blockchain.module.home.quotation.tradermemory.salesrecordandlargesinglerecordlist.view

import android.content.Context
import com.blinnnk.base.HoneyBaseAdapter
import io.goldstone.blockchain.module.home.quotation.tradermemory.salesrecordandlargesinglerecordlist.model.TraderMemorySalesRecordAndLargeSingleRecordListModel

/**
 * @date 18/09/2018 6:36 PM
 * @author wcx
 */

class TraderMemorySalesRecordAndLargeSingleRecordListAdapter(
	override val dataSet: ArrayList<TraderMemorySalesRecordAndLargeSingleRecordListModel>,
	private val isSalesRecord: Boolean,
	private val hold: (TraderMemorySalesRecordAndLargeSingleRecordListCell) -> Unit
) : HoneyBaseAdapter<TraderMemorySalesRecordAndLargeSingleRecordListModel, TraderMemorySalesRecordAndLargeSingleRecordListCell>() {

	override fun generateCell(context: Context) = TraderMemorySalesRecordAndLargeSingleRecordListCell(context, isSalesRecord)

	override fun TraderMemorySalesRecordAndLargeSingleRecordListCell.bindCell(data: TraderMemorySalesRecordAndLargeSingleRecordListModel, position: Int) {
		model = data
		hold(this)
	}

	override fun getItemCount(): Int {
		return dataSet.size
	}

}