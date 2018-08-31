package io.goldstone.blockchain.module.common.walletimport.privatekeyimport.presenter

import android.content.Context
import android.widget.EditText
import com.blinnnk.extension.isTrue
import com.blinnnk.extension.removeStartAndEndValue
import com.blinnnk.extension.replaceWithPattern
import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.common.language.ImportWalletText
import io.goldstone.blockchain.common.utils.alert
import io.goldstone.blockchain.crypto.CryptoSymbol
import io.goldstone.blockchain.crypto.CryptoValue
import io.goldstone.blockchain.crypto.bitcoin.*
import io.goldstone.blockchain.crypto.bitcoincash.BCHWalletUtils
import io.goldstone.blockchain.crypto.eos.EOSWalletUtils
import io.goldstone.blockchain.crypto.getWalletByPrivateKey
import io.goldstone.blockchain.crypto.litecoin.ChainPrefix
import io.goldstone.blockchain.crypto.litecoin.LTCWalletUtils
import io.goldstone.blockchain.crypto.litecoin.storeLTCBase58PrivateKey
import io.goldstone.blockchain.crypto.utils.CryptoUtils
import io.goldstone.blockchain.crypto.walletfile.WalletUtil
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.presenter.CreateWalletPresenter
import io.goldstone.blockchain.module.common.walletimport.privatekeyimport.view.PrivateKeyImportFragment
import io.goldstone.blockchain.module.common.walletimport.walletimport.presenter.WalletImportPresenter
import io.goldstone.blockchain.module.common.walletimport.walletimport.view.WalletImportFragment
import io.goldstone.blockchain.module.home.profile.chain.nodeselection.presenter.NodeSelectionPresenter.Companion.setAllMainnet
import io.goldstone.blockchain.module.home.profile.chain.nodeselection.presenter.NodeSelectionPresenter.Companion.setAllTestnet
import org.jetbrains.anko.runOnUiThread

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
			{
				// Failed callback
				callback(false)
			}
		) { passwordValue, walletName ->
			when (type) {
				CryptoValue.PrivateKeyType.ETHERCAndETC ->
					PrivateKeyImportPresenter.importWallet(
						privateKeyInput.text.toString(),
						passwordValue,
						walletName,
						fragment.context,
						true,
						hintInput.text?.toString(),
						callback
					)

				CryptoValue.PrivateKeyType.BTC -> {
					PrivateKeyImportPresenter.importWalletByBTCPrivateKey(
						privateKeyInput.text.toString(),
						passwordValue,
						walletName,
						fragment.context,
						hintInput.text?.toString(),
						false
					) {
						if (it) setAllMainnet { callback(true) }
						else callback(false)
					}
				}

				CryptoValue.PrivateKeyType.LTC -> {
					PrivateKeyImportPresenter.importWalletByLTCPrivateKey(
						privateKeyInput.text.toString(),
						passwordValue,
						walletName,
						fragment.context,
						hintInput.text?.toString()
					) {
						if (it) setAllMainnet { callback(true) }
						else callback(false)
					}
				}

				CryptoValue.PrivateKeyType.BCH -> {
					importWalletByBCHPrivateKey(
						privateKeyInput.text.toString(),
						passwordValue,
						walletName,
						fragment.context,
						hintInput.text?.toString()
					) {
						if (it) setAllMainnet { callback(true) }
						else callback(false)
					}
				}

				CryptoValue.PrivateKeyType.EOS -> {
					importWalletByEOSPrivateKey(
						privateKeyInput.text.toString(),
						passwordValue,
						walletName,
						fragment.context,
						hintInput.text?.toString()
					) {
						if (it) setAllMainnet { callback(true) }
						else callback(false)
					}
				}

				CryptoValue.PrivateKeyType.BTCTest -> {
					// 跟随导入的测试网私钥切换全局测试网络状态
					PrivateKeyImportPresenter.importWalletByBTCPrivateKey(
						privateKeyInput.text.toString(),
						passwordValue,
						walletName,
						fragment.context,
						hintInput.text?.toString(),
						true
					) {
						if (it) setAllTestnet { callback(true) }
						else callback(false)
					}
				}
			}
		}
	}

	override fun onFragmentShowFromHidden() {
		super.onFragmentShowFromHidden()
		setRootChildFragmentBackEvent<WalletImportFragment>(fragment)
	}

	companion object {
		fun importWalletByLTCPrivateKey(
			wifPrivateKey: String,
			password: String,
			name: String,
			context: Context?,
			hint: String?,
			callback: (Boolean) -> Unit
		) {
			if (wifPrivateKey.length != CryptoValue.bitcoinPrivateKeyLength) {
				context?.alert(ImportWalletText.invalidPrivateKey)
				callback(false)
				return
			}
			// 检查比特币私钥地址格式是否是对应的网络
			if (!LTCWalletUtils.isValidWIFKey(wifPrivateKey, ChainPrefix.Litecoin)) {
				context?.alert(ImportWalletText.invalidLTCPrivateKey)
				callback(false)
				return
			}
			LTCWalletUtils.generateBase58AddressByWIFKey(wifPrivateKey, ChainPrefix.Litecoin).let { address ->
				context?.apply {
					storeLTCBase58PrivateKey(wifPrivateKey, address, password, true)
					// 存储可读信息到数据库
					WalletImportPresenter.insertWalletToDatabase(
						this,
						MultiChainAddresses(address, CryptoSymbol.ltc),
						name,
						"",
						MultiChainPath(),
						hint
					) {
						if (it) setAllTestnet { callback(true) }
						else callback(false)
					}
				}
			}
		}

		fun importWalletByEOSPrivateKey(
			wifPrivateKey: String,
			password: String,
			name: String,
			context: Context?,
			hint: String?,
			callback: (Boolean) -> Unit
		) {
			if (wifPrivateKey.length != CryptoValue.bitcoinPrivateKeyLength) {
				context?.alert(ImportWalletText.invalidPrivateKey)
				callback(false)
				return
			}
			// 检查比特币私钥地址格式是否是对应的网络
			if (!EOSWalletUtils.isValidPrivateKey(wifPrivateKey)) {
				context?.alert(ImportWalletText.invalidEOSPrivateKey)
				callback(false)
				return
			}
			EOSWalletUtils.generateBase58AddressByWIFKey(wifPrivateKey).let { address ->
				context?.apply {
					// `EOS` 的 `ChainPrefix` 使用的是 `BTC` 的 `Mainnet Prefix` 所以这里无论是否是测试状态都是标记为 `False`
					storeBase58PrivateKey(wifPrivateKey, address, password, false, true)
					// 存储可读信息到数据库
					WalletImportPresenter.insertWalletToDatabase(
						this,
						MultiChainAddresses(address, CryptoSymbol.eos),
						name,
						"",
						MultiChainPath(),
						hint
					) {
						if (it) setAllTestnet { callback(true) }
						else callback(false)
					}
				}
			}
		}

		fun importWalletByBCHPrivateKey(
			privateKey: String,
			password: String,
			name: String,
			context: Context?,
			hint: String?,
			callback: (Boolean) -> Unit
		) {
			if (privateKey.length != CryptoValue.bitcoinPrivateKeyLength) {
				context?.alert(ImportWalletText.invalidPrivateKey)
				callback(false)
				return
			}
			// 检查比特币私钥地址格式是否是对应的网络
			if (!BTCUtils.isValidMainnetPrivateKey(privateKey)) {
				context?.alert(ImportWalletText.invalidMainnetBTCPrivateKey)
				callback(false)
				return
			}

			BCHWalletUtils.getBCHAddressByWIFKey(privateKey).let { address ->
				context?.apply {
					// 存储私钥的 `KeyStore` 文件
					storeBase58PrivateKey(
						privateKey,
						address,
						password,
						false,
						true
					)
					// 存储可读信息到数据库
					WalletImportPresenter.insertWalletToDatabase(
						this,
						MultiChainAddresses(address, CryptoSymbol.bch),
						name,
						"",
						MultiChainPath(),
						hint
					) {
						if (it) setAllMainnet { callback(true) }
						else callback(false)
					}
				}
			}
		}

		fun importWalletByBTCPrivateKey(
			privateKey: String,
			password: String,
			name: String,
			context: Context?,
			hint: String?,
			isTest: Boolean,
			callback: (Boolean) -> Unit
		) {
			if (privateKey.length != CryptoValue.bitcoinPrivateKeyLength) {
				context?.alert(ImportWalletText.invalidPrivateKey)
				callback(false)
				return
			}
			// 检查比特币私钥地址格式是否是对应的网络
			if (isTest) {
				if (!BTCUtils.isValidTestnetPrivateKey(privateKey)) {
					context?.alert(ImportWalletText.unvalidTestnetBTCPrivateKey)
					callback(false)
					return
				}
			} else {
				if (!BTCUtils.isValidMainnetPrivateKey(privateKey)) {
					context?.alert(ImportWalletText.invalidMainnetBTCPrivateKey)
					callback(false)
					return
				}
			}
			BTCWalletUtils.getPublicKeyFromBase58PrivateKey(privateKey, isTest).let { address ->
				context?.apply {
					// 存储私钥的 `KeyStore` 文件
					storeBase58PrivateKey(
						privateKey,
						address,
						password,
						isTest,
						true
					)
					// 存储可读信息到数据库
					WalletImportPresenter.insertWalletToDatabase(
						this,
						MultiChainAddresses(address, CryptoSymbol.pureBTCSymbol),
						name,
						"",
						MultiChainPath(),
						hint
					) {
						if (it) {
							if (isTest) setAllTestnet { callback(true) }
							else setAllMainnet { callback(true) }
						} else {
							callback(false)
						}
					}
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
			val formatPrivateKey = privateKey.removePrefix("0x")
			// `MetaMask` 的私钥有的时候回是 63 位的导致判断有效性的时候回出错这里弥补上
			val currentPrivateKey =
				(if (formatPrivateKey.length == 63) "0$formatPrivateKey" else formatPrivateKey)
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