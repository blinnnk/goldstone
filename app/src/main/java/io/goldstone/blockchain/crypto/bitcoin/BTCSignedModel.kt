package io.goldstone.blockchain.crypto.bitcoin

/**
 * @date 2018/7/25 2:19 PM
 * @author KaySaith
 */
data class BTCSignedModel(
	val signedMessage: String,
	val messageSize: Int
)