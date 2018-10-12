package io.goldstone.blockchain.module.home.wallet.transactions.transactionlist.ethereumtransactionlist.model

import com.blinnnk.extension.scaleTo
import com.blinnnk.extension.toDoubleOrZero
import com.blinnnk.extension.toMillisecond
import com.blinnnk.util.HoneyDateUtil
import io.goldstone.blockchain.common.language.DateAndTimeText
import io.goldstone.blockchain.common.language.TransactionText
import io.goldstone.blockchain.common.utils.TimeUtils
import io.goldstone.blockchain.common.utils.convertToDiskUnit
import io.goldstone.blockchain.common.utils.convertToTimeUnit
import io.goldstone.blockchain.crypto.ethereum.SolidityCode
import io.goldstone.blockchain.crypto.multichain.*
import io.goldstone.blockchain.crypto.utils.toStringFromHex
import io.goldstone.blockchain.kernel.commonmodel.BTCSeriesTransactionTable
import io.goldstone.blockchain.kernel.commonmodel.TransactionTable
import io.goldstone.blockchain.kernel.commonmodel.eos.EOSTransactionTable
import io.goldstone.blockchain.kernel.network.EtherScanApi
import io.goldstone.blockchain.kernel.network.EtherScanApi.bitcoinCashTransactionDetail
import io.goldstone.blockchain.kernel.network.EtherScanApi.bitcoinTransactionDetail
import io.goldstone.blockchain.kernel.network.EtherScanApi.eosTransactionDetail
import io.goldstone.blockchain.kernel.network.EtherScanApi.litecoinTransactionDetail
import org.json.JSONArray
import java.io.Serializable
import java.math.BigInteger

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
	var contract: TokenContract,
	var isFailed: Boolean,
	var isFee: Boolean = false
) : Serializable {

	constructor(data: EOSTransactionTable) : this(
		if (data.recordAccountName == data.transactionData.fromName)
			data.transactionData.toName
		else data.transactionData.fromName,
		data.transactionData.fromName,
		generateAddressInfo(data),
		data.transactionData.quantity.substringBeforeLast(" ").toDoubleOrZero(),
		data.transactionData.quantity.substringAfterLast(" ").toUpperCase(),
		data.recordAccountName == data.transactionData.toName,
		TimeUtils.formatDate(data.time),
		data.transactionData.toName,
		if (data.blockNumber == 0) "" else "${data.blockNumber}",
		data.txID,
		data.transactionData.memo,
		if (data.cupUsage * data.netUsage == BigInteger.ZERO) "" else generateEOSMinerContent(data.cupUsage, data.netUsage),
		generateTransactionURL(data.txID, CoinSymbol.eos, true),
		data.isPending,
		data.time.toString(),
		data.transactionData.quantity.substringBeforeLast(" "),
		false,
		TokenContract(""),
		false,
		false
	)

	constructor(data: TransactionTable) : this(
		if (data.isReceive) data.fromAddress
		else data.tokenReceiveAddress.orEmpty(),
		data.fromAddress,
		generateAddressInfo(data), // 副标题的生成
		data.value.toDouble(), // 转账个数
		data.symbol,
		data.isReceive,
		TimeUtils.formatDate(data.timeStamp), // 拼接时间
		data.tokenReceiveAddress.orEmpty(),
		data.blockNumber,
		data.hash,
		data.memo,
		data.minerFee + getUnitSymbol(data.symbol), // 计算燃气费使用情况
		generateTransactionURL(data.hash, data.symbol, false), // Api 地址拼接
		data.isPending,
		data.timeStamp,
		data.value,
		data.hasError == "1",
		TokenContract(data.contractAddress),
		data.isFailed,
		data.isFee
	)

	constructor(data: BTCSeriesTransactionTable) : this(
		if (data.isReceive) data.fromAddress
		else formatToAddress(data.to),
		data.fromAddress,
		HoneyDateUtil.getSinceTime(
			data.timeStamp.toMillisecond(),
			DateAndTimeText.getDateText()
		) + descriptionText(
			data.to.contains(data.recordAddress, true),
			formatToAddress(data.to),
			data.fromAddress
		).scaleTo(32), // 副标题的生成
		data.value.toDouble(),
		data.symbol,
		data.isReceive,
		TimeUtils.formatDate(data.timeStamp), // 拼接时间
		data.to,
		data.blockNumber,
		data.hash,
		"",
		"${data.fee.toDouble().toBigDecimal()} ${data.symbol}",
		generateTransactionURL(data.hash, data.symbol, false),
		data.isPending,
		data.timeStamp,
		data.value.toDouble().toString(),
		false,
		CoinSymbol(data.symbol).getContract().orEmpty(),
		false,
		data.isFee
	)

	companion object {

		fun generateEOSMinerContent(cpuUsage: BigInteger, netUsage: BigInteger): String {
			return "cpu usage: ${cpuUsage.convertToTimeUnit()}, net usage: ${netUsage.convertToDiskUnit()}"
		}

		fun generateAddressInfo(data: TransactionTable): String {
			return HoneyDateUtil.getSinceTime(
				data.timeStamp.toMillisecond(),
				DateAndTimeText.getDateText()
			) + descriptionText(
				data.isReceive,
				data.tokenReceiveAddress.orEmpty(),
				data.fromAddress
			).scaleTo(32)
		}

		fun generateAddressInfo(data: EOSTransactionTable): String {
			val isReceive = data.transactionData.toName == data.recordAccountName
			return HoneyDateUtil.getSinceTime(
				data.time,
				DateAndTimeText.getDateText()
			) + descriptionText(
				isReceive,
				data.transactionData.toName,
				data.transactionData.fromName
			).scaleTo(32)
		}

		fun formatToAddress(toAddress: String): String {
			var formattedAddresses = ""
			// `toAddress` 可能是数组也可能是单一地址, 根据不同的情况截取字符串的
			// `Start And End` 的值设定不同
			val miroSetIndex = if (toAddress.contains("[")) 1 else 0
			val addresses =
				toAddress.substring(miroSetIndex, toAddress.count() - miroSetIndex).split(",")
			addresses.forEachIndexed { index, item ->
				formattedAddresses += item + if (index == addresses.lastIndex) "" else "\n"
			}
			return formattedAddresses
		}

		fun convertMultiToOrFromAddresses(content: String): List<String> {
			return if (content.contains("[")) {
				(0 until JSONArray(content).length()).map {
					JSONArray(content)[it].toString()
				}
			} else {
				listOf(content)
			}
		}

		fun generateTransactionURL(taxHash: String, symbol: String?, isEOSSeries: Boolean): String {
			return when {
				CoinSymbol(symbol).isETC() ->
					EtherScanApi.gasTrackerHeader(taxHash)
				CoinSymbol(symbol).isBTC() ->
					bitcoinTransactionDetail(taxHash)
				CoinSymbol(symbol).isLTC() ->
					litecoinTransactionDetail(taxHash)
				CoinSymbol(symbol).isBCH() ->
					bitcoinCashTransactionDetail(taxHash)
				CoinSymbol(symbol).isEOS() || isEOSSeries -> {
					eosTransactionDetail(taxHash)
				}
				else -> EtherScanApi.transactionDetail(taxHash)
			}
		}

		private fun getUnitSymbol(symbol: String): String {
			return " " + if (symbol.equals(CoinSymbol.etc, true))
				CoinSymbol.etc
			else CoinSymbol.eth
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