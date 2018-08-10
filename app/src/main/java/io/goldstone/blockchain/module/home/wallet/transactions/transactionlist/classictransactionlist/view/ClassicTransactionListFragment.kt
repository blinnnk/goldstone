package io.goldstone.blockchain.module.home.wallet.transactions.transactionlist.classictransactionlist.view

import com.blinnnk.extension.getParentFragment
import com.blinnnk.extension.orEmptyArray
import com.blinnnk.extension.preventDuplicateClicks
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerFragment
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerView
import io.goldstone.blockchain.module.home.wallet.transactions.transaction.view.TransactionFragment
import io.goldstone.blockchain.module.home.wallet.transactions.transactionlist.classictransactionlist.presenter.ClassicTransactionListPresenter
import io.goldstone.blockchain.module.home.wallet.transactions.transactionlist.ethereumtransactionlist.model.TransactionListModel
import io.goldstone.blockchain.module.home.wallet.transactions.transactionlist.ethereumtransactionlist.presenter.TransactionListPresenter
import org.jetbrains.anko.sdk25.coroutines.onClick

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
		recyclerView.adapter = ClassicTransactionListAdapter(asyncData.orEmptyArray()) {
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
	
	override fun onHiddenChanged(hidden: Boolean) {
		super.onHiddenChanged(hidden)
		getParentFragment<TransactionFragment> {
			showMenuBar(!hidden)
		}
	}
}