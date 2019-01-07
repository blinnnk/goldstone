package io.goldstone.blinnnk.crypto.multichain

import android.support.annotation.UiThread
import io.goldstone.blinnnk.common.sharedpreference.SharedAddress
import io.goldstone.blinnnk.common.sharedpreference.SharedChain
import io.goldstone.blinnnk.common.thread.launchUI
import io.goldstone.blinnnk.crypto.multichain.node.ChainURL
import io.goldstone.blinnnk.module.common.walletgeneration.createwallet.model.Bip44Address
import io.goldstone.blinnnk.module.common.walletgeneration.createwallet.model.WalletTable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.Serializable


/**
 * @author KaySaith
 * @date  2018/09/17
 */
class ChainType(val id: Int) : Serializable {

	fun isBTCSeries() = isBTC() || isLTC() || isBCH()

	fun getChainURL(): ChainURL {
		return when (id) {
			ChainType.ETH.id -> SharedChain.getCurrentETH()
			ChainType.ETC.id -> SharedChain.getETCCurrent()
			ChainType.BTC.id -> SharedChain.getBTCCurrent()
			ChainType.AllTest.id -> SharedChain.getBTCCurrent()
			ChainType.LTC.id -> SharedChain.getLTCCurrent()
			ChainType.BCH.id -> SharedChain.getBCHCurrent()
			ChainType.EOS.id -> SharedChain.getEOSCurrent()
			else -> SharedChain.getCurrentETH()
		}
	}

	fun getSymbol(): CoinSymbol {
		return when (id) {
			ChainType.ETH.id -> CoinSymbol.ETH
			ChainType.ETC.id -> CoinSymbol.ETC
			ChainType.BTC.id -> CoinSymbol.BTC
			ChainType.AllTest.id -> CoinSymbol.BTC
			ChainType.LTC.id -> CoinSymbol.LTC
			ChainType.BCH.id -> CoinSymbol.BCH
			ChainType.EOS.id -> CoinSymbol.EOS
			else -> CoinSymbol.ETH
		}
	}

	fun getContract(): TokenContract {
		return when (id) {
			ChainType.ETH.id -> TokenContract.ETH
			ChainType.BTC.id -> TokenContract.BTC
			ChainType.LTC.id -> TokenContract.LTC
			ChainType.BCH.id -> TokenContract.BCH
			ChainType.EOS.id -> TokenContract.EOS
			else -> TokenContract.ETC
		}
	}

	// 与 `WalletTable` 有关联的非纯粹但是便捷的方法
	fun updateCurrentAddress(
		newAddress: Bip44Address,
		newEOSAccountName: String,
		@UiThread callback: (wallet: WalletTable) -> Unit
	) {
		GlobalScope.launch(Dispatchers.Default) {
			val walletDao = WalletTable.dao
			val currentWallet = walletDao.findWhichIsUsing()
			when (id) {
				ChainType.ETH.id -> {
					SharedAddress.updateCurrentEthereum(newAddress.address)
					currentWallet?.currentETHSeriesAddress = newAddress.address
				}
				ChainType.ETC.id -> {
					currentWallet?.currentETCAddress = newAddress.address
					SharedAddress.updateCurrentETC(newAddress.address)
				}
				ChainType.LTC.id -> {
					currentWallet?.currentLTCAddress = newAddress.address
					SharedAddress.updateCurrentLTC(newAddress.address)
				}
				ChainType.BCH.id -> {
					currentWallet?.currentBCHAddress = newAddress.address
					SharedAddress.updateCurrentBCH(newAddress.address)
				}
				ChainType.EOS.id -> {
					currentWallet?.currentEOSAddress = newAddress.address
					currentWallet?.currentEOSAccountName?.updateCurrent(newEOSAccountName)
					SharedAddress.updateCurrentEOS(newAddress.address)
					SharedAddress.updateCurrentEOSName(newEOSAccountName)
				}
				ChainType.BTC.id -> {
					currentWallet?.currentBTCAddress = newAddress.address
					SharedAddress.updateCurrentBTC(newAddress.address)
				}
				ChainType.AllTest.id -> {
					currentWallet?.currentBTCSeriesTestAddress = newAddress.address
					SharedAddress.updateCurrentBTCSeriesTest(newAddress.address)
				}
			}
			currentWallet?.apply {
				walletDao.update(this)
				launchUI { callback(this) }
			}
		}
	}

	companion object {
		@JvmStatic
		val BTC = ChainType(0)
		@JvmStatic
		val AllTest = ChainType(1)
		@JvmStatic
		val LTC = ChainType(2)
		@JvmStatic
		val BCH = ChainType(145)
		@JvmStatic
		val EOS = ChainType(194)
		@JvmStatic
		val ETH = ChainType(60)
		@JvmStatic
		val ETC = ChainType(61)

		// 比特的 `Bip44` 的比特币测试地址的  `CoinType` 为 `1`
		val isBTCTest: (chainType: Int) -> Boolean = {
			it == ChainType.AllTest.id
		}

		fun isSamePrivateKeyRule(type: ChainType): Boolean =
			listOf(ChainType.BCH, ChainType.BTC, ChainType.AllTest).any { it.id == type.id }

	}
}

fun ChainType?.isBTC() = this?.id == ChainType.BTC.id
fun ChainType?.isLTC() = this?.id == ChainType.LTC.id
fun ChainType?.isEOS() = this?.id == ChainType.EOS.id
fun ChainType?.isETH() = this?.id == ChainType.ETH.id
fun ChainType?.isETC() = this?.id == ChainType.ETC.id
fun ChainType?.isBCH() = this?.id == ChainType.BCH.id
fun ChainType?.isAllTest() = this?.id == ChainType.AllTest.id

fun ChainType?.isStoredInKeyStoreByAddress() =
	this?.id == ChainType.LTC.id || this?.id == ChainType.BCH.id || this?.id == ChainType.BTC.id || this?.id == ChainType.AllTest.id || this?.id == ChainType.EOS.id