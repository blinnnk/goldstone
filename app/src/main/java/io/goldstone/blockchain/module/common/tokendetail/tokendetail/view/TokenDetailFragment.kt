package io.goldstone.blockchain.module.common.tokendetail.tokendetail.view

import android.os.Bundle
import android.view.View
import com.blinnnk.extension.into
import com.blinnnk.extension.orEmptyArray
import com.blinnnk.extension.preventDuplicateClicks
import com.blinnnk.extension.setAlignParentBottom
import io.goldstone.blockchain.common.base.BaseRecyclerView
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerFragment
import io.goldstone.blockchain.module.common.tokendetail.tokendetail.presenter.TokenDetailPresenter
import io.goldstone.blockchain.module.home.wallet.transactions.transactionlist.model.TransactionListModel
import org.jetbrains.anko.sdk25.coroutines.onClick

/**
 * @date 27/03/2018 3:20 PM
 * @author KaySaith
 */

class TokenDetailFragment : BaseRecyclerFragment<TokenDetailPresenter, TransactionListModel>() {

  private val footer by lazy { TokenDetailFooter(context!!) }
  override val presenter = TokenDetailPresenter(this)

  override fun setRecyclerViewAdapter(recyclerView: BaseRecyclerView, asyncData: ArrayList<TransactionListModel>?) {
    recyclerView.adapter = TokenDetailAdapter(asyncData.orEmptyArray()) {
      onClick {
        presenter.showTransactionDetailFragment()
        preventDuplicateClicks()
      }
    }
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    footer.into(wrapper)
    footer.apply {
      setAlignParentBottom()
      sendButton.onClick { presenter.showAddressSelectionFragment() }
      receivedButton.onClick { presenter.showDepositFragment() }
    }

  }

}