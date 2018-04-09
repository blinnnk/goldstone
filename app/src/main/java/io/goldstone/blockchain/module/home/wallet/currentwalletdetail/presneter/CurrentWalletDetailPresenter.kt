package io.goldstone.blockchain.module.home.wallet.currentwalletdetail.presneter

import com.blinnnk.extension.addFragment
import com.blinnnk.extension.preventDuplicateClicks
import io.goldstone.blockchain.common.base.baseoverlayfragment.BaseOverlayPresenter
import io.goldstone.blockchain.common.value.ContainerID
import io.goldstone.blockchain.module.common.walletimport.walletimport.view.WalletImportFragment
import io.goldstone.blockchain.module.home.wallet.currentwalletdetail.view.CurrentWalletDetailFragment

/**
 * @date 24/03/2018 10:11 PM
 * @author KaySaith
 */

class CurrentWalletDetailPresenter(
  override val fragment: CurrentWalletDetailFragment
  ) : BaseOverlayPresenter<CurrentWalletDetailFragment>() {

  fun showCreateWalletFragment() {
    fragment.activity?.addFragment<WalletImportFragment>(ContainerID.main)
    fragment.createWalletButton.preventDuplicateClicks()
  }

}