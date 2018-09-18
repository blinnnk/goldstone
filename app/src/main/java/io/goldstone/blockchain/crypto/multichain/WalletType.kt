package io.goldstone.blockchain.crypto.multichain

import io.goldstone.blockchain.common.value.Config
import java.io.Serializable


/**
 * @author KaySaith
 * @date  2018/09/17
 */
class WalletType(val type: String?) : Serializable {

	fun isETHSeries(): Boolean = type.equals(ethSeries, true)
	fun isBTC(): Boolean = type.equals(btcOnly, true)
	fun isBTCTest(): Boolean = type.equals(btcTestOnly, true)
	fun isLTC(): Boolean = type.equals(ltcOnly, true)
	fun isBCH(): Boolean = type.equals(bchOnly, true)
	fun isEOS(): Boolean = type.equals(eosOnly, true)
	fun isBIP44(): Boolean = type.equals(bip44MultiChain, true)
	fun isMultiChain(): Boolean = type.equals(multiChain, true)
	fun isBTCSeries(): Boolean = isBTCSeriesType(type!!)

	fun updateSharedPreference() {
		Config.updateCurrentWalletType(type!!)
	}

	companion object {

		fun getBTC(): WalletType = WalletType(btcOnly)
		fun getLTC(): WalletType = WalletType(ltcOnly)
		fun getETHSeries(): WalletType = WalletType(ethSeries)
		fun getBCH(): WalletType = WalletType(bchOnly)
		fun getEOS(): WalletType = WalletType(eosOnly)
		fun getBIP44(): WalletType = WalletType(bip44MultiChain)
		fun getMultiChain(): WalletType = WalletType(multiChain)

		const val btcOnly = "btcOnly"
		const val ethSeries = "ethSeries"
		const val btcTestOnly = "btctestOnly"
		const val ltcOnly = "ltcOnly"
		const val bchOnly = "bchOnly"
		const val eosOnly = "eosOnly"
		const val bip44MultiChain = "bip44MultiChain"
		const val multiChain = "multiChain"
		fun isBTCSeriesType(type: String): Boolean {
			return type == btcOnly ||
				type == btcTestOnly ||
				type == ltcOnly ||
				type == bchOnly
		}
	}
}