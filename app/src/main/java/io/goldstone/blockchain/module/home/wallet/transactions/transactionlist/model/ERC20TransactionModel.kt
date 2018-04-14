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
  private val contract: String,
  @SerializedName("topics")
  private val topics: ArrayList<String>,
  val from: String = topics[1],
  val to: String = topics[2],
  @SerializedName("data")
  val value: String,
  @SerializedName("blockNumber")
  private val blockNumber: String,
  @SerializedName("timeStamp")
  private val timeStamp: String,
  @SerializedName("gasPrice")
  private val gasPrice: String,
  @SerializedName("gasUsed")
  private val gasUsed: String,
  @SerializedName("logIndex")
  private val logIndex: String,
  @SerializedName("transactionHash")
  private val transactionHash: String,
  @SerializedName("transactionIndex")
  private val transactionIndex: String
) {
  constructor(data: ERC20TransactionModel) : this(
    data.contract,
    data.topics,
    data.topics[1].toAddressFromCode(), // fromAddress
    data.topics[2].toAddressFromCode(), // toAddress
    data.value.toDecimalFromHex().toString(),
    data.blockNumber.toDecimalFromHex().toString(),
    data.timeStamp.toDecimalFromHex().toString(),
    data.gasPrice.toDecimalFromHex().toString(),
    data.gasUsed.toDecimalFromHex().toString(),
    data.logIndex.toDecimalFromHex().toString(),
    data.transactionHash,
    data.transactionIndex.toDecimalFromHex().toString()
  )
}