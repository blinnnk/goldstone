package io.goldstone.blockchain.module.common.tokenpayment.gasselection.model

import io.goldstone.blockchain.crypto.toEthValue
import io.goldstone.blockchain.crypto.toGWeiValue
import io.goldstone.blockchain.crypto.toGasValue
import io.goldstone.blockchain.crypto.toGwei

/**
 * @date 2018/5/16 11:37 PM
 * @author KaySaith
 */
data class GasSelectionModel(
	val id: Int = 0,
	val count: String = "0.000 ETH",
	val info: String = "≈ 0.00 Gwei (Gas Price) * 0.000 (Gas Limit)",
	var type: String = "calculating",
	var currentType: String = ""
) {
	
	constructor(
		id: Int,
		gWei: Double,
		gasLimit: Double,
		currentType: String
	) : this(
		id,
		(gWei * gasLimit).toEthValue(), // count 转换过的
		"≈ ${gWei.toGWeiValue()} Gwei (Gas Price) * ${gasLimit.toGasValue()} (Gas Limit)",
		calculateType(id, gWei),
		currentType
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