package io.goldstone.blockchain.module.common.tokendetail.tokendetail.view

import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.View
import com.blinnnk.extension.*
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.base.BaseRecyclerView
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerFragment
import io.goldstone.blockchain.common.value.ArgumentKey
import io.goldstone.blockchain.module.common.tokendetail.tokendetail.presenter.TokenDetailPresenter
import io.goldstone.blockchain.module.home.wallet.transactions.transactionlist.model.TransactionListModel
import org.jetbrains.anko.sdk25.coroutines.onClick

/**
 * @date 27/03/2018 3:20 PM
 * @author KaySaith
 */

class TokenDetailFragment : BaseRecyclerFragment<TokenDetailPresenter, TransactionListModel>() {

  // 首页的 `cell` 点击进入详情界面传入的 `Symbol`
  val symbol by lazy { arguments?.getString(ArgumentKey.tokenDetail) }

  private val footer by lazy { TokenDetailFooter(context!!) }
  override val presenter = TokenDetailPresenter(this)

  override fun setRecyclerViewAdapter(recyclerView: BaseRecyclerView, asyncData: ArrayList<TransactionListModel>?) {
    recyclerView.adapter = TokenDetailAdapter(asyncData.orEmptyArray()) {
      onClick {
        model?.let {
          presenter.showTransactionDetailFragment(it)
          preventDuplicateClicks()
        }
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