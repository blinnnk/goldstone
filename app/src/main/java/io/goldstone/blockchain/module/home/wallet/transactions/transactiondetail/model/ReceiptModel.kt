package io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.model

import io.goldstone.blockchain.crypto.eos.base.EOSResponse
import io.goldstone.blockchain.crypto.eos.transaction.EOSTransactionInfo
import io.goldstone.blockchain.module.home.wallet.transactions.transactionlist.ethereumtransactionlist.model.TransactionListModel
import io.goldstone.blockchain.module.home.wallet.walletdetail.model.WalletDetailCellModel
import java.io.Serializable
import java.math.BigInteger

/**
 * @date 12/04/2018 10:41 PM
 * @author KaySaith
 */
data class ReceiptModel(
	val fromAddress: String,
	val toAddress: String,
	val minerFee: String,
	val value: BigInteger,
	val token: WalletDetailCellModel,
	val taxHash: String,
	val timestamp: Long,
	val memo: String? = null
) : Serializable {
	constructor(
		info: EOSTransactionInfo,
		response: EOSResponse,
		token: WalletDetailCellModel
	) : this(
		info.fromAccount.accountName,
		info.toAccount.accountName,
		TransactionListModel.generateEOSMinerContent(response.cupUsageByte, response.netUsageByte),
		info.amount,
		token,
		response.transactionID,
		System.currentTimeMillis(),
		info.memo
	)
}