package io.goldstone.blinnnk.crypto.ethereum.walletfile

import android.util.Log
import io.goldstone.blinnnk.crypto.ethereum.ECKeyPair
import io.goldstone.blinnnk.crypto.keystore.convertKeystoreToModel
import io.goldstone.blinnnk.crypto.utils.clean0xPrefix

/**
 * @date 2018/6/17 9:51 PM
 * @author KaySaith
 */
object WalletUtil {

	fun getKeyPairFromWalletFile(
		walletJSON: String,
		password: String
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
			Log.e("getKeyPair", error.message)
			null
		}
	}

	fun isValidPrivateKey(key: String): Boolean {
		return key.clean0xPrefix().length == 63 || key.clean0xPrefix().length == 64
	}
}