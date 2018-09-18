package io.goldstone.blockchain.crypto.multichain

import android.support.annotation.UiThread
import io.goldstone.blockchain.common.language.ChainText
import io.goldstone.blockchain.common.value.Config
import io.goldstone.blockchain.kernel.database.GoldStoneDataBase
import io.goldstone.blockchain.kernel.network.GoldStoneAPI
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.EOSDefaultAllChainName
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.runOnUiThread
import java.io.Serializable


/**
 * @author KaySaith
 * @date  2018/09/17
 */
class ChainType(val id: Int) : Serializable {

	fun isBTC() = id == MultiChainType.BTC.id
	fun isLTC() = id == MultiChainType.LTC.id
	fun isEOS() = id == MultiChainType.EOS.id
	fun isETH() = id == MultiChainType.ETH.id
	fun isETC() = id == MultiChainType.ETC.id
	fun isBCH() = id == MultiChainType.BCH.id

	fun getCurrentChainName(): String {
		return when (id) {
			MultiChainType.ETH.id -> Config.getCurrentChainName()
			MultiChainType.ETC.id -> Config.getETCCurrentChainName()
			MultiChainType.BTC.id -> Config.getBTCCurrentChainName()
			MultiChainType.LTC.id -> Config.getLTCCurrentChainName()
			MultiChainType.BCH.id -> Config.getBCHCurrentChainName()
			MultiChainType.EOS.id -> Config.getEOSCurrentChainName()
			else -> Config.getCurrentChainName()
		}
	}

	fun getMainnetChainName(): String {
		return when (id) {
			MultiChainType.ETH.id -> {
				if (Config.getCurrentChain() != ChainID.ethMain) ChainText.infuraMain
				else Config.getCurrentChainName()
			}

			MultiChainType.BTC.id -> ChainText.btcMain
			MultiChainType.LTC.id -> ChainText.ltcMain
			MultiChainType.BCH.id -> ChainText.bchMain
			MultiChainType.EOS.id -> ChainText.eosMain

			else -> {
				if (Config.getETCCurrentChain() != ChainID.ethMain) ChainText.etcMainGasTracker
				else Config.getETCCurrentChainName()
			}
		}
	}

	fun getTestnetChainName(): String {
		return when (id) {
			MultiChainType.ETH.id -> {
				if (Config.getCurrentChain() == ChainID.ethMain) ChainText.infuraRopsten
				else Config.getCurrentChainName()
			}
			MultiChainType.BTC.id -> ChainText.btcTest
			MultiChainType.LTC.id -> ChainText.ltcTest
			MultiChainType.BCH.id -> ChainText.bchTest
			MultiChainType.EOS.id -> ChainText.eosTest
			else -> {
				if (Config.getETCCurrentChain() == ChainID.etcMain) ChainText.etcMorden
				else Config.getETCCurrentChainName()
			}
		}
	}

	// 与 `WalletTable` 有关联的非纯粹但是便捷的方法
	fun updateCurrentAddress(
		newAddress: String,
		@UiThread callback: (isSwitchEOSAddress: Boolean) -> Unit
	) {
		doAsync {
			val walletDao = GoldStoneDataBase.database.walletDao()
			val currentWallet = walletDao.findWhichIsUsing(true)
			when (id) {
				MultiChainType.ETH.id -> {
					Config.updateCurrentEthereumAddress(newAddress)
					currentWallet?.currentETHAndERCAddress = newAddress
				}
				MultiChainType.ETC.id -> {
					currentWallet?.currentETCAddress = newAddress
					Config.updateCurrentETCAddress(newAddress)
				}
				MultiChainType.LTC.id -> {
					currentWallet?.currentLTCAddress = newAddress
					Config.updateCurrentLTCAddress(newAddress)
				}
				MultiChainType.BCH.id -> {
					currentWallet?.currentBCHAddress = newAddress
					Config.updateCurrentBCHAddress(newAddress)
				}
				MultiChainType.EOS.id -> {
					currentWallet?.currentEOSAddress = newAddress
					// 切换 `EOS` 的默认地址, 把 `accountName` 的数据值为初始化状态,
					// 好在其他流程中重新走检查 `Account Name` 的逻辑
					currentWallet?.currentEOSAccountName = EOSDefaultAllChainName(newAddress, newAddress)
					Config.updateCurrentEOSAddress(newAddress)
				}
				MultiChainType.BTC.id -> {
					if (Config.isTestEnvironment()) {
						currentWallet?.currentBTCSeriesTestAddress = newAddress
						Config.updateCurrentBTCSeriesTestAddress(newAddress)
					} else {
						currentWallet?.currentBTCAddress = newAddress
						Config.updateCurrentBTCAddress(newAddress)
					}
				}
			}
			currentWallet?.apply {
				GoldStoneDataBase.database.walletDao().update(this)
				GoldStoneAPI.context.runOnUiThread {
					callback(ChainType(this@ChainType.id).isEOS())
				}
			}
		}
	}

	companion object {

		// 比特的 `Bip44` 的比特币测试地址的  `CoinType` 为 `1`
		val isBTCTest: (chainType: Int) -> Boolean = {
			it == MultiChainType.AllTest.id
		}

		private fun getAllBTCSeriesType(): List<Int> =
			listOf(MultiChainType.LTC.id, MultiChainType.AllTest.id, MultiChainType.BTC.id, MultiChainType.BCH.id)

		fun isBTCSeriesChainType(id: Int): Boolean =
			getAllBTCSeriesType().any { it == id }

		fun isSamePrivateKeyRule(id: Int): Boolean =
			listOf(MultiChainType.BCH.id, MultiChainType.BTC.id, MultiChainType.AllTest.id).any { it == id }

		fun getChainTypeBySymbol(symbol: String?): Int = when (symbol) {
			CoinSymbol.btc() -> MultiChainType.BTC.id
			CoinSymbol.ltc -> MultiChainType.LTC.id
			CoinSymbol.eth -> MultiChainType.ETH.id
			CoinSymbol.etc -> MultiChainType.ETC.id
			CoinSymbol.bch -> MultiChainType.BCH.id
			CoinSymbol.eos -> MultiChainType.EOS.id
			else -> MultiChainType.ETH.id
		}
	}
}

enum class MultiChainType(val id: Int) {
	BTC(0),
	AllTest(1),
	LTC(2),
	BCH(145),
	EOS(194),
	ETH(60),
	ETC(61),
	ERC(100); // 需要调大不然可能会和自然 `Type` 冲突
}