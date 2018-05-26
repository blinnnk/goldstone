package io.goldstone.blockchain.module.common.walletimport.keystoreimport.presenter

import android.widget.EditText
import com.blinnnk.extension.isTrue
import com.blinnnk.extension.otherwise
import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.common.utils.LogUtil
import io.goldstone.blockchain.common.utils.alert
import io.goldstone.blockchain.common.value.AlertText
import io.goldstone.blockchain.common.value.CreateWalletText
import io.goldstone.blockchain.crypto.convertKeystoreToModel
import io.goldstone.blockchain.module.common.walletimport.keystoreimport.view.KeystoreImportFragment
import io.goldstone.blockchain.module.common.walletimport.privatekeyimport.presenter.PrivateKeyImportPresenter
import io.goldstone.blockchain.module.home.wallet.walletdetail.view.DecryptKeystore
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.runOnUiThread
import org.web3j.crypto.Wallet

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
		callback: () -> Unit
	) {
		isAgree isTrue {
			doAsync {
				try {
					Wallet.decrypt(
						password.text.toString(),
						DecryptKeystore.GenerateFile(keystore.convertKeystoreToModel())
					)?.let {
						PrivateKeyImportPresenter.importWallet(
							it.privateKey.toString(16),
							password.text.toString(),
							nameInput.text.toString(),
							fragment, hintInput.text?.toString()
						) {
							fragment.context?.runOnUiThread { callback() }
						}
					}
				} catch (error: Exception) {
					fragment.context?.runOnUiThread {
						fragment.context?.alert(AlertText.wrongKeyStorePassword)
						callback()
					}
					LogUtil.error(this.javaClass.simpleName, error)
				}
			}
		} otherwise {
			fragment.context?.alert(CreateWalletText.agreeRemind)
		}

	}
}