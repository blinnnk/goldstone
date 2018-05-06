package io.goldstone.blockchain.module.common.tokenpayment.paymentvaluedetail.model

import io.goldstone.blockchain.crypto.toEthValue
import io.goldstone.blockchain.crypto.toGWeiValue
import io.goldstone.blockchain.crypto.toGasValue
import io.goldstone.blockchain.crypto.toGwei
import org.web3j.crypto.RawTransaction

/**
 * @date 28/03/2018 12:26 PM
 * @author KaySaith
 */

enum class MinerFeeType(val content: String, val value: Double) {
	Recommend("recommend", 30.0), Cheap("cheap", 1.0), Fast("fast", 100.0)
}

data class PaymentValueDetailModel(
	val count: String = "0.000 ETH",
	val info: String = "≈ 0.00 Gwei (Gas Price) * 0.000 (Gas Limit)",
	val type: String = "calculating",
	var isSelected: Boolean = false,
	var rawTransaction: RawTransaction? = null
) {

	constructor(gWei: Double, raw: RawTransaction, currentType: String) : this(
		(gWei * raw.gasLimit.toDouble()).toEthValue(), // count 转换过的
		"≈ ${gWei.toGWeiValue()} Gwei (Gas Price) * ${raw.gasLimit.toDouble().toGasValue()} (Gas Limit)",
		when (gWei.toGwei()) {
			MinerFeeType.Cheap.value -> MinerFeeType.Cheap.content
			MinerFeeType.Fast.value -> MinerFeeType.Fast.content
			else -> MinerFeeType.Recommend.content
		},
		gWei.toGwei() == when (currentType) {
			MinerFeeType.Recommend.content -> MinerFeeType.Recommend.value
			MinerFeeType.Fast.content -> MinerFeeType.Fast.value
			else -> MinerFeeType.Cheap.value
		},
		raw
	)

}