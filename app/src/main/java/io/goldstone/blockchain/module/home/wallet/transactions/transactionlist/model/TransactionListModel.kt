package io.goldstone.blockchain.module.home.wallet.transactions.transactionlist.model

import android.text.format.DateUtils
import android.text.format.DateUtils.FORMAT_SHOW_TIME
import android.text.format.DateUtils.FORMAT_SHOW_YEAR
import com.blinnnk.util.HoneyDateUtil
import io.goldstone.blockchain.crypto.CryptoUtils
import io.goldstone.blockchain.kernel.commonmodel.TransactionTable
import io.goldstone.blockchain.kernel.network.EtherScanApi
import io.goldstone.blockchain.kernel.network.GoldStoneAPI
import java.io.Serializable

/**
 * @date 24/03/2018 7:09 PM
 * @author KaySaith
 */

data class TransactionListModel(
  val addressName:String,
  val addressInfo: String,
  val count: Double,
  val symbol: String,
  val isReceived: Boolean,
  val date: String,
  val targetAddress: String,
  val blockNumber: String,
  val transactionHash: String,
  val memo: String,
  val minerFee: Double,
  val url: String
) : Serializable {

  constructor(data: TransactionTable) : this(
    data.to,
    CryptoUtils.scaleTo28(
      HoneyDateUtil.getSinceTime(data.timeStamp)
        + descriptionText(data.isReceive)
        + data.fromAddress
    ),
    CryptoUtils.formatDouble(data.value.toDouble()),
    data.symbol,
    data.isReceive,
    DateUtils.formatDateTime(GoldStoneAPI.context, data.timeStamp.toLong() * 1000, FORMAT_SHOW_YEAR) + " " + DateUtils.formatDateTime(GoldStoneAPI.context, data.timeStamp.toLong() * 1000, FORMAT_SHOW_TIME),
    if (data.isReceive) data.fromAddress else data.to,
    data.blockNumber,
    data.hash,
    "Leave A Message",
    data.gasUsed.toDouble() * data.gasPrice.toDouble(),
    EtherScanApi.singleTransactionHas(data.hash)
  )
}

private val descriptionText: (isReceive: Boolean) -> String = {
  if(it) " incoming from" else " send from"
}