package io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.model

/**
 * @date 12/04/2018 10:41 PM
 * @author KaySaith
 */

data class TransactionDetailModel(
	var info: String = "",
	val description: String = ""
)

data class TransactionProgressModel(
	val confirmed: Int = 0,
	val totalCount: Long = 6
)