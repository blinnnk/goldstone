package io.goldstone.blockchain.crypto.ethereum.walletfile

import io.goldstone.blockchain.common.error.AccountError
import io.goldstone.blockchain.common.utils.LogUtil
import io.goldstone.blockchain.crypto.ethereum.ECKeyPair
import io.goldstone.blockchain.crypto.keystore.convertKeystoreToModel
import io.goldstone.blockchain.crypto.utils.clean0xPrefix
import io.goldstone.blockchain.kernel.network.GoldStoneAPI
import org.jetbrains.anko.runOnUiThread

/**
 * @date 2018/6/17 9:51 PM
 * @author KaySaith
 */
object WalletUtil {

	fun getKeyPairFromWalletFile(
		walletJSON: String,
		password: String,
		errorCallback: (AccountError) -> Unit
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
			GoldStoneAPI.context.runOnUiThread { errorCallback(AccountError.DecryptKeyStoreError) }
			null
		}
	}

	fun isValidPrivateKey(key: String): Boolean {
		return key.clean0xPrefix().length == 63 || key.clean0xPrefix().length == 64
	}
}