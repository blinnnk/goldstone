package io.goldstone.blockchain.common.sharedpreference

import com.blinnnk.util.getBooleanFromSharedPreferences
import com.blinnnk.util.getDoubleFromSharedPreferences
import com.blinnnk.util.getStringFromSharedPreferences
import com.blinnnk.util.saveDataToSharedPreferences
import io.goldstone.blockchain.GoldStoneApp
import io.goldstone.blockchain.common.value.SharesPreference


/**
 * @author KaySaith
 * @date  2018/09/27
 */
object SharedValue {
	// EOS KB
	fun getRAMUnitPrice(): Double =
		GoldStoneApp.appContext.getDoubleFromSharedPreferences(SharesPreference.ramUnitPrice)

	fun updateRAMUnitPrice(unitPrice: Double) =
		GoldStoneApp.appContext.saveDataToSharedPreferences(SharesPreference.ramUnitPrice, unitPrice.toFloat())

	// DAPP JS Code
	fun getJSCode(): String {
		val localData = GoldStoneApp.appContext.getStringFromSharedPreferences(SharesPreference.jsCode)
		return localData.replace("goldStoneAccountName", "\"${SharedAddress.getCurrentEOSAccount().name}\"")
	}

	fun updateJSCode(code: String) =
		GoldStoneApp.appContext.saveDataToSharedPreferences(SharesPreference.jsCode, code)

	fun getCPUUnitPrice(): Double =
		GoldStoneApp.appContext.getDoubleFromSharedPreferences(SharesPreference.cpuUnitPrice)

	fun updateCPUUnitPrice(unitPrice: Double) =
		GoldStoneApp.appContext.saveDataToSharedPreferences(SharesPreference.cpuUnitPrice, unitPrice.toFloat())

	fun getNETUnitPrice(): Double =
		GoldStoneApp.appContext.getDoubleFromSharedPreferences(SharesPreference.netUnitPrice)

	fun updateNETUnitPrice(unitPrice: Double) =
		GoldStoneApp.appContext.saveDataToSharedPreferences(SharesPreference.netUnitPrice, unitPrice.toFloat())

	fun isTestEnvironment(): Boolean =
		GoldStoneApp.appContext.getBooleanFromSharedPreferences(SharesPreference.isTestEnvironment)

	fun updateIsTestEnvironment(isTest: Boolean) =
		GoldStoneApp.appContext.saveDataToSharedPreferences(
			SharesPreference.isTestEnvironment,
			isTest
		)

	fun getPincodeDisplayStatus(): Boolean =
		GoldStoneApp.appContext.getBooleanFromSharedPreferences(SharesPreference.needToShowPincode)

	fun updatePincodeDisplayStatus(status: Boolean) =
		GoldStoneApp.appContext.saveDataToSharedPreferences(
			SharesPreference.needToShowPincode,
			status
		)

	fun getAccountCheckedStatus(): Boolean =
		GoldStoneApp.appContext.getBooleanFromSharedPreferences(SharesPreference.accountCheckedStatus)

	fun updateAccountCheckedStatus(status: Boolean) =
		GoldStoneApp.appContext.saveDataToSharedPreferences(
			SharesPreference.accountCheckedStatus,
			status
		)

	fun getDeveloperModeStatus(): Boolean =
		GoldStoneApp.appContext.getBooleanFromSharedPreferences(SharesPreference.developerMode)

	fun updateDeveloperModeStatus(status: Boolean) =
		GoldStoneApp.appContext.saveDataToSharedPreferences(
			SharesPreference.developerMode,
			status
		)
}