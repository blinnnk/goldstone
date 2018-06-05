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
	val chartData: ArrayList<ChartPoint> = arrayListOf(),
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
		chartData: ArrayList<ChartPoint>
	) : this(
		data.baseSymnbol.toUpperCase(),
		data.name.toLowerCase(),
		price,
		percent,
		chartData,
		data.market.toLowerCase(),
		data.orderID,
		data.pairDisplay,
		data.pair,
		data.quoteSymbol
	)
}

/**
 * important `Serializable` 传参的时候 非继承与 `Serializable` 的会导致
 * 崩溃. Look `https://stackoverflow.com/questions/4670215/`
 */
class ChartPoint(label: String, value: Float) : Point(
	label,
	value
), Serializable