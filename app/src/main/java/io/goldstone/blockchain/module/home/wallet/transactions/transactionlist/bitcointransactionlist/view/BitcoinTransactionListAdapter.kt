package io.goldstone.blockchain.module.home.wallet.transactions.transactionlist.bitcointransactionlist.view

import android.content.Context
import com.blinnnk.base.HoneyBaseAdapter
import io.goldstone.blockchain.module.home.wallet.transactions.transactionlist.ethereumtransactionlist.model.TransactionListModel
import io.goldstone.blockchain.module.home.wallet.transactions.transactionlist.ethereumtransactionlist.view.TransactionListCell

/**
 * @date 2018/7/26 11:58 PM
 * @author KaySaith
 */
class BitcoinTransactionListAdapter(
	override val dataSet: ArrayList<TransactionListModel>,
	private val callback: TransactionListCell.() -> Unit
) : HoneyBaseAdapter<TransactionListModel, TransactionListCell>() {
	
	override fun generateCell(context: Context) = TransactionListCell(context)
	
	override fun TransactionListCell.bindCell(data: TransactionListModel, position: Int) {
		model = data
		callback(this)
	}
}