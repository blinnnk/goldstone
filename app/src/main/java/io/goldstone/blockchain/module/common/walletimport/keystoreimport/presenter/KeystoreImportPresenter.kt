package io.goldstone.blockchain.module.common.walletimport.keystoreimport.presenter

import android.widget.EditText
import com.blinnnk.extension.isNull
import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.common.error.AccountError
import io.goldstone.blockchain.common.error.GoldStoneError
import io.goldstone.blockchain.common.utils.UIUtils
import io.goldstone.blockchain.crypto.ethereum.walletfile.WalletUtil
import io.goldstone.blockchain.module.common.walletimport.keystoreimport.view.KeystoreImportFragment
import io.goldstone.blockchain.module.common.walletimport.privatekeyimport.presenter.PrivateKeyImportPresenter
import io.goldstone.blockchain.module.common.walletimport.walletimport.view.WalletImportFragment
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

/**
 * @date 23/03/2018 1:49 AM
 * @author KaySaith
 */
class KeystoreImportPresenter(
	override val fragment: KeystoreImportFragment
) : BasePresenter<KeystoreImportFragment>() {

	fun importKeystoreWallet(
		keystore: String,
		password: EditText,
		nameInput: EditText,
		isAgree: Boolean,
		hintInput: EditText,
		callback: (GoldStoneError) -> Unit
	) {
		if (isAgree) {
			val walletName =
				if (nameInput.text.isEmpty()) UIUtils.generateDefaultName()
				else nameInput.text.toString()
			doAsync {
				val keyPair =
					WalletUtil.getKeyPairFromWalletFile(keystore, password.text.toString())
				if (keyPair.isNull()) uiThread {
					callback(AccountError.WrongPassword)
				} else fragment.context?.apply {
					PrivateKeyImportPresenter.importWalletByRootKey(
						this,
						keyPair!!.privateKey,
						walletName,
						password.text.toString(),
						hintInput.text.toString(),
						callback
					)
				}
			}
		} else callback(AccountError.AgreeTerms)
	}

	override fun onFragmentShowFromHidden() {
		super.onFragmentShowFromHidden()
		setRootChildFragmentBackEvent<WalletImportFragment>(fragment)
	}
}