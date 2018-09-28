package io.goldstone.blockchain.common.sharedpreference

import com.blinnnk.util.getBooleanFromSharedPreferences
import com.blinnnk.util.getDoubleFromSharedPreferences
import com.blinnnk.util.saveDataToSharedPreferences
import io.goldstone.blockchain.common.language.ChainText
import io.goldstone.blockchain.common.value.SharesPreference
import io.goldstone.blockchain.kernel.network.GoldStoneAPI


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


	fun isEncryptERCNodeRequest(): Boolean =
		if (SharedChain.getCurrentETHName().equals(ChainText.goldStoneMain, true)) {
			// 初始化 App 的时候默认节点是 `GoldStone Main` 这里判断一下
			updateEncryptERCNodeRequest(true)
			true
		} else {
			GoldStoneAPI.context.getBooleanFromSharedPreferences(SharesPreference.isEncryptERCNodeRequest)
		}

	fun updateEncryptERCNodeRequest(isEncrypt: Boolean) =
		GoldStoneAPI.context.saveDataToSharedPreferences(
			SharesPreference.isEncryptERCNodeRequest,
			isEncrypt
		)

	fun isTestEnvironment(): Boolean =
		GoldStoneAPI.context.getBooleanFromSharedPreferences(SharesPreference.isTestEnvironment)

	fun updateIsTestEnvironment(isTest: Boolean) =
		GoldStoneAPI.context.saveDataToSharedPreferences(
			SharesPreference.isTestEnvironment,
			isTest
		)

	fun isEncryptETCNodeRequest(): Boolean =
		GoldStoneAPI.context.getBooleanFromSharedPreferences(SharesPreference.isEncryptETCNodeRequest)

	fun updateEncryptETCNodeRequest(isEncrypt: Boolean) =
		GoldStoneAPI.context.saveDataToSharedPreferences(
			SharesPreference.isEncryptETCNodeRequest,
			isEncrypt
		)
}