package io.goldstone.blockchain.module.home.quotation.markettokendetail.view

import android.annotation.SuppressLint
import android.content.Context
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import com.blinnnk.extension.CustomTargetTextStyle
import com.blinnnk.extension.into
import com.blinnnk.extension.orElse
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.observing
import io.goldstone.blockchain.GoldStoneApp
import io.goldstone.blockchain.common.component.TopBottomLineCell
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.value.GrayScale
import io.goldstone.blockchain.common.value.QuotationText
import io.goldstone.blockchain.common.value.Spectrum
import io.goldstone.blockchain.common.value.fontSize
import io.goldstone.blockchain.crypto.formatCurrency
import io.goldstone.blockchain.module.home.quotation.quotation.model.CurrencyPriceInfoModel
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.textColor
import org.jetbrains.anko.wrapContent

/**
 * @date 25/04/2018 8:22 AM
 * @author KaySaith
 */

data class CurrentPriceModel(
	val currentPrice: Double = 0.0,
	val baseCurrency: String = "",
	val percent: String = "0.0",
	val usdtPrice: Double = 1.0
) {
	constructor(
		data: CurrencyPriceInfoModel,
		symbol: String
	) : this(
		data.price.toDouble(), symbol, data.percent,
		if (data.usdtPrice.isNullOrBlank()) 1.0 else data.usdtPrice?.toDouble().orElse(1.0)
	)
}

@SuppressLint("SetTextI18n")
class CurrentPriceView(context: Context) : TopBottomLineCell(context) {

	var model: CurrentPriceModel by observing(CurrentPriceModel()) {
		val value =
			" ${model.baseCurrency}" + " ≈ ${(model.currentPrice * model.usdtPrice).formatCurrency()} ${GoldStoneApp.currencyCode}"
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

		orientation = VERTICAL
		title.text = QuotationText.currentPrice
		showTopLine = true

		priceTitles.apply {
			textColor = GrayScale.black
			textSize = fontSize(24)
			typeface = GoldStoneFont.black(context)
			layoutParams = LinearLayout.LayoutParams(matchParent, wrapContent)
			gravity = Gravity.START or Gravity.BOTTOM
			y -= 5.uiPX()
		}.into(this)
		percent.apply {
			textColor = Spectrum.green
			textSize = fontSize(15)
			typeface = GoldStoneFont.heavy(context)
			layoutParams = LinearLayout.LayoutParams(matchParent, 20.uiPX())
			gravity = Gravity.END or Gravity.BOTTOM
			y -= 31.uiPX()
		}.into(this)
	}

}