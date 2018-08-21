package io.goldstone.blockchain.crypto.walletfile

import io.goldstone.blockchain.common.utils.LogUtil
import io.goldstone.blockchain.common.value.Config
import io.goldstone.blockchain.crypto.CryptoSymbol
import io.goldstone.blockchain.crypto.ECKeyPair
import io.goldstone.blockchain.crypto.convertKeystoreToModel
import io.goldstone.blockchain.crypto.utils.clean0xPrefix

/**
 * @date 2018/6/17 9:51 PM
 * @author KaySaith
 */
object WalletUtil {

	fun getKeyPairFromWalletFile(
		walletJSON: String,
		password: String,
		errorCallback: (Throwable) -> Unit
	): ECKeyPair? {
		val keystoreModel = walletJSON.convertKeystoreToModel()
		val cryptWallet = WalletCrypto(
			keystoreModel.cipher,
			keystoreModel.ciphertext,
			CipherParams(keystoreModel.iv),
			keystoreModel.kdf,
			ScryptKdfParams(
				keystoreModel.n,
				keystoreModel.p,
				keystoreModel.r,
				keystoreModel.dklen,
				keystoreModel.salt
			),
			keystoreModel.mac
		)
		val wallet = Wallet(
			keystoreModel.address,
			cryptWallet,
			keystoreModel.id,
			keystoreModel.version
		)
		return try {
			wallet.decrypt(password)
		} catch (error: Exception) {
			LogUtil.error("getKeyPairFromWalletFile", error)
			errorCallback(error)
			null
		}
	}

	fun isValidPrivateKey(key: String): Boolean {
		return key.clean0xPrefix().length == 63 || key.clean0xPrefix().length == 64
	}

	fun getAddressBySymbol(symbol: String): String {
		return when (symbol) {
			CryptoSymbol.btc() -> {
				if (Config.isTestEnvironment()) {
					Config.getCurrentBTCSeriesTestAddress()
				} else {
					Config.getCurrentBTCAddress()
				}
			}

			CryptoSymbol.ltc -> {
				if (Config.isTestEnvironment()) {
					Config.getCurrentBTCSeriesTestAddress()
				} else {
					Config.getCurrentLTCAddress()
				}
			}

			CryptoSymbol.bch -> {
				if (Config.isTestEnvironment()) {
					Config.getCurrentBTCSeriesTestAddress()
				} else {
					Config.getCurrentBCHAddress()
				}
			}

			CryptoSymbol.etc ->
				Config.getCurrentETCAddress()
			else ->
				Config.getCurrentEthereumAddress()
		}
	}
}