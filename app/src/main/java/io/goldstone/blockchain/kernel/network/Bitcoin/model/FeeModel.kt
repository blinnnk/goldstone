package io.goldstone.blockchain.kernel.network.bitcoin.model

import com.google.gson.annotations.SerializedName

/**
 * @date 2018/7/24 3:27 PM
 * @author KaySaith
 */
data class FeeModel(
	@SerializedName("feerate")
	val fee: Double,
	@SerializedName("blocks")
	val blocks: Int
) {
	constructor() : this(
		0.0,
		0
	)
}