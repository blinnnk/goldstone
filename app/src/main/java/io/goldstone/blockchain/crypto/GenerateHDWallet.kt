@file:Suppress("INACCESSIBLE_TYPE")

package io.goldstone.blockchain.crypto

import android.content.Context
import com.blinnnk.extension.*
import io.goldstone.blockchain.common.language.CommonText
import io.goldstone.blockchain.common.language.ImportWalletText
import io.goldstone.blockchain.common.utils.LogUtil
import io.goldstone.blockchain.common.utils.TinyNumberUtils
import io.goldstone.blockchain.common.utils.alert
import io.goldstone.blockchain.common.value.Config
import io.goldstone.blockchain.common.value.WalletType
import io.goldstone.blockchain.crypto.bip39.Mnemonic
import io.goldstone.blockchain.crypto.utils.CryptoUtils
import io.goldstone.blockchain.crypto.utils.hexToByteArray
import io.goldstone.blockchain.crypto.walletfile.WalletUtil
import org.ethereum.geth.Geth
import org.ethereum.geth.KeyStore
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.runOnUiThread
import org.jetbrains.anko.uiThread
import java.io.File

/**
 * @date 29/03/2018 4:25 PM
 * @author KaySaith
 */
fun Context.generateWallet(
	password: String,
	path: String = DefaultPath.ethPath,
	holdAddress: (mnemonicCode: String, address: String) -> Unit
) {
	val keystoreFile by lazy { File(filesDir!!, "keystore") }
	try {
		/** Generate Mnemonic */
		val mnemonicCode = Mnemonic.generateMnemonic()
		/** Generate HD Wallet */
		val masterWallet = Mnemonic.mnemonicToKey(mnemonicCode, path)
		/** Generate Keystore */
		val keyStore = KeyStore(keystoreFile.absolutePath, Geth.LightScryptN, Geth.LightScryptP)
		/** Generate Keys */
		val masterKey = masterWallet.keyPair
		/** Get Public Key and Private Key*/
		val publicKey = masterKey.getAddress()
		val address = "0x" + publicKey.toLowerCase()
		holdAddress(mnemonicCode, address)
		/** Import Private Key to Keystore */
		keyStore.importECDSAKey(masterKey.privateKey.toString(16).hexToByteArray(), password)
	} catch (error: Exception) {
		LogUtil.error("generateWallet", error)
	}
}

fun Context.getEthereumWalletByMnemonic(
	mnemonicCode: String,
	pathValue: String,
	password: String,
	hold: (address: String) -> Unit
) {
	val keystoreFile by lazy { File(filesDir!!, CryptoValue.keystoreFilename) }
	/** Generate HD Wallet */
	val masterWallet = Mnemonic.mnemonicToKey(mnemonicCode, pathValue)
	/** Generate Keystore */
	val keyStore = KeyStore(keystoreFile.absolutePath, Geth.LightScryptN, Geth.LightScryptP)
	/** Generate Keys */
	val masterKey = masterWallet.keyPair
	/** Get Public Key and Private Key*/
	val publicKey = masterKey.getAddress()
	val address = "0x" + publicKey.toLowerCase()
	/** Import Private Key to Keystore */
	try {
		keyStore.importECDSAKey(
			keyString(masterKey.privateKey.toString(16)).hexToByteArray(),
			password
		)
		hold(address)
	} catch (error: Exception) {
		if (error.toString().contains("account already exists")) {
			hold(ImportWalletText.existAddress)
		}
		println("getEthereumWalletByMnemonic $error")
	}
}

val keyString: (secret: String) -> String = {
	val currentPrivateKey = it.toBigInteger(16)
	when {
		it.substring(0, 1) == "0" -> "0" + currentPrivateKey.toString(16)
		it.length == 63 -> "0" + currentPrivateKey.toString(16)
		else -> currentPrivateKey.toString(16)
	}
}

fun Context.getWalletByPrivateKey(
	privateKey: String,
	password: String,
	filename: String,
	hold: (address: String?) -> Unit
) {
	val keystoreFile by lazy { File(filesDir!!, filename) }
	/** Generate Keystore */
	val keyStore = KeyStore(keystoreFile.absolutePath, Geth.LightScryptN, Geth.LightScryptP)
	val address = CryptoUtils.getAddressFromPrivateKey(privateKey)
	/** Format PrivateKey */
	/** Import Private Key to Keystore */
	try {
		keyStore.importECDSAKey(keyString(privateKey).hexToByteArray(), password)
		hold(address)
	} catch (error: Exception) {
		if (error.toString().contains("account already exists")) {
			hold(ImportWalletText.existAddress)
		}
		println(error)
	}
}

fun Context.getKeystoreFile(
	walletAddress: String,
	password: String,
	isBTCWallet: Boolean,
	isSingleChainWallet: Boolean,
	errorCallback: (Throwable) -> Unit,
	hold: (String) -> Unit
) {
	val isBTCOrSingChainWallet = TinyNumberUtils.hasTrue(isBTCWallet, isSingleChainWallet)
	val filename = CryptoValue.filename(walletAddress, isBTCWallet, isSingleChainWallet)
	val keystoreFile by lazy { File(filesDir!!, filename) }
	val keyStore = KeyStore(keystoreFile.absolutePath, Geth.LightScryptN, Geth.LightScryptP)
	if (isBTCOrSingChainWallet) {
		try {
			hold(String(keyStore.exportKey(keyStore.accounts.get(0), password, password)))
		} catch (error: Exception) {
			runOnUiThread {
				errorCallback(error)
				alert(CommonText.wrongPassword)
			}
			LogUtil.error("getKeystoreFile", error)
		}
	} else {
		(0 until keyStore.accounts.size()).forEach { index ->
			keyStore.accounts.get(index).address.hex.let {
				it.equals(walletAddress, true) isTrue {
					try {
						hold(String(keyStore.exportKey(keyStore.accounts.get(index), password, password)))
					} catch (error: Exception) {
						runOnUiThread {
							errorCallback(error)
							alert(CommonText.wrongPassword)
						}
						LogUtil.error("getKeystoreFile", error)
					}
				}
			}
		}
	}
}

fun Context.getPrivateKey(
	walletAddress: String,
	password: String,
	isBTCWallet: Boolean,
	isSingleChainWallet: Boolean,
	errorCallback: (Throwable) -> Unit,
	hold: (String) -> Unit
) {
	getKeystoreFile(
		walletAddress,
		password,
		isBTCWallet,
		isSingleChainWallet,
		errorCallback
	) { it ->
		WalletUtil.getKeyPairFromWalletFile(
			it,
			password,
			errorCallback
		)?.let {
			runOnUiThread {
				hold(it.privateKey.toString(16))
			}
		}
	}
}

fun Context.deleteAccount(
	walletAddress: String,
	password: String,
	isBTCWallet: Boolean,
	isSingleChainWallet: Boolean,
	callback: (isSuccessFul: Boolean) -> Unit
) {
	val isBTCOrSingChainWallet = TinyNumberUtils.hasTrue(isBTCWallet, isSingleChainWallet)
	val filename = CryptoValue.filename(walletAddress, isBTCWallet, isSingleChainWallet)
	val keystoreFile by lazy { File(filesDir!!, filename) }
	val keyStore = KeyStore(keystoreFile.absolutePath, Geth.LightScryptN, Geth.LightScryptP)
	// If there is't account found then return
	if (keyStore.accounts.size() == 0L) {
		callback(true)
		return
	}
	var targentAccountIndex: Long? = if (isBTCOrSingChainWallet) 0 else null
	(0 until keyStore.accounts.size()).forEachOrEnd { index, isEnd ->
		keyStore.accounts.get(index).address.hex.let {
			if (it.equals(walletAddress, true) && !isBTCOrSingChainWallet) {
				targentAccountIndex = index
			}
			if (isEnd && !targentAccountIndex.isNull() || isBTCOrSingChainWallet) {
				// `BTC` 的 `Filename` 就是 `Address`
				try {
					keyStore.deleteAccount(keyStore.accounts.get(targentAccountIndex!!), password)
					callback(true)
				} catch (error: Exception) {
					callback(false)
				}
			}
		}
	}
}

fun Context.verifyCurrentWalletKeyStorePassword(password: String, hold: (Boolean) -> Unit) {
	when (Config.getCurrentWalletType()) {
		WalletType.BTCTestOnly.content -> {
			verifyKeystorePassword(
				password,
				Config.getCurrentBTCTestAddress(),
				true,
				true,
				hold
			)
		}

		WalletType.BTCOnly.content -> {
			verifyKeystorePassword(
				password,
				Config.getCurrentBTCAddress(),
				true,
				true,
				hold
			)
		}

		WalletType.ETHERCAndETCOnly.content -> {
			verifyKeystorePassword(
				password,
				Config.getCurrentEthereumAddress(),
				false,
				true,
				hold
			)
		}
		// 多链钱包随便找一个名下钱包地址进行验证即可
		WalletType.MultiChain.content -> {
			verifyKeystorePassword(
				password,
				Config.getCurrentBTCAddress(),
				true,
				false,
				hold
			)
		}
	}
}

fun Context.verifyKeystorePassword(
	password: String,
	address: String,
	isBTCWallet: Boolean,
	isSingleChainWallet: Boolean,
	hold: (Boolean) -> Unit
) {
	val isBTCOrSingChainWallet = TinyNumberUtils.hasTrue(isBTCWallet, isSingleChainWallet)
	val filename = CryptoValue.filename(address, isBTCWallet, isSingleChainWallet)
	val keystoreFile by lazy { File(filesDir!!, filename) }
	val keyStore = KeyStore(keystoreFile.absolutePath, Geth.LightScryptN, Geth.LightScryptP)
	// 先通过解锁来验证密码的正确性, 在通过结果执行删除钱包操作
	var accountIndex = 0L
	if (isBTCOrSingChainWallet) {
		try {
			keyStore.unlock(keyStore.accounts.get(0), password)
		} catch (error: Exception) {
			hold(false)
			LogUtil.error("wrong keystore password", error)
			return
		}
		keyStore.lock(keyStore.accounts.get(accountIndex).address)
		hold(true)
	} else {
		(0 until keyStore.accounts.size()).forEach {
			if (keyStore.accounts.get(it).address.hex.equals(address, true)) {
				accountIndex = it
				try {
					keyStore.unlock(keyStore.accounts.get(it), password)
				} catch (error: Exception) {
					hold(false)
					LogUtil.error("wrong keystore password", error)
					return@forEach
				}
				keyStore.lock(keyStore.accounts.get(accountIndex).address)
				hold(true)
			}
		}
	}
}

fun Context.updatePassword(
	walletAddress: String,
	oldPassword: String,
	newPassword: String,
	isBTCWallet: Boolean,
	isSingleChainWallet: Boolean,
	errorCallback: (Throwable) -> Unit,
	callback: () -> Unit
) {
	doAsync {
		getPrivateKey(
			walletAddress,
			oldPassword,
			isBTCWallet,
			isSingleChainWallet,
			{ error ->
				uiThread {
					errorCallback(error)
				}
			}
		) { privateKey ->
			deleteAccount(
				walletAddress,
				oldPassword,
				isBTCWallet,
				isSingleChainWallet
			) { isSuccessful ->
				if (isSuccessful) {
					getWalletByPrivateKey(
						privateKey,
						newPassword,
						CryptoValue.filename(walletAddress, isBTCWallet, isSingleChainWallet)
					) {
						uiThread {
							it isNotNull {
								callback()
							} otherwise {
								errorCallback(Throwable("private is null"))
							}
						}
					}
				} else {
					uiThread { errorCallback(Throwable("private is null")) }
				}
			}
		}
	}
}