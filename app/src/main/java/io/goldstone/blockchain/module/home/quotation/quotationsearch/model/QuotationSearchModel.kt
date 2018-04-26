package io.goldstone.blockchain.module.home.quotation.quotationsearch.model

import com.google.gson.annotations.SerializedName
import io.goldstone.blockchain.common.utils.toUpperCaseFirstLetter

/**
 * @date 26/04/2018 10:47 AM
 * @author KaySaith
 */

data class QuotationSearchModel (
  @SerializedName("market_id") var id: Int,
  @SerializedName("pair_display") var pairDisplay: String,
  @SerializedName("base") var baseSymnbol: String,
  @SerializedName("quote") var quoteSymbol: String,
  @SerializedName("pair") var pair: String,
  @SerializedName("market") var market: String,
  var infoTitle: String
) {

  constructor(data: QuotationSearchModel) : this(
    data.id,
    data.pairDisplay,
    data.baseSymnbol,
    data.quoteSymbol,
    data.pair,
    data.market,
    data.pairDisplay + " " + data.market.toUpperCaseFirstLetter()
  )

}