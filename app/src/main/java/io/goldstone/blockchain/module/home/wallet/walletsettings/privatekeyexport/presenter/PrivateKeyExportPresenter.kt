package io.goldstone.blockchain.module.home.wallet.walletsettings.privatekeyexport.presenter

import android.widget.EditText
import com.blinnnk.util.SoftKeyboard
import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.common.value.ArgumentKey
import io.goldstone.blockchain.common.value.ImportWalletText
import io.goldstone.blockchain.crypto.bitcoin.BTCWalletUtils
import io.goldstone.blockchain.crypto.getPrivateKey
import io.goldstone.blockchain.crypto.utils.JavaKeystoreUtil
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
	
	private val address by lazy {
		fragment.arguments?.getString(ArgumentKey.address)
	}
	private val isBTCAddress by lazy {
		fragment.arguments?.getBoolean(ArgumentKey.isBTCAddress)
	}
	
	fun getPrivateKeyByAddress(
		passwordInput: EditText,
		hold: String.() -> Unit
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
					{
						fragment.context?.runOnUiThread { hold("") }
					}
				) {
					fragment.context?.runOnUiThread { hold(it) }
				}
			}
		}
	}
	
	private fun getBTCPrivateKeyByAddress(
		passwordInput: EditText,
		hold: String.() -> Unit
	) {
		if (passwordInput.text?.toString().isNullOrBlank()) {
			fragment.toast(ImportWalletText.exportWrongPassword)
			hold("")
			return
		}
		fragment.activity?.apply { SoftKeyboard.hide(this) }
		address?.let { address ->
			WalletTable.getCurrentWallet {
				it?.apply {
					// 解析当前地址的 `Address Index`
					val addressIndex = if (btcAddresses.contains(",")) {
						btcAddresses.split(",").find {
							it.contains(address)
						}?.substringAfter("|")?.toInt()
					} else {
						btcAddresses.substringAfter("|").toInt()
					}
					// 生成对应的 `Path`
					val targetPath = btcPath.substringBeforeLast("/") + "/" + addressIndex
					val mnemonicCode = JavaKeystoreUtil().decryptData(encryptMnemonic!!)
					// 获取该 `Address` 的 `PrivateKey`
					BTCWalletUtils.getBitcoinWalletByMnemonic(mnemonicCode, targetPath) { _, secret ->
						hold(secret)
					}
				}
			}
		}
	}
}