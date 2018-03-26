package io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.presenter

import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerPresenter
import io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.model.TransactionDetailModel
import io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.view.TransactionDetailFragment

/**
 * @date 27/03/2018 3:27 AM
 * @author KaySaith
 */

class TransactionDetailPresenter(
  override val fragment: TransactionDetailFragment
  ) : BaseRecyclerPresenter<TransactionDetailFragment, TransactionDetailModel>() {

}