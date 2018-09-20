package io.goldstone.blockchain.module.home.quotation.tradermemory.eosmemorytransactionhistorylist.view

import android.content.Context
import com.blinnnk.base.HoneyBaseAdapter
import io.goldstone.blockchain.module.home.quotation.tradermemory.eosmemorytransactionhistorylist.model.EOSMemoryTransactionHistoryListTable

/**
 * @date 18/09/2018 6:36 PM
 * @author wcx
 */

class EOSMemoryTransactionHistoryListAdapter(
	override val dataSet: ArrayList<EOSMemoryTransactionHistoryListTable>,
	private val isSalesRecord: Boolean,
	private val hold: (EOSMemoryTransactionHistoryListCell) -> Unit
) : HoneyBaseAdapter<EOSMemoryTransactionHistoryListTable, EOSMemoryTransactionHistoryListCell>() {

	override fun generateCell(context: Context) = EOSMemoryTransactionHistoryListCell(context, isSalesRecord)

	override fun EOSMemoryTransactionHistoryListCell.bindCell(data: EOSMemoryTransactionHistoryListTable, position: Int) {
		model = data
		hold(this)
	}

	override fun getItemCount(): Int {
		return dataSet.size
	}

}