package io.goldstone.blockchain.common.value

import com.blinnnk.util.*
import io.goldstone.blockchain.kernel.network.GoldStoneAPI

/**
 * @date 2018/6/8 3:18 PM
 * @author KaySaith
 */
object Config {
	
	fun isNotchScreen(): Boolean =
		GoldStoneAPI.context.getBooleanFromSharedPreferences(SharesPreference.isNotchScreen)
	
	fun updateNotchScreenStatus(isNotchScreen: Boolean) =
		GoldStoneAPI.context.saveDataToSharedPreferences(SharesPreference.isNotchScreen, isNotchScreen)
	
	fun getInstaBugStatus(): String =
		GoldStoneAPI.context.getStringFromSharedPreferences(SharesPreference.instaBugStatus)
	
	fun updateInstaBugStatus(statisCode: String) =
		GoldStoneAPI.context.saveDataToSharedPreferences(SharesPreference.instaBugStatus, statisCode)
	
	fun getCurrentAddress(): String =
		GoldStoneAPI.context.getStringFromSharedPreferences(SharesPreference.currentAddress)
	
	fun updateCurrentAddress(address: String) =
		GoldStoneAPI.context.saveDataToSharedPreferences(SharesPreference.currentAddress, address)
	
	fun getCurrentName(): String =
		GoldStoneAPI.context.getStringFromSharedPreferences(SharesPreference.currentName)
	
	fun updateCurrentName(name: String) =
		GoldStoneAPI.context.saveDataToSharedPreferences(SharesPreference.currentName, name)
	
	fun getCurrentID(): Int =
		GoldStoneAPI.context.getIntFromSharedPreferences(SharesPreference.currentID)
	
	fun updateCurrentID(id: Int) =
		GoldStoneAPI.context.saveDataToSharedPreferences(SharesPreference.currentID, id)
	
	fun getCurrentIsWatchOnlyOrNot(): Boolean =
		GoldStoneAPI.context.getBooleanFromSharedPreferences(SharesPreference.currentIsWatchOrNot)
	
	fun updateCurrentIsWatchOnlyOrNot(isWatchOnly: Boolean) =
		GoldStoneAPI.context.saveDataToSharedPreferences(
			SharesPreference.currentIsWatchOrNot,
			isWatchOnly
		)
	
	fun getCurrentBalance(): Double =
		GoldStoneAPI.context.getDoubleFromSharedPreferences(SharesPreference.currentBalance)
	
	fun updateCurrentBalance(balance: Double) =
		GoldStoneAPI.context.saveDataToSharedPreferences(
			SharesPreference.currentBalance,
			balance.toFloat()
		)
	
	fun getCurrentLanguageCode(): Int =
		GoldStoneAPI.context.getIntFromSharedPreferences(SharesPreference.currentLanguage)
	
	fun updateCurrentLanguageCode(languageCode: Int) =
		GoldStoneAPI.context.saveDataToSharedPreferences(SharesPreference.currentLanguage, languageCode)
	
	fun getCurrentChain(): String =
		if (GoldStoneAPI.context
				.getStringFromSharedPreferences(SharesPreference.currentChain)
				.equals("Default", true)
		) {
			ChainID.Main.id
		} else {
			GoldStoneAPI.context.getStringFromSharedPreferences(SharesPreference.currentChain)
		}
	
	fun updateCurrentChain(chainID: String) =
		GoldStoneAPI.context.saveDataToSharedPreferences(SharesPreference.currentChain, chainID)
	
	fun getETCCurrentChain(): String =
		if (GoldStoneAPI.context
				.getStringFromSharedPreferences(SharesPreference.etcCurrentChain)
				.equals("Default", true)
		) {
			ChainID.ETCMain.id
		} else {
			GoldStoneAPI.context.getStringFromSharedPreferences(SharesPreference.etcCurrentChain)
		}
	
	fun updateETCCurrentChain(chainID: String) =
		GoldStoneAPI.context.saveDataToSharedPreferences(SharesPreference.etcCurrentChain, chainID)
	
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