package io.goldstone.blockchain.module.common.walletimport.keystoreimport.presenter

import android.widget.EditText
import com.blinnnk.extension.isTrue
import com.blinnnk.extension.otherwise
import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.common.utils.LogUtil
import io.goldstone.blockchain.common.utils.UIUtils
import io.goldstone.blockchain.common.utils.alert
import io.goldstone.blockchain.common.value.AlertText
import io.goldstone.blockchain.common.value.CreateWalletText
import io.goldstone.blockchain.crypto.walletfile.WalletUtil
import io.goldstone.blockchain.module.common.walletimport.keystoreimport.view.KeystoreImportFragment
import io.goldstone.blockchain.module.common.walletimport.privatekeyimport.presenter.PrivateKeyImportPresenter
import io.goldstone.blockchain.module.common.walletimport.walletimport.view.WalletImportFragment
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.runOnUiThread

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
				WalletUtil.getKeyPairFromWalletFile(
					keystore,
					password.text.toString()
				) {
					fragment.context?.runOnUiThread {
						fragment.context?.runOnUiThread {
							fragment.context?.alert(AlertText.wrongKeyStorePassword)
							callback()
						}
						LogUtil.error(this.javaClass.simpleName, it)
					}
				}?.let {
					val walletName =
						if (nameInput.text.isEmpty()) UIUtils.generateDefaultName()
						else nameInput.text.toString()
					PrivateKeyImportPresenter.importWallet(
						it.privateKey.toString(16),
						password.text.toString(),
						walletName,
						fragment, hintInput.text?.toString()
					) {
						fragment.context?.runOnUiThread { callback() }
					}
				}
			}
		} otherwise {
			fragment.context?.alert(CreateWalletText.agreeRemind)
			callback()
		}
	}
	
	override fun onFragmentShowFromHidden() {
		super.onFragmentShowFromHidden()
		setRootChildFragmentBackEvent<WalletImportFragment>(fragment)
	}
}