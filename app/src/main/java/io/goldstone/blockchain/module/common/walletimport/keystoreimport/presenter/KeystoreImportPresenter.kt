package io.goldstone.blockchain.module.common.walletimport.keystoreimport.presenter

import android.widget.EditText
import com.blinnnk.extension.isNull
import com.blinnnk.extension.isTrue
import com.blinnnk.extension.otherwise
import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.common.language.AlertText
import io.goldstone.blockchain.common.language.CreateWalletText
import io.goldstone.blockchain.common.utils.LogUtil
import io.goldstone.blockchain.common.utils.UIUtils
import io.goldstone.blockchain.common.utils.alert
import io.goldstone.blockchain.crypto.ethereum.walletfile.WalletUtil
import io.goldstone.blockchain.module.common.walletimport.keystoreimport.view.KeystoreImportFragment
import io.goldstone.blockchain.module.common.walletimport.privatekeyimport.presenter.PrivateKeyImportPresenter
import io.goldstone.blockchain.module.common.walletimport.walletimport.view.WalletImportFragment
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.runOnUiThread
import java.math.BigInteger

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
		callback: (Boolean) -> Unit
	) {
		isAgree isTrue {
			val walletName =
				if (nameInput.text.isEmpty()) UIUtils.generateDefaultName()
				else nameInput.text.toString()
			doAsync {
				getPrivatekeyByKeystoreFile(keystore, password) { rootPrivateKey ->
					if (rootPrivateKey.isNull()) callback(false)
					else fragment.context?.let {
						PrivateKeyImportPresenter.importWalletByRootKey(
							it,
							rootPrivateKey!!,
							walletName,
							password.text.toString(),
							hintInput.text.toString(),
							callback
						)
					}
				}
			}
		} otherwise {
			fragment.context?.alert(CreateWalletText.agreeRemind)
			callback(false)
		}
	}

	override fun onFragmentShowFromHidden() {
		super.onFragmentShowFromHidden()
		setRootChildFragmentBackEvent<WalletImportFragment>(fragment)
	}

	private fun getPrivatekeyByKeystoreFile(
		keystore: String,
		password: EditText,
		callback: (privateKey: BigInteger?) -> Unit
	) {
		WalletUtil.getKeyPairFromWalletFile(
			keystore,
			password.text.toString()
		) {
			fragment.context?.runOnUiThread {
				fragment.context?.alert(AlertText.wrongKeyStorePassword)
				callback(null)
			}
			LogUtil.error(this.javaClass.simpleName, it)
		}?.let {
			callback(it.privateKey)
		}
	}
}