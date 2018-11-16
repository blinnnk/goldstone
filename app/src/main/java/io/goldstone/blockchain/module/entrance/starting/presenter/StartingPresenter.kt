package io.goldstone.blockchain.module.entrance.starting.presenter

import com.blinnnk.extension.addFragment
import com.blinnnk.extension.orZero
import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.common.sharedpreference.SharedWallet
import io.goldstone.blockchain.common.value.ContainerID
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable
import io.goldstone.blockchain.module.common.walletgeneration.walletgeneration.view.WalletGenerationFragment
import io.goldstone.blockchain.module.common.walletimport.walletimport.view.WalletImportFragment
import io.goldstone.blockchain.module.entrance.starting.view.StartingFragment

@Suppress("IMPLICIT_CAST_TO_ANY")
/**
 * @date 22/03/2018 2:56 AM
 * @author KaySaith
 */
class StartingPresenter(override val fragment: StartingFragment) :
	BasePresenter<StartingFragment>() {

	fun showCreateWalletFragment() {
		fragment.activity?.addFragment<WalletGenerationFragment>(ContainerID.splash)
	}

	fun showImportWalletFragment() {
		fragment.activity?.addFragment<WalletImportFragment>(ContainerID.splash)
	}

	fun updateWalletInfoForUserInfo(walletList: List<WalletTable>) {
		walletList.apply {
			// 记录当前最大的钱包 `ID` 用来生成默认头像和名字
			SharedWallet.updateMaxWalletID(maxBy { it.id }?.id.orZero())
			SharedWallet.updateWalletCount(size)
		}
	}
}