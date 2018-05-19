package io.goldstone.blockchain.module.home.wallet.walletmanagement.walletaddingmethod.presenter

import com.blinnnk.extension.addFragment
import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.common.value.ContainerID
import io.goldstone.blockchain.module.common.walletgeneration.walletgeneration.view.WalletGenerationFragment
import io.goldstone.blockchain.module.common.walletimport.walletimport.view.WalletImportFragment
import io.goldstone.blockchain.module.home.wallet.walletmanagement.walletaddingmethod.view.WalletAddingMethodFragment

/**
 * @date 18/04/2018 9:40 PM
 * @author KaySaith
 */

class WalletAddingMethodPresenter(
	override val fragment: WalletAddingMethodFragment
) : BasePresenter<WalletAddingMethodFragment>() {

	fun showImportWalletFragment() {
		fragment.activity?.addFragment<WalletImportFragment>(ContainerID.main)
	}

	fun showCreateWalletFragment() {
		fragment.activity?.addFragment<WalletGenerationFragment>(ContainerID.main)
	}

}