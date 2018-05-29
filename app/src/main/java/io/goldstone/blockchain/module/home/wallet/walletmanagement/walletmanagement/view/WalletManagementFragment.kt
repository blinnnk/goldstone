package io.goldstone.blockchain.module.home.wallet.walletmanagement.walletmanagement.view

import android.view.ViewGroup
import io.goldstone.blockchain.common.base.baseoverlayfragment.BaseOverlayFragment
import io.goldstone.blockchain.common.value.CurrentWalletText
import io.goldstone.blockchain.module.home.wallet.walletmanagement.walletmanagement.presneter.WalletManagementPresenter

/**
 * @date 24/03/2018 10:10 PM
 * @author KaySaith
 */

class WalletManagementFragment : BaseOverlayFragment<WalletManagementPresenter>() {

  override val presenter = WalletManagementPresenter(this)

  override fun ViewGroup.initView() {

    headerTitle = CurrentWalletText.Wallets

    overlayView.header.showAddButton(true) {
      presenter.showWalletAddingMethodFragment()
    }

    presenter.showWalletListFragment()
  }

}