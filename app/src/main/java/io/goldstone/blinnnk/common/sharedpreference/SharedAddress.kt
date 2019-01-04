package io.goldstone.blinnnk.common.sharedpreference

import com.blinnnk.util.getStringFromSharedPreferences
import com.blinnnk.util.saveDataToSharedPreferences
import io.goldstone.blinnnk.GoldStoneApp
import io.goldstone.blinnnk.common.value.SharesPreference
import io.goldstone.blinnnk.crypto.eos.account.EOSAccount


/**
 * @author KaySaith
 * @date  2018/09/27
 */

object SharedAddress {
	/** Coin Address In SharedPreference */
	// EOS Account Name
	fun getCurrentEOSAccount(): EOSAccount =
		EOSAccount(GoldStoneApp.appContext.getStringFromSharedPreferences(SharesPreference.currentEOSName))

	fun updateCurrentEOSName(name: String) =
		GoldStoneApp.appContext.saveDataToSharedPreferences(SharesPreference.currentEOSName, name)

	fun getCurrentEthereum(): String =
		GoldStoneApp.appContext.getStringFromSharedPreferences(SharesPreference.currentEthereumAddress)

	fun updateCurrentEthereum(address: String) =
		GoldStoneApp.appContext.saveDataToSharedPreferences(
			SharesPreference.currentEthereumAddress,
			address
		)

	fun getCurrentETC(): String =
		GoldStoneApp.appContext.getStringFromSharedPreferences(SharesPreference.currentETCAddress)

	fun updateCurrentETC(address: String) =
		GoldStoneApp.appContext.saveDataToSharedPreferences(SharesPreference.currentETCAddress, address)

	fun getCurrentEOS(): String =
		GoldStoneApp.appContext.getStringFromSharedPreferences(SharesPreference.currentEOSAddress)

	fun updateCurrentEOS(address: String) =
		GoldStoneApp.appContext.saveDataToSharedPreferences(SharesPreference.currentEOSAddress, address)

	fun getCurrentBTC(): String =
		GoldStoneApp.appContext.getStringFromSharedPreferences(SharesPreference.currentBTCAddress)

	fun updateCurrentBTC(address: String) =
		GoldStoneApp.appContext.saveDataToSharedPreferences(SharesPreference.currentBTCAddress, address)

	fun getCurrentBTCSeriesTest(): String =
		GoldStoneApp.appContext.getStringFromSharedPreferences(SharesPreference.currentBTCTestAddress)

	fun updateCurrentBTCSeriesTest(address: String) =
		GoldStoneApp.appContext.saveDataToSharedPreferences(
			SharesPreference.currentBTCTestAddress,
			address
		)

	fun getCurrentLTC(): String =
		GoldStoneApp.appContext.getStringFromSharedPreferences(SharesPreference.currentLTCAddress)

	fun updateCurrentLTC(address: String) =
		GoldStoneApp.appContext.saveDataToSharedPreferences(SharesPreference.currentLTCAddress, address)

	fun getCurrentBCH(): String =
		GoldStoneApp.appContext.getStringFromSharedPreferences(SharesPreference.currentBCHAddress)

	fun updateCurrentBCH(address: String) =
		GoldStoneApp.appContext.saveDataToSharedPreferences(SharesPreference.currentBCHAddress, address)
}