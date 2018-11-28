@file:Suppress("INACCESSIBLE_TYPE")

package io.goldstone.blockchain.crypto.keystore

import android.content.Context
import com.blinnnk.extension.forEachOrEnd
import com.blinnnk.extension.isNotNull
import com.blinnnk.extension.isNull
import com.blinnnk.extension.isTrue
import com.blinnnk.util.TinyNumberUtils
import io.goldstone.blockchain.common.error.AccountError
import io.goldstone.blockchain.common.language.ImportWalletText
import io.goldstone.blockchain.common.utils.LogUtil
import io.goldstone.blockchain.crypto.bip39.Mnemonic
import io.goldstone.blockchain.crypto.ethereum.ECKeyPair
import io.goldstone.blockchain.crypto.ethereum.getAddress
import io.goldstone.blockchain.crypto.ethereum.walletfile.WalletUtil
import io.goldstone.blockchain.crypto.multichain.CryptoValue
import io.goldstone.blockchain.crypto.utils.CryptoUtils
import io.goldstone.blockchain.crypto.utils.hexToByteArray
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.ethereum.geth.Geth
import org.ethereum.geth.KeyStore
import java.io.File
import java.math.BigInteger

/**
 * @date 29/03/2018 4:25 PM
 * @author KaySaith
 */

fun generateETHSeriesAddress(
	mnemonicCode: String,
	pathValue: String
): ECKeyPair {
	/** Generate HD Wallet */
	val masterWallet = Mnemonic.mnemonicToKey(mnemonicCode, pathValue)
	return masterWallet.keyPair
}

fun Context.generateMnemonicVerifyKeyStore(
	walletID: Int,
	mnemonicCode: String,
	pathValue: String,
	password: String,
	hold: (address: String?, error: AccountError) -> Unit
) {
	val keystoreFile by lazy { File(filesDir!!, "$walletID${CryptoValue.keystoreFilename}") }
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
		hold(address, AccountError.None)
	} catch (error: Exception) {
		if (error.toString().contains("account already exists")) {
			hold(null, AccountError.ExistAddress)
		} else hold(null, AccountError.WrongPassword)
	}
}

fun Context.getEthereumWalletByMnemonic(
	mnemonicCode: String,
	pathValue: String,
	password: String,
	hold: (address: String?, error: AccountError) -> Unit
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
		hold(address, AccountError.None)
	} catch (error: Exception) {
		if (error.toString().contains("account already exists")) {
			hold(null, AccountError.ExistAddress)
		} else hold(null, AccountError.WrongPassword)
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

fun Context.getKeystoreFile(
	walletAddress: String,
	password: String,
	isBTCSeriesWallet: Boolean,
	hold: (keystoreFile: String?, error: AccountError) -> Unit
) {
	val isBTCSeriesOrSingChainWallet = TinyNumberUtils.hasTrue(isBTCSeriesWallet)
	val filename = CryptoValue.filename(walletAddress, isBTCSeriesWallet)
	val keystoreFile by lazy { File(filesDir!!, filename) }
	val keyStore = KeyStore(keystoreFile.absolutePath, Geth.LightScryptN, Geth.LightScryptP)
	if (isBTCSeriesOrSingChainWallet) try {
		hold(String(keyStore.exportKey(keyStore.accounts.get(0), password, password)), AccountError.None)
	} catch (error: Exception) {
		hold(null, AccountError.WrongPassword)
	} else (0 until keyStore.accounts.size()).forEach { index ->
		keyStore.accounts.get(index).address.hex.let {
			it.equals(walletAddress, true) isTrue {
				try {
					hold(String(keyStore.exportKey(keyStore.accounts.get(index), password, password)), AccountError.None)
				} catch (error: Exception) {
					hold(null, AccountError.WrongPassword)
					LogUtil.error("getKeystoreFile", error)
				}
			}
		}
	}
}

fun Context.getKeystoreFileByWalletID(
	password: String,
	walletID: Int,
	hold: (keystoreFile: String?, error: AccountError) -> Unit
) {
	val filename = "$walletID${CryptoValue.keystoreFilename}"
	val keystoreFile by lazy { File(filesDir!!, filename) }
	val keyStore = KeyStore(keystoreFile.absolutePath, Geth.LightScryptN, Geth.LightScryptP)
	try {
		hold(String(keyStore.exportKey(keyStore.accounts.get(0), password, password)), AccountError.None)
	} catch (error: Exception) {
		hold(null, AccountError.WrongPassword)
	}
}

fun Context.getPrivateKey(
	walletAddress: String,
	password: String,
	isBTCSeriesWallet: Boolean,
	hold: (privateKey: String?, error: AccountError) -> Unit
) {
	getKeystoreFile(walletAddress, password, isBTCSeriesWallet) { keyStoreFile, error ->
		if (keyStoreFile.isNotNull() && error.isNone()) {
			val keyPair =
				WalletUtil.getKeyPairFromWalletFile(keyStoreFile, password)
			if (keyPair.isNull()) hold(null, AccountError.WrongPassword)
			else hold(keyPair.privateKey.toString(16), AccountError.None)
		} else hold(null, error)
	}
}

fun Context.getBigIntegerPrivateKeyByWalletID(
	password: String,
	walletID: Int,
	hold: (privateKey: BigInteger?, error: AccountError) -> Unit
) = getKeystoreFileByWalletID(password, walletID) { keyStoreFile, error ->
	// 因为提取和解析 `Keystore` 比较耗时, 所以 `KeyStore` 的操作放到异步
	if (keyStoreFile.isNotNull() && error.isNone()) {
		val keyPair = WalletUtil.getKeyPairFromWalletFile(keyStoreFile, password)
		if (keyPair == null) hold(null, AccountError.WrongPassword)
		else hold(keyPair.privateKey, AccountError.None)
	} else hold(null, error)
}

fun Context.deleteAccount(
	walletAddress: String,
	password: String,
	isBTCSeriesWallet: Boolean,
	callback: (error: AccountError) -> Unit
) {
	val filename = CryptoValue.filename(walletAddress, isBTCSeriesWallet)
	val keystoreFile by lazy { File(filesDir!!, filename) }
	val keyStore = KeyStore(keystoreFile.absolutePath, Geth.LightScryptN, Geth.LightScryptP)
	// If there is't account found then return
	if (keyStore.accounts.size() == 0L) {
		callback(AccountError.None)
		return
	}
	var targetAccountIndex: Long? = if (isBTCSeriesWallet) 0 else null
	(0 until keyStore.accounts.size()).forEachOrEnd { index, isEnd ->
		keyStore.accounts.get(index).address.hex.let {
			if (it.equals(walletAddress, true) && !isBTCSeriesWallet) {
				targetAccountIndex = index
			}
			// `BTC` 的 `Filename` 就是 `Address`
			if (isEnd && !targetAccountIndex.isNull() || isBTCSeriesWallet) {
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

fun Context.deleteWalletByWalletID(
	walletID: Int,
	password: String,
	callback: (error: AccountError) -> Unit
) {
	val filename = "$walletID${CryptoValue.keystoreFilename}"
	val keystoreFile by lazy { File(filesDir!!, filename) }
	val keyStore = KeyStore(keystoreFile.absolutePath, Geth.LightScryptN, Geth.LightScryptP)
	// If there is't account found then return
	if (keyStore.accounts.size() == 0L) {
		callback(AccountError.None)
	} else try {
		// `BTC` 的 `Filename` 就是 `Address`
		keyStore.deleteAccount(keyStore.accounts.get(0), password)
		callback(AccountError.None)
	} catch (error: Exception) {
		callback(AccountError.PasswordFormatted(error.message.orEmpty()))
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

fun Context.updatePasswordByWalletID(
	walletID: Int,
	oldPassword: String,
	newPassword: String,
	callback: (AccountError) -> Unit
) = GlobalScope.launch(Dispatchers.Default) {
	getBigIntegerPrivateKeyByWalletID(
		oldPassword,
		walletID
	) { privateKey, error ->
		if (privateKey.isNotNull() && error.isNone()) {
			deleteWalletByWalletID(walletID, oldPassword) { deleteError ->
				if (deleteError.isNone()) {
					storeRootKeyByWalletID(walletID, privateKey, newPassword)
					callback(AccountError.None)
				} else callback(deleteError)
			}
		} else callback(error)
	}
}