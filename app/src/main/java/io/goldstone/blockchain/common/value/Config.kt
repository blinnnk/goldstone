package io.goldstone.blockchain.common.value

import com.blinnnk.util.*
import io.goldstone.blockchain.common.language.ChainText
import io.goldstone.blockchain.crypto.eos.account.EOSAccount
import io.goldstone.blockchain.crypto.multichain.ChainID
import io.goldstone.blockchain.crypto.multichain.WalletType
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

	/** Coin Address In SharedPreference */
	// EOS Account Name
	fun getCurrentEOSName(): EOSAccount =
		EOSAccount(GoldStoneAPI.context.getStringFromSharedPreferences(SharesPreference.currentEOSName))

	fun updateCurrentEOSName(name: String) =
		GoldStoneAPI.context.saveDataToSharedPreferences(SharesPreference.currentEOSName, name)

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

	fun getCurrentEOSAddress(): String =
		GoldStoneAPI.context.getStringFromSharedPreferences(SharesPreference.currentEOSAddress)

	fun updateCurrentEOSAddress(address: String) =
		GoldStoneAPI.context.saveDataToSharedPreferences(SharesPreference.currentEOSAddress, address)

	fun getCurrentBTCAddress(): String =
		GoldStoneAPI.context.getStringFromSharedPreferences(SharesPreference.currentBTCAddress)

	fun updateCurrentBTCAddress(address: String) =
		GoldStoneAPI.context.saveDataToSharedPreferences(SharesPreference.currentBTCAddress, address)

	fun getCurrentBTCSeriesTestAddress(): String =
		GoldStoneAPI.context.getStringFromSharedPreferences(SharesPreference.currentBTCTestAddress)

	fun updateCurrentBTCSeriesTestAddress(address: String) =
		GoldStoneAPI.context.saveDataToSharedPreferences(
			SharesPreference.currentBTCTestAddress,
			address
		)

	fun getCurrentLTCAddress(): String =
		GoldStoneAPI.context.getStringFromSharedPreferences(SharesPreference.currentLTCAddress)

	fun updateCurrentLTCAddress(address: String) =
		GoldStoneAPI.context.saveDataToSharedPreferences(SharesPreference.currentLTCAddress, address)

	fun getCurrentBCHAddress(): String =
		GoldStoneAPI.context.getStringFromSharedPreferences(SharesPreference.currentBCHAddress)

	fun updateCurrentBCHAddress(address: String) =
		GoldStoneAPI.context.saveDataToSharedPreferences(SharesPreference.currentBCHAddress, address)

	/** Chain Name in Shared Preference */
	fun getCurrentName(): String =
		GoldStoneAPI.context.getStringFromSharedPreferences(SharesPreference.currentName)

	fun updateCurrentName(name: String) =
		GoldStoneAPI.context.saveDataToSharedPreferences(SharesPreference.currentName, name)

	fun getCurrentWalletID(): Int =
		GoldStoneAPI.context.getIntFromSharedPreferences(SharesPreference.currentID)

	fun updateCurrentWalletID(id: Int) =
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
	fun getCurrentChain(): ChainID =
		if (GoldStoneAPI.context
				.getStringFromSharedPreferences(SharesPreference.currentChain)
				.equals("Default", true)
		) {
			ChainID(ChainID.ethMain)
		} else {
			ChainID(GoldStoneAPI.context.getStringFromSharedPreferences(SharesPreference.currentChain))
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

	/** LTC ChainID And Chain Name in Shared Preference*/
	fun getLTCCurrentChain(): ChainID =
		if (GoldStoneAPI.context
				.getStringFromSharedPreferences(SharesPreference.ltcCurrentChain)
				.equals("Default", true)
		) {
			ChainID.getLTCMain()
		} else {
			ChainID(GoldStoneAPI.context.getStringFromSharedPreferences(SharesPreference.ltcCurrentChain))
		}

	fun getLTCCurrentChainName(): String =
		if (GoldStoneAPI.context
				.getStringFromSharedPreferences(SharesPreference.ltcCurrentChainName)
				.equals("Default", true)
		) {
			ChainText.ltcMain
		} else {
			GoldStoneAPI.context.getStringFromSharedPreferences(SharesPreference.ltcCurrentChainName)
		}

	fun updateLTCCurrentChain(chainID: String) =
		GoldStoneAPI.context.saveDataToSharedPreferences(SharesPreference.ltcCurrentChain, chainID)

	fun updateLTCCurrentChainName(chainName: String) =
		GoldStoneAPI.context.saveDataToSharedPreferences(
			SharesPreference.ltcCurrentChainName,
			chainName
		)

	/** BCH ChainID And Chain Name in Shared Preference */
	fun getBCHCurrentChain(): ChainID =
		if (GoldStoneAPI.context
				.getStringFromSharedPreferences(SharesPreference.bchCurrentChain)
				.equals("Default", true)
		) {
			ChainID.getBCHMain()
		} else {
			ChainID(GoldStoneAPI.context.getStringFromSharedPreferences(SharesPreference.bchCurrentChain))
		}

	fun getBCHCurrentChainName(): String =
		if (GoldStoneAPI.context
				.getStringFromSharedPreferences(SharesPreference.bchCurrentChainName)
				.equals("Default", true)
		) {
			ChainText.bchMain
		} else {
			GoldStoneAPI.context.getStringFromSharedPreferences(SharesPreference.bchCurrentChainName)
		}

	fun updateBCHCurrentChain(chainID: String) =
		GoldStoneAPI.context.saveDataToSharedPreferences(SharesPreference.bchCurrentChain, chainID)

	fun updateBCHCurrentChainName(chainName: String) =
		GoldStoneAPI.context.saveDataToSharedPreferences(
			SharesPreference.bchCurrentChainName,
			chainName
		)

	/** EOS ChainID And ChainName In Shared Preference*/
	fun getEOSCurrentChain(): ChainID =
		if (GoldStoneAPI.context
				.getStringFromSharedPreferences(SharesPreference.eosCurrentChain)
				.equals("Default", true)
		) {
			ChainID.getEOSMain()
		} else {
			ChainID(GoldStoneAPI.context.getStringFromSharedPreferences(SharesPreference.eosCurrentChain))
		}

	fun getEOSCurrentChainName(): String =
		if (GoldStoneAPI.context
				.getStringFromSharedPreferences(SharesPreference.eosCurrentChainName)
				.equals("Default", true)
		) {
			ChainText.eosMain
		} else {
			GoldStoneAPI.context.getStringFromSharedPreferences(SharesPreference.eosCurrentChainName)
		}

	fun updateEOSCurrentChain(chainID: String) =
		GoldStoneAPI.context.saveDataToSharedPreferences(SharesPreference.eosCurrentChain, chainID)

	fun updateEOSCurrentChainName(chainName: String) =
		GoldStoneAPI.context.saveDataToSharedPreferences(
			SharesPreference.eosCurrentChainName,
			chainName
		)

	/** ETC ChainID And Chain Name in Shared Preference*/
	fun getETCCurrentChain(): ChainID =
		if (GoldStoneAPI.context
				.getStringFromSharedPreferences(SharesPreference.etcCurrentChain)
				.equals("Default", true)
		) {
			ChainID.getETCMain()
		} else {
			ChainID(GoldStoneAPI.context.getStringFromSharedPreferences(SharesPreference.etcCurrentChain))
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

	fun getBTCCurrentChain(): ChainID =
		if (GoldStoneAPI.context
				.getStringFromSharedPreferences(SharesPreference.btcCurrentChain)
				.equals("Default", true)
		) {
			ChainID.getBTCMain()
		} else {
			ChainID(GoldStoneAPI.context.getStringFromSharedPreferences(SharesPreference.btcCurrentChain))
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
}