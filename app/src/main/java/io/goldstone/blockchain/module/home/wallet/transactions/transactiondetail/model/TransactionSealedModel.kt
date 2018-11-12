package io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.model

import com.blinnnk.extension.orElse
import com.blinnnk.extension.toMillisecond
import com.blinnnk.util.TinyNumber
import io.goldstone.blockchain.common.utils.TimeUtils
import io.goldstone.blockchain.crypto.multichain.ChainID
import io.goldstone.blockchain.crypto.multichain.ChainType
import io.goldstone.blockchain.crypto.multichain.TokenContract
import io.goldstone.blockchain.kernel.commonmodel.BTCSeriesTransactionTable
import io.goldstone.blockchain.kernel.commonmodel.TransactionTable
import java.io.Serializable
import java.math.BigInteger


/**
 * @author KaySaith
 * @date  2018/11/07
 */
open class TransactionSealedModel(
	open val isPending: Boolean,
	open val hash: String,
	open val symbol: String,
	open val fromAddress: String,
	open val toAddress: String,
	open val count: Double,
	open val value: BigInteger,
	open val isReceive: Boolean,
	open val contract: TokenContract,
	open val isFee: Boolean,
	open val hasError: Boolean,
	open val blockNumber: Int,
	open val date: String, // 如果是继承自 Notification 那么这个值会显示为 timeStamp
	open val confirmations: Int,
	open val memo: String,
	open val minerFee: String,
	// 这个字段为 `Notification` 服务的, 因为` Notification` 会接收不同的链的 `Push`
	// 而且要在不同的环境下打开不同的链的信息, 所以需要这个值作为判断
	open val chainID: ChainID?
) : Serializable {
	constructor(data: TransactionTable) : this(
		data.isPending,
		data.hash,
		data.symbol,
		data.fromAddress,
		data.to,
		data.count,
		BigInteger.valueOf(data.value.toLongOrNull().orElse(0)),
		data.isReceive,
		TokenContract(data.contractAddress, data.symbol, null),
		data.isFee,
		data.hasError.toIntOrNull() == TinyNumber.True.value,
		data.blockNumber.toIntOrNull() ?: -1,
		TimeUtils.formatDate(data.timeStamp.toMillisecond()),
		data.confirmations.toIntOrNull() ?: -1,
		data.memo,
		data.minerFee,
		null
	)

	constructor(data: BTCSeriesTransactionTable) : this(
		data.isPending,
		data.hash,
		data.symbol,
		data.fromAddress,
		data.to,
		data.value.toDouble(),
		BigInteger.valueOf(data.value.toLongOrNull().orElse(0)),
		data.isReceive,
		ChainType(data.chainType).getContract(),
		data.isFee,
		false,
		data.blockNumber,
		TimeUtils.formatDate(data.timeStamp.toMillisecond()),
		data.confirmations,
		"",
		data.fee,
		null
	)
}