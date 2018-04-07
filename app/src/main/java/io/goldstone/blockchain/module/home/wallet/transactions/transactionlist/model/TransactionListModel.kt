package io.goldstone.blockchain.module.home.wallet.transactions.transactionlist.model

import com.blinnnk.util.HoneyDateUtil
import io.goldstone.blockchain.crypto.CryptoUtils
import io.goldstone.blockchain.kernel.commonmodel.TransactionTable

/**
 * @date 24/03/2018 7:09 PM
 * @author KaySaith
 */

data class TransactionListModel(
  val addressName:String,
  val addressInfo: String,
  val count: Double,
  val symbol: String,
  val isReceived: Boolean
) {

  constructor(data: TransactionTable) : this(
    data.to,
    CryptoUtils.scaleTo28(HoneyDateUtil.getSinceTime(data.timeStamp) + " incoming from" + data.fromAddress),
    CryptoUtils.formatDouble(data.value.toDouble()),
    data.symbol,
    data.isReceive
  )

}