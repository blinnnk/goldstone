package io.goldstone.blockchain.module.home.quotation.quotation.model

import com.db.chart.model.Point

/**
 * @date 26/03/2018 8:57 PM
 * @author KaySaith
 */

data class QuotationModel(
  val symbol: String = "",
  val name: String = "",
  val price: String = "",
  val percent: String = "",
  val chartData: ArrayList<Point> = arrayListOf(),
  val exchangeName: String = ""
)