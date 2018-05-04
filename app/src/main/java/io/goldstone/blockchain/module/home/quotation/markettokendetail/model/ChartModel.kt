package io.goldstone.blockchain.module.home.quotation.markettokendetail.model

import com.google.gson.annotations.SerializedName

/**
 * @date 2018/5/4 4:33 PM
 * @author KaySaith
 */

data class ChartModel(
	@SerializedName("price") val price: String, @SerializedName("time") val timestamp: String
)