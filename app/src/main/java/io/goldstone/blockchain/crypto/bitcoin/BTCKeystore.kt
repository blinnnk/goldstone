package io.goldstone.blockchain.crypto.bitcoin

import android.content.Context
import io.goldstone.blockchain.common.utils.LogUtil
import io.goldstone.blockchain.crypto.getKeystoreFile
import io.goldstone.blockchain.crypto.getPrivateKey
import io.goldstone.blockchain.crypto.walletfile.WalletUtil
import org.bitcoinj.core.DumpedPrivateKey
import org.bitcoinj.core.ECKey
import org.bitcoinj.params.MainNetParams
import org.bitcoinj.params.TestNet3Params
import org.ethereum.geth.Geth
import org.ethereum.geth.KeyStore
import org.jetbrains.anko.runOnUiThread
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
	fileName: String,
	password: String,
	isTest: Boolean,
	hold: (String?) -> Unit
) {
	val net = if (isTest) TestNet3Params.get() else MainNetParams.get()
	val keystoreFile by lazy { File(filesDir!!, fileName) }
	/** Generate Keystore */
	val keyStore = KeyStore(keystoreFile.absolutePath, Geth.LightScryptN, Geth.LightScryptP)
	getPrivateKey(
		keyStore.accounts[0].address.hex,
		password,
		fileName,
		{
			hold(null)
			LogUtil.error("exportBase58PrivateKey", it)
		}
	) {
		hold(ECKey.fromPrivate(it.toBigInteger(16)).getPrivateKeyAsWiF(net))
	}
}

fun Context.exportBase58KeyStoreFile(
	fileName: String,
	password: String,
	hold: (String?) -> Unit
) {
	val keystoreFile by lazy { File(filesDir!!, fileName) }
	/** Generate Keystore */
	val keyStore = KeyStore(keystoreFile.absolutePath, Geth.LightScryptN, Geth.LightScryptP)
	getKeystoreFile(
		keyStore.accounts[0].address.hex,
		password,
		fileName,
		{
			hold(null)
		},
		hold
	)
}

fun Context.getBase58PrivateKeyByKeystoreFile(
	keystoreFile: String,
	password: String,
	isTest: Boolean,
	errorCallback: (Throwable) -> Unit,
	hold: (String) -> Unit
) {
	val net = if (isTest) TestNet3Params.get() else MainNetParams.get()
	WalletUtil.getKeyPairFromWalletFile(
		keystoreFile,
		password,
		errorCallback
	)?.let {
		runOnUiThread {
			hold(ECKey.fromPrivate(it.privateKey).getPrivateKeyAsWiF(net))
		}
	}
}