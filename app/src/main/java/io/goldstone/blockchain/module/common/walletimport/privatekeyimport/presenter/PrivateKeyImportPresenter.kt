package io.goldstone.blockchain.module.common.walletimport.privatekeyimport.presenter

import android.content.Context
import android.widget.EditText
import com.blinnnk.extension.isTrue
import com.blinnnk.extension.removeStartAndEndValue
import com.blinnnk.extension.replaceWithPattern
import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.common.language.ImportWalletText
import io.goldstone.blockchain.common.utils.alert
import io.goldstone.blockchain.common.value.Config
import io.goldstone.blockchain.crypto.multichain.MultiChainAddresses
import io.goldstone.blockchain.crypto.multichain.MultiChainPath
import io.goldstone.blockchain.crypto.ethereum.walletfile.WalletUtil
import io.goldstone.blockchain.crypto.keystore.getWalletByPrivateKey
import io.goldstone.blockchain.crypto.keystore.keyString
import io.goldstone.blockchain.crypto.keystore.storeRootKeyByWalletID
import io.goldstone.blockchain.crypto.multichain.CryptoValue
import io.goldstone.blockchain.crypto.utils.CryptoUtils
import io.goldstone.blockchain.crypto.utils.MultiChainUtils
import io.goldstone.blockchain.crypto.utils.clean0xPrefix
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.presenter.CreateWalletPresenter
import io.goldstone.blockchain.module.common.walletimport.privatekeyimport.view.PrivateKeyImportFragment
import io.goldstone.blockchain.module.common.walletimport.walletimport.presenter.WalletImportPresenter
import io.goldstone.blockchain.module.common.walletimport.walletimport.view.WalletImportFragment
import org.jetbrains.anko.runOnUiThread
import java.math.BigInteger

/**
 * @date 23/03/2018 2:13 AM
 * @author KaySaith
 */
class PrivateKeyImportPresenter(
	override val fragment: PrivateKeyImportFragment
) : BasePresenter<PrivateKeyImportFragment>() {

	fun importWalletByPrivateKey(
		privateKeyInput: EditText,
		passwordInput: EditText,
		repeatPasswordInput: EditText,
		isAgree: Boolean,
		nameInput: EditText,
		hintInput: EditText,
		callback: (Boolean) -> Unit
	) {
		privateKeyInput.text.isEmpty() isTrue {
			fragment.context?.alert(ImportWalletText.privateKeyAlert)
			callback(false)
			return
		}
		CreateWalletPresenter.checkInputValue(
			nameInput.text.toString(),
			passwordInput.text.toString(),
			repeatPasswordInput.text.toString(),
			isAgree,
			fragment.context,
			{ callback(false) } // Error Callback
		) { passwordValue, walletName ->
			val rootPrivateKey =
				MultiChainUtils.getRootPrivateKey(privateKeyInput.text.toString())
			fragment.context?.let {
				importWalletByRootKey(
					it,
					rootPrivateKey,
					walletName,
					passwordValue,
					hintInput.text.toString(),
					callback
				)
			}
		}
	}

	override fun onFragmentShowFromHidden() {
		super.onFragmentShowFromHidden()
		setRootChildFragmentBackEvent<WalletImportFragment>(fragment)
	}

	companion object {

		fun importWalletByRootKey(
			context: Context,
			rootPrivateKey: BigInteger,
			walletName: String,
			password: String,
			hint: String,
			callback: (Boolean) -> Unit
		) {
			val multiChainAddresses =
				MultiChainUtils.getMultiChainAddressesByRootKey(rootPrivateKey)
			context.apply {
				// 即将创建的钱包 `ID` 的值是当前最大钱包 `ID + 1`
				val thisWalletID = Config.getMaxWalletID() + 1
				// 如果成功存储 私钥 到 KeyStore
				if (storeRootKeyByWalletID(thisWalletID, rootPrivateKey, password)) {
					// 那么就存储可读信息到数据库
					WalletImportPresenter.insertWalletToDatabase(
						this,
						multiChainAddresses,
						walletName,
						"",
						MultiChainPath(),
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
			context: Context?,
			isSingleChainWallet: Boolean,
			hint: String? = null,
			callback: (Boolean) -> Unit
		) {
			// 如果是包含 `0x` 开头格式的私钥地址移除 `0x`
			val formatPrivateKey = privateKey.clean0xPrefix()
			// `MetaMask` 的私钥有的时候回是 63 位的导致判断有效性的时候回出错这里弥补上
			val currentPrivateKey =
				keyString(formatPrivateKey)
					.replaceWithPattern()
					.replace("\n", "")
					.removeStartAndEndValue(" ")
			// 首先检查私钥地址是否合规
			if (!WalletUtil.isValidPrivateKey(currentPrivateKey)) {
				context?.alert(ImportWalletText.invalidPrivateKey)
				callback(false)
				return
			}
			/**
			 * `Single Chain Wallet` 的地址可能存在于某个已经导入的 `Bip44 Mnemonic Wallet` 的名下
			 * 所以所有单链钱包全部用额外的 `Keystore` 规则进行存储
			 * [CryptoValue.singleChainFile] + `Wallet Address`
			 */
			val filename =
				if (isSingleChainWallet)
					CryptoValue.singleChainFile(CryptoUtils.getAddressFromPrivateKey(currentPrivateKey))
				else CryptoValue.keystoreFilename
			// 解析私钥并导入钱包
			context?.getWalletByPrivateKey(
				currentPrivateKey,
				password,
				filename
			) { address ->
				if (address.equals(ImportWalletText.existAddress, true)) {
					context.runOnUiThread {
						alert(ImportWalletText.existAddress)
						callback(false)
					}
				} else {
					address?.let {
						WalletImportPresenter.insertWalletToDatabase(
							context,
							MultiChainAddresses(it),
							name,
							"",
							MultiChainPath(),
							hint,
							callback
						)
					}
				}
			}
		}
	}
}