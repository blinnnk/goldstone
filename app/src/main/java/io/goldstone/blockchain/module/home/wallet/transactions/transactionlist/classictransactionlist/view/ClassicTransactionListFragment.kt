package io.goldstone.blockchain.module.home.wallet.transactions.transactionlist.classictransactionlist.view

import io.goldstone.blockchain.common.base.BaseRecyclerView
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerFragment
import io.goldstone.blockchain.module.home.wallet.transactions.transactionlist.classictransactionlist.presenter.ClassicTransactionListPresenter
import io.goldstone.blockchain.module.home.wallet.transactions.transactionlist.ethereumtransactionlist.model.TransactionListModel

/**
 * @date 2018/6/25 12:07 PM
 * @author KaySaith
 */
class ClassicTransactionListFragment
	: BaseRecyclerFragment<ClassicTransactionListPresenter, TransactionListModel>() {
	
	override val presenter = ClassicTransactionListPresenter(this)
	
	override fun setRecyclerViewAdapter(
		recyclerView: BaseRecyclerView,
		asyncData: ArrayList<TransactionListModel>?
	) {
	}
}