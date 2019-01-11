@file:Suppress("INACCESSIBLE_TYPE")

package io.goldstone.blinnnk.crypto.keystore

import android.content.Context
import android.support.annotation.WorkerThread
import com.blinnnk.extension.isNotNull
import io.goldstone.blinnnk.common.error.AccountError
import io.goldstone.blinnnk.common.language.ImportWalletText
import io.goldstone.blinnnk.crypto.bip39.Mnemonic
import io.goldstone.blinnnk.crypto.ethereum.ECKeyPair
import io.goldstone.blinnnk.crypto.ethereum.getAddress
import io.goldstone.blinnnk.crypto.ethereum.walletfile.WalletUtil
import io.goldstone.blinnnk.crypto.multichain.CryptoValue
import io.goldstone.blinnnk.crypto.utils.hexToByteArray
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

fun Context.generateTemporaryKeyStore(
	mnemonicCode: String,
	pathValue: String,
	password: String,
	hold: (keyStore: String?, error: AccountError) -> Unit
) {
	val keystoreFile by lazy { File(filesDir!!, "temp") }
	/** Generate HD Wallet */
	val masterWallet = Mnemonic.mnemonicToKey(mnemonicCode, pathValue)
	/** Generate Keystore */
	val keyStore = KeyStore(keystoreFile.absolutePath, Geth.LightScryptN, Geth.LightScryptP)
	/** Generate Keys */
	val masterKey = masterWallet.keyPair
	/** Import Private Key to Keystore */
	try {
		keyStore.importECDSAKey(
			keyString(masterKey.privateKey.toString(16)).hexToByteArray(),
			password
		)
		hold(String(keyStore.exportKey(keyStore.accounts.get(0), password, password)), AccountError.None)
		keyStore.deleteAccount(keyStore.accounts.get(0), password)
	} catch (error: Exception) {
		hold(null, AccountError.WrongPassword)
	}
}

// 因为提取和解析 `Keystore` 比较耗时, 所以 `KeyStore` 的操作放到异步
@WorkerThread
fun Context.getBigIntegerPrivateKeyByWalletID(
	password: String,
	walletID: Int,
	hold: (privateKey: BigInteger?, error: AccountError) -> Unit
) = getKeystoreFileByWalletID(password, walletID) { keyStoreFile, error ->
	if (keyStoreFile.isNotNull() && error.isNone()) {
		val keyPair = WalletUtil.getKeyPairFromWalletFile(keyStoreFile, password)
		if (keyPair == null) hold(null, AccountError.WrongPassword)
		else hold(keyPair.privateKey, error)
	} else hold(null, error)
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
		android.util.Log.e("wrong keystore password", error.message)
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
	getBigIntegerPrivateKeyByWalletID(oldPassword, walletID) { privateKey, error ->
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