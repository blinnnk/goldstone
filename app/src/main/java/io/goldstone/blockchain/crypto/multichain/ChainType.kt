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

	fun isBTC() = id == ChainType.BTC.id
	fun isLTC() = id == ChainType.LTC.id
	fun isEOS() = id == ChainType.EOS.id
	fun isETH() = id == ChainType.ETH.id
	fun isETC() = id == ChainType.ETC.id
	fun isBCH() = id == ChainType.BCH.id

	fun getCurrentChainName(): String {
		return when (id) {
			ChainType.ETH.id -> Config.getCurrentChainName()
			ChainType.ETC.id -> Config.getETCCurrentChainName()
			ChainType.BTC.id -> Config.getBTCCurrentChainName()
			ChainType.LTC.id -> Config.getLTCCurrentChainName()
			ChainType.BCH.id -> Config.getBCHCurrentChainName()
			ChainType.EOS.id -> Config.getEOSCurrentChainName()
			else -> Config.getCurrentChainName()
		}
	}

	fun getMainnetChainName(): String {
		return when (id) {
			ChainType.ETH.id -> {
				// 这个是节点选择显示链名字的地方用到的, 如果当前不是主网链, 那么默认推荐使用 `InfuraMain`
				if (!Config.getCurrentChain().isETHMain()) ChainText.infuraMain
				else Config.getCurrentChainName()
			}

			ChainType.BTC.id -> ChainText.btcMain
			ChainType.LTC.id -> ChainText.ltcMain
			ChainType.BCH.id -> ChainText.bchMain
			ChainType.EOS.id -> ChainText.eosMain

			else -> {
				if (!Config.getETCCurrentChain().isETCMain()) ChainText.etcMainGasTracker
				else Config.getETCCurrentChainName()
			}
		}
	}

	fun getTestnetChainName(): String {
		return when (id) {
			ChainType.ETH.id -> {
				// 这个是节点选择显示链名字的地方用到的, 如果当前是主网链, 那么默认推荐使用 `InfuraRopsten`
				if (Config.getCurrentChain().isETHMain()) ChainText.infuraRopsten
				else Config.getCurrentChainName()
			}
			ChainType.BTC.id -> ChainText.btcTest
			ChainType.LTC.id -> ChainText.ltcTest
			ChainType.BCH.id -> ChainText.bchTest
			ChainType.EOS.id -> ChainText.eosTest
			else -> {
				if (Config.getETCCurrentChain().isETCMain()) ChainText.etcMorden
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
				ChainType.ETH.id -> {
					Config.updateCurrentEthereumAddress(newAddress)
					currentWallet?.currentETHAndERCAddress = newAddress
				}
				ChainType.ETC.id -> {
					currentWallet?.currentETCAddress = newAddress
					Config.updateCurrentETCAddress(newAddress)
				}
				ChainType.LTC.id -> {
					currentWallet?.currentLTCAddress = newAddress
					Config.updateCurrentLTCAddress(newAddress)
				}
				ChainType.BCH.id -> {
					currentWallet?.currentBCHAddress = newAddress
					Config.updateCurrentBCHAddress(newAddress)
				}
				ChainType.EOS.id -> {
					currentWallet?.currentEOSAddress = newAddress
					// 切换 `EOS` 的默认地址, 把 `accountName` 的数据值为初始化状态,
					// 好在其他流程中重新走检查 `Account Name` 的逻辑
					currentWallet?.currentEOSAccountName = EOSDefaultAllChainName(newAddress, newAddress)
					Config.updateCurrentEOSAddress(newAddress)
				}
				ChainType.BTC.id -> {
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