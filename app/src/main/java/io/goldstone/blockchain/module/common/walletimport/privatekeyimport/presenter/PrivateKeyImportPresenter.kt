package io.goldstone.blockchain.module.common.walletimport.privatekeyimport.presenter

import android.support.v4.app.Fragment
import android.widget.EditText
import com.blinnnk.extension.isTrue
import com.blinnnk.extension.removeStartAndEndValue
import com.blinnnk.extension.replaceWithPattern
import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.common.utils.alert
import io.goldstone.blockchain.common.value.Config
import io.goldstone.blockchain.common.value.ImportWalletText
import io.goldstone.blockchain.crypto.CryptoValue
import io.goldstone.blockchain.crypto.MultiChainAddresses
import io.goldstone.blockchain.crypto.MultiChainPath
import io.goldstone.blockchain.crypto.bitcoin.BTCUtils
import io.goldstone.blockchain.crypto.bitcoin.BTCWalletUtils
import io.goldstone.blockchain.crypto.getWalletByPrivateKey
import io.goldstone.blockchain.crypto.walletfile.WalletUtil
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.presenter.CreateWalletPresenter
import io.goldstone.blockchain.module.common.walletimport.privatekeyimport.view.PrivateKeyImportFragment
import io.goldstone.blockchain.module.common.walletimport.walletimport.presenter.WalletImportPresenter
import io.goldstone.blockchain.module.common.walletimport.walletimport.view.WalletImportFragment

/**
 * @date 23/03/2018 2:13 AM
 * @author KaySaith
 */
class PrivateKeyImportPresenter(
	override val fragment: PrivateKeyImportFragment
) : BasePresenter<PrivateKeyImportFragment>() {
	
	fun importWalletByPrivateKey(
		type: CryptoValue.PrivateKeyType,
		privateKeyInput: EditText,
		passwordInput: EditText,
		repeatPasswordInput: EditText,
		isAgree: Boolean,
		nameInput: EditText,
		hintInput: EditText,
		callback: () -> Unit
	) {
		privateKeyInput.text.isEmpty() isTrue {
			fragment.context?.alert(ImportWalletText.privateKeyAlert)
			callback()
			return
		}
		CreateWalletPresenter.checkInputValue(
			nameInput.text.toString(),
			passwordInput.text.toString(),
			repeatPasswordInput.text.toString(),
			isAgree,
			fragment.context,
			{
				// Failed callback
				callback()
			}
		) { passwordValue, walletName ->
			when (type) {
				CryptoValue.PrivateKeyType.ETHERCAndETC ->
					PrivateKeyImportPresenter.importWallet(
						privateKeyInput.text.toString(),
						passwordValue,
						walletName,
						fragment,
						hintInput.text?.toString(),
						callback
					)
				CryptoValue.PrivateKeyType.BTC ->
					PrivateKeyImportPresenter.importWalletByBTCPrivateKey(
						privateKeyInput.text.toString(),
						passwordValue,
						walletName,
						fragment,
						hintInput.text?.toString(),
						false,
						callback
					)
				
				CryptoValue.PrivateKeyType.BTCTest -> {
					Config.updateIsTestEnvironment(true)
					PrivateKeyImportPresenter.importWalletByBTCPrivateKey(
						privateKeyInput.text.toString(),
						passwordValue,
						walletName,
						fragment,
						hintInput.text?.toString(),
						true,
						callback
					)
				}
			}
		}
	}
	
	override fun onFragmentShowFromHidden() {
		super.onFragmentShowFromHidden()
		setRootChildFragmentBackEvent<WalletImportFragment>(fragment)
	}
	
	companion object {
		fun importWalletByBTCPrivateKey(
			privateKey: String,
			password: String,
			name: String,
			fragment: Fragment,
			hint: String?,
			isTest: Boolean,
			callback: () -> Unit
		) {
			if (privateKey.length != CryptoValue.bitcoinPrivateKeyLength) {
				fragment.context?.alert(ImportWalletText.unvalidPrivateKey)
				callback()
				return
			}
			// 检查比特币私钥地址格式是否是对应的网络
			if (isTest) {
				if (!BTCUtils.isValidTestnetPrivateKey(privateKey)) {
					fragment.context?.alert(ImportWalletText.unvalidTestnetBTCPrivateKey)
					callback()
					return
				}
			} else {
				if (!BTCUtils.isValidMainnetPrivateKey(privateKey)) {
					fragment.context?.alert(ImportWalletText.unvalidMainnetBTCPrivateKey)
					callback()
					return
				}
			}
			fragment.context?.getWalletByPrivateKey(CryptoValue.basicLockKey, password) { _ ->
				// use ethereum geth keystore to verify btc user password, this value is unuseful
				// just ignore the value
				BTCWalletUtils.getPublicKeyFromBase58PrivateKey(privateKey, isTest) { address ->
					WalletImportPresenter.insertWalletToDatabase(
						fragment,
						MultiChainAddresses(
							"",
							"",
							if (isTest) "" else address,
							if (isTest) address else ""
						),
						name,
						"",
						MultiChainPath("", "", "", ""),
						hint,
						callback
					)
				}
			}
		}
		
		/**
		 * 导入 `keystore` 是先把 `keystore` 解密成 `private key` 在存储, 所以这个方法是公用的
		 */
		fun importWallet(
			privateKey: String,
			password: String,
			name: String,
			fragment: Fragment,
			hint: String? = null,
			callback: () -> Unit
		) {
			// 如果是包含 `0x` 开头格式的私钥地址移除 `0x`
			val formatPrivateKey = privateKey.removePrefix("0x")
			// `Metamask` 的私钥有的时候回是 63 位的导致判断有效性的时候回出错这里弥补上
			val currentPrivateKey =
				(if (formatPrivateKey.length == 63) "0$formatPrivateKey" else formatPrivateKey)
					.replaceWithPattern()
					.replace("\n", "")
					.removeStartAndEndValue(" ")
			// 首先检查私钥地址是否合规
			if (!WalletUtil.isValidPrivateKey(currentPrivateKey)) {
				fragment.context?.alert(ImportWalletText.unvalidPrivateKey)
				callback()
				return
			}
			// 解析私钥并导入钱包
			fragment.context?.getWalletByPrivateKey(currentPrivateKey, password) { address ->
				address?.let {
					WalletImportPresenter.insertWalletToDatabase(
						fragment,
						MultiChainAddresses(it, it, "", ""),
						name,
						"",
						MultiChainPath("", "", "", ""),
						hint,
						callback
					)
				}
			}
		}
	}
}