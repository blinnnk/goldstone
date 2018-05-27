package io.goldstone.blockchain.module.home.wallet.transactions.transactionlist.model

import android.text.format.DateUtils
import android.text.format.DateUtils.FORMAT_SHOW_TIME
import android.text.format.DateUtils.FORMAT_SHOW_YEAR
import com.blinnnk.util.HoneyDateUtil
import io.goldstone.blockchain.crypto.CryptoUtils
import io.goldstone.blockchain.crypto.SolidityCode
import io.goldstone.blockchain.crypto.toAscii
import io.goldstone.blockchain.crypto.toEthValue
import io.goldstone.blockchain.kernel.commonmodel.TransactionTable
import io.goldstone.blockchain.kernel.network.EtherScanApi
import io.goldstone.blockchain.kernel.network.GoldStoneAPI
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
	val memo: String,
	val minerFee: String,
	val url: String,
	val isPending: Boolean,
	val timeStamp: String,
	val value: String,
	val hasError: Boolean
) : Serializable {
	
	constructor(data: TransactionTable) : this(
		data.tokenReceiveAddress.orEmpty(),
		CryptoUtils.scaleTo28(HoneyDateUtil.getSinceTime(data.timeStamp) + descriptionText(data.isReceive) + data.fromAddress), // 副标题的生成
		data.value.toDouble(), // 转账个数
		data.symbol,
		data.isReceive,
		DateUtils.formatDateTime(GoldStoneAPI.context, data.timeStamp.toLong() * 1000, FORMAT_SHOW_YEAR) + " " + DateUtils.formatDateTime(GoldStoneAPI.context, data.timeStamp.toLong() * 1000, FORMAT_SHOW_TIME), // 拼接时间
		if (data.isReceive) data.fromAddress else data.tokenReceiveAddress.orEmpty(),
		data.blockNumber,
		data.hash, getMemoFromInputCode(data.input),
		(data.gasUsed.toDouble() * data.gasPrice.toDouble()).toEthValue(), // 计算燃气费使用情况
		EtherScanApi.singleTransactionHas(data.hash), // Api 地址拼接
		data.isPending,
		data.timeStamp,
		data.value,
		data.hasError == "1"
	)
}

val getMemoFromInputCode: (inputCode: String) -> String = {
	if (it == SolidityCode.ethTransfer) {
		it.toAscii(false)
	} else {
		if (it.length > 138) {
			it.substring(136, it.length).toAscii(false)
		} else "There isn't a memo"
	}
}
private val descriptionText: (isReceive: Boolean) -> String = {
	if (it) " incoming from " else " send from "
}