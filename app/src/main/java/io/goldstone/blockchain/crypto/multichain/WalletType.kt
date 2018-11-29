package io.goldstone.blockchain.crypto.multichain

import io.goldstone.blockchain.common.language.WalletText
import io.goldstone.blockchain.common.sharedpreference.SharedWallet
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
	fun isEOSMainnet(): Boolean = type.equals(eosMainnetOnly, true)
	fun isEOSJungle(): Boolean = type.equals(eosJungleOnly, true)
	fun isBIP44(): Boolean = type.equals(bip44MultiChain, true)
	fun isMultiChain(): Boolean = type.equals(multiChain, true)
	fun isBTCSeries(): Boolean = isBTCSeriesType(type!!)
	fun isEOSSeries(): Boolean = isEOSSeriesType(type!!)

	fun updateSharedPreference() {
		SharedWallet.updateCurrentWalletType(type!!)
	}

	fun getDisplayName(): String {
		return when {
			isBIP44() -> WalletText.bip44MultiChain
			isMultiChain() -> WalletText.multiChain
			isBTC() -> WalletText.btcMainnet
			isLTC() -> WalletText.ltcMainnet
			isBCH() -> WalletText.bchMainnet
			isETHSeries() -> WalletText.ethERCAndETC
			isEOS() -> WalletText.eosWallet
			isEOSMainnet() -> WalletText.eosMainnet
			isEOSJungle() -> WalletText.eosJungle
			isBTCTest() -> WalletText.btcTestnet
			else -> ""
		}
	}

	companion object {

		const val btcOnly = "btcOnly"
		const val ethSeries = "ethSeries"
		const val btcTestOnly = "btctestOnly"
		const val ltcOnly = "ltcOnly"
		const val bchOnly = "bchOnly"
		const val eosOnly = "eosOnly"
		const val eosMainnetOnly = "eosMainnetOnly"
		const val eosJungleOnly = "eosJungleOnly"
		const val bip44MultiChain = "bip44MultiChain"
		const val multiChain = "multiChain"
		fun isBTCSeriesType(type: String): Boolean {
			return type == btcOnly ||
				type == btcTestOnly ||
				type == ltcOnly ||
				type == bchOnly
		}

		fun isEOSSeriesType(type: String): Boolean {
			return type == eosMainnetOnly ||
				type == eosJungleOnly ||
				type == eosOnly
		}

		@JvmStatic
		val BTC = WalletType(btcOnly)
		@JvmStatic
		val LTC = WalletType(ltcOnly)
		@JvmStatic
		val ETHSeries = WalletType(ethSeries)
		@JvmStatic
		val BCH = WalletType(bchOnly)
		// 这三种类型是服务导入的观察钱包的,
		// 1. 如果用户导入的是 `PublicKey EOS Account` 那么意味着可以同时存在 `Mainnet` 和 `TestNet` 的账户
		// 2. 如果用户导入的是 AccountName 那么就会区分是 `Mainnet` 的测试账户还是 `Jungle` 的账户了
		@JvmStatic
		val EOS = WalletType(eosOnly)
		@JvmStatic
		val EOSMainnet = WalletType(eosJungleOnly)
		@JvmStatic
		val EOSJungle = WalletType(eosJungleOnly)
		@JvmStatic
		val BIP44 = WalletType(bip44MultiChain)
		@JvmStatic
		val MultiChain = WalletType(multiChain)
	}
}