package io.goldstone.blockchain.module.home.wallet.walletsettings.privatekeyexport.presenter

import android.widget.EditText
import com.blinnnk.util.SoftKeyboard
import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.common.value.ImportWalletText
import io.goldstone.blockchain.crypto.getPrivateKey
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable
import io.goldstone.blockchain.module.home.wallet.walletsettings.privatekeyexport.view.PrivateKeyExportFragment
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.runOnUiThread
import org.jetbrains.anko.support.v4.toast

/**
 * @date 06/04/2018 1:02 AM
 * @author KaySaith
 */
class PrivateKeyExportPresenter(
	override val fragment: PrivateKeyExportFragment
) : BasePresenter<PrivateKeyExportFragment>() {

	fun getPrivateKeyByAddress(
		passwordInput: EditText,
		hold: String.() -> Unit
	) {
		if (passwordInput.text?.toString().isNullOrBlank()) {
			fragment.toast(ImportWalletText.exportWrongPassword)
			hold("")
			return
		}

		fragment.activity?.apply { SoftKeyboard.hide(this) }
		WalletTable.getCurrentWallet {
			doAsync {
				fragment.context?.getPrivateKey(it!!.address, passwordInput.text.toString(), {
					hold("")
				}) {
					fragment.context?.runOnUiThread { hold(it) }
				}
			}
		}
	}

}