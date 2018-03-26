package io.goldstone.blockchain.module.home.wallet.transactions.transactionlist.view

import android.os.Bundle
import android.view.View
import com.blinnnk.extension.preventDuplicateClicks
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.base.BaseRecyclerView
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerFragment
import io.goldstone.blockchain.module.home.wallet.transactions.transactionlist.model.TransactionListModel
import io.goldstone.blockchain.module.home.wallet.transactions.transactionlist.presenter.TransactionListPresenter
import org.jetbrains.anko.sdk25.coroutines.onClick

/**
 * @date 24/03/2018 2:12 PM
 * @author KaySaith
 */

class TransactionListFragment : BaseRecyclerFragment<TransactionListPresenter, TransactionListModel>() {

  override val presenter = TransactionListPresenter(this)

  override fun setRecyclerViewAdapter(
    recyclerView: BaseRecyclerView,
    asyncData: ArrayList<TransactionListModel>?
  ) {
    asyncData?.let {
      recyclerView.adapter = TransactionListAdapter(it) {
        onClick {
          presenter.showTransactionDetail()
          preventDuplicateClicks()
        }
      }
    }
  }

  override fun setSlideUpWithCellHeight() = 75.uiPX()

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    asyncData = arrayListOf(
      TransactionListModel("KingsDom", "3 days ago incoming from 0x89d7", 12.92, "ETH", true),
      TransactionListModel("Jean Jelly", "1 days ago incoming from 0x89ds", 5.0, "EOS", false),
      TransactionListModel("0x82u7...67s65d", "1 days ago incoming from 0x89d7", 1.0, "EOS", false),
      TransactionListModel("KingsDom", "3 days ago incoming from 0x89d7", 18.92, "ETH", true),
      TransactionListModel("Jean Jelly", "1 days ago incoming from 0x89d7", 5.1, "EOS", false),
      TransactionListModel("0x82u7...67s65d", "1 days ago incoming from 0x89d7", 6.5, "EOS", false)
    )
  }

}