package io.goldstone.blockchain.module.home.wallet.transactions.transactionlist.ethereumtransactionlist.model

import com.blinnnk.extension.*
import com.blinnnk.util.HoneyDateUtil
import io.goldstone.blockchain.common.language.DateAndTimeText
import io.goldstone.blockchain.common.language.TransactionText
import io.goldstone.blockchain.common.utils.TimeUtils
import io.goldstone.blockchain.common.utils.convertToDiskUnit
import io.goldstone.blockchain.common.utils.convertToTimeUnit
import io.goldstone.blockchain.crypto.ethereum.SolidityCode
import io.goldstone.blockchain.crypto.multichain.*
import io.goldstone.blockchain.crypto.utils.CryptoUtils
import io.goldstone.blockchain.crypto.utils.toSatoshi
import io.goldstone.blockchain.crypto.utils.toStringFromHex
import io.goldstone.blockchain.kernel.commonmodel.BTCSeriesTransactionTable
import io.goldstone.blockchain.kernel.commonmodel.TransactionTable
import io.goldstone.blockchain.kernel.commonmodel.eos.EOSTransactionTable
import io.goldstone.blockchain.kernel.network.ChainExplorer
import io.goldstone.blockchain.kernel.network.ethereum.EtherScanApi
import io.goldstone.blockchain.kernel.network.ethereum.EtherScanApi.bitcoinCashTransactionDetail
import io.goldstone.blockchain.kernel.network.ethereum.EtherScanApi.bitcoinTransactionDetail
import io.goldstone.blockchain.kernel.network.ethereum.EtherScanApi.litecoinTransactionDetail
import io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.model.TransactionSealedModel
import java.math.BigInteger

/**
 * @date 24/03/2018 7:09 PM
 * @author KaySaith
 */
data class TransactionListModel(
	var addressName: String,
	override var fromAddress: String,
	val addressInfo: String,
	override var count: Double,
	override val symbol: String,
	val isReceived: Boolean,
	override val date: String,
	override val toAddress: String,
	override val blockNumber: Int,
	val transactionHash: String,
	override var memo: String,
	override val minerFee: String,
	val url: List<String>,
	override val isPending: Boolean,
	val timeStamp: String,
	override val value: BigInteger,
	override val hasError: Boolean,
	override val contract: TokenContract,
	var pageID: Long,
	override var isFee: Boolean = false,
	override val confirmations: Int,
	val dataIndex: Int
) : TransactionSealedModel(
	isPending,
	transactionHash,
	symbol,
	fromAddress,
	toAddress,
	count,
	value,
	isReceived,
	contract,
	isFee,
	hasError,
	blockNumber,
	timeStamp,
	confirmations,
	memo,
	minerFee,
	null
) {

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
		data.blockNumber,
		data.txID,
		data.transactionData.memo,
		if (data.cupUsage * data.netUsage == BigInteger.ZERO) "" else generateEOSMinerContent(data.cupUsage, data.netUsage),
		generateTransactionURL(data.txID, TokenContract.ETH, true),
		data.isPending,
		data.time.toString(),
		BigInteger.valueOf(data.transactionData.quantity.substringBeforeLast(" ").toLongOrZero()),
		false,
		TokenContract(data.codeName, data.symbol, null),
		data.serverID,
		false,
		0,
		data.dataIndex
	)

	constructor(data: TransactionTable) : this(
		if (data.isReceive) data.fromAddress else data.to,
		data.fromAddress,
		generateAddressInfo(data), // 副标题的生成
		data.count, // 转账个数
		data.symbol,
		data.isReceive,
		TimeUtils.formatDate(data.timeStamp.toLongOrZero()), // 拼接时间
		data.to,
		data.blockNumber,
		data.hash,
		data.memo,
		data.minerFee, // 计算燃气费使用情况
		generateTransactionURL(
			data.hash,
			if (data.contractAddress.equals(TokenContract.etcContract, true))
				TokenContract.ETC
			else TokenContract.ETH,
			false
		), // Api 地址拼接
		data.isPending,
		data.timeStamp,
		BigInteger.valueOf(data.value.toLongOrZero()),
		data.hasError == "1",
		TokenContract(data.contractAddress, data.symbol, null),
		0L, // TODO
		data.isFee,
		data.confirmations.toIntOrZero(),
		0
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
		TimeUtils.formatDate(data.timeStamp.toLongOrZero()), // 拼接时间
		data.to,
		data.blockNumber,
		data.hash,
		"",
		data.fee.toDouble().toBigDecimal().toPlainString(),
		generateTransactionURL(data.hash, TokenContract.BTC, false),
		data.isPending,
		data.timeStamp,
		BigInteger.valueOf(data.value.toDoubleOrZero().toSatoshi()),
		false,
		CoinSymbol(data.symbol).getContract().orEmpty(),
		data.dataIndex.toLong(), // TODO
		data.isFee,
		data.confirmations,
		data.dataIndex
	)

	companion object {

		fun generateEOSMinerContent(cpuUsage: BigInteger, netUsage: BigInteger): String {
			return "cpu usage: ${cpuUsage.convertToTimeUnit()}, net usage: ${netUsage.convertToDiskUnit()}"
		}

		fun generateAddressInfo(data: TransactionTable): String {
			return HoneyDateUtil.getSinceTime(
				data.timeStamp.toMillisecond(),
				DateAndTimeText.getDateText()
			) + descriptionText(data.isReceive, data.to, data.fromAddress).scaleTo(32)
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
				if (content.contains(","))
					content.substring(1, content.lastIndex).split(",")
				else listOf(content.substring(1, content.lastIndex))
			} else {
				listOf(content)
			}
		}

		fun generateTransactionURL(taxHash: String, contract: TokenContract, isEOSSeries: Boolean): List<String> {
			return when {
				contract.isETC() ->
					listOf(EtherScanApi.gasTrackerHeader(taxHash))
				contract.isBTC() ->
					listOf(bitcoinTransactionDetail(taxHash))
				contract.isLTC() ->
					listOf(litecoinTransactionDetail(taxHash))
				contract.isBCH() ->
					listOf(bitcoinCashTransactionDetail(taxHash))
				contract.isEOS() || isEOSSeries -> {
					listOf(ChainExplorer.eosTransactionDetail(taxHash), ChainExplorer.eosParkTXDetail(taxHash))
				}
				else -> listOf(EtherScanApi.transactionDetail(taxHash))
			}
		}
	}
}

val getMemoFromInputCode: (inputCode: String) -> String = { input ->
	if (!CryptoUtils.isERC20Transfer(input)) {
		if (input.equals(SolidityCode.ethTransfer, true))
			TransactionText.noMemo
		else input.toUpperCase().toStringFromHex()
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