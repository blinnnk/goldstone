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
import io.goldstone.blockchain.common.component.cell.TopBottomLineCell
import io.goldstone.blockchain.common.language.QuotationText
import io.goldstone.blockchain.common.sharedpreference.SharedWallet
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.value.*
import io.goldstone.blockchain.common.value.ScreenSize
import io.goldstone.blockchain.crypto.utils.formatCurrency
import io.goldstone.blockchain.module.home.quotation.quotation.model.CurrencyPriceInfoModel
import io.goldstone.blockchain.module.home.quotation.quotation.model.QuotationModel
import org.jetbrains.anko.*

/**
 * @date 25/04/2018 8:22 AM
 * @author KaySaith
 */
data class CurrentPriceModel(
	val currentPrice: Float = 0f,
	val baseCurrency: String = "",
	val percent: String = "0.0",
	val usdtPrice: Double = 1.0,
	var isDisconnected: Boolean = false
) {

	constructor(
		data: CurrencyPriceInfoModel,
		symbol: String,
		isDisconnected: Boolean
	) : this(
		data.price.toFloat(),
		symbol,
		data.percent,
		if (data.usdtPrice.isNullOrBlank()) 1.0 else data.usdtPrice.toDouble(),
		isDisconnected
	)

	constructor(data: QuotationModel) : this(
		data.price.toFloatOrNull().orElse(0f),
		data.symbol,
		data.percent,
		1.0
	)
}

@SuppressLint("SetTextI18n")
class CurrentPriceView(context: Context) : TopBottomLineCell(context) {

	var model: CurrentPriceModel by observing(CurrentPriceModel()) {
		val value =
			" ${model.baseCurrency} ≈ ${(model.currentPrice * model.usdtPrice).formatCurrency()} ${SharedWallet.getCurrencyCode()}"
		priceTitles.text = CustomTargetTextStyle(
			value, "${model.currentPrice.toBigDecimal()}" + value, GrayScale.black, 12.uiPX(), true, false
		)
		percent.text = model.percent + "%"
		// 增减显示不同的颜色
		when {
			model.isDisconnected -> percent.textColor = GrayScale.midGray
			model.percent.toDouble() < 0.0 -> percent.textColor = Spectrum.red
			else -> percent.textColor = Spectrum.green
		}
	}
	private var priceTitles: TextView
	private var percent: TextView

	init {
		setHorizontalPadding(PaddingSize.content.toFloat())
		orientation = VERTICAL
		setTitle(QuotationText.currentPrice)
		layoutParams = LinearLayout.LayoutParams(matchParent, 80.uiPX())
		showTopLine = true
		priceTitles = textView {
			textColor = GrayScale.black
			textSize = fontSize(24)
			typeface = GoldStoneFont.black(context)
			layoutParams = LinearLayout.LayoutParams(ScreenSize.widthWithPadding, wrapContent)
			gravity = Gravity.START or Gravity.BOTTOM
			x = PaddingSize.gsCard.toFloat()
			y -= 5.uiPX()
		}
		percent = textView {
			textColor = Spectrum.green
			textSize = fontSize(15)
			typeface = GoldStoneFont.heavy(context)
			layoutParams = LinearLayout.LayoutParams(ScreenSize.widthWithPadding, 20.uiPX())
			gravity = Gravity.END or Gravity.BOTTOM
			x -= PaddingSize.gsCard
			y -= 31.uiPX()
		}
	}
}