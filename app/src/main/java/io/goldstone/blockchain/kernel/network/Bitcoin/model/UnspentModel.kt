package io.goldstone.blockchain.kernel.network.bitcoin.model

import com.google.gson.annotations.SerializedName

/**
 * @date 2018/7/24 12:58 PM
 * @author KaySaith
 */
data class UnspentModel(
	@SerializedName("vout")
	val outputNumber: Long,
	@SerializedName("scriptPubKey")
	val scriptPubKey: String,
	@SerializedName("satoshis")
	val value: Long,
	@SerializedName("txid")
	val txid: String,
	@SerializedName("confirmations")
	val confirmations: Int
) {
	constructor() : this(
		0,
		"",
		0L,
		"",
		0
	)
}