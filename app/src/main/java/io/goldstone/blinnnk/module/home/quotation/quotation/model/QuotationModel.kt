package io.goldstone.blinnnk.module.home.quotation.quotation.model

import com.blinnnk.extension.safeGet
import io.goldstone.blinnnk.module.home.quotation.quotationsearch.model.QuotationSelectionTable
import org.json.JSONObject
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
	val chartData: List<ChartPoint> = listOf(),
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
		chartData: List<ChartPoint>
	) : this(
		data.baseSymbol.toUpperCase(),
		data.name.toLowerCase(),
		price,
		percent,
		chartData,
		data.market,
		data.orderID,
		data.pairDisplay,
		data.pair,
		data.quoteSymbol,
		data.contract
	)
}

/**
 * important `Serializable` 传参的时候 非继承与 `Serializable` 的会导致
 * 崩溃. Look `https://stackoverflow.com/questions/4670215/`
 */
data class ChartPoint(val label: String, val value: Float) : Serializable {
	// 没有 `Price` 的时候用 `Close` 当天的收盘值作为 `Price` 记录
	constructor(data: JSONObject) : this(
		data.safeGet("time"),
		if (!data.toString().contains("close")) data.safeGet("price").toFloat()
		else data.safeGet("close").toFloat()
	)
}