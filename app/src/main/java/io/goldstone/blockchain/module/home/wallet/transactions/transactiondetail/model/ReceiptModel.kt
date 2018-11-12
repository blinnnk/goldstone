package io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.model

import io.goldstone.blockchain.common.utils.TimeUtils
import io.goldstone.blockchain.common.utils.toMillisecond
import io.goldstone.blockchain.crypto.eos.base.EOSResponse
import io.goldstone.blockchain.crypto.eos.transaction.EOSTransactionInfo
import io.goldstone.blockchain.crypto.utils.CryptoUtils
import io.goldstone.blockchain.module.home.wallet.transactions.transactionlist.ethereumtransactionlist.model.TransactionListModel
import io.goldstone.blockchain.module.home.wallet.walletdetail.model.WalletDetailCellModel
import java.math.BigInteger

/**
 * @date 12/04/2018 10:41 PM
 * @author KaySaith
 */
data class ReceiptModel(
	override val fromAddress: String,
	override val toAddress: String,
	override val minerFee: String,
	override val value: BigInteger,
	val token: WalletDetailCellModel,
	val taxHash: String,
	val timestamp: Long,
	override val memo: String
) : TransactionSealedModel(
	true,
	taxHash,
	token.symbol,
	fromAddress,
	toAddress,
	CryptoUtils.toCountByDecimal(value, token.decimal),
	value,
	false,
	token.contract,
	false,
	false,
	-1,
	timestamp.toString(),
	-1,
	memo,
	minerFee,
	null
) {
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