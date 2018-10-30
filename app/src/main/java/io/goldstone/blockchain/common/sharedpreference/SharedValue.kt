package io.goldstone.blockchain.common.sharedpreference

import com.blinnnk.util.getBooleanFromSharedPreferences
import com.blinnnk.util.getDoubleFromSharedPreferences
import com.blinnnk.util.saveDataToSharedPreferences
import io.goldstone.blockchain.common.value.SharesPreference
import io.goldstone.blockchain.kernel.network.common.GoldStoneAPI


/**
 * @author KaySaith
 * @date  2018/09/27
 */
object SharedValue {
	// EOS KB
	fun getRAMUnitPrice(): Double =
		GoldStoneAPI.context.getDoubleFromSharedPreferences(SharesPreference.ramUnitPrice)

	fun updateRAMUnitPrice(unitPrice: Double) =
		GoldStoneAPI.context.saveDataToSharedPreferences(SharesPreference.ramUnitPrice, unitPrice.toFloat())

	fun getCPUUnitPrice(): Double =
		GoldStoneAPI.context.getDoubleFromSharedPreferences(SharesPreference.cpuUnitPrice)

	fun updateCPUUnitPrice(unitPrice: Double) =
		GoldStoneAPI.context.saveDataToSharedPreferences(SharesPreference.cpuUnitPrice, unitPrice.toFloat())

	fun getNETUnitPrice(): Double =
		GoldStoneAPI.context.getDoubleFromSharedPreferences(SharesPreference.netUnitPrice)

	fun updateNETUnitPrice(unitPrice: Double) =
		GoldStoneAPI.context.saveDataToSharedPreferences(SharesPreference.netUnitPrice, unitPrice.toFloat())

	fun isTestEnvironment(): Boolean =
		GoldStoneAPI.context.getBooleanFromSharedPreferences(SharesPreference.isTestEnvironment)

	fun updateIsTestEnvironment(isTest: Boolean) =
		GoldStoneAPI.context.saveDataToSharedPreferences(
			SharesPreference.isTestEnvironment,
			isTest
		)
}