package io.goldstone.blockchain.module.common.walletimport.walletimportcenter.presenter

import com.blinnnk.util.addFragmentAndSetArgument
import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.common.language.ImportMethodText
import io.goldstone.blockchain.common.language.ImportWalletText
import io.goldstone.blockchain.common.language.WalletText
import io.goldstone.blockchain.common.value.ContainerID
import io.goldstone.blockchain.module.common.walletimport.keystoreimport.view.KeystoreImportFragment
import io.goldstone.blockchain.module.common.walletimport.mnemonicimport.view.MnemonicImportDetailFragment
import io.goldstone.blockchain.module.common.walletimport.privatekeyimport.view.PrivateKeyImportFragment
import io.goldstone.blockchain.module.common.walletimport.walletimport.view.WalletImportFragment
import io.goldstone.blockchain.module.common.walletimport.walletimportcenter.view.WalletImportCenterFragment


/**
 * @author KaySaith
 * @date  2018/09/06
 */
class WalletImportCenterPresenter(
	override val fragment: WalletImportCenterFragment
) : BasePresenter<WalletImportCenterFragment>() {

		fun showMnemonicImportFragment() {
			showTargetFragment<MnemonicImportDetailFragment, WalletImportFragment>(
				ImportMethodText.mnemonic,
				ImportWalletText.importWallet
			)
		}

	fun showPrivateKeyImportFragment() {
		showTargetFragment<PrivateKeyImportFragment, WalletImportFragment>(
			ImportMethodText.privateKey,
			ImportWalletText.importWallet
		)
	}

	fun showKeystoreImportFragment() {
		showTargetFragment<KeystoreImportFragment, WalletImportFragment>(
			ImportMethodText.keystore,
			ImportWalletText.importWallet
		)
	}
}