package io.goldstone.blockchain.crypto.multichain

import android.support.annotation.UiThread
import io.goldstone.blockchain.common.language.ChainText
import io.goldstone.blockchain.common.sharedpreference.SharedAddress
import io.goldstone.blockchain.common.sharedpreference.SharedChain
import io.goldstone.blockchain.common.sharedpreference.SharedValue
import io.goldstone.blockchain.kernel.database.GoldStoneDataBase
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.io.Serializable


/**
 * @author KaySaith
 * @date  2018/09/17
 */
class ChainType(val id: Int) : Serializable {

	fun getCurrentChainName(): String {
		return when (id) {
			ChainType.ETH.id -> SharedChain.getCurrentETHName()
			ChainType.ETC.id -> SharedChain.getETCCurrentName()
			ChainType.BTC.id -> SharedChain.getBTCCurrentName()
			ChainType.LTC.id -> SharedChain.getLTCCurrentName()
			ChainType.BCH.id -> SharedChain.getBCHCurrentName()
			ChainType.EOS.id -> SharedChain.getEOSCurrentName()
			else -> SharedChain.getCurrentETHName()
		}
	}

	fun getSymbol(): CoinSymbol {
		return when (id) {
			ChainType.ETH.id -> CoinSymbol.ETH
			ChainType.ETC.id -> CoinSymbol.ETC
			ChainType.BTC.id -> CoinSymbol.BTC
			ChainType.LTC.id -> CoinSymbol.LTC
			ChainType.BCH.id -> CoinSymbol.BCH
			ChainType.EOS.id -> CoinSymbol.EOS
			else -> CoinSymbol.ETH
		}
	}

	fun getMainnetChainName(): String {
		return when (id) {
			ChainType.ETH.id -> {
				// 这个是节点选择显示链名字的地方用到的, 如果当前不是主网链, 那么默认推荐使用 `InfuraMain`
				if (!SharedChain.getCurrentETH().isETHMain()) ChainText.infuraMain
				else SharedChain.getCurrentETHName()
			}

			ChainType.BTC.id -> ChainText.btcMain
			ChainType.LTC.id -> ChainText.ltcMain
			ChainType.BCH.id -> ChainText.bchMain
			ChainType.EOS.id -> ChainText.eosMain

			else -> {
				if (!SharedChain.getETCCurrent().isETCMain()) ChainText.etcMainGasTracker
				else SharedChain.getETCCurrentName()
			}
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

	fun getTestnetChainName(): String {
		return when (id) {
			ChainType.ETH.id -> {
				// 这个是节点选择显示链名字的地方用到的, 如果当前是主网链, 那么默认推荐使用 `InfuraRopsten`
				if (SharedChain.getCurrentETH().isETHMain()) ChainText.infuraRopsten
				else SharedChain.getCurrentETHName()
			}
			ChainType.BTC.id -> ChainText.btcTest
			ChainType.LTC.id -> ChainText.ltcTest
			ChainType.BCH.id -> ChainText.bchTest
			ChainType.EOS.id -> ChainText.eosTest
			else -> {
				if (SharedChain.getETCCurrent().isETCMain()) ChainText.etcMorden
				else SharedChain.getETCCurrentName()
			}
		}
	}

	// 与 `WalletTable` 有关联的非纯粹但是便捷的方法
	fun updateCurrentAddress(
		newAddress: String,
		newEOSAccountName: String,
		@UiThread callback: (isSwitchEOSAddress: Boolean, wallet: WalletTable) -> Unit
	) {
		doAsync {
			val walletDao = GoldStoneDataBase.database.walletDao()
			val currentWallet = walletDao.findWhichIsUsing(true)
			when (id) {
				ChainType.ETH.id -> {
					SharedAddress.updateCurrentEthereum(newAddress)
					currentWallet?.currentETHSeriesAddress = newAddress
				}
				ChainType.ETC.id -> {
					currentWallet?.currentETCAddress = newAddress
					SharedAddress.updateCurrentETC(newAddress)
				}
				ChainType.LTC.id -> {
					currentWallet?.currentLTCAddress = newAddress
					SharedAddress.updateCurrentLTC(newAddress)
				}
				ChainType.BCH.id -> {
					currentWallet?.currentBCHAddress = newAddress
					SharedAddress.updateCurrentBCH(newAddress)
				}
				ChainType.EOS.id -> {
					currentWallet?.currentEOSAddress = newAddress
					// 切换 `EOS` 的默认地址, 把 `accountName` 的数据值为初始化状态,
					// 好在其他流程中重新走检查 `Account Name` 的逻辑
					currentWallet?.currentEOSAccountName?.updateCurrent(newEOSAccountName)
					SharedAddress.updateCurrentEOS(newAddress)
				}
				ChainType.BTC.id -> if (SharedValue.isTestEnvironment()) {
					currentWallet?.currentBTCSeriesTestAddress = newAddress
					SharedAddress.updateCurrentBTCSeriesTest(newAddress)
				} else {
					currentWallet?.currentBTCAddress = newAddress
					SharedAddress.updateCurrentBTC(newAddress)
				}
			}
			currentWallet?.apply {
				walletDao.update(this)
				uiThread {
					callback(ChainType(it.id).isEOS(), this)
				}
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
		@JvmStatic
		val ERC = ChainType(100)

		// 比特的 `Bip44` 的比特币测试地址的  `CoinType` 为 `1`
		val isBTCTest: (chainType: Int) -> Boolean = {
			it == ChainType.AllTest.id
		}

		private fun getAllBTCSeriesType(): List<ChainType> =
			listOf(ChainType.LTC, ChainType.AllTest, ChainType.BTC, ChainType.BCH)

		fun isBTCSeriesChainType(type: ChainType): Boolean =
			getAllBTCSeriesType().any { it == type }

		fun isSamePrivateKeyRule(id: ChainType): Boolean =
			listOf(ChainType.BCH, ChainType.BTC, ChainType.AllTest).any { it == id }

		fun getChainTypeBySymbol(symbol: String?): ChainType = when (symbol) {
			CoinSymbol.btc() -> ChainType.BTC
			CoinSymbol.ltc -> ChainType.LTC
			CoinSymbol.eth -> ChainType.ETH
			CoinSymbol.etc -> ChainType.ETC
			CoinSymbol.bch -> ChainType.BCH
			CoinSymbol.eos -> ChainType.EOS
			else -> ChainType.ETH
		}
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