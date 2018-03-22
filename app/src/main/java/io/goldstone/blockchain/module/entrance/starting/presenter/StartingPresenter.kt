package io.goldstone.blockchain.module.entrance.starting.presenter

import com.blinnnk.extension.addFragment
import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.common.value.ContainerID
import io.goldstone.blockchain.module.common.walletgeneration.walletgeneration.view.WalletGenerationFragment
import io.goldstone.blockchain.module.common.walletimport.walletimport.view.WalletImportFragment
import io.goldstone.blockchain.module.entrance.starting.view.StartingFragment

/**
 * @date 22/03/2018 2:56 AM
 * @author KaySaith
 */

class StartingPresenter(override val fragment: StartingFragment) : BasePresenter<StartingFragment>() {

  fun showCreateWalletFragment() {
    fragment.activity?.addFragment<WalletGenerationFragment>(ContainerID.splash)
  }

  fun showImportWalletFragment() {
    fragment.activity?.addFragment<WalletImportFragment>(ContainerID.splash)
  }

}