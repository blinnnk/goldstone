package io.goldstone.blockchain.common.sharedpreference

import com.blinnnk.util.*
import io.goldstone.blockchain.common.value.SharesPreference
import io.goldstone.blockchain.common.value.SharesPreference.pincodeIsOpened
import io.goldstone.blockchain.crypto.multichain.WalletType
import io.goldstone.blockchain.kernel.network.GoldStoneAPI


/**
 * @author KaySaith
 * @date  2018/09/27
 * @rewriteDate 28/09/2018 18:42 PM
 * @reWriter wcx
 * @description 添加PincodeIsOpened和FingerprintUnlockerIsOpened相关存储取出方法
 */
object SharedWallet {
	fun getCurrencyCode(): String =
		GoldStoneAPI.context.getStringFromSharedPreferences(SharesPreference.currencyCode)

	fun updateCurrencyCode(code: String) =
		GoldStoneAPI.context.saveDataToSharedPreferences(SharesPreference.currencyCode, code)

	fun getCurrentRate(): Double =
		GoldStoneAPI.context.getDoubleFromSharedPreferences(SharesPreference.rate)

	fun updateCurrentRate(rate: Double) =
		GoldStoneAPI.context.saveDataToSharedPreferences(SharesPreference.rate, rate.toFloat())

	fun updateWalletCount(count: Int) =
		GoldStoneAPI.context.saveDataToSharedPreferences(SharesPreference.walletCount, count)

	fun getMaxWalletID(): Int {
		val default = GoldStoneAPI.context.getIntFromSharedPreferences(SharesPreference.maxWalletID)
		return if (default == -1) 0 else default
	}

	fun updateMaxWalletID(id: Int) =
		GoldStoneAPI.context.saveDataToSharedPreferences(SharesPreference.maxWalletID, id)

	fun getCurrentWalletType(): WalletType =
		WalletType(GoldStoneAPI.context.getStringFromSharedPreferences(SharesPreference.walletType))

	fun updateCurrentWalletType(type: String) =
		GoldStoneAPI.context.saveDataToSharedPreferences(SharesPreference.walletType, type)

	fun getGoldStoneID(): String =
		GoldStoneAPI.context.getStringFromSharedPreferences(SharesPreference.goldStoneID)

	fun updateGoldStoneID(goldStoneID: String) =
		GoldStoneAPI.context.saveDataToSharedPreferences(SharesPreference.goldStoneID, goldStoneID)

	// Configs For Review Or UpdateDatabase ETC.
	fun getNeedUnregisterGoldStoneID(): String =
		GoldStoneAPI.context.getStringFromSharedPreferences(SharesPreference.unregisterGoldStoneID)

	fun updateUnregisterGoldStoneID(goldStoneID: String) =
		GoldStoneAPI.context.saveDataToSharedPreferences(SharesPreference.unregisterGoldStoneID, goldStoneID)

	fun getYingYongBaoInReviewStatus(): Boolean =
		GoldStoneAPI.context.getBooleanFromSharedPreferences(SharesPreference.yingYongBaoInReview)

	fun updateYingYongBaoInReviewStatus(status: Boolean) =
		GoldStoneAPI.context.saveDataToSharedPreferences(SharesPreference.yingYongBaoInReview, status)

	fun getCurrentName(): String =
		GoldStoneAPI.context.getStringFromSharedPreferences(SharesPreference.currentName)

	fun updateCurrentName(name: String) =
		GoldStoneAPI.context.saveDataToSharedPreferences(SharesPreference.currentName, name)

	fun getCurrentWalletID(): Int =
		GoldStoneAPI.context.getIntFromSharedPreferences(SharesPreference.currentID)

	fun updateCurrentWalletID(id: Int) =
		GoldStoneAPI.context.saveDataToSharedPreferences(SharesPreference.currentID, id)

	fun isWatchOnlyWallet(): Boolean =
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

	fun isNotchScreen(): Boolean =
		GoldStoneAPI.context.getBooleanFromSharedPreferences(SharesPreference.isNotchScreen)

	fun updateNotchScreenStatus(isNotchScreen: Boolean) =
		GoldStoneAPI.context.saveDataToSharedPreferences(SharesPreference.isNotchScreen, isNotchScreen)

	fun updatePincodeIsOpened(pincodeIsOpened: Boolean) =
		GoldStoneAPI.context.saveDataToSharedPreferences(
			SharesPreference.pincodeIsOpened,
			pincodeIsOpened
		)

	fun isPincodeOpened() {
		GoldStoneAPI.context.getBooleanFromSharedPreferences(SharesPreference.pincodeIsOpened)
	}

	fun updateFingerprintUnlockerIsOpened(fingerprintUnlockerIsOpened: Boolean) =
		GoldStoneAPI.context.saveDataToSharedPreferences(
			SharesPreference.fingerprintUnlockerIsOpened,
			fingerprintUnlockerIsOpened
		)

	fun isFingerprintUnlockerOpened() {
		GoldStoneAPI.context.getBooleanFromSharedPreferences(SharesPreference.fingerprintUnlockerIsOpened)
	}
}