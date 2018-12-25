package io.goldstone.blockchain.common.sharedpreference

import com.blinnnk.util.*
import io.goldstone.blockchain.GoldStoneApp
import io.goldstone.blockchain.common.value.SharesPreference
import io.goldstone.blockchain.crypto.multichain.WalletType


/**
 * @author KaySaith
 * @date  2018/09/27
 */
object SharedWallet {

	fun hasFingerprint(): Boolean =
		GoldStoneApp.appContext.getBooleanFromSharedPreferences(SharesPreference.hasFingerprint)

	fun updateFingerprint(has: Boolean) =
		GoldStoneApp.appContext.saveDataToSharedPreferences(SharesPreference.hasFingerprint, has)

	fun getCurrencyCode(): String =
		GoldStoneApp.appContext.getStringFromSharedPreferences(SharesPreference.currencyCode)

	fun updateCurrencyCode(code: String) =
		GoldStoneApp.appContext.saveDataToSharedPreferences(SharesPreference.currencyCode, code)

	fun getCurrentRate(): Double =
		GoldStoneApp.appContext.getDoubleFromSharedPreferences(SharesPreference.rate)

	fun updateCurrentRate(rate: Double) =
		GoldStoneApp.appContext.saveDataToSharedPreferences(SharesPreference.rate, rate.toFloat())

	fun updateWalletCount(count: Int) =
		GoldStoneApp.appContext.saveDataToSharedPreferences(SharesPreference.walletCount, count)

	fun getMaxWalletID(): Int {
		val default = GoldStoneApp.appContext.getIntFromSharedPreferences(SharesPreference.maxWalletID)
		return if (default == -1) 0 else default
	}

	fun updateMaxWalletID(id: Int) =
		GoldStoneApp.appContext.saveDataToSharedPreferences(SharesPreference.maxWalletID, id)

	fun getCurrentWalletType(): WalletType =
		WalletType(GoldStoneApp.appContext.getStringFromSharedPreferences(SharesPreference.walletType))

	fun updateCurrentWalletType(type: String) =
		GoldStoneApp.appContext.saveDataToSharedPreferences(SharesPreference.walletType, type)

	fun getGoldStoneID(): String =
		GoldStoneApp.appContext.getStringFromSharedPreferences(SharesPreference.goldStoneID)

	fun updateGoldStoneID(goldStoneID: String) =
		GoldStoneApp.appContext.saveDataToSharedPreferences(SharesPreference.goldStoneID, goldStoneID)

	// Configs For Review Or UpdateDatabase ETC.
	fun getNeedUnregisterGoldStoneID(): String =
		GoldStoneApp.appContext.getStringFromSharedPreferences(SharesPreference.unregisterGoldStoneID)

	fun updateUnregisterGoldStoneID(goldStoneID: String) =
		GoldStoneApp.appContext.saveDataToSharedPreferences(SharesPreference.unregisterGoldStoneID, goldStoneID)

	fun getCurrentName(): String =
		GoldStoneApp.appContext.getStringFromSharedPreferences(SharesPreference.currentName)

	fun updateCurrentName(name: String) =
		GoldStoneApp.appContext.saveDataToSharedPreferences(SharesPreference.currentName, name)

	fun getCurrentWalletID(): Int =
		GoldStoneApp.appContext.getIntFromSharedPreferences(SharesPreference.currentID)

	fun updateCurrentWalletID(id: Int) =
		GoldStoneApp.appContext.saveDataToSharedPreferences(SharesPreference.currentID, id)

	fun isWatchOnlyWallet(): Boolean =
		GoldStoneApp.appContext.getBooleanFromSharedPreferences(SharesPreference.currentIsWatchOrNot)

	fun updateCurrentIsWatchOnlyOrNot(isWatchOnly: Boolean) =
		GoldStoneApp.appContext.saveDataToSharedPreferences(
			SharesPreference.currentIsWatchOrNot,
			isWatchOnly
		)

	fun hasBackUpMnemonic(): Boolean =
		GoldStoneApp.appContext.getBooleanFromSharedPreferences(SharesPreference.hasBackUpMnemonic)

	fun updateBackUpMnemonicStatus(hasBackUp: Boolean) =
		GoldStoneApp.appContext.saveDataToSharedPreferences(
			SharesPreference.hasBackUpMnemonic,
			hasBackUp
		)

	fun getCurrentBalance(): Double =
		GoldStoneApp.appContext.getDoubleFromSharedPreferences(SharesPreference.currentBalance)

	fun updateCurrentBalance(balance: Double) =
		GoldStoneApp.appContext.saveDataToSharedPreferences(
			SharesPreference.currentBalance,
			balance.toFloat()
		)

	fun getCurrentLanguageCode(): Int =
		GoldStoneApp.appContext.getIntFromSharedPreferences(SharesPreference.currentLanguage)

	fun updateCurrentLanguageCode(languageCode: Int) =
		GoldStoneApp.appContext.saveDataToSharedPreferences(SharesPreference.currentLanguage, languageCode)

	fun isNotchScreen(): Boolean =
		GoldStoneApp.appContext.getBooleanFromSharedPreferences(SharesPreference.isNotchScreen)

	fun updateNotchScreenStatus(isNotchScreen: Boolean) =
		GoldStoneApp.appContext.saveDataToSharedPreferences(SharesPreference.isNotchScreen, isNotchScreen)
}