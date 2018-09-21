package io.goldstone.blockchain.module.common.walletgeneration.createwallet.model

import android.arch.persistence.room.*
import android.content.Context
import android.support.annotation.UiThread
import com.blinnnk.extension.isFalse
import com.blinnnk.extension.isTrue
import com.blinnnk.extension.orFalse
import com.blinnnk.extension.otherwise
import io.goldstone.blockchain.R
import io.goldstone.blockchain.common.component.overlay.GoldStoneDialog
import io.goldstone.blockchain.common.language.AlertText
import io.goldstone.blockchain.common.language.DialogText
import io.goldstone.blockchain.common.language.WalletText
import io.goldstone.blockchain.common.utils.alert
import io.goldstone.blockchain.common.utils.load
import io.goldstone.blockchain.common.utils.then
import io.goldstone.blockchain.common.value.Config
import io.goldstone.blockchain.crypto.eos.EOSWalletType
import io.goldstone.blockchain.crypto.eos.EOSWalletUtils
import io.goldstone.blockchain.crypto.multichain.ChainType
import io.goldstone.blockchain.crypto.multichain.CoinSymbol
import io.goldstone.blockchain.crypto.multichain.WalletType
import io.goldstone.blockchain.kernel.commonmodel.MyTokenTable
import io.goldstone.blockchain.kernel.database.GoldStoneDataBase
import io.goldstone.blockchain.kernel.network.GoldStoneAPI
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.runOnUiThread
import java.io.Serializable

/**
 * @date 29/03/2018 10:35 PM
 * @author KaySaith
 */
@Entity(tableName = "wallet")
data class WalletTable(
	//@PrimaryKey autoGenerate 自增
	@PrimaryKey(autoGenerate = true)
	var id: Int,
	var name: String,
	var currentETHAndERCAddress: String,
	var currentETCAddress: String,
	var currentBTCAddress: String,
	var currentBTCSeriesTestAddress: String,
	var currentLTCAddress: String,
	var currentBCHAddress: String,
	var currentEOSAddress: String,
	var currentEOSAccountName: EOSDefaultAllChainName,
	var ethAddresses: String, // format - "address|index,0x288832ds23...|0"
	var btcAddresses: String,
	var btcSeriesTestAddresses: String,
	var etcAddresses: String,
	var ltcAddresses: String,
	var bchAddresses: String,
	var eosAddresses: String,
	var eosAccountNames: List<EOSAccountInfo>,
	var ethPath: String,
	var etcPath: String,
	var btcPath: String,
	var btcTestPath: String,
	var ltcPath: String,
	var bchPath: String,
	var eosPath: String,
	var isUsing: Boolean,
	var hint: String? = null,
	var isWatchOnly: Boolean = false,
	var balance: Double? = 0.0,
	var encryptMnemonic: String? = null,
	var hasBackUpMnemonic: Boolean = false
) : Serializable {

	fun getCurrentAddressAndSymbol(): List<Pair<String, String>> {
		return arrayListOf<Pair<String, String>>().apply {
			// 如果是测试环境展示 `BTCSeriesTest Address`. Bip44 规则, 目前多数 `比特币` 系列的测试网是公用的
			if (currentBTCAddress.isNotEmpty() && !Config.isTestEnvironment()) {
				add(Pair(currentBTCAddress, CoinSymbol.btc()))
			} else if (currentBTCSeriesTestAddress.isNotEmpty() && Config.isTestEnvironment()) {
				add(Pair(currentBTCSeriesTestAddress, CoinSymbol.btc()))
			}
			// Litecoin Mainnet and Testnet Addresses
			if (currentLTCAddress.isNotEmpty() && !Config.isTestEnvironment()) {
				add(Pair(currentLTCAddress, CoinSymbol.ltc))
			} else if (currentBTCSeriesTestAddress.isNotEmpty() && Config.isTestEnvironment()) {
				add(Pair(currentBTCSeriesTestAddress, CoinSymbol.ltc))
			}
			// Bitcoin Cash Mainnet and Testnet Addresses
			if (currentBCHAddress.isNotEmpty() && !Config.isTestEnvironment()) {
				add(Pair(currentBCHAddress, CoinSymbol.bch))
			} else if (currentBTCSeriesTestAddress.isNotEmpty() && Config.isTestEnvironment()) {
				add(Pair(currentBTCSeriesTestAddress, CoinSymbol.bch))
			}
			// Ethereum & Ethereum Classic Mainnet and Testnet Addresses
			if (currentETHAndERCAddress.isNotEmpty()) {
				add(Pair(currentETHAndERCAddress, CoinSymbol.erc))
				add(Pair(currentETHAndERCAddress, CoinSymbol.eth))
				add(Pair(currentETCAddress, CoinSymbol.etc))
			}
			// EOS.io Mainnet and Testnet Addresses
			if (currentEOSAddress.isNotEmpty()) {
				add(Pair(currentEOSAddress, CoinSymbol.eos))
			}
		}
	}

	fun getCurrentAddressesAndChainID(): List<Pair<String, ChainType>> {
		return listOf(
			Pair(currentBTCAddress, ChainType.BTC),
			Pair(currentLTCAddress, ChainType.LTC),
			Pair(currentBCHAddress, ChainType.BCH),
			Pair(currentBTCSeriesTestAddress, ChainType.AllTest),
			Pair(currentETCAddress, ChainType.ETC),
			Pair(currentETHAndERCAddress, ChainType.ETH),
			Pair(currentEOSAddress, ChainType.EOS)
		).filter { it.first.isNotEmpty() }
	}

	fun getCurrentAddresses(): List<String> {
		return listOf(
			currentBTCAddress,
			currentBTCSeriesTestAddress,
			currentETCAddress,
			currentETHAndERCAddress,
			currentLTCAddress,
			currentBCHAddress,
			currentEOSAddress
		).asSequence().filter { it.isNotEmpty() }.distinctBy { it }.toList()
	}

	fun getAddressDescription(): String {
		val walletType = getWalletType()
		return when {
			walletType.isLTC() -> currentLTCAddress
			walletType.isBCH() -> currentBCHAddress
			walletType.isETHSeries() -> currentETHAndERCAddress
			walletType.isBTCTest() -> btcSeriesTestAddresses
			walletType.isBTC() -> btcAddresses
			walletType.isEOS() -> eosAddresses
			walletType.isBIP44() -> WalletText.bip44MultiChain
			else -> WalletText.multiChain
		}
	}

	fun getWalletType(): WalletType {
		val types = listOf(
			Pair(WalletType.btcOnly, currentBTCAddress),
			Pair(WalletType.btcTestOnly, currentBTCSeriesTestAddress),
			Pair(WalletType.ethSeries, currentETHAndERCAddress),
			Pair(WalletType.ltcOnly, currentLTCAddress),
			Pair(WalletType.bchOnly, currentBCHAddress),
			Pair(WalletType.eosOnly, currentEOSAddress)
		).filter {
			it.second.isNotEmpty()
		}
		return when (types.size) {
			6 -> {
				// 通过私钥导入的多链钱包没有 Path 值所以通过这个来判断是否是
				// BIP44 钱包还是单纯的多链钱包
				if (ethPath.isNotEmpty()) WalletType.getBIP44()
				else WalletType.getMultiChain()
			}
			else -> WalletType(types.firstOrNull()?.first)
		}
	}

	companion object {
		fun getWalletAddressCount(hold: (Int) -> Unit) {
			WalletTable.getCurrentWallet {
				val currentType = Config.getCurrentWalletType()
				when {
					currentType.isBIP44() -> {
						val ethAddressCount = ethAddresses.split(",").size
						val etcAddressCount = etcAddresses.split(",").size
						val btcAddressCount = btcAddresses.split(",").size
						val btcTestAddressCount = btcSeriesTestAddresses.split(",").size
						val ltcAddressCount = ltcAddresses.split(",").size
						val bchAddressCount = bchAddresses.split(",").size
						val eosAddressCount = eosAddresses.split(",").size
						hold(
							ethAddressCount +
								etcAddressCount +
								btcAddressCount +
								btcTestAddressCount +
								ltcAddressCount +
								bchAddressCount +
								eosAddressCount
						)
					}
					currentType.isETHSeries() -> hold(1)
					currentType.isBTCTest() -> hold(1)
					currentType.isBTC() -> hold(1)
					currentType.isLTC() -> hold(1)
					currentType.isBCH() -> hold(1)
					currentType.isEOS() -> hold(1)
				}
			}
		}

		fun insert(
			model: WalletTable,
			callback: (wallet: WalletTable?) -> Unit
		) {
			load {
				GoldStoneDataBase.database.walletDao().apply {
					findWhichIsUsing(true)?.let {
						update(it.apply { isUsing = false })
					}
					insert(model)
				}.findWhichIsUsing(true)
			} then {
				Config.updateCurrentIsWatchOnlyOrNot(it?.isWatchOnly.orFalse())
				callback(it)
			}
		}

		fun saveEncryptMnemonicIfUserSkip(
			encryptMnemonic: String,
			address: String,
			@UiThread callback: () -> Unit
		) {
			doAsync {
				GoldStoneDataBase.database.walletDao().apply {
					getWalletByAddress(address)?.let {
						update(it.apply { this.encryptMnemonic = encryptMnemonic })
						GoldStoneAPI.context.runOnUiThread { callback() }
					}
				}
			}
		}

		fun getAll(hold: List<WalletTable>.() -> Unit) {
			load { GoldStoneDataBase.database.walletDao().getAllWallets() } then (hold)
		}

		fun getAllETHAndERCAddresses(hold: List<String>.() -> Unit) {
			load {
				GoldStoneDataBase.database.walletDao().getAllWallets()
			} then { it ->
				hold(it.map { it.currentETHAndERCAddress })
			}
		}

		fun getAllBTCMainnetAddresses(hold: List<String>.() -> Unit) {
			load {
				GoldStoneDataBase.database.walletDao().getAllWallets()
			} then { it ->
				hold(it.map { it.currentBTCAddress })
			}
		}

		fun getAllLTCAddresses(hold: List<String>.() -> Unit) {
			load {
				GoldStoneDataBase.database.walletDao().getAllWallets()
			} then { it ->
				hold(it.map { it.currentLTCAddress })
			}
		}

		fun getAllEOSAccountNames(hold: List<String>.() -> Unit) {
			load {
				GoldStoneDataBase.database.walletDao().getAllWallets()
			} then { it ->
				hold(it.map { it.currentEOSAccountName.getCurrent() })
			}
		}

		fun getAllBCHAddresses(hold: List<String>.() -> Unit) {
			load {
				GoldStoneDataBase.database.walletDao().getAllWallets()
			} then { it ->
				hold(it.map { it.currentBCHAddress })
			}
		}

		fun getAllBTCSeriesTestnetAddresses(hold: List<String>.() -> Unit) {
			load {
				GoldStoneDataBase.database.walletDao().getAllWallets()
			} then { it ->
				hold(it.map { it.currentBTCSeriesTestAddress })
			}
		}

		fun getCurrentWallet(@UiThread hold: WalletTable.() -> Unit) {
			load {
				GoldStoneDataBase
					.database
					.walletDao()
					.findWhichIsUsing(true)
					?.apply { balance = Config.getCurrentBalance() }
			} then {
				it?.apply(hold)
			}
		}

		fun getWatchOnlyWallet(hold: Pair<String, ChainType>.() -> Unit) {
			WalletTable.getCurrentWallet {
				if (isWatchOnly) getCurrentAddressesAndChainID().firstOrNull()?.let(hold)
			}
		}

		fun getCurrentAddresses(hold: (List<String>) -> Unit) {
			WalletTable.getCurrentWallet { hold(getCurrentAddresses()) }
		}

		fun getCurrentEOSWalletType(hold: (EOSWalletType) -> Unit) {
			WalletTable.getCurrentWallet {
				val type = when {
					EOSWalletUtils.isValidAccountName(currentEOSAccountName.getCurrent()) ->
						EOSWalletType.Available
					// 当前 `ChainID` 下的 `Name` 个数大于 `1` 并且越过第一步判断那么为未设置默认账户状态
					eosAccountNames.filter {
						it.chainID.equals(Config.getEOSCurrentChain().id, true)
					}.size > 1 -> EOSWalletType.NoDefault
					else -> EOSWalletType.Inactivated
				}
				hold(type)
			}
		}

		fun getWalletType(@UiThread hold: (WalletType, WalletTable) -> Unit) {
			WalletTable.getCurrentWallet {
				hold(getWalletType(), this)
			}
		}

		fun getETHAndERCWalletLatestChildAddressIndex(
			hold: (wallet: WalletTable, ethChildAddressIndex: Int) -> Unit
		) {
			WalletTable.getCurrentWallet {
				// 清理数据格式
				val pureAddresses = if (ethAddresses.contains(",")) {
					ethAddresses.replace(",", "")
				} else ethAddresses
				// 获取最近的 `Address Index` 数值
				hold(this, pureAddresses.substringAfterLast("|").toInt())
			}
		}

		fun getETCWalletLatestChildAddressIndex(
			hold: (
				wallet: WalletTable,
				etcChildAddressIndex: Int
			) -> Unit
		) {
			WalletTable.getCurrentWallet {
				// 清理数据格式
				val pureAddresses = if (etcAddresses.contains(",")) {
					etcAddresses.replace(",", "")
				} else etcAddresses
				// 获取最近的 `Address Index` 数值
				hold(this, pureAddresses.substringAfterLast("|").toInt())
			}
		}

		fun getBTCWalletLatestChildAddressIndex(
			hold: (
				wallet: WalletTable,
				btcChildAddressIndex: Int
			) -> Unit
		) {
			WalletTable.getCurrentWallet {
				// 清理数据格式
				val pureAddresses = if (btcAddresses.contains(",")) {
					btcAddresses.replace(",", "")
				} else btcAddresses
				// 获取最近的 `Address Index` 数值
				hold(this, pureAddresses.substringAfterLast("|").toInt())
			}
		}

		fun getBTCTestWalletLatestChildAddressIndex(
			hold: (
				wallet: WalletTable,
				btcSeriesTestChildAddressIndex: Int
			) -> Unit
		) {
			WalletTable.getCurrentWallet {
				// 清理数据格式
				val pureAddresses = if (btcSeriesTestAddresses.contains(",")) {
					btcSeriesTestAddresses.replace(",", "")
				} else btcSeriesTestAddresses
				// 获取最近的 `Address Index` 数值
				hold(this, pureAddresses.substringAfterLast("|").toInt())
			}
		}

		fun getLTCWalletLatestChildAddressIndex(
			hold: (
				wallet: WalletTable,
				ltcChildAddressIndex: Int
			) -> Unit
		) {
			WalletTable.getCurrentWallet {
				// 清理数据格式
				val pureAddresses = if (ltcAddresses.contains(",")) {
					ltcAddresses.replace(",", "")
				} else ltcAddresses
				// 获取最近的 `Address Index` 数值
				hold(this, pureAddresses.substringAfterLast("|").toInt())
			}
		}

		fun getBCHWalletLatestChildAddressIndex(
			hold: (
				wallet: WalletTable,
				bchChildAddressIndex: Int
			) -> Unit
		) {
			WalletTable.getCurrentWallet {
				// 清理数据格式
				val pureAddresses = if (bchAddresses.contains(",")) {
					bchAddresses.replace(",", "")
				} else bchAddresses
				// 获取最近的 `Address Index` 数值
				hold(this, pureAddresses.substringAfterLast("|").toInt())
			}
		}

		fun getEOSWalletLatestChildAddressIndex(
			hold: (
				wallet: WalletTable,
				eosChildAddressIndex: Int
			) -> Unit
		) {
			WalletTable.getCurrentWallet {
				// 清理数据格式
				val pureAddresses = if (eosAddresses.contains(",")) {
					eosAddresses.replace(",", "")
				} else eosAddresses
				// 获取最近的 `Address Index` 数值
				hold(this, pureAddresses.substringAfterLast("|").toInt())
			}
		}

		fun updateName(newName: String, callback: () -> Unit) {
			load { GoldStoneDataBase.database.walletDao().updateWalletName(newName) } then { callback() }
		}

		fun updateHint(newHint: String, callback: () -> Unit = {}) {
			load { GoldStoneDataBase.database.walletDao().updateHint(newHint) } then { callback() }
		}

		fun updateHasBackupMnemonic(callback: () -> Unit) {
			load { GoldStoneDataBase.database.walletDao().updateHasBackUp() } then { callback() }
		}

		fun updateETHAndERCAddresses(
			newAddress: String,
			newAddressIndex: Int,
			callback: (ethereumSeriesAddresses: String) -> Unit
		) {
			doAsync {
				GoldStoneDataBase.database.walletDao().apply {
					findWhichIsUsing(true)?.let {
						it.apply {
							val addresses = this.ethAddresses + "," + newAddress + "|$newAddressIndex"
							update(this.apply {
								ethAddresses = addresses
							})
							GoldStoneAPI.context.runOnUiThread {
								callback(addresses)
							}
						}
					}
				}
			}
		}

		fun updateETCAddresses(
			newAddress: String,
			newAddressIndex: Int,
			callback: (ethereumClassicAddresses: String) -> Unit
		) {
			doAsync {
				GoldStoneDataBase.database.walletDao().apply {
					findWhichIsUsing(true)?.let {
						it.apply {
							val addresses = this.etcAddresses + "," + newAddress + "|$newAddressIndex"
							update(this.apply {
								etcAddresses = addresses
							})
							GoldStoneAPI.context.runOnUiThread {
								callback(addresses)
							}
						}
					}
				}
			}
		}

		fun updateBTCAddresses(
			newAddress: String,
			newAddressIndex: Int,
			callback: (bitcoinAddresses: String) -> Unit
		) {
			doAsync {
				GoldStoneDataBase.database.walletDao().apply {
					findWhichIsUsing(true)?.let {
						it.apply {
							val addresses = this.btcAddresses + "," + newAddress + "|$newAddressIndex"
							update(this.apply {
								btcAddresses = addresses
							})
							GoldStoneAPI.context.runOnUiThread {
								callback(addresses)
							}
						}
					}
				}
			}
		}

		fun updateBCHAddresses(
			newAddress: String,
			newAddressIndex: Int,
			callback: (bitcoinCashAddresses: String) -> Unit
		) {
			doAsync {
				GoldStoneDataBase.database.walletDao().apply {
					findWhichIsUsing(true)?.let {
						it.apply {
							val addresses = this.bchAddresses + "," + newAddress + "|$newAddressIndex"
							update(this.apply {
								bchAddresses = addresses
							})
							GoldStoneAPI.context.runOnUiThread {
								callback(addresses)
							}
						}
					}
				}
			}
		}

		fun updateBTCTestAddresses(
			newAddress: String,
			newAddressIndex: Int,
			callback: (bitcoinAddresses: String) -> Unit
		) {
			doAsync {
				GoldStoneDataBase.database.walletDao().apply {
					findWhichIsUsing(true)?.let {
						it.apply {
							val addresses = this.btcSeriesTestAddresses + "," + newAddress + "|$newAddressIndex"
							update(this.apply {
								btcSeriesTestAddresses = addresses
							})
							GoldStoneAPI.context.runOnUiThread {
								callback(addresses)
							}
						}
					}
				}
			}
		}

		fun updateLTCAddresses(
			newAddress: String,
			newAddressIndex: Int,
			callback: (litecoinAddresses: String) -> Unit
		) {
			doAsync {
				GoldStoneDataBase.database.walletDao().apply {
					findWhichIsUsing(true)?.let {
						it.apply {
							val addresses = this.ltcAddresses + "," + newAddress + "|$newAddressIndex"
							update(this.apply {
								ltcAddresses = addresses
							})
							GoldStoneAPI.context.runOnUiThread {
								callback(addresses)
							}
						}
					}
				}
			}
		}

		fun updateEOSAddresses(
			newAddress: String,
			newAddressIndex: Int,
			callback: (eosAddresses: String) -> Unit
		) {
			doAsync {
				GoldStoneDataBase.database.walletDao().apply {
					findWhichIsUsing(true)?.let {
						it.apply {
							val addresses = this.eosAddresses + "," + newAddress + "|$newAddressIndex"
							update(this.apply {
								eosAddresses = addresses
							})
							GoldStoneAPI.context.runOnUiThread {
								callback(addresses)
							}
						}
					}
				}
			}
		}

		fun updateEOSAccountName(
			accountNames: List<EOSAccountInfo>,
			@UiThread callback: (hasDefaultAccount: Boolean) -> Unit
		) {
			doAsync {
				GoldStoneDataBase.database.walletDao().apply {
					// 增量存储同一公钥下的多 `AccountName`
					var currentAccountNames =
						getWalletByAddress(Config.getCurrentEOSAddress())?.eosAccountNames ?: listOf()
					currentAccountNames += accountNames
					updateCurrentEOSAccountNames(currentAccountNames)
				}
				// 如果公钥下只有一个 `AccountName` 那么直接设为 `DefaultName`
				if (accountNames.size == 1) {
					val accountName = accountNames.first().name
					WalletTable.updateEOSDefaultName(accountName) { callback(true) }
				} else GoldStoneAPI.context.runOnUiThread { callback(false) }
			}
		}

		fun updateEOSDefaultName(
			defaultName: String,
			@UiThread callback: () -> Unit
		) {
			doAsync {
				// 更新钱包数据库的 `Default EOS Address`
				GoldStoneDataBase.database.walletDao().apply {
					findWhichIsUsing(true)?.let {
						it.apply {
							update(apply { currentEOSAccountName.updateCurrent(defaultName) })
							// 同时更新 `MyTokenTable` 里面的 `OwnerName`
							MyTokenTable.updateEOSAccountName(defaultName, currentEOSAddress)
							GoldStoneAPI.context.runOnUiThread { callback() }
						}
					}
				}
			}
		}

		fun switchCurrentWallet(
			walletAddress: String,
			callback: (WalletTable?) -> Unit
		) {
			doAsync {
				GoldStoneDataBase.database.walletDao().apply {
					findWhichIsUsing(true)?.let {
						update(it.apply { it.isUsing = false })
					}
					getWalletByAddress(walletAddress)?.let { wallet ->
						update(wallet.apply { wallet.isUsing = true })
						GoldStoneAPI.context.runOnUiThread { callback(wallet) }
					}
				}
			}
		}

		fun deleteCurrentWallet(callback: (WalletTable?) -> Unit) {
			doAsync {
				GoldStoneDataBase.database.walletDao().apply {
					val willDeleteWallet = findWhichIsUsing(true)
					willDeleteWallet?.let { delete(it) }
					getAllWallets().let { wallets ->
						wallets.isEmpty() isTrue {
							callback(willDeleteWallet)
						} otherwise {
							update(wallets.first().apply { isUsing = true })
							Config.updateCurrentIsWatchOnlyOrNot(wallets.first().isWatchOnly.orFalse())
							callback(willDeleteWallet)
						}
					}
				}
			}
		}

		fun isWatchOnlyWalletShowAlertOrElse(context: Context, callback: () -> Unit) {
			Config.getCurrentIsWatchOnlyOrNot() isTrue {
				context.alert(AlertText.watchOnly)
				return
			}
			callback()
		}

		fun getWalletByAddress(address: String, hold: (WalletTable?) -> Unit) {
			load {
				GoldStoneDataBase.database.walletDao().getWalletByAddress(address)
			} then (hold)
		}

		fun checkIsWatchOnlyAndHasBackupOrElse(
			context: Context,
			confirmEvent: () -> Unit,
			callback: () -> Unit
		) {
			WalletTable.isWatchOnlyWalletShowAlertOrElse(context) {
				context.hasBackUpOrElse(confirmEvent, callback)
			}
		}

		private fun Context.hasBackUpOrElse(confirmEvent: () -> Unit, callback: () -> Unit) {
			WalletTable.getCurrentWallet {
				hasBackUpMnemonic isFalse {
					GoldStoneDialog.show(this@hasBackUpOrElse) {
						showButtons(DialogText.goToBackUp) {
							confirmEvent()
							GoldStoneDialog.remove(this@hasBackUpOrElse)
						}
						setImage(R.drawable.succeed_banner)
						setContent(
							DialogText.backUpMnemonic, DialogText.backUpMnemonicDescription
						)
					}
				} otherwise {
					callback()
				}
			}
		}
	}
}

@Dao
interface WalletDao {

	@Query("UPDATE wallet SET hasBackUpMnemonic = :hasBackUp WHERE isUsing LIKE :isUsing")
	fun updateHasBackUp(hasBackUp: Boolean = true, isUsing: Boolean = true)

	@Query("UPDATE wallet SET hint = :hint WHERE isUsing LIKE :isUsing")
	fun updateHint(hint: String, isUsing: Boolean = true)

	@Query("UPDATE wallet SET name = :walletName WHERE isUsing LIKE :isUsing")
	fun updateWalletName(walletName: String, isUsing: Boolean = true)

	@Query("SELECT * FROM wallet WHERE isUsing LIKE :status ORDER BY id DESC")
	fun findWhichIsUsing(status: Boolean): WalletTable?

	@Query("SELECT * FROM wallet WHERE currentETHAndERCAddress LIKE :walletAddress OR currentEOSAddress LIKE :walletAddress OR currentBCHAddress LIKE :walletAddress OR currentLTCAddress LIKE :walletAddress OR currentBTCAddress LIKE :walletAddress OR currentBTCSeriesTestAddress LIKE :walletAddress")
	fun getWalletByAddress(walletAddress: String): WalletTable?

	@Query("SELECT * FROM wallet")
	fun getAllWallets(): List<WalletTable>

	@Insert
	fun insert(wallet: WalletTable)

	@Delete
	fun delete(wallet: WalletTable)

	@Update
	fun update(wallet: WalletTable)

	@Query("UPDATE wallet SET eosAccountNames = :accounts  WHERE isUsing = :status")
	fun updateCurrentEOSAccountNames(accounts: List<EOSAccountInfo>, status: Boolean = true)
}