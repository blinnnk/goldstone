package io.goldstone.blinnnk.module.home.wallet.notifications.notificationlist.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable


/**
 * @author KaySaith
 * @date  2018/11/10
 */
data class ExtraTransactionModel(
	@SerializedName("value")
	val value: String,
	@SerializedName("address")
	val address: String
) : Serializable {
	constructor() : this(
		"",
		""
	)
}