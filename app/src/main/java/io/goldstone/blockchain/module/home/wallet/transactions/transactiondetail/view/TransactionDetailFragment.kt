package io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.view

import android.os.Bundle
import android.view.View
import com.blinnnk.extension.orEmptyArray
import io.goldstone.blockchain.common.base.BaseRecyclerView
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerFragment
import io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.model.TransactionDetailModel
import io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.presenter.TransactionDetailPresenter

/**
 * @date 27/03/2018 3:26 AM
 * @author KaySaith
 */

class TransactionDetailFragment : BaseRecyclerFragment<TransactionDetailPresenter, TransactionDetailModel>() {

  override val presenter = TransactionDetailPresenter(this)

  override fun setRecyclerViewAdapter(recyclerView: BaseRecyclerView, asyncData: ArrayList<TransactionDetailModel>?) {
    recyclerView.adapter = TransactionDetailAdapter(asyncData.orEmptyArray())
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    asyncData = arrayListOf(
      TransactionDetailModel("0.000000289 ETH", "Miner Fee"),
      TransactionDetailModel("please remember this transaction", "Memo"),
      TransactionDetailModel("0x98s98d78x7c68d78s76d6f8s76x8c68d7s", "Transaction Number"),
      TransactionDetailModel("58928394", "Block Number"),
      TransactionDetailModel("03/07/2018 21:17:15 +0800", "Transaction Date"),
      TransactionDetailModel("https://ethereumScan.io/transaction/9x8s...", "Open A Url")
    )

  }

}