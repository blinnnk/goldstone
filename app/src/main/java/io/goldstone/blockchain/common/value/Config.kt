package io.goldstone.blockchain.common.value

import com.blinnnk.util.getDoubleFromSharedPreferences
import com.blinnnk.util.getIntFromSharedPreferences
import com.blinnnk.util.getStringFromSharedPreferences
import com.blinnnk.util.saveDataToSharedPreferences
import io.goldstone.blockchain.kernel.network.GoldStoneAPI

/**
 * @date 2018/6/8 3:18 PM
 * @author KaySaith
 */
object Config {
	fun getCurrentLanguageCode(): Int =
		GoldStoneAPI.context.getIntFromSharedPreferences(SharesPreference.currentLanguage)
	
	fun updateCurrentLanguageCode(languageCode: Int) =
		GoldStoneAPI.context.saveDataToSharedPreferences(
			SharesPreference.currentLanguage,
			languageCode
		)
	
	fun getCurrentChain(): String =
		GoldStoneAPI.context.getStringFromSharedPreferences(SharesPreference.currentChain)
	
	fun updateCurrentChain(chainID: String) =
		GoldStoneAPI.context.saveDataToSharedPreferences(SharesPreference.currentChain, chainID)
	
	fun getCurrencyCode(): String =
		GoldStoneAPI.context.getStringFromSharedPreferences(SharesPreference.currencyCode)
	
	fun updateCurrencyCode(code: String) =
		GoldStoneAPI.context.saveDataToSharedPreferences(SharesPreference.currencyCode, code)
	
	fun getCurrentRate(): Double =
		GoldStoneAPI.context.getDoubleFromSharedPreferences(SharesPreference.rate)
	
	fun updateCurrentRate(rate: Double) =
		GoldStoneAPI.context.saveDataToSharedPreferences(SharesPreference.rate, rate.toFloat())
	
	fun getWalletCount(): Int =
		GoldStoneAPI.context.getIntFromSharedPreferences(SharesPreference.walletCount)
	
	fun updateWalletCount(count: Int) =
		GoldStoneAPI.context.saveDataToSharedPreferences(SharesPreference.walletCount, count)
	
	fun getMaxWalletID(): Int =
		GoldStoneAPI.context.getIntFromSharedPreferences(SharesPreference.maxWalletID)
	
	fun updateMaxWalletID(id: Int) =
		GoldStoneAPI.context.saveDataToSharedPreferences(SharesPreference.maxWalletID, id)
}