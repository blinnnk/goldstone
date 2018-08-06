package io.goldstone.blockchain.module.home.quotation.quotationsearch.model

import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName

/**
 * @date 2018/4/27 2:49 PM
 * @author KaySaith
 */

data class QuotationSelectionLineChartModel(
	@SerializedName("pair") val pair: String,
	@SerializedName("point_list") val pointList: ArrayList<JsonObject>
) {
	constructor() : this(
		"",
		arrayListOf()
	)
}