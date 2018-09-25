@file:Suppress("INACCESSIBLE_TYPE")

package io.goldstone.blockchain.crypto.keystore

import android.content.Context
import android.support.annotation.UiThread
import android.support.annotation.WorkerThread
import com.blinnnk.extension.forEachOrEnd
import com.blinnnk.extension.isNull
import com.blinnnk.extension.isTrue
import com.blinnnk.util.TinyNumberUtils
import io.goldstone.blockchain.common.error.AccountError
import io.goldstone.blockchain.common.language.ImportWalletText
import io.goldstone.blockchain.common.utils.LogUtil
import io.goldstone.blockchain.common.value.Config
import io.goldstone.blockchain.crypto.bip39.Mnemonic
import io.goldstone.blockchain.crypto.ethereum.getAddress
import io.goldstone.blockchain.crypto.ethereum.walletfile.WalletUtil
import io.goldstone.blockchain.crypto.multichain.CryptoValue
import io.goldstone.blockchain.crypto.utils.CryptoUtils
import io.goldstone.blockchain.crypto.utils.hexToByteArray
import io.goldstone.blockchain.kernel.network.GoldStoneAPI
import org.ethereum.geth.Geth
import org.ethereum.geth.KeyStore
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.runOnUiThread
import org.jetbrains.anko.uiThread
import java.io.File
import java.math.BigInteger

/**
 * @date 29/03/2018 4:25 PM
 * @author KaySaith
 */

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

@Throws
fun Context.storeRootKeyByWalletID(
	walletID: Int,
	privateKey: BigInteger,
	password: String
): Boolean {
	val fileName = "$walletID${CryptoValue.keystoreFilename}"
	val keystoreFile by lazy { File(filesDir!!, fileName) }
	/** Generate Keystore */
	val keyStore = KeyStore(keystoreFile.absolutePath, Geth.LightScryptN, Geth.LightScryptP)
	return try {
		keyStore.importECDSAKey(
			keyString(privateKey.toString(16)).hexToByteArray(),
			password
		)
		true
	} catch (error: Exception) {
		if (error.toString().contains("account already exists")) {
			throw Exception(ImportWalletText.existAddress)
		}
		false
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
	hold: (address: String?, error: AccountError) -> Unit
) {
	val keystoreFile by lazy { File(filesDir!!, filename) }
	/** Generate Keystore */
	val keyStore = KeyStore(keystoreFile.absolutePath, Geth.LightScryptN, Geth.LightScryptP)
	val address = CryptoUtils.getAddressFromPrivateKey(privateKey)
	/** Format PrivateKey */
	/** Import Private Key to Keystore */
	try {
		keyStore.importECDSAKey(keyString(privateKey).hexToByteArray(), password)
		hold(address, AccountError.None)
	} catch (error: Exception) {
		if (error.toString().contains("account already exists")) {
			hold(ImportWalletText.existAddress, AccountError.ExistAddress)
		}
		println(error)
	}
}

fun Context.getKeystoreFile(
	walletAddress: String,
	password: String,
	isBTCSeriesWallet: Boolean,
	isSingleChainWallet: Boolean,
	errorCallback: (AccountError) -> Unit,
	hold: (String) -> Unit
) {
	val isBTCSeriesOrSingChainWallet = TinyNumberUtils.hasTrue(isBTCSeriesWallet, isSingleChainWallet)
	val filename = CryptoValue.filename(walletAddress, isBTCSeriesWallet, isSingleChainWallet)
	val keystoreFile by lazy { File(filesDir!!, filename) }
	val keyStore = KeyStore(keystoreFile.absolutePath, Geth.LightScryptN, Geth.LightScryptP)
	if (isBTCSeriesOrSingChainWallet) try {
		hold(String(keyStore.exportKey(keyStore.accounts.get(0), password, password)))
	} catch (error: Exception) {
		runOnUiThread {
			errorCallback(AccountError.WrongPassword)
		}
		LogUtil.error("getKeystoreFile", error)
	} else (0 until keyStore.accounts.size()).forEach { index ->
		keyStore.accounts.get(index).address.hex.let {
			it.equals(walletAddress, true) isTrue {
				try {
					hold(String(keyStore.exportKey(keyStore.accounts.get(index), password, password)))
				} catch (error: Exception) {
					runOnUiThread {
						errorCallback(AccountError.WrongPassword)
					}
					LogUtil.error("getKeystoreFile", error)
				}
			}
		}
	}
}

fun Context.getKeystoreFileByWalletID(
	password: String,
	walletID: Int,
	errorCallback: (AccountError) -> Unit,
	hold: (String) -> Unit
) {
	val filename = "$walletID${CryptoValue.keystoreFilename}"
	val keystoreFile by lazy { File(filesDir!!, filename) }
	val keyStore = KeyStore(keystoreFile.absolutePath, Geth.LightScryptN, Geth.LightScryptP)
	try {
		hold(String(keyStore.exportKey(keyStore.accounts.get(0), password, password)))
	} catch (error: Exception) {
		runOnUiThread {
			errorCallback(AccountError.WrongPassword)
		}
	}
}

fun Context.getPrivateKey(
	walletAddress: String,
	password: String,
	isBTCSeriesWallet: Boolean,
	isSingleChainWallet: Boolean,
	isMainThreadResult: Boolean = true,
	@UiThread hold: (privateKey: String?, error: AccountError) -> Unit
) {
	doAsync {
		getKeystoreFile(
			walletAddress,
			password,
			isBTCSeriesWallet,
			isSingleChainWallet,
			{ hold(null, it) }
		) { keyStoreFile ->
			WalletUtil.getKeyPairFromWalletFile(
				keyStoreFile,
				password
			) { hold(null, it) }?.let {
				if (isMainThreadResult) runOnUiThread {
					hold(it.privateKey.toString(16), AccountError.None)
				} else hold(it.privateKey.toString(16), AccountError.None)
			}
		}
	}
}

fun Context.getPrivateKeyByWalletID(
	password: String,
	walletID: Int,
	errorCallback: (AccountError) -> Unit,
	@UiThread hold: (BigInteger) -> Unit
) {
	getKeystoreFileByWalletID(
		password,
		walletID,
		errorCallback
	) { it ->
		doAsync {
			// 因为提取和解析 `Keystore` 比较耗时, 所以 `KeyStore` 的操作放到异步
			WalletUtil.getKeyPairFromWalletFile(
				it,
				password,
				errorCallback
			)?.let {
				GoldStoneAPI.context.runOnUiThread { hold(it.privateKey) }
			}
		}
	}
}

fun Context.deleteAccount(
	walletAddress: String,
	password: String,
	isBTCSeriesWallet: Boolean,
	isSingleChainWallet: Boolean,
	callback: (error: AccountError) -> Unit
) {
	val isBTCSeriesOrSingChainWallet = TinyNumberUtils.hasTrue(isBTCSeriesWallet, isSingleChainWallet)
	val filename = CryptoValue.filename(walletAddress, isBTCSeriesWallet, isSingleChainWallet)
	val keystoreFile by lazy { File(filesDir!!, filename) }
	val keyStore = KeyStore(keystoreFile.absolutePath, Geth.LightScryptN, Geth.LightScryptP)
	// If there is't account found then return
	if (keyStore.accounts.size() == 0L) {
		callback(AccountError.None)
		return
	}
	var targetAccountIndex: Long? = if (isBTCSeriesOrSingChainWallet) 0 else null
	(0 until keyStore.accounts.size()).forEachOrEnd { index, isEnd ->
		keyStore.accounts.get(index).address.hex.let {
			if (it.equals(walletAddress, true) && !isBTCSeriesOrSingChainWallet) {
				targetAccountIndex = index
			}
			if (isEnd && !targetAccountIndex.isNull() || isBTCSeriesOrSingChainWallet) {
				// `BTC` 的 `Filename` 就是 `Address`
				try {
					keyStore.deleteAccount(keyStore.accounts.get(targetAccountIndex!!), password)
					callback(AccountError.None)
				} catch (error: Exception) {
					callback(AccountError.WrongPassword)
				}
			}
		}
	}
}

fun Context.deleteAccountByWalletID(
	walletID: Int,
	password: String,
	callback: (isSuccessFul: Boolean) -> Unit
) {
	val filename = "$walletID${CryptoValue.keystoreFilename}"
	val keystoreFile by lazy { File(filesDir!!, filename) }
	val keyStore = KeyStore(keystoreFile.absolutePath, Geth.LightScryptN, Geth.LightScryptP)
	// If there is't account found then return
	if (keyStore.accounts.size() == 0L) {
		callback(true)
		return
	}
	// `BTC` 的 `Filename` 就是 `Address`
	try {
		keyStore.deleteAccount(keyStore.accounts.get(0), password)
		callback(true)
	} catch (error: Exception) {
		callback(false)
	}
}

fun Context.verifyCurrentWalletKeyStorePassword(
	password: String,
	walletID: Int,
	@WorkerThread hold: (Boolean) -> Unit
) {
	doAsync {
		val currentType = Config.getCurrentWalletType()
		when {
			// 多链钱包随便找一个名下钱包地址进行验证即可
			currentType.isBIP44() -> {
				verifyKeystorePassword(
					password,
					Config.getCurrentBTCAddress(),
					true,
					false,
					hold
				)
			}
			currentType.isMultiChain() -> {
				verifyKeystorePasswordByWalletID(
					password,
					walletID,
					hold
				)
			}
		}
	}
}

fun Context.verifyKeystorePassword(
	password: String,
	address: String,
	isBTCSeriesWallet: Boolean,
	isSingleChainWallet: Boolean,
	hold: (Boolean) -> Unit
) {
	val isBTCSeriesOrSingChainWallet = TinyNumberUtils.hasTrue(isBTCSeriesWallet, isSingleChainWallet)
	val filename = CryptoValue.filename(address, isBTCSeriesWallet, isSingleChainWallet)
	val keystoreFile by lazy { File(filesDir!!, filename) }
	val keyStore = KeyStore(keystoreFile.absolutePath, Geth.LightScryptN, Geth.LightScryptP)
	// 先通过解锁来验证密码的正确性, 在通过结果执行删除钱包操作
	var accountIndex = 0L
	if (isBTCSeriesOrSingChainWallet) {
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

fun Context.verifyKeystorePasswordByWalletID(
	password: String,
	walletID: Int,
	hold: (Boolean) -> Unit
) {
	val filename = "$walletID${CryptoValue.keystoreFilename}"
	val keystoreFile by lazy { File(filesDir!!, filename) }
	val keyStore = KeyStore(keystoreFile.absolutePath, Geth.LightScryptN, Geth.LightScryptP)
	// 先通过解锁来验证密码的正确性, 在通过结果执行删除钱包操作
	try {
		keyStore.unlock(keyStore.accounts.get(0), password)
	} catch (error: Exception) {
		hold(false)
		LogUtil.error("wrong keystore password", error)
		return
	}
	keyStore.lock(keyStore.accounts.get(0).address)
	hold(true)
}

fun Context.updatePassword(
	walletAddress: String,
	oldPassword: String,
	newPassword: String,
	isBTCSeriesWallet: Boolean,
	isSingleChainWallet: Boolean,
	@UiThread hold: (privateKey: String?, error: AccountError) -> Unit
) {
	getPrivateKey(
		walletAddress,
		oldPassword,
		isBTCSeriesWallet,
		isSingleChainWallet
	) { privateKey, error ->
		if (!privateKey.isNull() && error.isNone()) deleteAccount(
			walletAddress,
			oldPassword,
			isBTCSeriesWallet,
			isSingleChainWallet
		) { accountError ->
			if (accountError.isNone()) getWalletByPrivateKey(
				privateKey!!,
				newPassword,
				CryptoValue.filename(walletAddress, isBTCSeriesWallet, isSingleChainWallet),
				hold
			)
			else hold(null, accountError)
		} else hold(null, error)
	}
}

fun Context.updatePasswordByWalletID(
	walletID: Int,
	oldPassword: String,
	newPassword: String,
	errorCallback: (Throwable) -> Unit,
	callback: () -> Unit
) {
	doAsync {
		getPrivateKeyByWalletID(
			oldPassword,
			walletID,
			{ error ->
				uiThread {
					errorCallback(error)
				}
			}
		) { privateKey ->
			deleteAccountByWalletID(
				walletID,
				oldPassword
			) { isSuccessful ->
				if (isSuccessful) {
					storeRootKeyByWalletID(walletID, privateKey, newPassword)
					callback()
				} else {
					uiThread { errorCallback(Throwable("private is null")) }
				}
			}
		}
	}
}