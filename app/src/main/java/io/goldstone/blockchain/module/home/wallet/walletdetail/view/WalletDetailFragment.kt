package io.goldstone.blockchain.module.home.wallet.walletdetail.view

import android.os.Bundle
import android.view.View
import com.blinnnk.extension.orEmptyArray
import com.blinnnk.extension.preventDuplicateClicks
import com.blinnnk.util.HoneyUIUtils
import io.goldstone.blockchain.common.base.BaseRecyclerView
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerFragment
import io.goldstone.blockchain.module.home.wallet.walletdetail.model.WalletDetailCellModel
import io.goldstone.blockchain.module.home.wallet.walletdetail.presenter.WalletDetailPresenter
import org.jetbrains.anko.sdk25.coroutines.onClick
import java.util.*

/**
 * @date 23/03/2018 3:44 PM
 * @author KaySaith
 */

class WalletDetailFragment : BaseRecyclerFragment<WalletDetailPresenter, WalletDetailCellModel>() {

  private val slideHeader by lazy { WalletSlideHeader(context!!) }

  override val presenter = WalletDetailPresenter(this)

  override fun setRecyclerViewAdapter(
    recyclerView: BaseRecyclerView, asyncData: ArrayList<WalletDetailCellModel>?
  ) {
    recyclerView.adapter = WalletDetailAdapter(asyncData.orEmptyArray(), {
      onClick {
        getTokenInfo()?.apply { presenter.showMyTokenDetailFragment(this) }
        preventDuplicateClicks()
      }
    }) {
      currentAccount.onClick { presenter.showWalletSettingsFragment() }
      manageButton.onClick { presenter.showWalletListFragment() }
      addTokenButton.onClick { presenter.showTokenManagementFragment() }
    }
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    wrapper.addView(slideHeader)

    // this `slideHeader` will show or hide depends on the distance that user sliding the
    // recyclerView, and not in the same layer with `RecyclerView's headerView`

    slideHeader.apply {
      historyButton.onClick { presenter.showTransactionsFragment() }
      notifyButton.onClick { presenter.showNotificationListFragment() }
    }
  }

  private var isShow = false
  private val headerHeight by lazy { HoneyUIUtils.getHeight(slideHeader) }

  override fun observingRecyclerViewVerticalOffset(offset: Int) {
    if (offset >= headerHeight && !isShow) {
      slideHeader.onHeaderShowedStyle()
      isShow = true
    }

    if (offset < headerHeight && isShow) {
      slideHeader.onHeaderHidesStyle()
      isShow = false
    }
  }
}

