package io.goldstone.blockchain.module.home.wallet.transactions.transactionlist.ethereumtransactionlist.view

import com.blinnnk.extension.preventDuplicateClicks
import io.goldstone.blockchain.common.base.BaseRecyclerView
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerFragment
import io.goldstone.blockchain.module.home.wallet.transactions.transaction.view.TransactionFragment
import io.goldstone.blockchain.module.home.wallet.transactions.transactionlist.ethereumtransactionlist.model.TransactionListModel
import io.goldstone.blockchain.module.home.wallet.transactions.transactionlist.ethereumtransactionlist.presenter.TransactionListPresenter
import org.jetbrains.anko.sdk25.coroutines.onClick

/**
 * @date 24/03/2018 2:12 PM
 * @author KaySaith
 */
class TransactionListFragment :
	BaseRecyclerFragment<TransactionListPresenter, TransactionListModel>() {
	
	override val presenter = TransactionListPresenter(this)
	
	override fun setRecyclerViewAdapter(
		recyclerView: BaseRecyclerView,
		asyncData: ArrayList<TransactionListModel>?
	) {
		asyncData?.let {
			recyclerView.adapter = TransactionListAdapter(it) {
				onClick {
					TransactionListPresenter.showTransactionDetail(
						parentFragment as? TransactionFragment,
						model,
						true
					)
					preventDuplicateClicks()
				}
			}
		}
	}
}