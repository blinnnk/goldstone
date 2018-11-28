package io.goldstone.blockchain.crypto.bitcoin

import android.content.Context
import com.blinnnk.extension.isNotNull
import io.goldstone.blockchain.common.error.AccountError
import io.goldstone.blockchain.common.utils.LogUtil
import io.goldstone.blockchain.crypto.keystore.getKeystoreFile
import io.goldstone.blockchain.crypto.keystore.getPrivateKey
import org.bitcoinj.core.DumpedPrivateKey
import org.bitcoinj.core.ECKey
import org.bitcoinj.params.MainNetParams
import org.bitcoinj.params.TestNet3Params
import org.ethereum.geth.Geth
import org.ethereum.geth.KeyStore
import java.io.File

/**
 * @date 2018/8/1 12:13 AM
 * @author KaySaith
 * @important
 * 这个 `Keystore` 是复用了 `Ethereum Geth` 的 `Keystore`， 摒弃了它提供的地址管理系统,
 * 为此, 一个 `Bitcoin` 私钥管理对应一个地址文件. 要保证每一个存入 `Keystore` 的文件对应
 * 一个独立的 `FileName`
 */
fun Context.storeBase58PrivateKey(
	base58PrivateKey: String,
	fileName: String,
	password: String,
	isTestNet: Boolean
) {
	val net = if (isTestNet) TestNet3Params.get() else MainNetParams.get()
	val keystoreFile by lazy { File(filesDir!!, fileName) }
	try {
		/** Generate Keystore */
		val keyStore = KeyStore(keystoreFile.absolutePath, Geth.LightScryptN, Geth.LightScryptP)
		val eckey = DumpedPrivateKey.fromBase58(net, base58PrivateKey).key
		/** Import Private Key to Keystore */
		keyStore.importECDSAKey(eckey.privKeyBytes, password)
	} catch (error: Exception) {
		LogUtil.error("generateWallet", error)
	}
}

fun Context.exportBase58PrivateKey(
	walletAddress: String,
	password: String,
	isTest: Boolean,
	isCompress: Boolean = true,
	hold: (privateKey: String?, error: AccountError) -> Unit
) {
	getPrivateKey(walletAddress, password, true) { privateKey, error ->
		if (privateKey.isNotNull() && error.isNone()) {
			val net = if (isTest) TestNet3Params.get() else MainNetParams.get()
			hold(ECKey.fromPrivate(privateKey.toBigInteger(16), isCompress).getPrivateKeyAsWiF(net), AccountError.None)
		} else hold(null, error)
	}
}

fun Context.exportBase58KeyStoreFile(
	walletAddress: String,
	password: String,
	hold: (keystoreFile: String?, error: AccountError) -> Unit
) {
	getKeystoreFile(
		walletAddress,
		password,
		true,
		hold
	)
}