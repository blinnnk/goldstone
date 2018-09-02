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
	constructor(blockInfoModel: BlockInfoUnspentModel) : this(
		blockInfoModel.outputNumber,
		blockInfoModel.scriptPubKey,
		blockInfoModel.value,
		blockInfoModel.txid,
		blockInfoModel.confirmations
	)
}

// 当 `Insight` 数据出问题会重新从 BlockInfo 拉取数据, 配合 `GSON`
// 这里有需要额外的 `Model`
data class BlockInfoUnspentModel(
	@SerializedName("tx_output_n")
	val outputNumber: Long,
	@SerializedName("script")
	val scriptPubKey: String,
	@SerializedName("value")
	val value: Long,
	@SerializedName("tx_hash_big_endian")
	val txid: String,
	@SerializedName("confirmations")
	val confirmations: Int
)