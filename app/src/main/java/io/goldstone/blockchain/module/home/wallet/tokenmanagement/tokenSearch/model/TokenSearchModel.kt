package io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenSearch.model

import com.google.gson.annotations.SerializedName

/**
 * @date 16/04/2018 5:49 PM
 * @author KaySaith
 */

data class TokenSearchModel(
	@SerializedName("address")
	var contract: String = "",
	@SerializedName("url")
	var iconUrl: String = "",
	@SerializedName("symbol")
	var symbol: String = "",
	@SerializedName("price")
	var price: String = "",
	@SerializedName("name")
	var name: String = "",
	@SerializedName("decimals")
	var decimal: Int = 0,
	@SerializedName("type")
	var type: Int = 0,
	@SerializedName("weight")
	var weight: Int = 0
)