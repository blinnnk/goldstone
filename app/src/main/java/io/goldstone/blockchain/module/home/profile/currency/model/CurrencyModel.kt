package io.goldstone.blockchain.module.home.profile.currency.model

import com.google.gson.annotations.SerializedName

/**
 * @date 26/03/2018 2:25 PM
 * @author KaySaith
 */

data class CurrencyModel(
  @SerializedName("currency")
  val symbol: String = "",
  var isChecked: Boolean = false
)