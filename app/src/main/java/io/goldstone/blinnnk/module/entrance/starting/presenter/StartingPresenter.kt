package io.goldstone.blinnnk.module.entrance.starting.presenter

import com.blinnnk.extension.addFragment
import io.goldstone.blinnnk.common.base.basefragment.BasePresenter
import io.goldstone.blinnnk.common.sharedpreference.SharedWallet
import io.goldstone.blinnnk.common.value.ContainerID
import io.goldstone.blinnnk.module.common.walletgeneration.createwallet.model.WalletTable
import io.goldstone.blinnnk.module.common.walletgeneration.walletgeneration.view.WalletGenerationFragment
import io.goldstone.blinnnk.module.common.walletimport.walletimport.view.WalletImportFragment
import io.goldstone.blinnnk.module.entrance.starting.view.StartingFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

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

	fun updateWalletInfoForUserInfo(count: Int) {
		GlobalScope.launch(Dispatchers.Default) {
			val maxWalletID = WalletTable.dao.getMaxID()
			// 记录当前最大的钱包 `ID` 用来生成默认头像和名字
			SharedWallet.updateMaxWalletID(maxWalletID)
			SharedWallet.updateWalletCount(count)
		}
	}
}