package io.goldstone.blockchain.module.home.quotation.tradermemory.personalmemorytransactionrecord.view

import android.annotation.SuppressLint
import android.content.Context
import com.blinnnk.util.observing
import io.goldstone.blockchain.common.component.OneToTwoLinesOfText
import io.goldstone.blockchain.module.home.quotation.tradermemory.eosmemorytransactionhistorylist.model.EOSMemoryTransactionHistoryListTable
import io.goldstone.blockchain.module.home.quotation.tradermemory.personalmemorytransactionrecord.model.PersonalMemoryTransactionRecordTable

/**
 * @date 18/09/2018 6:36 PM
 * @author wcx
 */
@SuppressLint("ViewConstructor")
class PersonalMemoryTransactionRecordCell(
	context: Context,
	private val salesRecord: Boolean
) : OneToTwoLinesOfText(context) {

	var model: PersonalMemoryTransactionRecordTable by observing(PersonalMemoryTransactionRecordTable()) {
		setText(
			model.time.toString(),
			model.txId,
			model.quantity.toString(),
			model.type.toString()
		)
	}

	init {
		if (salesRecord) {
			setText(
				"买卖记录时间",
				"买卖记录名字",
				"买卖记录数量",
				"买卖记录价格"
			)
		} else {
			setText(
				"大单记录时间",
				"大单记录名字",
				"大单记录数量",
				"大单记录价格"
			)
		}
	}

}