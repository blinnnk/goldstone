package io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.presenter

import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerPresenter
import io.goldstone.blockchain.common.value.ArgumentKey
import io.goldstone.blockchain.crypto.toEthValue
import io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.model.TransactionDetailModel
import io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.view.TransactionDetailFragment
import io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.view.TransactionDetailHeaderView
import io.goldstone.blockchain.module.home.wallet.transactions.transactionlist.model.TransactionListModel

/**
 * @date 27/03/2018 3:27 AM
 * @author KaySaith
 */

class TransactionDetailPresenter(
  override val fragment: TransactionDetailFragment
  ) : BaseRecyclerPresenter<TransactionDetailFragment, TransactionDetailModel>() {

  val data by lazy {
    fragment.arguments?.get(ArgumentKey.transactionDetail) as TransactionListModel
  }

  override fun updateData() {

    fragment.asyncData = arrayListOf(
      TransactionDetailModel(data.minerFee.toEthValue(), "Miner Fee"),
      TransactionDetailModel(data.memo, "Memo"),
      TransactionDetailModel(data.targetAddress, "Transaction Number"),
      TransactionDetailModel(data.blockNumber, "Block Number"),
      TransactionDetailModel(data.date, "Transaction Date"),
      TransactionDetailModel(data.url, "Open A Url")
    )

    fragment.recyclerView.getItemViewAtAdapterPosition<TransactionDetailHeaderView>(0) {
      setIconStyle(data.count, data.targetAddress, data.isReceived)
    }

  }

}