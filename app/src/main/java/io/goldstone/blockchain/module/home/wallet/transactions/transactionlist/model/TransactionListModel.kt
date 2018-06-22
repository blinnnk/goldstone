package io.goldstone.blockchain.module.home.wallet.transactions.transactionlist.model

import com.blinnnk.util.HoneyDateUtil
import io.goldstone.blockchain.common.utils.TimeUtils
import io.goldstone.blockchain.common.value.DateAndTimeText
import io.goldstone.blockchain.common.value.TransactionText
import io.goldstone.blockchain.crypto.CryptoSymbol
import io.goldstone.blockchain.crypto.SolidityCode
import io.goldstone.blockchain.crypto.utils.CryptoUtils
import io.goldstone.blockchain.crypto.utils.toStringFromHex
import io.goldstone.blockchain.crypto.utils.toUnitValue
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
	var contract: String,
	var isFailed: Boolean
) : Serializable {
	
	constructor(data: TransactionTable) : this(
		data.tokenReceiveAddress.orEmpty(),
		CryptoUtils.scaleTo32(
			HoneyDateUtil.getSinceTime(
				data.timeStamp,
				DateAndTimeText.getDateText()
			) + descriptionText(
				data.isReceive,
				data.tokenReceiveAddress.orEmpty(),
				data.fromAddress
			)
		), // 副标题的生成
		data.value.toDouble(), // 转账个数
		data.symbol,
		data.isReceive,
		TimeUtils.formatDate(data.timeStamp), // 拼接时间
		if (data.isReceive) data.fromAddress else data.tokenReceiveAddress.orEmpty(),
		data.blockNumber,
		data.hash,
		data.memo,
		((data.gas.toDoubleOrNull()
		  ?: data.gasUsed.toDouble()) * data.gasPrice.toDouble())
			.toUnitValue(
				if (data.symbol.equals(CryptoSymbol.etc, true))
					CryptoSymbol.eth
				else CryptoSymbol.eth
			), // 计算燃气费使用情况
		EtherScanApi.transactionDetail(data.hash), // Api 地址拼接
		data.isPending,
		data.timeStamp,
		data.value,
		data.hasError == "1",
		data.contractAddress,
		data.isFailed
	)
}

val getMemoFromInputCode: (inputCode: String, isERC20: Boolean) -> String = { input, isERC20 ->
	if (!isERC20) {
		if (input.equals(SolidityCode.ethTransfer, true)) {
			TransactionText.noMemo
		} else {
			input.toUpperCase().toStringFromHex()
		}
	} else {
		if (input.length > 138) {
			input.substring(138, input.length).toUpperCase().toStringFromHex()
		} else TransactionText.noMemo
	}
}
private val descriptionText: (
	isReceive: Boolean,
	toAddress: String,
	fromAddress: String
) -> String = { isReceive, to, from ->
	if (isReceive) TransactionText.sentTo + to
	else TransactionText.receivedFrom + from
}