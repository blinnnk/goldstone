package io.goldstone.blinnnk.module.home.wallet.transactions.transactiondetail.model

import io.goldstone.blinnnk.crypto.utils.CryptoUtils

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
)  {

	constructor(data: TransactionSealedModel, isPending: Boolean? = null, isError: Boolean? = null) : this(
		data.count,
		if (data.isReceive) data.fromAddress else data.toAddress,
		data.symbol,
		isPending ?: data.isPending,
		data.isReceive,
		isError ?: data.hasError
	)

	constructor(data: TransactionSealedModel, symbol: String, count: Double) : this(
		count,
		if (data.isReceive) data.fromAddress else data.toAddress,
		symbol,
		data.isPending,
		data.isReceive,
		data.hasError
	)

	constructor(data: ReceiptModel, isPending: Boolean? = null, isError: Boolean? = null) : this(
		CryptoUtils.toCountByDecimal(data.value, data.token.decimal),
		data.toAddress,
		data.token.symbol.symbol,
		isPending ?: true,
		false,
		isError ?: false
	)
}