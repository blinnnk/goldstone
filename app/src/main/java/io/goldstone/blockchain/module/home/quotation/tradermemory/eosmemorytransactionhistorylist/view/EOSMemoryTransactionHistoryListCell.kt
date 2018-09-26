package io.goldstone.blockchain.module.home.quotation.tradermemory.eosmemorytransactionhistorylist.view

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import com.blinnnk.util.observing
import io.goldstone.blockchain.common.component.OneToTwoLinesOfText
import io.goldstone.blockchain.common.utils.TimeUtils
import io.goldstone.blockchain.common.utils.toMillisecond
import io.goldstone.blockchain.module.home.quotation.tradermemory.eosmemorytransactionhistorylist.model.EOSMemoryTransactionHistoryListTable

/**
 * @date 18/09/2018 6:36 PM
 * @author wcx
 */
@SuppressLint("ViewConstructor")
class EOSMemoryTransactionHistoryListCell(
	context: Context,
	private val salesRecord: Boolean
) : OneToTwoLinesOfText(context) {

	var model: EOSMemoryTransactionHistoryListTable by observing(EOSMemoryTransactionHistoryListTable()) {
		model.apply {
			val formatDate = TimeUtils.formatMdHmDate(time.toMillisecond())
			Log.e("nHistoryListCell", "${model.type}++++")
			if (type == 0) {
				setText(
					formatDate,
					account,
					"卖出 $quantity EOS",
					price.toString()
				)
			} else if (model.type == 1) {
				setText(
					formatDate,
					account,
					"买入 $quantity EOS",
					price.toString()
				)
			}
		}
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