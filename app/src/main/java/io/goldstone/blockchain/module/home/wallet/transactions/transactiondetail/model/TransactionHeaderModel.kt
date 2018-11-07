package io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.model

import java.io.Serializable

/**
 * @date 2018/6/6 4:37 PM
 * @author KaySaith
 */
data class TransactionHeaderModel(
	val count: Double,
	val address: String,
	val symbol: String,
	val isPending: Boolean,
	val isReceive: Boolean = false,
	val isError: Boolean = false
) : Serializable {
	constructor(data: TransactionHeaderModel, count: Double, symbol: String) : this(
		count,
		data.address,
		symbol,
		data.isPending,
		data.isReceive,
		data.isError
	)
}