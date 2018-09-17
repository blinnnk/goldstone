package io.goldstone.blockchain.crypto.utils

import io.goldstone.blockchain.crypto.ethereum.*
import io.goldstone.blockchain.crypto.extensions.hexToBigInteger

/**
 * @date 2018/6/17 7:26 PM
 * @author KaySaith
 */
object TransactionUtils {
	
	fun signTransaction(
		transaction: Transaction,
		privateKey: String
	): String {
		val publicKey = publicKeyFromPrivate(privateKey.hexToBigInteger())
		val keyPair = ECKeyPair(privateKey.hexToBigInteger(), publicKey)
		val signatureData =
			transaction.signViaEIP155(keyPair, transaction.chain!!)
		return transaction.encodeRLP(signatureData).toHexString()
	}
}