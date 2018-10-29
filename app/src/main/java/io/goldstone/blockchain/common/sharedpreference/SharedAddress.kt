package io.goldstone.blockchain.common.sharedpreference

import com.blinnnk.util.getStringFromSharedPreferences
import com.blinnnk.util.saveDataToSharedPreferences
import io.goldstone.blockchain.common.value.SharesPreference
import io.goldstone.blockchain.crypto.eos.account.EOSAccount
import io.goldstone.blockchain.kernel.network.common.GoldStoneAPI


/**
 * @author KaySaith
 * @date  2018/09/27
 */

object SharedAddress {
	/** Coin Address In SharedPreference */
	// EOS Account Name
	fun getCurrentEOSAccount(): EOSAccount =
		EOSAccount(GoldStoneAPI.context.getStringFromSharedPreferences(SharesPreference.currentEOSName))

	fun updateCurrentEOSName(name: String) =
		GoldStoneAPI.context.saveDataToSharedPreferences(SharesPreference.currentEOSName, name)

	fun getCurrentEthereum(): String =
		GoldStoneAPI.context.getStringFromSharedPreferences(SharesPreference.currentEthereumAddress)

	fun updateCurrentEthereum(address: String) =
		GoldStoneAPI.context.saveDataToSharedPreferences(
			SharesPreference.currentEthereumAddress,
			address
		)

	fun getCurrentETC(): String =
		GoldStoneAPI.context.getStringFromSharedPreferences(SharesPreference.currentETCAddress)

	fun updateCurrentETC(address: String) =
		GoldStoneAPI.context.saveDataToSharedPreferences(SharesPreference.currentETCAddress, address)

	fun getCurrentEOS(): String =
		GoldStoneAPI.context.getStringFromSharedPreferences(SharesPreference.currentEOSAddress)

	fun updateCurrentEOS(address: String) =
		GoldStoneAPI.context.saveDataToSharedPreferences(SharesPreference.currentEOSAddress, address)

	fun getCurrentBTC(): String =
		GoldStoneAPI.context.getStringFromSharedPreferences(SharesPreference.currentBTCAddress)

	fun updateCurrentBTC(address: String) =
		GoldStoneAPI.context.saveDataToSharedPreferences(SharesPreference.currentBTCAddress, address)

	fun getCurrentBTCSeriesTest(): String =
		GoldStoneAPI.context.getStringFromSharedPreferences(SharesPreference.currentBTCTestAddress)

	fun updateCurrentBTCSeriesTest(address: String) =
		GoldStoneAPI.context.saveDataToSharedPreferences(
			SharesPreference.currentBTCTestAddress,
			address
		)

	fun getCurrentLTC(): String =
		GoldStoneAPI.context.getStringFromSharedPreferences(SharesPreference.currentLTCAddress)

	fun updateCurrentLTC(address: String) =
		GoldStoneAPI.context.saveDataToSharedPreferences(SharesPreference.currentLTCAddress, address)

	fun getCurrentBCH(): String =
		GoldStoneAPI.context.getStringFromSharedPreferences(SharesPreference.currentBCHAddress)

	fun updateCurrentBCH(address: String) =
		GoldStoneAPI.context.saveDataToSharedPreferences(SharesPreference.currentBCHAddress, address)
}