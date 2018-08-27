package io.goldstone.blockchain.module.home.quotation.quotation.model

import com.db.chart.model.Point
import io.goldstone.blockchain.module.home.quotation.pricealarmclock.pricealarmclocklist.model.PriceAlarmTable
import io.goldstone.blockchain.module.home.quotation.quotationsearch.model.QuotationSelectionTable
import java.io.Serializable

/**
 * @date 26/03/2018 8:57 PM
 * @author KaySaith
 * @rewriteDate 24/08/2018 17:43 PM
 * @rewriter wcx
 * @description 添加解析PriceAlarmTable构造函数
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
	val quoteSymbol: String = "",
	val contract: String = "",
	var isDisconnected: Boolean = false
) : Serializable {

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
		data.quoteSymbol,
		data.contract
	)

	constructor(priceAlarmTable: PriceAlarmTable) : this(
		priceAlarmTable.symbol,
		priceAlarmTable.name.toString(),
		priceAlarmTable.price,
		"",
		ArrayList(),
		priceAlarmTable.marketName,
		0.0,
		priceAlarmTable.pairDisplay,
		priceAlarmTable.pair,
		priceAlarmTable.currencyName,
		"",
		false
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