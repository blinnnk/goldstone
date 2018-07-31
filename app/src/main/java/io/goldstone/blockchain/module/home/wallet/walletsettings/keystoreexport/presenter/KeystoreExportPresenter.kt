package io.goldstone.blockchain.module.home.wallet.walletsettings.keystoreexport.presenter

import android.widget.EditText
import com.blinnnk.util.SoftKeyboard
import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.common.value.ArgumentKey
import io.goldstone.blockchain.common.value.ImportWalletText
import io.goldstone.blockchain.crypto.CryptoValue
import io.goldstone.blockchain.crypto.bitcoin.exportBase58KeyStoreFile
import io.goldstone.blockchain.crypto.getKeystoreFile
import io.goldstone.blockchain.module.home.wallet.walletsettings.keystoreexport.view.KeystoreExportFragment
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.support.v4.toast
import org.jetbrains.anko.uiThread

/**
 * @date 06/04/2018 1:46 AM
 * @author KaySaith
 */
class KeystoreExportPresenter(
	override val fragment: KeystoreExportFragment
) : BasePresenter<KeystoreExportFragment>() {
	
	private val address by lazy { fragment.arguments?.getString(ArgumentKey.address) }
	
	fun getKeystoreByAddress(
		passwordInput: EditText,
		hold: String?.() -> Unit
	) {
		if (passwordInput.text?.toString().isNullOrBlank()) {
			fragment.toast(ImportWalletText.exportWrongPassword)
			hold(null)
			return
		}
		
		fragment.activity?.apply {
			SoftKeyboard.hide(this)
		}
		address?.let {
			val isBTC = address?.length == CryptoValue.bitcoinAddressLength
			doAsync {
				if (isBTC) {
					getBTCKeystoreFile(it, passwordInput) { keystoreJSON ->
						uiThread { hold(keystoreJSON) }
					}
				} else {
					getETHERC20OrETCKeystoreFile(it, passwordInput) { keystoreJSON ->
						uiThread { hold(keystoreJSON) }
					}
				}
			}
		}
	}
	
	private fun getBTCKeystoreFile(
		address: String,
		passwordInput: EditText,
		hold: (String?) -> Unit
	) {
		fragment.context?.exportBase58KeyStoreFile(
			address,
			passwordInput.text.toString()
		) {
			hold(it)
		}
	}
	
	private fun getETHERC20OrETCKeystoreFile(
		address: String,
		passwordInput: EditText,
		hold: (String?) -> Unit
	) {
		fragment.context?.getKeystoreFile(
			address,
			passwordInput.text.toString(),
			"keystore",
			{
				hold("")
			}
		) { it ->
			hold(it)
		}
	}
}