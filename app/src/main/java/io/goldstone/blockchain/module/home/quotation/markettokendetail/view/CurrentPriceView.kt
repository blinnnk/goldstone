package io.goldstone.blockchain.module.home.quotation.markettokendetail.view

import android.annotation.SuppressLint
import android.content.Context
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import com.blinnnk.extension.CustomTargetTextStyle
import com.blinnnk.extension.into
import com.blinnnk.extension.setAlignParentBottom
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.observing
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.value.GrayScale
import io.goldstone.blockchain.common.value.QuotationText
import io.goldstone.blockchain.common.value.Spectrum
import io.goldstone.blockchain.module.home.quotation.quotation.model.CurrencyPriceInfoModel
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.textColor

/**
 * @date 25/04/2018 8:22 AM
 * @author KaySaith
 */

data class CurrentPriceModel(
	val currentPrice: Double = 0.0, val baseCurrency: String = "", val percent: String = "0.0"
) {
	constructor(data: CurrencyPriceInfoModel, symbol: String) : this(
		data.price.toDouble(), symbol, data.percent
	)
}

@SuppressLint("SetTextI18n")
class CurrentPriceView(context: Context) : MarketTokenDetailBaseCell(context) {

	var model: CurrentPriceModel by observing(CurrentPriceModel()) {
		val value = " ${model.baseCurrency}" + " ≈ 99.701 RMB"
		priceTitles.text = CustomTargetTextStyle(
			value, "${model.currentPrice}" + value, GrayScale.black, 12.uiPX(), true, false
		)

		percent.text = model.percent + "%"

		// 增减显示不同的颜色
		if (model.percent.toDouble() < 0.0) {
			percent.textColor = Spectrum.red
		} else {
			percent.textColor = Spectrum.green
		}

	}

	private val priceTitles by lazy { TextView(context) }
	private val percent by lazy { TextView(context) }

	init {
		title.text = QuotationText.currentPrice
		showTopLine = true

		priceTitles.apply {
			textColor = GrayScale.black
			textSize = 8.uiPX().toFloat()
			typeface = GoldStoneFont.black(context)
			layoutParams = LinearLayout.LayoutParams(matchParent, matchParent)
			gravity = Gravity.START or Gravity.BOTTOM
			y -= 5.uiPX()
		}.into(this)
		priceTitles.setAlignParentBottom()
		percent.apply {
			textColor = Spectrum.green
			textSize = 5.uiPX().toFloat()
			typeface = GoldStoneFont.heavy(context)
			layoutParams = LinearLayout.LayoutParams(matchParent, matchParent)
			gravity = Gravity.END or Gravity.BOTTOM
			y -= 8.uiPX()
		}.into(this)
		percent.setAlignParentBottom()
	}

}