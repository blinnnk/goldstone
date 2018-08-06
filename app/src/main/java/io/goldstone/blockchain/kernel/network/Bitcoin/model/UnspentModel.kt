package io.goldstone.blockchain.kernel.network.bitcoin.model

import com.google.gson.annotations.SerializedName

/**
 * @date 2018/7/24 12:58 PM
 * @author KaySaith
 */
data class UnspentModel(
	@SerializedName("tx_output_n")
	val outputNumber: Long,
	@SerializedName("script")
	val script: String,
	@SerializedName("value")
	val value: Long,
	@SerializedName("value_hex")
	val balanceHex: String,
	@SerializedName("tx_hash")
	val littleEndianHash: String,
	@SerializedName("tx_hash_big_endian")
	val txid: String,
	@SerializedName("confirmations")
	val confirmations: Int
) {
	constructor() : this(
		0,
		"",
		0L,
		"",
		"",
		"",
		0
	)
}