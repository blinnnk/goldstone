package io.goldstone.blockchain.module.common.walletimport.keystoreimport.presenter

import android.widget.EditText
import com.blinnnk.extension.isNull
import com.blinnnk.extension.isTrue
import com.blinnnk.extension.otherwise
import io.goldstone.blockchain.common.Language.CreateWalletText
import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.common.language.AlertText
import io.goldstone.blockchain.common.utils.LogUtil
import io.goldstone.blockchain.common.utils.UIUtils
import io.goldstone.blockchain.common.utils.alert
import io.goldstone.blockchain.crypto.CryptoValue.PrivateKeyType.BTCTest
import io.goldstone.blockchain.crypto.CryptoValue.PrivateKeyType.ETHERCAndETC
import io.goldstone.blockchain.crypto.walletfile.WalletUtil
import io.goldstone.blockchain.module.common.walletimport.keystoreimport.view.KeystoreImportFragment
import io.goldstone.blockchain.module.common.walletimport.privatekeyimport.presenter.PrivateKeyImportPresenter
import io.goldstone.blockchain.module.common.walletimport.walletimport.view.WalletImportFragment
import org.bitcoinj.core.ECKey
import org.bitcoinj.params.MainNetParams
import org.bitcoinj.params.TestNet3Params
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
		currentType: String,
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
				getPrivatekeyByKeystoreFile(keystore, password) {
					if (it.isNull()) callback(false)
					
					it?.let { privateKey ->
						when {
							currentType.equals(ETHERCAndETC.content, true) -> {
								importETHERC20OrETCWallet(
									walletName,
									password,
									hintInput,
									privateKey.toString(16),
									callback
								)
							}
							
							currentType.equals(BTCTest.content, true) -> {
								importBitcoinWallet(
									walletName,
									password,
									hintInput,
									ECKey.fromPrivate(privateKey).getPrivateKeyAsWiF(TestNet3Params.get()),
									true,
									callback
								)
							}
							
							else -> {
								importBitcoinWallet(
									walletName,
									password,
									hintInput,
									ECKey.fromPrivate(privateKey).getPrivateKeyAsWiF(MainNetParams.get()),
									false,
									callback
								)
							}
						}
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
	
	private fun importETHERC20OrETCWallet(
		walletName: String,
		password: EditText,
		hintInput: EditText,
		privateKey: String,
		callback: (Boolean) -> Unit
	) {
		PrivateKeyImportPresenter.importWallet(
			privateKey,
			password.text.toString(),
			walletName,
			fragment.context,
			true,
			hintInput.text?.toString(),
			callback
		)
	}
	
	private fun importBitcoinWallet(
		walletName: String,
		password: EditText,
		hintInput: EditText,
		privateKey: String,
		isTest: Boolean,
		callback: (Boolean) -> Unit
	) {
		PrivateKeyImportPresenter.importWalletByBTCPrivateKey(
			privateKey,
			password.text.toString(),
			walletName,
			fragment.context,
			hintInput.text?.toString(),
			isTest,
			callback
		)
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