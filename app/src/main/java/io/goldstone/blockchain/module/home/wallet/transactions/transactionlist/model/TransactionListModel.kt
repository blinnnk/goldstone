package io.goldstone.blockchain.module.home.wallet.transactions.transactionlist.model

/**
 * @date 24/03/2018 7:09 PM
 * @author KaySaith
 */

data class TransactionListModel(
  val addressName: String = "",
  val addressInfo: String = "",
  val count: Double = 0.0,
  val symbol: String = "",
  val isReceived: Boolean = false
)