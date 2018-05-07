package io.goldstone.blockchain.module.common.walletimport.mnemonicimport.presenter

import android.widget.EditText
import com.blinnnk.extension.isTrue
import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.common.utils.alert
import io.goldstone.blockchain.crypto.getWalletByMnemonic
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.presenter.CreateWalletPresenter
import io.goldstone.blockchain.module.common.walletimport.mnemonicimport.view.MnemonicImportDetailFragment
import io.goldstone.blockchain.module.common.walletimport.walletimport.presenter.WalletImportPresenter

/**
 * @date 23/03/2018 1:46 AM
 * @author KaySaith
 */

class MnemonicImportDetailPresenter(
	override val fragment: MnemonicImportDetailFragment
) : BasePresenter<MnemonicImportDetailFragment>() {

	fun importWalletByMnemonic(
		mnemonicInput: EditText,
		passwordInput: EditText,
		repeatPasswordInput: EditText,
		hintInput: EditText,
		isAgree: Boolean,
		nameInput: EditText
	) {
		mnemonicInput.text.isEmpty() isTrue {
			fragment.context?.alert("mnemonic is not correct")
			return
		}
		CreateWalletPresenter.checkInputValue(
			nameInput.text.toString(),
			passwordInput.text.toString(),
			repeatPasswordInput.text.toString(),
			isAgree,
			fragment.context
		) { passwordValue, walletName ->
			importWallet(
				mnemonicInput.text.toString(), passwordValue, walletName, hintInput.text?.toString()
			)
		}
	}

	private fun importWallet(mnemonic: String, password: String, name: String, hint: String? = null) {
		fragment.context?.getWalletByMnemonic(mnemonic, password) { address ->
			address?.let {
				WalletImportPresenter.insertWalletToDatabase(fragment, it, name, hint)
			}
		}
	}

}