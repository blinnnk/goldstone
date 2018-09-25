package io.goldstone.blockchain.crypto.litecoin

import android.content.Context
import android.support.annotation.UiThread
import com.blinnnk.extension.isNull
import io.goldstone.blockchain.common.error.AccountError
import io.goldstone.blockchain.common.utils.LogUtil
import io.goldstone.blockchain.crypto.keystore.getPrivateKey
import io.goldstone.blockchain.crypto.multichain.CryptoValue
import org.bitcoinj.core.ECKey
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
fun Context.storeLTCBase58PrivateKey(
	wifPrivateKey: String,
	fileName: String,
	password: String,
	isSingleChainWallet: Boolean
) {
	val finalFilename = if (isSingleChainWallet) "${CryptoValue.singleChainFilename}$fileName" else fileName
	val keystoreFile by lazy { File(filesDir!!, finalFilename) }
	try {
		/** Generate Keystore */
		val keyStore = KeyStore(keystoreFile.absolutePath, Geth.LightScryptN, Geth.LightScryptP)
		val privateKey = LTCWalletUtils.getPrivateKeyFromWIFKey(wifPrivateKey, ChainPrefix.Litecoin)
		val key = ECKey.fromPrivate(privateKey.toBigInteger(16)).privKeyBytes
		/** Import Private Key to Keystore */
		keyStore.importECDSAKey(key, password)
	} catch (error: Exception) {
		LogUtil.error("generateWallet", error)
	}
}

fun Context.exportLTCBase58PrivateKey(
	walletAddress: String,
	password: String,
	isSingleChainWallet: Boolean,
	@UiThread hold: (privateKey: String?, error: AccountError) -> Unit
) {
	getPrivateKey(
		walletAddress,
		password,
		true,
		isSingleChainWallet
	) { privateKey, error ->
		if (!privateKey.isNull() && error.isNone()) {
			val key = LTCWalletUtils.generateWIFSecret(ECKey.fromPrivate(privateKey!!.toBigInteger(16)).privKey)
			hold(key, AccountError.None)
		}
	}
}