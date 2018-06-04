package io.goldstone.blockchain.module.home.wallet.transactions.transactionlist.model

import com.blinnnk.util.HoneyDateUtil
import io.goldstone.blockchain.common.utils.TimeUtils
import io.goldstone.blockchain.crypto.CryptoUtils
import io.goldstone.blockchain.crypto.SolidityCode
import io.goldstone.blockchain.crypto.toAscii
import io.goldstone.blockchain.crypto.toEthValue
import io.goldstone.blockchain.kernel.commonmodel.TransactionTable
import io.goldstone.blockchain.kernel.network.EtherScanApi
import java.io.Serializable

/**
 * @date 24/03/2018 7:09 PM
 * @author KaySaith
 */
data class TransactionListModel(
	val addressName: String,
	val addressInfo: String,
	var count: Double,
	val symbol: String,
	val isReceived: Boolean,
	val date: String,
	val targetAddress: String,
	val blockNumber: String,
	val transactionHash: String,
	var memo: String,
	val minerFee: String,
	val url: String,
	val isPending: Boolean,
	val timeStamp: String,
	val value: String,
	val hasError: Boolean,
	var contract: String
) : Serializable {
	
	constructor(data: TransactionTable) : this(
		data.tokenReceiveAddress.orEmpty(),
		CryptoUtils.scaleTo28(HoneyDateUtil.getSinceTime(data.timeStamp) + descriptionText(data.isReceive) + data.fromAddress), // 副标题的生成
		data.value.toDouble(), // 转账个数
		data.symbol,
		data.isReceive,
		TimeUtils.formatDate(data.timeStamp.toLong()), // 拼接时间
		if (data.isReceive) data.fromAddress else data.tokenReceiveAddress.orEmpty(),
		data.blockNumber,
		data.hash,
		data.memo,
		(data.gasUsed.toDouble() * data.gasPrice.toDouble()).toEthValue(), // 计算燃气费使用情况
		EtherScanApi.transactionDetail(data.hash), // Api 地址拼接
		data.isPending,
		data.timeStamp,
		data.value,
		data.hasError == "1",
		data.contractAddress
	)
}

val getMemoFromInputCode: (inputCode: String, isERC20: Boolean) -> String = { input, isERC20 ->
	if (!isERC20) {
		if (input.equals(SolidityCode.ethTransfer, true)) {
			"There isn't a memo"
		} else {
			input.toAscii(false)
		}
	} else {
		if (input.length > 138) {
			input.substring(137, input.length).toAscii(false)
		} else "There isn't a memo"
	}
}
private val descriptionText: (isReceive: Boolean) -> String = {
	if (it) " incoming from " else " send from "
}