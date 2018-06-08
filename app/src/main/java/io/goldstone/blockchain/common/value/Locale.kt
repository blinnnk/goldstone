@file:Suppress("DEPRECATION")

package io.goldstone.blockchain.common.value

import android.content.res.Resources
import java.util.*

/**
 * @date 17/04/2018 6:35 PM
 * @author KaySaith
 */

object CountryCode {
	val china =
		Locale(
			"cn",
			"CN"
		)
	val korean =
		Locale(
			"kr",
			"KR"
		)
	val japan =
		Locale(
			"ja",
			"JP"
		)
	val russia =
		Locale(
			"ru",
			"RU"
		)
	val america =
		Locale(
			"en",
			"US"
		)

	val currentCurrency =
		Currency.getInstance(Resources.getSystem().configuration.locale).currencyCode!!
	val currentCountry = Resources.getSystem().configuration.locale.country!!
	val currentLanguage = Resources.getSystem().configuration.locale.displayLanguage!!
	val currentLanguageSymbol = Resources.getSystem().configuration.locale.language!!

}
