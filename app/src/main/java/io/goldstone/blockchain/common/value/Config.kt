package io.goldstone.blockchain.common.value

import com.blinnnk.util.*
import io.goldstone.blockchain.common.language.ChainText
import io.goldstone.blockchain.kernel.network.GoldStoneAPI

/**
 * @date 2018/6/8 3:18 PM
 * @author KaySaith
 */
object Config {

	fun isEncryptERCNodeRequest(): Boolean =
		if (Config.getCurrentChainName().equals(ChainText.goldStoneMain, true)) {
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

	fun isNotchScreen(): Boolean =
		GoldStoneAPI.context.getBooleanFromSharedPreferences(SharesPreference.isNotchScreen)

	fun updateNotchScreenStatus(isNotchScreen: Boolean) =
		GoldStoneAPI.context.saveDataToSharedPreferences(SharesPreference.isNotchScreen, isNotchScreen)

	fun getCurrentEthereumAddress(): String =
		GoldStoneAPI.context.getStringFromSharedPreferences(SharesPreference.currentEthereumAddress)

	fun updateCurrentEthereumAddress(address: String) =
		GoldStoneAPI.context.saveDataToSharedPreferences(
			SharesPreference.currentEthereumAddress,
			address
		)

	fun getCurrentETCAddress(): String =
		GoldStoneAPI.context.getStringFromSharedPreferences(SharesPreference.currentETCAddress)

	fun updateCurrentETCAddress(address: String) =
		GoldStoneAPI.context.saveDataToSharedPreferences(SharesPreference.currentETCAddress, address)

	fun getCurrentBTCAddress(): String =
		GoldStoneAPI.context.getStringFromSharedPreferences(SharesPreference.currentBTCAddress)

	fun updateCurrentBTCAddress(address: String) =
		GoldStoneAPI.context.saveDataToSharedPreferences(SharesPreference.currentBTCAddress, address)

	fun getCurrentBTCTestAddress(): String =
		GoldStoneAPI.context.getStringFromSharedPreferences(SharesPreference.currentBTCTestAddress)

	fun updateCurrentBTCTestAddress(address: String) =
		GoldStoneAPI.context.saveDataToSharedPreferences(
			SharesPreference.currentBTCTestAddress,
			address
		)

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

	/** Chain Config */
	fun getCurrentChain(): String =
		if (GoldStoneAPI.context
				.getStringFromSharedPreferences(SharesPreference.currentChain)
				.equals("Default", true)
		) {
			ChainID.Main.id
		} else {
			GoldStoneAPI.context.getStringFromSharedPreferences(SharesPreference.currentChain)
		}

	fun getCurrentChainName(): String =
		if (GoldStoneAPI.context
				.getStringFromSharedPreferences(SharesPreference.currentChainName)
				.equals("Default", true)
		) {
			ChainText.infuraMain
		} else {
			GoldStoneAPI.context.getStringFromSharedPreferences(SharesPreference.currentChainName)
		}

	fun updateCurrentChain(chainID: String) =
		GoldStoneAPI.context.saveDataToSharedPreferences(SharesPreference.currentChain, chainID)

	fun updateCurrentChainName(chainName: String) =
		GoldStoneAPI.context.saveDataToSharedPreferences(SharesPreference.currentChainName, chainName)

	fun getETCCurrentChain(): String =
		if (GoldStoneAPI.context
				.getStringFromSharedPreferences(SharesPreference.etcCurrentChain)
				.equals("Default", true)
		) {
			ChainID.ETCMain.id
		} else {
			GoldStoneAPI.context.getStringFromSharedPreferences(SharesPreference.etcCurrentChain)
		}

	fun getETCCurrentChainName(): String =
		if (GoldStoneAPI.context
				.getStringFromSharedPreferences(SharesPreference.etcCurrentChainName)
				.equals("Default", true)
		) {
			ChainText.etcMainGasTracker
		} else {
			GoldStoneAPI.context.getStringFromSharedPreferences(SharesPreference.etcCurrentChainName)
		}

	fun updateETCCurrentChain(chainID: String) =
		GoldStoneAPI.context.saveDataToSharedPreferences(SharesPreference.etcCurrentChain, chainID)

	fun updateETCCurrentChainName(chainName: String) =
		GoldStoneAPI.context.saveDataToSharedPreferences(
			SharesPreference.etcCurrentChainName,
			chainName
		)

	fun getBTCCurrentChain(): String =
		if (GoldStoneAPI.context
				.getStringFromSharedPreferences(SharesPreference.btcCurrentChain)
				.equals("Default", true)
		) {
			ChainID.BTCMain.id
		} else {
			GoldStoneAPI.context.getStringFromSharedPreferences(SharesPreference.btcCurrentChain)
		}

	fun updateBTCCurrentChain(chainID: String) =
		GoldStoneAPI.context.saveDataToSharedPreferences(SharesPreference.btcCurrentChain, chainID)

	fun getBTCCurrentChainName(): String =
		if (GoldStoneAPI.context
				.getStringFromSharedPreferences(SharesPreference.btcCurrentChainName)
				.equals("Default", true)
		) {
			ChainText.btcMain
		} else {
			GoldStoneAPI.context.getStringFromSharedPreferences(SharesPreference.btcCurrentChainName)
		}

	fun updateBTCCurrentChainName(chainName: String) =
		GoldStoneAPI.context.saveDataToSharedPreferences(
			SharesPreference.btcCurrentChainName,
			chainName
		)

	/** Wallet Info Config */
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

	fun getMaxWalletID(): Int =
		GoldStoneAPI.context.getIntFromSharedPreferences(SharesPreference.maxWalletID)

	fun updateMaxWalletID(id: Int) =
		GoldStoneAPI.context.saveDataToSharedPreferences(SharesPreference.maxWalletID, id)

	fun getCurrentWalletType(): String =
		GoldStoneAPI.context.getStringFromSharedPreferences(SharesPreference.walletType)

	fun updateCurrentWalletType(type: String) =
		GoldStoneAPI.context.saveDataToSharedPreferences(SharesPreference.walletType, type)

	fun getGoldStoneID(): String =
		GoldStoneAPI.context.getStringFromSharedPreferences(SharesPreference.goldStoneID)

	fun updateGoldStoneID(goldStoneID: String) =
		GoldStoneAPI.context.saveDataToSharedPreferences(SharesPreference.goldStoneID, goldStoneID)

	fun getneedUnregisterGoldStoneID(): String =
		GoldStoneAPI.context.getStringFromSharedPreferences(SharesPreference.unregisterGoldStoneID)

	fun updateUnregisterGoldStoneID(goldStoneID: String) =
		GoldStoneAPI.context.saveDataToSharedPreferences(SharesPreference.unregisterGoldStoneID, goldStoneID)
}