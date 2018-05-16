package io.goldstone.blockchain.module.home.wallet.transactions.transactionlist.model

import com.google.gson.annotations.SerializedName
import io.goldstone.blockchain.crypto.toAddressFromCode
import io.goldstone.blockchain.crypto.toDecimalFromHex

/**
 * @date 15/04/2018 1:13 AM
 * @author KaySaith
 */

data class ERC20TransactionModel(
	@SerializedName("address")
	val contract: String,
	@SerializedName("topics")
	private val topics: ArrayList<String>,
	val from: String = topics[1],
	val to: String = topics[2],
	@SerializedName("data")
	val value: String,
	@SerializedName("blockNumber")
	val blockNumber: String,
	@SerializedName("timeStamp")
	val timeStamp: String,
	@SerializedName("gasPrice")
	val gasPrice: String,
	@SerializedName("gasUsed")
	val gasUsed: String,
	@SerializedName("logIndex")
	val logIndex: String,
	@SerializedName("transactionHash")
	val transactionHash: String,
	@SerializedName("transactionIndex")
	val transactionIndex: String,
	val isReceive: Boolean,
	var symbol: String
) {
	constructor(data: ERC20TransactionModel) : this(
		data.contract, data.topics,
		data.topics[1].toAddressFromCode(), // fromAddress
		data.topics[2].toAddressFromCode(), // toAddress
		data.value.toDecimalFromHex(),
		data.blockNumber.toDecimalFromHex(),
		data.timeStamp.toDecimalFromHex(),
		data.gasPrice.toDecimalFromHex(),
		data.gasUsed.toDecimalFromHex(),
		data.logIndex.toDecimalFromHex(),
		data.transactionHash,
		data.transactionIndex.toDecimalFromHex(),
		true,
		""
	)
}