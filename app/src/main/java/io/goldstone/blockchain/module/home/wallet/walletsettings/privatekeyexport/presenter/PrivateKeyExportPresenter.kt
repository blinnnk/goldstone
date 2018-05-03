package io.goldstone.blockchain.module.home.wallet.walletsettings.privatekeyexport.presenter

import android.widget.EditText
import com.blinnnk.util.SoftKeyboard
import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.crypto.getPrivateKey
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable
import io.goldstone.blockchain.module.home.wallet.walletsettings.privatekeyexport.view.PrivateKeyExportFragment

/**
 * @date 06/04/2018 1:02 AM
 * @author KaySaith
 */

class PrivateKeyExportPresenter(
	override val fragment: PrivateKeyExportFragment
) : BasePresenter<PrivateKeyExportFragment>() {

	fun getPrivateKeyByAddress(passwordInput: EditText, hold: String.() -> Unit) {
		fragment.activity?.apply { SoftKeyboard.hide(this) }
		WalletTable.getCurrentWalletInfo {
			fragment.context?.getPrivateKey(it!!.address, passwordInput.text.toString()) {
				hold(it)
			}
		}
	}

}