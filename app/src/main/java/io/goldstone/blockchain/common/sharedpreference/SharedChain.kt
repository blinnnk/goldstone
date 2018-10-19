package io.goldstone.blockchain.common.sharedpreference

import com.blinnnk.util.getStringFromSharedPreferences
import com.blinnnk.util.saveDataToSharedPreferences
import io.goldstone.blockchain.common.language.ChainText
import io.goldstone.blockchain.common.value.SharesPreference
import io.goldstone.blockchain.crypto.multichain.ChainID
import io.goldstone.blockchain.kernel.network.GoldStoneAPI


/**
 * @author KaySaith
 * @date  2018/09/27
 */
object SharedChain {
	/** Chain Config */
	fun getCurrentETH(): ChainID =
		if(GoldStoneAPI.context
				.getStringFromSharedPreferences(SharesPreference.currentChain)
				.equals("Default",true)
		) {
			ChainID(ChainID.ethMain)
		} else {
			ChainID(GoldStoneAPI.context.getStringFromSharedPreferences(SharesPreference.currentChain))
		}

	fun getCurrentETHName(): String =
		if(GoldStoneAPI.context.getStringFromSharedPreferences(SharesPreference.currentChainName).equals("Default",true)) {
			ChainText.infuraMain
		} else {
			GoldStoneAPI.context.getStringFromSharedPreferences(SharesPreference.currentChainName)
		}

	fun updateCurrentETH(chainID: String) =
		GoldStoneAPI.context.saveDataToSharedPreferences(SharesPreference.currentChain,chainID)

	fun updateCurrentETHName(chainName: String) =
		GoldStoneAPI.context.saveDataToSharedPreferences(SharesPreference.currentChainName,chainName)

	/** LTC ChainID And Chain Name in Shared Preference*/
	fun getLTCCurrent(): ChainID =
		if(GoldStoneAPI.context
				.getStringFromSharedPreferences(SharesPreference.ltcCurrentChain)
				.equals("Default",true)
		) {
			ChainID.LTC
		} else {
			ChainID(GoldStoneAPI.context.getStringFromSharedPreferences(SharesPreference.ltcCurrentChain))
		}

	fun getLTCCurrentName(): String =
		if(GoldStoneAPI.context
				.getStringFromSharedPreferences(SharesPreference.ltcCurrentChainName)
				.equals("Default",true)
		) {
			ChainText.ltcMain
		} else {
			GoldStoneAPI.context.getStringFromSharedPreferences(SharesPreference.ltcCurrentChainName)
		}

	fun updateLTCCurrent(chainID: String) =
		GoldStoneAPI.context.saveDataToSharedPreferences(SharesPreference.ltcCurrentChain,chainID)

	fun updateLTCCurrentName(chainName: String) =
		GoldStoneAPI.context.saveDataToSharedPreferences(
			SharesPreference.ltcCurrentChainName,
			chainName
		)

	/** BCH ChainID And Chain Name in Shared Preference */
	fun getBCHCurrent(): ChainID =
		if(GoldStoneAPI.context
				.getStringFromSharedPreferences(SharesPreference.bchCurrentChain)
				.equals("Default",true)
		) {
			ChainID.BCH
		} else {
			ChainID(GoldStoneAPI.context.getStringFromSharedPreferences(SharesPreference.bchCurrentChain))
		}

	fun getBCHCurrentName(): String =
		if(GoldStoneAPI.context
				.getStringFromSharedPreferences(SharesPreference.bchCurrentChainName)
				.equals("Default",true)
		) {
			ChainText.bchMain
		} else {
			GoldStoneAPI.context.getStringFromSharedPreferences(SharesPreference.bchCurrentChainName)
		}

	fun updateBCHCurrent(chainID: String) =
		GoldStoneAPI.context.saveDataToSharedPreferences(SharesPreference.bchCurrentChain,chainID)

	fun updateBCHCurrentName(chainName: String) =
		GoldStoneAPI.context.saveDataToSharedPreferences(
			SharesPreference.bchCurrentChainName,
			chainName
		)

	/** EOS ChainID And ChainName In Shared Preference*/
	fun getEOSCurrent(): ChainID =
		if(GoldStoneAPI.context
				.getStringFromSharedPreferences(SharesPreference.eosCurrentChain)
				.equals("Default",true)
		) {
			ChainID.EOS
		} else {
			ChainID(GoldStoneAPI.context.getStringFromSharedPreferences(SharesPreference.eosCurrentChain))
		}

	fun getEOSCurrentName(): String =
		if(GoldStoneAPI.context
				.getStringFromSharedPreferences(SharesPreference.eosCurrentChainName)
				.equals("Default",true)
		) {
			ChainText.eosMain
		} else {
			GoldStoneAPI.context.getStringFromSharedPreferences(SharesPreference.eosCurrentChainName)
		}

	fun updateEOSCurrent(chainID: String) =
		GoldStoneAPI.context.saveDataToSharedPreferences(SharesPreference.eosCurrentChain,chainID)

	fun updateEOSCurrentName(chainName: String) =
		GoldStoneAPI.context.saveDataToSharedPreferences(
			SharesPreference.eosCurrentChainName,
			chainName
		)

	/** ETC ChainID And Chain Name in Shared Preference*/
	fun getETCCurrent(): ChainID =
		if(GoldStoneAPI.context
				.getStringFromSharedPreferences(SharesPreference.etcCurrentChain)
				.equals("Default",true)
		) {
			ChainID.ETC
		} else {
			ChainID(GoldStoneAPI.context.getStringFromSharedPreferences(SharesPreference.etcCurrentChain))
		}

	fun getETCCurrentName(): String =
		if(GoldStoneAPI.context
				.getStringFromSharedPreferences(SharesPreference.etcCurrentChainName)
				.equals("Default",true)
		) {
			ChainText.etcMainGasTracker
		} else {
			GoldStoneAPI.context.getStringFromSharedPreferences(SharesPreference.etcCurrentChainName)
		}

	fun updateETCCurrent(chainID: String) =
		GoldStoneAPI.context.saveDataToSharedPreferences(SharesPreference.etcCurrentChain,chainID)

	fun updateETCCurrentName(chainName: String) =
		GoldStoneAPI.context.saveDataToSharedPreferences(
			SharesPreference.etcCurrentChainName,
			chainName
		)

	fun getBTCCurrent(): ChainID =
		if(GoldStoneAPI.context
				.getStringFromSharedPreferences(SharesPreference.btcCurrentChain)
				.equals("Default",true)
		) {
			ChainID.BTC
		} else {
			ChainID(GoldStoneAPI.context.getStringFromSharedPreferences(SharesPreference.btcCurrentChain))
		}

	fun updateBTCCurrent(chainID: String) =
		GoldStoneAPI.context.saveDataToSharedPreferences(SharesPreference.btcCurrentChain,chainID)

	fun getBTCCurrentName(): String =
		if(GoldStoneAPI.context
				.getStringFromSharedPreferences(SharesPreference.btcCurrentChainName)
				.equals("Default",true)
		) {
			ChainText.btcMain
		} else {
			GoldStoneAPI.context.getStringFromSharedPreferences(SharesPreference.btcCurrentChainName)
		}

	fun updateBTCCurrentName(chainName: String) =
		GoldStoneAPI.context.saveDataToSharedPreferences(
			SharesPreference.btcCurrentChainName,
			chainName
		)
}