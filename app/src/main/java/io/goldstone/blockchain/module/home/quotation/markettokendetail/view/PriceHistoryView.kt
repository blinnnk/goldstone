package io.goldstone.blockchain.module.home.quotation.markettokendetail.view

import android.content.Context
import android.widget.RelativeLayout
import com.blinnnk.extension.into
import com.blinnnk.extension.setAlignParentBottom
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.observing
import io.goldstone.blockchain.common.utils.safeGet
import io.goldstone.blockchain.common.value.QuotationText
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.verticalLayout
import org.json.JSONObject

/**
 * @date 25/04/2018 9:04 AM
 * @author KaySaith
 */

data class PriceHistoryModel(
	val dayHighest: String,
	val dayLow: String,
	val totalHighest: String,
	val totalLow: String,
	val baseSymbol: String
) {
	constructor(data: JSONObject, symbol: String) : this(
		data.safeGet("high_24"),
		data.safeGet("low_24"),
		data.safeGet("high_total"),
		data.safeGet("low_total"),
		symbol
	)
}

class PriceHistoryView(context: Context) : MarketTokenDetailBaseCell(context) {

	var model: PriceHistoryModel? by observing(null) {
		model?.apply {
			dayPrice.setPricesubtitle("$dayHighest / $dayLow", baseSymbol)
			totalPrice.setPricesubtitle("$totalHighest / $totalLow", baseSymbol)
		}
	}

	private val dayPrice = MarketTokenDetailBaseInfoCell(context)
	private val totalPrice = MarketTokenDetailBaseInfoCell(context)

	init {
		title.text = QuotationText.priceHistory
		layoutParams = RelativeLayout.LayoutParams(matchParent, 160.uiPX())

		dayPrice.setPriceTitle("24 Hours")
		totalPrice.setPriceTitle("Total")

		verticalLayout {
			dayPrice.into(this)
			totalPrice.into(this)
			y -= 10.uiPX()
		}.setAlignParentBottom()
	}

}