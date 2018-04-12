package io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.model

import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.model.DefaultTokenTable
import org.web3j.crypto.RawTransaction
import java.io.Serializable

/**
 * @date 12/04/2018 10:41 PM
 * @author KaySaith
 */

data class ReceiptModel(
  val address: String,
  val raw: RawTransaction,
  val token: DefaultTokenTable,
  val taxHash: String,
  val timestamp: Long,
  val memo: String? = null
): Serializable