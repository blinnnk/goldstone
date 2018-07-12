@file:Suppress("INACCESSIBLE_TYPE")

package io.goldstone.blockchain.crypto

import android.content.Context
import com.blinnnk.extension.forEachOrEnd
import com.blinnnk.extension.isNull
import com.blinnnk.extension.isTrue
import io.goldstone.blockchain.common.utils.LogUtil
import io.goldstone.blockchain.common.utils.alert
import io.goldstone.blockchain.common.value.CommonText
import io.goldstone.blockchain.crypto.bip39.Mnemonic
import io.goldstone.blockchain.crypto.utils.hexToByteArray
import io.goldstone.blockchain.crypto.utils.prepend0xPrefix
import io.goldstone.blockchain.crypto.walletfile.WalletUtil
import io.goldstone.blockchain.kernel.network.GoldStoneAPI
import org.ethereum.geth.Geth
import org.ethereum.geth.KeyStore
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.runOnUiThread
import java.io.File

/**
 * @date 29/03/2018 4:25 PM
 * @author KaySaith
 */
fun Context.generateWallet(
	password: String,
	holdAddress: (mnemonicCode: String, address: String) -> Unit
) {
	val keystoreFile by lazy { File(filesDir!!, "keystore") }
	val path = "m/44'/60'/0'/0/0"
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
	hold: (address: String?) -> Unit
) {
	val keystoreFile by lazy { File(filesDir!!, "keystore") }
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
		keyStore.importECDSAKey(masterKey.privateKey.toString(16).hexToByteArray(), password)
	} catch (error: Exception) {
		println("getEthereumWalletByMnemonic$error")
	}
	hold(address)
}

fun Context.getWalletByPrivateKey(
	privateKey: String,
	password: String,
	hold: (address: String?) -> Unit
) {
	val keystoreFile by lazy { File(filesDir!!, "keystore") }
	/** Generate Keystore */
	val keyStore = KeyStore(keystoreFile.absolutePath, Geth.LightScryptN, Geth.LightScryptP)
	/** Convert PrivateKey To BigInteger */
	val currentPrivateKey = privateKey.toBigInteger(16)
	/** Get Public Key and Private Key*/
	val publicKey = ECKeyPair(currentPrivateKey, publicKeyFromPrivate(currentPrivateKey)).getAddress()
	val address = publicKey.prepend0xPrefix()
	/** Format PrivateKey */
	val keyString = when {
		privateKey.substring(0, 1) == "0" -> "0" + currentPrivateKey.toString(16)
		privateKey.length == 63 -> "0" + currentPrivateKey.toString(16)
		else -> currentPrivateKey.toString(16)
	}
	/** Import Private Key to Keystore */
	try {
		keyStore.importECDSAKey(keyString.hexToByteArray(), password)
	} catch (error: Exception) {
		println(error)
	}
	hold(address)
}

fun Context.getKeystoreFile(
	walletAddress: String,
	password: String,
	errorCallback: (Throwable) -> Unit,
	hold: (String) -> Unit
) {
	val keystoreFile by lazy { File(filesDir!!, "keystore") }
	val keyStore = KeyStore(keystoreFile.absolutePath, Geth.LightScryptN, Geth.LightScryptP)
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

fun Context.getPrivateKey(
	walletAddress: String,
	password: String,
	errorCallback: (Throwable) -> Unit,
	hold: (String) -> Unit
) {
	System.out.println("hello 1")
	getKeystoreFile(walletAddress, password, errorCallback) {
		WalletUtil.getKeyPairFromWalletFile(
			it,
			password,
			errorCallback
		)?.let {
			System.out.println("hello 2")
			runOnUiThread {
				hold(it.privateKey.toString(16))
			}
		}
	}
}

fun Context.deleteAccount(
	walletAddress: String,
	password: String,
	callback: (correctPassword: Boolean) -> Unit
) {
	val keystoreFile by lazy { File(filesDir!!, "keystore") }
	val keyStore = KeyStore(keystoreFile.absolutePath, Geth.LightScryptN, Geth.LightScryptP)
	// If there is't account found then return
	if (keyStore.accounts.size() == 0L) {
		callback(true)
		return
	}
	var targentAccountIndex: Long? = null
	
	(0 until keyStore.accounts.size()).forEachOrEnd { index, isEnd ->
		keyStore.accounts.get(index).address.hex.let {
			if (it.equals(walletAddress, true)) {
				targentAccountIndex = index
			}
			if (isEnd && !targentAccountIndex.isNull()) {
				verifyKeystorePassword(password) {
					if (it) {
						keyStore.deleteAccount(keyStore.accounts.get(targentAccountIndex!!), password)
						callback(true)
					} else {
						callback(false)
					}
				}
			}
		}
	}
}

fun Context.verifyKeystorePassword(password: String, hold: (Boolean) -> Unit) {
	val keystoreFile by lazy { File(filesDir!!, "keystore") }
	val keyStore = KeyStore(keystoreFile.absolutePath, Geth.LightScryptN, Geth.LightScryptP)
	// 先通过解锁来验证密码的正确性, 在通过结果执行删除钱包操作
	var isCorrect: Boolean
	try {
		keyStore.unlock(keyStore.accounts.get(0), password)
		isCorrect = true
	} catch (error: Exception) {
		hold(false)
		isCorrect = false
		LogUtil.error("wrong keystore password", error)
	}
	if (isCorrect) {
		keyStore.lock(keyStore.accounts.get(0).address)
		hold(true)
	}
}

fun Context.updatePassword(
	walletAddress: String,
	oldPassword: String,
	newPassword: String,
	errorCallback: () -> Unit = {},
	callback: () -> Unit
) {
	doAsync {
		getPrivateKey(walletAddress, oldPassword, {
			runOnUiThread {
				errorCallback()
			}
		}) { privateKey ->
			deleteAccount(walletAddress, oldPassword) {
				getWalletByPrivateKey(privateKey, newPassword) {
					GoldStoneAPI.context.runOnUiThread {
						callback()
					}
				}
			}
		}
	}
}