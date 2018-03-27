package io.goldstone.blockchain.module.common.tokendetail.tokendetail.view

import android.os.Bundle
import android.view.View
import com.blinnnk.extension.into
import com.blinnnk.extension.orEmptyArray
import com.blinnnk.extension.setAlignParentBottom
import io.goldstone.blockchain.common.base.BaseRecyclerView
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerFragment
import io.goldstone.blockchain.module.common.tokendetail.tokendetail.presenter.TokenDetailPresenter
import io.goldstone.blockchain.module.home.wallet.transactions.transactionlist.model.TransactionListModel

/**
 * @date 27/03/2018 3:20 PM
 * @author KaySaith
 */

class TokenDetailFragment : BaseRecyclerFragment<TokenDetailPresenter, TransactionListModel>() {

  private val footer by lazy { TokenDetailFooter(context!!) }

  override val presenter = TokenDetailPresenter(this)

  override fun setRecyclerViewAdapter(recyclerView: BaseRecyclerView, asyncData: ArrayList<TransactionListModel>?) {
    recyclerView.adapter = TokenDetailAdapter(asyncData.orEmptyArray())
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    footer.into(wrapper)
    footer.setAlignParentBottom()

    asyncData = arrayListOf(
      TransactionListModel("KingsDom", "3 days ago incoming from 0x89d7", 12.92, "ETH", true),
      TransactionListModel("Jean Jelly", "1 days ago incoming from 0x89ds", 5.0, "EOS", false),
      TransactionListModel("0x82u7...67s65d", "1 days ago incoming from 0x89d7", 1.0, "EOS", false),
      TransactionListModel("KingsDom", "3 days ago incoming from 0x89d7", 18.92, "ETH", true),
      TransactionListModel("Jean Jelly", "1 days ago incoming from 0x89d7", 5.1, "EOS", false),
      TransactionListModel("0x82u7...67s65d", "1 days ago incoming from 0x89d7", 6.5, "EOS", false),
      TransactionListModel("KingsDom", "3 days ago incoming from 0x89d7", 18.92, "ETH", true),
      TransactionListModel("Jean Jelly", "1 days ago incoming from 0x89d7", 5.1, "EOS", false)

    )

  }

}