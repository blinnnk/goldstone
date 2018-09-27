@file:Suppress("DEPRECATION")

package io.goldstone.blockchain.common.value

import android.content.res.Resources
import java.util.*

/**
 * @date 17/04/2018 6:35 PM
 * @author KaySaith
 */

object CountryCode {
	val china = Locale("cn", "CN")
	val currentCurrency =
		Currency.getInstance(Resources.getSystem().configuration.locale).currencyCode!!
	val currentCountry = Resources.getSystem().configuration.locale.country!!
	val currentLanguageSymbol = Resources.getSystem().configuration.locale.language!!

}
