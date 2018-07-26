package io.goldstone.blockchain.module.home.wallet.transactions.transactionlist.bitcointransactionlist.view

import com.blinnnk.extension.orEmptyArray
import io.goldstone.blockchain.common.base.BaseRecyclerView
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerFragment
import io.goldstone.blockchain.module.home.wallet.transactions.transactionlist.bitcointransactionlist.bitcointransactionlistPresenter.BitcoinTransactionListPresenter
import io.goldstone.blockchain.module.home.wallet.transactions.transactionlist.ethereumtransactionlist.model.TransactionListModel
import org.jetbrains.anko.sdk25.coroutines.onClick

/**
 * @date 2018/7/26 11:57 PM
 * @author KaySaith
 */
class BitcoinTransactionListFragment
	: BaseRecyclerFragment<BitcoinTransactionListPresenter, TransactionListModel>() {
	
	override val presenter = BitcoinTransactionListPresenter(this)
	override fun setRecyclerViewAdapter(
		recyclerView: BaseRecyclerView,
		asyncData: ArrayList<TransactionListModel>?
	) {
		recyclerView.adapter = BitcoinTransactionListAdapter(asyncData.orEmptyArray()) {
			onClick {
				// TODO
			}
		}
	}
}