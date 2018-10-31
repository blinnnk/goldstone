package io.goldstone.blockchain.common.sharedpreference

import com.blinnnk.util.getStringFromSharedPreferences
import com.blinnnk.util.saveDataToSharedPreferences
import io.goldstone.blockchain.common.value.SharesPreference
import io.goldstone.blockchain.crypto.multichain.node.ChainURL
import io.goldstone.blockchain.kernel.network.common.GoldStoneAPI
import org.json.JSONObject


/**
 * @author KaySaith
 * @date  2018/09/27
 */
object SharedChain {
	/** Chain Config */
	fun getCurrentETH(): ChainURL {
		val chainObject =
			JSONObject(GoldStoneAPI.context.getStringFromSharedPreferences(SharesPreference.ethCurrentChain))
		return ChainURL(chainObject)
	}

	fun updateCurrentETH(chainInfo: ChainURL) =
		GoldStoneAPI.context.saveDataToSharedPreferences(SharesPreference.ethCurrentChain, chainInfo.generateObject())

	/** LTC ChainID And Chain Name in Shared Preference*/
	fun getLTCCurrent(): ChainURL {
		val chainObject =
			JSONObject(GoldStoneAPI.context.getStringFromSharedPreferences(SharesPreference.ltcCurrentChain))
		return ChainURL(chainObject)
	}

	fun updateLTCCurrent(chainInfo: ChainURL) =
		GoldStoneAPI.context.saveDataToSharedPreferences(SharesPreference.ltcCurrentChain, chainInfo.generateObject())

	/** BCH ChainID And Chain Name in Shared Preference */
	fun getBCHCurrent(): ChainURL {
		val chainObject =
			JSONObject(GoldStoneAPI.context.getStringFromSharedPreferences(SharesPreference.bchCurrentChain))
		return ChainURL(chainObject)
	}

	fun updateBCHCurrent(chainInfo: ChainURL) =
		GoldStoneAPI.context.saveDataToSharedPreferences(SharesPreference.bchCurrentChain, chainInfo.generateObject())

	/** EOS ChainID And ChainName In Shared Preference*/
	fun getEOSCurrent(): ChainURL {
		val chainObject =
			JSONObject(GoldStoneAPI.context.getStringFromSharedPreferences(SharesPreference.eosCurrentChain))
		return ChainURL(chainObject)
	}

	fun updateEOSCurrent(chainInfo: ChainURL) =
		GoldStoneAPI.context.saveDataToSharedPreferences(SharesPreference.eosCurrentChain, chainInfo.generateObject())

	fun getEOSMainnet(): ChainURL {
		val chainObject =
			JSONObject(GoldStoneAPI.context.getStringFromSharedPreferences(SharesPreference.eosMainnet))
		return ChainURL(chainObject)
	}

	fun updateEOSMainnet(chainInfo: ChainURL) =
		GoldStoneAPI.context.saveDataToSharedPreferences(SharesPreference.eosMainnet, chainInfo.generateObject())

	fun getEOSTestnet(): ChainURL {
		val chainObject =
			JSONObject(GoldStoneAPI.context.getStringFromSharedPreferences(SharesPreference.eosTestnet))
		return ChainURL(chainObject)
	}

	fun updateEOSTestnet(chainInfo: ChainURL) =
		GoldStoneAPI.context.saveDataToSharedPreferences(SharesPreference.eosTestnet, chainInfo.generateObject())


	/** ETC ChainID And Chain Name in Shared Preference*/
	fun getETCCurrent(): ChainURL {
		val chainObject =
			JSONObject(GoldStoneAPI.context.getStringFromSharedPreferences(SharesPreference.etcCurrentChain))
		return ChainURL(chainObject)
	}

	fun updateETCCurrent(chainInfo: ChainURL) =
		GoldStoneAPI.context.saveDataToSharedPreferences(SharesPreference.etcCurrentChain, chainInfo.generateObject())

	fun getBTCCurrent(): ChainURL {
		val chainObject =
			JSONObject(GoldStoneAPI.context.getStringFromSharedPreferences(SharesPreference.btcCurrentChain))
		return ChainURL(chainObject)
	}

	fun updateBTCCurrent(chainInfo: ChainURL) =
		GoldStoneAPI.context.saveDataToSharedPreferences(SharesPreference.btcCurrentChain, chainInfo.generateObject())

}