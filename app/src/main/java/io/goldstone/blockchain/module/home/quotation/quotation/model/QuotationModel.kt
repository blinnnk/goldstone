package io.goldstone.blockchain.module.home.quotation.quotation.model

import com.db.chart.model.Point
import io.goldstone.blockchain.module.home.quotation.quotationsearch.model.QuotationSelectionTable
import java.io.Serializable

/**
 * @date 26/03/2018 8:57 PM
 * @author KaySaith
 */

data class QuotationModel(
	val symbol: String = "",
	val name: String = "",
	var price: String = "",
	var percent: String = "",
	val chartData: ArrayList<Point> = arrayListOf(),
	val exchangeName: String = "",
	val orderID: Double = 0.0,
	val pairDisplay: String = "",
	val pair: String = "",
	val quoteSymbol: String = ""
): Serializable {
	constructor(
		data: QuotationSelectionTable,
		price: String,
		percent: String,
		chartData: ArrayList<Point>
	) : this(
		data.baseSymnbol.toUpperCase(),
		data.name.toLowerCase(),
		price,
		percent,
		chartData,
		data.market.toLowerCase(),
		data.orderID,
		data.infoTitle,
		data.pair,
		data.quoteSymbol
	)
}