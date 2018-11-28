package io.goldstone.blockchain.kernel.network.eos.commonmodel

import com.google.gson.annotations.SerializedName
import java.io.Serializable


/**
 * @author KaySaith
 * @date  2018/11/27
 */
data class EOSTokenBalance(
	@SerializedName("symbol")
	val symbol: String,
	@SerializedName("code")
	val codeName: String,
	@SerializedName("balance")
	val balance: Double
) : Serializable