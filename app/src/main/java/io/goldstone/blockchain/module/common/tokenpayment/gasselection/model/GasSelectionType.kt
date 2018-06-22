package io.goldstone.blockchain.module.common.tokenpayment.gasselection.model

import io.goldstone.blockchain.common.value.LoadingText
import io.goldstone.blockchain.common.value.TransactionText
import io.goldstone.blockchain.crypto.CryptoSymbol
import io.goldstone.blockchain.crypto.utils.toGWeiValue
import io.goldstone.blockchain.crypto.utils.toGasValue
import io.goldstone.blockchain.crypto.utils.toGwei
import io.goldstone.blockchain.crypto.utils.toUnitValue

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
	}
}