package io.goldstone.blockchain.module.home.wallet.walletdetail.model

import com.google.gson.annotations.SerializedName

/**
 * @date 2018/5/9 5:02 PM
 * @author KaySaith
 */

data class TokenPriceModel(
	@SerializedName("address")
	val contract: String,
	@SerializedName("price")
	val price: Double
) {
	// 这个没设定构造函数会导致 Gson 解析失败
	constructor() : this(
		"",
		0.0
	)
}