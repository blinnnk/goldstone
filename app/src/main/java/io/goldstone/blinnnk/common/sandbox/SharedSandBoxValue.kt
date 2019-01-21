package io.goldstone.blinnnk.common.sandbox

import com.blinnnk.util.getIntFromSharedPreferences
import com.blinnnk.util.saveDataToSharedPreferences
import io.goldstone.blinnnk.GoldStoneApp

/**
 * @date: 2019-01-14.
 * @author: yangLiHai
 * @description:
 */
object SharedSandBoxValue {
	fun getUnRecoveredWalletCount(): Int {
		return GoldStoneApp.appContext.getIntFromSharedPreferences("rest_wallet_count")
	}
	
	fun updateUnRecoveredWalletCount(count: Int) {
		GoldStoneApp.appContext.saveDataToSharedPreferences("rest_wallet_count", count)
	}
	
}