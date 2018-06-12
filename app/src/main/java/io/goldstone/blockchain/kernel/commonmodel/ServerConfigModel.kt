package io.goldstone.blockchain.kernel.commonmodel

import com.google.gson.annotations.SerializedName

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
)