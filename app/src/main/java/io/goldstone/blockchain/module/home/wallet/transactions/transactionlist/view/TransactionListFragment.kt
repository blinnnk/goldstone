package io.goldstone.blockchain.module.home.wallet.transactions.transactionlist.view

import io.goldstone.blockchain.common.base.BaseRecyclerView
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerFragment
import io.goldstone.blockchain.module.home.wallet.transactions.transactionlist.presenter.TransactionListPresenter

/**
 * @date 24/03/2018 2:12 PM
 * @author KaySaith
 */

class TransactionListFragment : BaseRecyclerFragment<TransactionListPresenter, Int>() {

  override val presenter = TransactionListPresenter(this)

  override fun setRecyclerViewAdapter(recyclerView: BaseRecyclerView, asyncData: ArrayList<Int>?) {
    recyclerView.adapter = TransactionListAdapter(arrayListOf(1))
  }

}