package io.goldstone.blockchain.module.common.tokenpayment.gasselection.model

import io.goldstone.blockchain.common.language.LoadingText
import io.goldstone.blockchain.common.language.TransactionText
import io.goldstone.blockchain.crypto.CryptoSymbol
import io.goldstone.blockchain.crypto.utils.*

/**
 * @date 2018/5/16 11:37 PM
 * @author KaySaith
 */
data class GasSelectionModel(
	val id: Int = 0,
	val count: String = "0.000 ETH",
	val info: String = "≈ 0.00 Gwei (${TransactionText.gasPrice}) * 0.000 (${TransactionText.gasLimit})",
	var type: String = LoadingText.calculating,
	var currentType: String = "",
	val unitSymbol: String = CryptoSymbol.eth
) {
	
	constructor(
		id: Int,
		gWei: Double,
		gasLimit: Double,
		currentType: String,
		unitSymbol: String
	) : this(
		id,
		(gWei * gasLimit).toUnitValue(unitSymbol), // count 转换过的
		"≈ ${gWei.toGWeiValue()} Gwei (${TransactionText.gasPrice}) * ${gasLimit.toGasValue()} (${TransactionText.gasLimit})",
		calculateType(id, gWei),
		currentType,
		unitSymbol
	)
	
	constructor(
		id: Int,
		price: Long, // Satoshi
		bytes: Long,
		currentType: String
	) : this(
		id,
		"${(price * bytes).toBTCCount().toBigDecimal()} ${CryptoSymbol.btc}",
		"≈ $price Satoshi  * $bytes bytes",
		calculateBTCType(id, price),
		currentType,
		CryptoSymbol.btc
	)
	
	companion object {
		fun calculateType(id: Int, gWei: Double): String {
			return if (id == 3) MinerFeeType.Custom.content
			else {
				when (gWei.toGwei()) {
					MinerFeeType.Cheap.value -> MinerFeeType.Cheap.content
					MinerFeeType.Fast.value -> MinerFeeType.Fast.content
					MinerFeeType.Recommend.value -> MinerFeeType.Recommend.content
					else -> MinerFeeType.Custom.content
				}
			}
		}
		
		fun calculateBTCType(id: Int, satoshi: Long): String {
			return if (id == 3) MinerFeeType.Custom.content
			else {
				when (satoshi) {
					MinerFeeType.Cheap.satoshi -> MinerFeeType.Cheap.content
					MinerFeeType.Fast.satoshi -> MinerFeeType.Fast.content
					MinerFeeType.Recommend.satoshi -> MinerFeeType.Recommend.content
					else -> MinerFeeType.Custom.content
				}
			}
		}
	}
}