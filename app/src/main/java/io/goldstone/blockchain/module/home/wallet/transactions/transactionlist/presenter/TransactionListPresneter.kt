package io.goldstone.blockchain.module.home.wallet.transactions.transactionlist.presenter

import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerPresenter
import io.goldstone.blockchain.module.home.wallet.transactions.transaction.view.TransactionFragment
import io.goldstone.blockchain.module.home.wallet.transactions.transactionlist.model.TransactionListModel
import io.goldstone.blockchain.module.home.wallet.transactions.transactionlist.view.TransactionListFragment

/**
 * @date 24/03/2018 2:12 PM
 * @author KaySaith
 */

class TransactionListPresenter(
  override val fragment: TransactionListFragment
  ) : BaseRecyclerPresenter<TransactionListFragment, TransactionListModel>() {



}