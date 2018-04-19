package io.goldstone.blockchain.module.home.wallet.walletmanagement.walletmanagement.presneter

import com.blinnnk.util.addFragmentAndSetArgument
import io.goldstone.blockchain.common.base.baseoverlayfragment.BaseOverlayPresenter
import io.goldstone.blockchain.common.value.ContainerID
import io.goldstone.blockchain.common.value.CurrentWalletText
import io.goldstone.blockchain.common.value.WalletText
import io.goldstone.blockchain.module.home.wallet.walletmanagement.walletaddingmethod.view.WalletAddingMethodFragment
import io.goldstone.blockchain.module.home.wallet.walletmanagement.walletlist.view.WalletListFragment
import io.goldstone.blockchain.module.home.wallet.walletmanagement.walletmanagement.view.WalletManagementFragment

/**
 * @date 24/03/2018 10:11 PM
 * @author KaySaith
 */

class WalletManagementPresenter(
  override val fragment: WalletManagementFragment
  ) : BaseOverlayPresenter<WalletManagementFragment>() {

  fun showWalletAddingMethodFragment() {
    showTargetFragment<WalletAddingMethodFragment>(WalletText.addWallet, CurrentWalletText.Wallets)
  }

  fun showWalletListFragment() {
    fragment.addFragmentAndSetArgument<WalletListFragment>(ContainerID.content) {
      // Send Argument
    }
  }

}