package io.goldstone.blinnnk.kernel.commontable.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * @date 2018/6/12 3:42 PM
 * @author KaySaith
 */
data class ServerConfigModel(
	@SerializedName("name")
	val name: String,
	@SerializedName("on_off")
	val switch: String,
	@SerializedName("value")
	val value: String
) : Serializable {
	constructor() : this(
		"",
		"",
		""
	)
}

data class QRCodeModel(
	val amount: Double,
	val walletAddress: String,
	val contractAddress: String,
	val chainID: String
) : Serializable