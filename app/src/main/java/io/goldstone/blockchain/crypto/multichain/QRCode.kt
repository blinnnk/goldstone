package io.goldstone.blockchain.crypto.multichain

import com.blinnnk.extension.orZero
import io.goldstone.blockchain.common.utils.LogUtil
import io.goldstone.blockchain.kernel.commonmodel.QRCodeModel


/**
 * @author KaySaith
 * @date  2018/09/23
 */
class QRCode(val content: String) {

	fun isValid(): Boolean {
		return when {
			content.isEmpty() -> false
			!content.contains(":") -> false
			CryptoName.allChainName.none {
				content.contains(it, true)
			} -> false
			content.length < CryptoValue.bitcoinAddressClassicLength -> false
			else -> true
		}
	}

	/** Convert Code Content */
	fun convertEOSQRCode(): QRCodeModel {
		val chainIDContent =
			if (content.contains("chain_id=")) content.substringAfter("chain_id=") else ""
		val chainID = when {
			chainIDContent.isEmpty() -> ChainID.eosMain
			chainIDContent.contains("&") -> chainIDContent.substringBefore("&")
			else -> chainIDContent
		}
		val accountName = content.substringAfter("address=").substringBefore("&")
		val contract = content.substringBefore("/transfer").substringAfter(":")
		return if (content.contains("value")) {
			val value = content.substringAfter("value=").substringBefore("e")
			val decimalContent = content.substringAfter("value=").substringAfter("e")
			val decimal =
				if (decimalContent.contains("&")) decimalContent.substringBefore("&")
				else decimalContent
			// 如果未来需要解析精度会用到 `Decimal` 这里暂时保留方法 By KaySaith
			LogUtil.debug("convertERC20QRCode", decimal)
			QRCodeModel(value.toDoubleOrNull().orZero(), accountName, contract, chainID)
		} else {
			QRCodeModel(0.0, accountName, contract, chainID)
		}
	}

	fun convertETHSeriesQRCode(): QRCodeModel {
		val chainID = if (content.contains("chain_id"))
			content.substringAfter("chain_id=").substringBefore("e18")
		else ""
		val contract =
			if (ChainID(chainID).isETCMain() || ChainID(chainID).isETCTest())
				TokenContract.etcContract else TokenContract.ethContract
		return if (content.contains("?")) {
			val address = content.substringBefore("?").substringAfter(":")
			val value = content.substringAfter("value=").substringBefore("e18")
			val amount = value.toDoubleOrNull().orZero()
			QRCodeModel(amount, address, contract, chainID)
		} else {
			val address = content.substringAfter(":")
			QRCodeModel(0.0, address, contract, chainID)
		}
	}

	fun convertERC20QRCode(): QRCodeModel {
		val chainIDContent =
			if (content.contains("chain_id="))
				content.substringAfter("chain_id=")
			else ""
		val chainID = when {
			chainIDContent.isEmpty() -> ChainID.ethMain
			chainIDContent.contains("&") -> chainIDContent.substringBefore("&")
			else -> chainIDContent
		}
		val walletAddress = content.substringAfter("address=").substringBefore("&")
		val contract = content.substringBefore("/transfer").substringAfter(":")
		return if (content.contains("value")) {
			val value = content.substringAfter("value=").substringBefore("e")
			val decimalContent = content.substringAfter("value=").substringAfter("e")
			val decimal =
				if (decimalContent.contains("&")) {
					decimalContent.substringBefore("&")
				} else {
					decimalContent
				}
			// 如果未来需要解析精度会用到 `Decimal` 这里暂时保留方法 By KaySaith
			LogUtil.debug("convertERC20QRCode", decimal)
			QRCodeModel(value.toDoubleOrNull().orZero(), walletAddress, contract, chainID)
		} else {
			QRCodeModel(0.0, walletAddress, contract, chainID)
		}
	}

	fun convertBitcoinQRCode(): QRCodeModel? {
		val chainName = content.substringBefore(":")
		val chainID = CryptoName.getBTCSeriesChainIDByName(chainName)?.id
		val contract = CryptoName.getBTCSeriesContractByChainName(chainName)
		if (chainID.isNullOrEmpty() || contract.isNullOrEmpty()) {
			return null
		}
		return if (content.contains("?")) {
			val address = content.substringBefore("?").substringAfter(":")
			val amount = content.substringAfter("amount=").toDoubleOrNull().orZero()
			QRCodeModel(amount, address, contract, chainID)
		} else {
			val address = content.substringAfter(":")
			QRCodeModel(0.0, address, contract, chainID)
		}
	}

	companion object {
		/** Generate Code Content */
		fun generateETHOrETCCode(
			walletAddress: String,
			value: Double,
			chainID: String
		): String {
			// 没有设置 `金额`
			return when (value) {
				0.0 ->
					"ethereum:$walletAddress?value=${0.0}&chain_id=$chainID"
				// 有设置 `燃气费` 或 `Memo`
				else -> "ethereum:$walletAddress?value=${value}e18&chain_id=$chainID"
			}
		}

		fun generateBitcoinCode(
			walletAddress: String,
			amount: Double
		): String {
			// 没有设置 `金额`
			return when (amount) {
				0.0 ->
					"bitcoin:$walletAddress"
				// 有设置 `燃气费` 或 `Memo`
				else -> "bitcoin:$walletAddress?amount=$amount"
			}
		}

		fun generateLitecoinCode(
			walletAddress: String,
			amount: Double
		): String {
			// 没有设置 `金额`
			return when (amount) {
				0.0 ->
					"litecoin:$walletAddress"
				// 有设置 `燃气费` 或 `Memo`
				else -> "litecoin:$walletAddress?amount=$amount"
			}
		}

		fun generateBitcoinCashCode(
			walletAddress: String,
			amount: Double
		): String {
			// 没有设置 `金额`
			return when (amount) {
				0.0 -> "bitcoinCash:$walletAddress"
				else -> "bitcoinCash:$walletAddress?amount=$amount"
			}
		}

		fun generateERC20Code(
			walletAddress: String,
			contractAddress: String,
			value: Double,
			decimal: Int,
			chainID: String
		): String {
			return when ( // 没有设置 `金额`
				value) {
				0.0 ->
					"ethereum:$contractAddress/transfer?address=$walletAddress&unit256=1&chain_id=$chainID"
				// 有设置 `燃气费` 或 `Memo`
				else ->
					"ethereum:$contractAddress/transfer?address=$walletAddress&value=${value}e$decimal&unit256=1&chain_id=$chainID"
			}
		}

		fun generateEOSCode(
			accountName: String,
			codeName: String,
			value: Double,
			decimal: Int,
			chainID: String
		): String {
			return when ( // 没有设置 `金额`
				value) {
				0.0 ->
					"eos:$codeName/transfer?address=$accountName&unit256=1&chain_id=$chainID"
				// 有设置 `燃气费` 或 `Memo`
				else ->
					"eos:$codeName/transfer?address=$accountName&value=${value}e$decimal&unit256=1&chain_id=$chainID"
			}
		}
	}
}