package io.goldstone.blinnnk.module.home.profile.currency.view

import android.content.Context
import com.blinnnk.util.observing
import io.goldstone.blinnnk.R
import io.goldstone.blinnnk.common.base.basecell.BaseRadioCell
import io.goldstone.blinnnk.kernel.commontable.SupportCurrencyTable

/**
 * @date 26/03/2018 2:26 PM
 * @author KaySaith
 */
class CurrencyCell(context: Context) : BaseRadioCell(context) {

	var model: SupportCurrencyTable by observing(SupportCurrencyTable()) {
		title.text = model.currencySymbol
		checkedStatus = model.isUsed
		val image = when (model.currencySymbol) {
			"CNY" -> R.drawable.china_icon
			"JPY" -> R.drawable.japan_icon
			"KRW" -> R.drawable.korea_icon
			"RUB" -> R.drawable.russia_icon
			"EUR" -> R.drawable.europe_icon
			"SGD" -> R.drawable.singapore_icon
			"INR" -> R.drawable.india_icon
			"GBP" -> R.drawable.england_icon
			"CAD" -> R.drawable.canada_icon
			"BYN" -> R.drawable.belarus_icon
			"AUD" -> R.drawable.australian_icon
			"THB" -> R.drawable.thailand_icon
			else -> R.drawable.amercia_icon
		}

		showIcon(image)
	}
}

enum class CurrencySymbol(val value: String, val symbol: String) {
	CNY("CNY", "¥"),
	JPY("JPY", "¥"),
	KRW("KRW", ""),
	RUB("RUB", ""),
	EUR("EUR", ""),
	SGD("SGD", ""),
	INR("INR", ""),
	GBP("GBP", ""),
	CAD("CAD", ""),
	BYN("BYN", ""),
	AUD("AUD", ""),
	THB("THB", ""),
	USD("USD", "$");

	companion object {
		fun getSymbol(currency: String): String {
			return when (currency) {
				CurrencySymbol.CNY.value -> CurrencySymbol.CNY.symbol
				CurrencySymbol.JPY.value -> CurrencySymbol.JPY.symbol
				CurrencySymbol.KRW.value -> CurrencySymbol.KRW.symbol
				CurrencySymbol.RUB.value -> CurrencySymbol.RUB.symbol
				CurrencySymbol.EUR.value -> CurrencySymbol.EUR.symbol
				CurrencySymbol.SGD.value -> CurrencySymbol.SGD.symbol
				CurrencySymbol.INR.value -> CurrencySymbol.INR.symbol
				CurrencySymbol.GBP.value -> CurrencySymbol.GBP.symbol
				CurrencySymbol.CAD.value -> CurrencySymbol.CAD.symbol
				CurrencySymbol.BYN.value -> CurrencySymbol.BYN.symbol
				CurrencySymbol.AUD.value -> CurrencySymbol.AUD.symbol
				CurrencySymbol.THB.value -> CurrencySymbol.THB.symbol
				else -> CurrencySymbol.USD.symbol
			}
		}
	}
}

