package io.goldstone.blockchain.module.home.wallet.walletsettings.privatekeyexport.presenter

import android.widget.EditText
import com.blinnnk.util.SoftKeyboard
import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.common.value.ArgumentKey
import io.goldstone.blockchain.common.value.ImportWalletText
import io.goldstone.blockchain.crypto.CryptoValue
import io.goldstone.blockchain.crypto.bitcoin.BTCUtils
import io.goldstone.blockchain.crypto.bitcoin.exportBase58PrivateKey
import io.goldstone.blockchain.crypto.getPrivateKey
import io.goldstone.blockchain.module.home.wallet.walletsettings.privatekeyexport.view.PrivateKeyExportFragment
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.runOnUiThread
import org.jetbrains.anko.support.v4.toast
import org.jetbrains.anko.uiThread

/**
 * @date 06/04/2018 1:02 AM
 * @author KaySaith
 */
class PrivateKeyExportPresenter(
	override val fragment: PrivateKeyExportFragment
) : BasePresenter<PrivateKeyExportFragment>() {
	
	private val address by lazy {
		fragment.arguments?.getString(ArgumentKey.address)
	}
	private val isBTCAddress by lazy {
		fragment.arguments?.getBoolean(ArgumentKey.isBTCAddress)
	}
	
	fun getPrivateKeyByAddress(
		passwordInput: EditText,
		hold: String?.() -> Unit
	) {
		if (isBTCAddress == true) {
			getBTCPrivateKeyByAddress(passwordInput, hold)
		} else {
			getETHERCorETCPrivateKeyByAddress(passwordInput, hold)
		}
	}
	
	private fun getETHERCorETCPrivateKeyByAddress(
		passwordInput: EditText,
		hold: String.() -> Unit
	) {
		if (passwordInput.text?.toString().isNullOrBlank()) {
			fragment.toast(ImportWalletText.exportWrongPassword)
			hold("")
			return
		}
		
		fragment.activity?.apply { SoftKeyboard.hide(this) }
		address?.let {
			doAsync {
				fragment.context?.getPrivateKey(
					it,
					passwordInput.text.toString(),
					CryptoValue.keystoreFilename,
					{
						uiThread { hold("") }
					}
				) {
					fragment.context?.runOnUiThread { hold(it) }
				}
			}
		}
	}
	
	private fun getBTCPrivateKeyByAddress(
		passwordInput: EditText,
		hold: String?.() -> Unit
	) {
		val password = passwordInput.text?.toString().orEmpty()
		if (password.isEmpty()) {
			fragment.toast(ImportWalletText.exportWrongPassword)
			hold(null)
			return
		}
		fragment.activity?.apply { SoftKeyboard.hide(this) }
		address?.let { address ->
			doAsync {
				val isTest = BTCUtils.isValidTestnetAddress(address)
				fragment.context?.exportBase58PrivateKey(address, password, isTest) { secret ->
					uiThread {
						hold(secret)
					}
				}
			}
		}
	}
}