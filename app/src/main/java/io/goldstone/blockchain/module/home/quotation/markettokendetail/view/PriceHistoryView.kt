package io.goldstone.blockchain.module.home.quotation.markettokendetail.view

import android.content.Context
import android.widget.RelativeLayout
import com.blinnnk.extension.into
import com.blinnnk.extension.safeGet
import com.blinnnk.extension.setAlignParentBottom
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.observing
import io.goldstone.blockchain.common.component.GraySqualCell
import io.goldstone.blockchain.common.component.TopBottomLineCell
import io.goldstone.blockchain.common.value.DateAndTimeText
import io.goldstone.blockchain.common.value.PaddingSize
import io.goldstone.blockchain.common.value.QuotationText
import io.goldstone.blockchain.module.home.quotation.quotationsearch.model.QuotationSelectionTable
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
	var baseSymbol: String
) {
	
	constructor(
		data: JSONObject,
		symbol: String
	) : this(
		data.safeGet("high_24"),
		data.safeGet("low_24"),
		data.safeGet("high_total"),
		data.safeGet("low_total"),
		symbol
	)
	
	constructor(
		data: QuotationSelectionTable,
		symbol: String
	) : this(
		data.high24,
		data.low24,
		data.highTotal,
		data.lowTotal,
		symbol
	)
	
	constructor(symbol: String) : this(
		"",
		"",
		"",
		"",
		symbol
	)
}

class PriceHistoryView(context: Context) : TopBottomLineCell(context) {
	
	var model: PriceHistoryModel? by observing(null) {
		model?.apply {
			dayPrice.setPricesubtitle("$dayHighest / $dayLow", baseSymbol)
			totalPrice.setPricesubtitle("$totalHighest / $totalLow", baseSymbol)
		}
	}
	private val dayPrice = GraySqualCell(context)
	private val totalPrice = GraySqualCell(context)
	
	init {
		setTitle(QuotationText.priceHistory)
		layoutParams = RelativeLayout.LayoutParams(matchParent, 150.uiPX())
		setHorizontalPadding(PaddingSize.device.toFloat())
		dayPrice.setPriceTitle(DateAndTimeText.hours)
		totalPrice.setPriceTitle(DateAndTimeText.total)
		
		verticalLayout {
			dayPrice.into(this)
			totalPrice.into(this)
		}.setAlignParentBottom()
	}
}