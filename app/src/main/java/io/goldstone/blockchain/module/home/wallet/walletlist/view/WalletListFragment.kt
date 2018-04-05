package io.goldstone.blockchain.module.home.wallet.walletlist.view

import com.blinnnk.extension.preventDuplicateClicks
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.base.BaseRecyclerView
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerFragment
import io.goldstone.blockchain.module.home.wallet.walletlist.model.WalletListModel
import io.goldstone.blockchain.module.home.wallet.walletlist.presenter.WalletListPresenter
import org.jetbrains.anko.sdk25.coroutines.onClick

/**
 * @date 24/03/2018 8:50 PM
 * @author KaySaith
 */

class WalletListFragment : BaseRecyclerFragment<WalletListPresenter, WalletListModel>() {

  override val presenter = WalletListPresenter(this)

  override fun setRecyclerViewAdapter(recyclerView: BaseRecyclerView, asyncData: ArrayList<WalletListModel>?) {
    asyncData?.let {
      recyclerView.adapter = WalletListAdapter(it) {
        onClick {
          presenter.switchWalle(model.address)
          preventDuplicateClicks()
        }
      }
    }
  }

  override fun setSlideUpWithCellHeight() = 75.uiPX()

}