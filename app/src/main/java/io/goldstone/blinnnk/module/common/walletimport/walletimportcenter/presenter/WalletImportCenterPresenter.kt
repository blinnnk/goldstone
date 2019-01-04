package io.goldstone.blinnnk.module.common.walletimport.walletimportcenter.presenter

import io.goldstone.blinnnk.common.base.basefragment.BasePresenter
import io.goldstone.blinnnk.module.common.walletimport.keystoreimport.view.KeystoreImportFragment
import io.goldstone.blinnnk.module.common.walletimport.mnemonicimport.view.MnemonicImportDetailFragment
import io.goldstone.blinnnk.module.common.walletimport.privatekeyimport.view.PrivateKeyImportFragment
import io.goldstone.blinnnk.module.common.walletimport.walletimport.view.WalletImportFragment
import io.goldstone.blinnnk.module.common.walletimport.walletimportcenter.view.WalletImportCenterFragment


/**
 * @author KaySaith
 * @date  2018/09/06
 */
class WalletImportCenterPresenter(
	override val fragment: WalletImportCenterFragment
) : BasePresenter<WalletImportCenterFragment>() {

	fun showMnemonicImportFragment() {
		showTargetFragment<MnemonicImportDetailFragment, WalletImportFragment>()
	}

	fun showPrivateKeyImportFragment() {
		showTargetFragment<PrivateKeyImportFragment, WalletImportFragment>()
	}

	fun showKeystoreImportFragment() {
		showTargetFragment<KeystoreImportFragment, WalletImportFragment>()
	}
}