package io.goldstone.blockchain.module.home.wallet.transactions.transactionlist.classictransactionlist.view

import android.content.Context
import com.blinnnk.base.HoneyBaseAdapter
import io.goldstone.blockchain.module.home.wallet.transactions.transactionlist.ethereumtransactionlist.model.TransactionListModel
import io.goldstone.blockchain.module.home.wallet.transactions.transactionlist.ethereumtransactionlist.view.TransactionListCell

/**
 * @date 2018/6/25 8:48 PM
 * @author KaySaith
 */
class ClassicTransactionListAdapter(
	override val dataSet: ArrayList<TransactionListModel>,
	private val callback: TransactionListCell.() -> Unit
) : HoneyBaseAdapter<TransactionListModel, TransactionListCell>() {
	
	override fun generateCell(context: Context) = TransactionListCell(context)
	
	override fun TransactionListCell.bindCell(data: TransactionListModel, position: Int) {
		model = data
		callback(this)
	}
}