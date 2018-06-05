package io.goldstone.blockchain.module.home.profile.currency.view

import android.content.Context
import com.blinnnk.util.observing
import io.goldstone.blockchain.R
import io.goldstone.blockchain.common.base.BaseRadioCell
import io.goldstone.blockchain.kernel.commonmodel.SupportCurrencyTable

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

