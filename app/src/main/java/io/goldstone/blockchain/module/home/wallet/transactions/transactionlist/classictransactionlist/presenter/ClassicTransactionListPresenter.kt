package io.goldstone.blockchain.module.home.wallet.transactions.transactionlist.classictransactionlist.presenter

import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerPresenter
import io.goldstone.blockchain.module.home.wallet.transactions.transactionlist.classictransactionlist.view.ClassicTransactionListFragment
import io.goldstone.blockchain.module.home.wallet.transactions.transactionlist.ethereumtransactionlist.model.TransactionListModel

/**
 * @date 2018/6/25 12:08 PM
 * @author KaySaith
 */
class ClassicTransactionListPresenter(
	override val fragment: ClassicTransactionListFragment
) : BaseRecyclerPresenter<ClassicTransactionListFragment, TransactionListModel>() {
	
	override fun updateData() {
		fragment.asyncData = arrayListOf()
	}
}