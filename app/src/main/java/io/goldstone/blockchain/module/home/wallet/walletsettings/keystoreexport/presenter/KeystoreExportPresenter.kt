package io.goldstone.blockchain.module.home.wallet.walletsettings.keystoreexport.presenter

import android.widget.EditText
import com.blinnnk.util.SoftKeyboard
import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.crypto.getKeystoreFile
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable
import io.goldstone.blockchain.module.home.wallet.walletsettings.keystoreexport.view.KeystoreExportFragment

/**
 * @date 06/04/2018 1:46 AM
 * @author KaySaith
 */

class KeystoreExportPresenter(
	override val fragment: KeystoreExportFragment
) : BasePresenter<KeystoreExportFragment>() {

	fun getPrivateKeyByAddress(
		passwordInput: EditText,
		hold: String.() -> Unit
	) {
		fragment.activity?.apply {
			SoftKeyboard.hide(this)
		}
		WalletTable.getCurrentWalletInfo {
			fragment.context?.getKeystoreFile(it!!.address, passwordInput.text.toString()) {
				hold(it)
			}
		}
	}

}