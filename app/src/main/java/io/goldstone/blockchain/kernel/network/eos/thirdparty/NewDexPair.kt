package io.goldstone.blockchain.kernel.network.eos.thirdparty

import com.google.gson.annotations.SerializedName
import java.io.Serializable


/**
 * @author KaySaith
 * @date  2018/10/23
 * @description
 * {"symbol":"lite_eos","contract":"buildertoken","currency":"LITE","price_precision":7,"currency_precision":4}
 */
data class NewDexPair(
	@SerializedName("symbol")
	val pair: String,
	@SerializedName("currency")
	val symbol: String,
	@SerializedName("contract")
	val contract: String
) : Serializable