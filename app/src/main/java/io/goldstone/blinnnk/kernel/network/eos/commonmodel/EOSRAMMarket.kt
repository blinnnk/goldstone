package io.goldstone.blinnnk.kernel.network.eos.commonmodel

import com.blinnnk.extension.getTargetChild
import com.blinnnk.extension.safeGet
import org.json.JSONObject
import java.io.Serializable
import java.math.BigInteger


/**
 * @author KaySaith
 * @date  2018/09/24
 */
data class EOSRAMMarket(
	val ramCore: Double,
	val ramBalance: BigInteger,
	val ramWeight: Double,
	val eosBalance: Double,
	val eosWeight: Double
) : Serializable {
	constructor(data: JSONObject) : this(
		data.safeGet("supply").substringBeforeLast(" ").toDouble(),
		BigInteger(data.getTargetChild("base", "balance").substringBeforeLast(" ")),
		data.getTargetChild("base", "weight").toDouble(),
		data.getTargetChild("quote", "balance").substringBeforeLast(" ").toDouble(),
		data.getTargetChild("quote", "weight").toDouble()
	)
}