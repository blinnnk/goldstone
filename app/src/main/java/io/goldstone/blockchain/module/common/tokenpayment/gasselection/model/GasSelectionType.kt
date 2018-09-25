package io.goldstone.blockchain.module.common.tokenpayment.gasselection.model

import io.goldstone.blockchain.common.language.LoadingText
import io.goldstone.blockchain.common.language.TransactionText
import io.goldstone.blockchain.crypto.multichain.CoinSymbol
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
	val unitSymbol: String = CoinSymbol.eth
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
		currentType: String,
		symbol: String
	) : this(
		id,
		"${(price * bytes).toBTCCount().toBigDecimal()} $symbol",
		"≈ $price Satoshi  * $bytes bytes",
		calculateBTCSeriesType(id, price),
		currentType,
		symbol
	)

	companion object {
		fun calculateType(id: Int, gWei: Double): String {
			return if (id == 3) MinerFeeType.Custom.type
			else {
				when (gWei.toGwei()) {
					MinerFeeType.Cheap.value -> MinerFeeType.Cheap.type
					MinerFeeType.Fast.value -> MinerFeeType.Fast.type
					MinerFeeType.Recommend.value -> MinerFeeType.Recommend.type
					else -> MinerFeeType.Custom.type
				}
			}
		}

		fun calculateBTCSeriesType(id: Int, satoshi: Long): String {
			return if (id == 3) MinerFeeType.Custom.type
			else {
				when (satoshi) {
					MinerFeeType.Cheap.satoshi -> MinerFeeType.Cheap.type
					MinerFeeType.Fast.satoshi -> MinerFeeType.Fast.type
					MinerFeeType.Recommend.satoshi -> MinerFeeType.Recommend.type
					else -> MinerFeeType.Custom.type
				}
			}
		}
	}
}