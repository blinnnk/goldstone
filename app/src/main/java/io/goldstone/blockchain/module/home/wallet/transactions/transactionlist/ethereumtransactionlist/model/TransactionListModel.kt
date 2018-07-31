package io.goldstone.blockchain.module.home.wallet.transactions.transactionlist.ethereumtransactionlist.model

import com.blinnnk.util.HoneyDateUtil
import io.goldstone.blockchain.common.utils.TimeUtils
import io.goldstone.blockchain.common.utils.toMillsecond
import io.goldstone.blockchain.common.value.DateAndTimeText
import io.goldstone.blockchain.common.value.TransactionText
import io.goldstone.blockchain.crypto.CryptoSymbol
import io.goldstone.blockchain.crypto.CryptoValue
import io.goldstone.blockchain.crypto.SolidityCode
import io.goldstone.blockchain.crypto.utils.CryptoUtils
import io.goldstone.blockchain.crypto.utils.toBTCCount
import io.goldstone.blockchain.crypto.utils.toStringFromHex
import io.goldstone.blockchain.kernel.commonmodel.BitcoinSeriesTransactionTable
import io.goldstone.blockchain.kernel.commonmodel.TransactionTable
import io.goldstone.blockchain.kernel.network.EtherScanApi
import io.goldstone.blockchain.kernel.network.EtherScanApi.bitcoinTransactionDetail
import java.io.Serializable

/**
 * @date 24/03/2018 7:09 PM
 * @author KaySaith
 */
data class TransactionListModel(
	var addressName: String,
	var fromAddress: String,
	val addressInfo: String,
	var count: Double,
	val symbol: String,
	val isReceived: Boolean,
	val date: String,
	val toAddress: String,
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
	var isFailed: Boolean,
	var isFee: Boolean = false
) : Serializable {
	
	constructor(data: TransactionTable) : this(
		if (data.isReceive) data.fromAddress
		else data.tokenReceiveAddress.orEmpty(),
		data.fromAddress,
		CryptoUtils.scaleTo32(
			HoneyDateUtil.getSinceTime(
				data.timeStamp.toMillsecond(),
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
		data.minerFee + getUnitSymbol(data.symbol), // 计算燃气费使用情况
		generateTransactionURL(data.hash, data.symbol), // Api 地址拼接
		data.isPending,
		data.timeStamp,
		data.value,
		data.hasError == "1",
		data.contractAddress,
		data.isFailed,
		data.isFee
	)
	
	constructor(data: BitcoinSeriesTransactionTable) : this(
		if (!data.fromAddress.equals(data.recordAddress, true)) data.fromAddress else formatToAddress(
			data.to
		),
		data.fromAddress,
		CryptoUtils.scaleTo32(
			HoneyDateUtil.getSinceTime(
				data.timeStamp.toMillsecond(),
				DateAndTimeText.getDateText()
			) + descriptionText(
				data.to.contains(data.recordAddress, true),
				formatToAddress(data.to),
				data.fromAddress
			)
		), // 副标题的生成
		data.value.toDouble().toBTCCount(),
		CryptoSymbol.btc,
		data.isReceive,
		TimeUtils.formatDate(data.timeStamp), // 拼接时间
		formatToAddress(data.to),
		data.blockNumber,
		data.hash,
		"",
		"${data.fee.toDouble().toBTCCount().toBigDecimal()} ${CryptoSymbol.btc}",
		generateTransactionURL(data.hash, CryptoSymbol.btc),
		data.isPending,
		data.timeStamp,
		data.value.toDouble().toBTCCount().toString(),
		false,
		CryptoValue.btcContract,
		false,
		data.isFee
	)
	
	companion object {
		fun formatToAddress(toAddress: String): String {
			var formatedAddresses = ""
			// `ToAddress` 可能是数组也可能是单一地址, 根据不同的情况截取字符串的
			// `Start And End` 的值设定不同
			val miroSetIndex = if (toAddress.contains("[")) 1 else 0
			val addresses =
				toAddress.substring(miroSetIndex, toAddress.count() - miroSetIndex).split(",")
			addresses.forEachIndexed { index, item ->
				formatedAddresses += item + if (index == addresses.lastIndex) "" else "\n"
			}
			return formatedAddresses
		}
		
		fun generateTransactionURL(taxHash: String, symbol: String?): String {
			return when {
				symbol.equals(CryptoSymbol.etc, true) ->
					EtherScanApi.gasTrackerHeader(taxHash)
				symbol.equals(CryptoSymbol.btc, true) ->
					bitcoinTransactionDetail(taxHash)
				else -> EtherScanApi.transactionDetail(taxHash)
			}
		}
		
		private fun getUnitSymbol(symbol: String): String {
			return " " + if (symbol.equals(CryptoSymbol.etc, true))
				CryptoSymbol.etc
			else CryptoSymbol.eth
		}
	}
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