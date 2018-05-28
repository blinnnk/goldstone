package io.goldstone.blockchain.module.home.wallet.walletsettings.keystoreexport.presenter

import android.widget.EditText
import com.blinnnk.util.SoftKeyboard
import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.common.value.ImportWalletText
import io.goldstone.blockchain.crypto.getKeystoreFile
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable
import io.goldstone.blockchain.module.home.wallet.walletsettings.keystoreexport.view.KeystoreExportFragment
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.runOnUiThread
import org.jetbrains.anko.support.v4.toast

/**
 * @date 06/04/2018 1:46 AM
 * @author KaySaith
 */
class KeystoreExportPresenter(
	override val fragment: KeystoreExportFragment
) : BasePresenter<KeystoreExportFragment>() {

	fun getKeystoreByAddress(
		passwordInput: EditText,
		hold: String.() -> Unit
	) {
		if (passwordInput.text?.toString().isNullOrBlank()) {
			fragment.toast(ImportWalletText.exportWrongPassword)
			hold("")
			return
		}
		System.out.println("hell3")

		fragment.activity?.apply {
			SoftKeyboard.hide(this)
		}
		WalletTable.getCurrentWallet {
			doAsync {
				fragment.context?.getKeystoreFile(it!!.address, passwordInput.text.toString(), {
					hold("")
				}) {
					fragment.context?.runOnUiThread { hold(it) }
				}
			}
		}
	}

}