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
import io.goldstone.blockchain.common.utils.alert
import io.goldstone.blockchain.common.utils.load
import io.goldstone.blockchain.common.utils.then
import io.goldstone.blockchain.common.value.Config
import io.goldstone.blockchain.common.value.WalletType
import io.goldstone.blockchain.crypto.eos.EOSWalletType
import io.goldstone.blockchain.crypto.eos.EOSWalletUtils
import io.goldstone.blockchain.crypto.multichain.MultiChainType
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

	fun getCurrentAddressesAndChainID(): List<Pair<String, Int>> {
		return listOf(
			Pair(currentBTCAddress, MultiChainType.BTC.id),
			Pair(currentLTCAddress, MultiChainType.LTC.id),
			Pair(currentBCHAddress, MultiChainType.BCH.id),
			Pair(currentBTCSeriesTestAddress, MultiChainType.AllTest.id),
			Pair(currentETCAddress, MultiChainType.ETC.id),
			Pair(currentETHAndERCAddress, MultiChainType.ETH.id),
			Pair(currentEOSAddress, MultiChainType.EOS.id)
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

	companion object {
		fun getWalletAddressCount(hold: (Int) -> Unit) {
			WalletTable.getCurrentWallet {
				when (Config.getCurrentWalletType()) {
					WalletType.Bip44MultiChain.content -> {
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
					WalletType.ETHERCAndETCOnly.content -> hold(1)
					WalletType.BTCTestOnly.content -> hold(1)
					WalletType.BTCOnly.content -> hold(1)
					WalletType.LTCOnly.content -> hold(1)
					WalletType.BCHOnly.content -> hold(1)
					WalletType.EOSOnly.content -> hold(1)
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

		fun getWatchOnlyWallet(hold: (String?) -> Unit) {
			WalletTable.getCurrentWallet {
				if (isWatchOnly) {
					WalletTable.getCurrentAddresses {
						hold(it.first())
					}
				} else {
					hold(null)
				}
			}
		}

		fun getCurrentAddresses(hold: (List<String>) -> Unit) {
			WalletTable.getCurrentWallet {
				hold(getCurrentAddresses())
			}
		}

		fun getTargetWalletType(walletTable: WalletTable): WalletType {
			val types = listOf(
				Pair(WalletType.BTCOnly, walletTable.currentBTCAddress),
				Pair(WalletType.BTCTestOnly, walletTable.currentBTCSeriesTestAddress),
				Pair(WalletType.ETHERCAndETCOnly, walletTable.currentETHAndERCAddress),
				Pair(WalletType.LTCOnly, walletTable.currentLTCAddress),
				Pair(WalletType.BCHOnly, walletTable.currentBCHAddress),
				Pair(WalletType.EOSOnly, walletTable.currentEOSAddress)
			).filter {
				it.second.isNotEmpty()
			}
			return when (types.size) {
				6 -> {
					// 通过私钥导入的多链钱包没有 Path 值所以通过这个来判断是否是
					// BIP44 钱包还是单纯的多链钱包
					if (walletTable.ethPath.isNotEmpty()) WalletType.Bip44MultiChain
					else WalletType.MultiChain
				}
				else -> types.firstOrNull()?.first ?: WalletType.Bip44MultiChain
			}
		}

		fun getCurrentEOSWalletType(hold: (EOSWalletType) -> Unit) {
			WalletTable.getCurrentWallet {
				val type = when {
					EOSWalletUtils.isValidAccountName(currentEOSAccountName.getCurrent()) ->
						EOSWalletType.Available
					// 当前 `ChainID` 下的 `Name` 个数大于 `1` 并且越过第一步判断那么为未设置默认账户状态
					eosAccountNames.filter {
						it.chainID.equals(Config.getEOSCurrentChain(), true)
					}.size > 1 -> EOSWalletType.NoDefault
					else -> EOSWalletType.Inactivated
				}
				hold(type)
			}
		}

		fun getWalletType(@UiThread hold: (WalletType, WalletTable) -> Unit) {
			WalletTable.getCurrentWallet {
				hold(getTargetWalletType(this), this)
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
			GoldStoneDataBase.database.walletDao().updateCurrentEOSAccountNames(accountNames)
			// 如果公钥下只有一个 `AccountName` 那么直接设为 `DefaultName`
			if (accountNames.size == 1) {
				val accountName = accountNames.first().name
				WalletTable.updateEOSDefaultName(accountName) { callback(true) }
			} else GoldStoneAPI.context.runOnUiThread { callback(false) }
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

		fun updateCurrentAddressByChainType(
			chainType: Int,
			newAddress: String,
			@UiThread callback: () -> Unit
		) {
			WalletTable.getCurrentWallet wallet@{
				doAsync {
					when (chainType) {
						MultiChainType.ETH.id -> {
							doAsync {
								GoldStoneDataBase.database.walletDao().update(
									this@wallet.apply {
										currentETHAndERCAddress = newAddress
										Config.updateCurrentEthereumAddress(newAddress)
									}
								)
								GoldStoneAPI.context.runOnUiThread { callback() }
							}
						}

						MultiChainType.ETC.id -> {
							doAsync {
								GoldStoneDataBase.database.walletDao().update(
									this@wallet.apply {
										currentETCAddress = newAddress
										Config.updateCurrentETCAddress(newAddress)
									}
								)
								GoldStoneAPI.context.runOnUiThread { callback() }
							}
						}

						MultiChainType.LTC.id -> {
							doAsync {
								GoldStoneDataBase.database.walletDao().update(
									this@wallet.apply {
										currentLTCAddress = newAddress
										Config.updateCurrentLTCAddress(newAddress)
									}
								)
								GoldStoneAPI.context.runOnUiThread { callback() }
							}
						}

						MultiChainType.BCH.id -> {
							doAsync {
								GoldStoneDataBase.database.walletDao().update(
									this@wallet.apply {
										currentBCHAddress = newAddress
										Config.updateCurrentBCHAddress(newAddress)
									}
								)
								GoldStoneAPI.context.runOnUiThread { callback() }
							}
						}

						MultiChainType.EOS.id -> {
							doAsync {
								GoldStoneDataBase.database.walletDao().update(
									this@wallet.apply {
										currentEOSAddress = newAddress
										Config.updateCurrentEOSAddress(newAddress)
									}
								)
								GoldStoneAPI.context.runOnUiThread { callback() }
							}
						}

						MultiChainType.BTC.id -> {
							if (Config.isTestEnvironment()) {
								GoldStoneDataBase.database.walletDao().update(
									this@wallet.apply {
										currentBTCSeriesTestAddress = newAddress
										Config.updateCurrentBTCSeriesTestAddress(newAddress)
									}
								)
							} else {
								GoldStoneDataBase.database.walletDao().update(
									this@wallet.apply {
										currentBTCAddress = newAddress
										Config.updateCurrentBTCAddress(newAddress)
									}
								)
							}
							GoldStoneAPI.context.runOnUiThread {
								callback()
							}
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

		fun isWatchOnlyWalletShowAlertOrElse(
			context: Context,
			callback: () -> Unit
		) {
			Config.getCurrentIsWatchOnlyOrNot() isTrue {
				context.alert(AlertText.watchOnly)
				return
			}
			callback()
		}

		fun getWalletByAddress(
			address: String,
			hold: (WalletTable?) -> Unit
		) {
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

		private fun Context.hasBackUpOrElse(
			confirmEvent: () -> Unit,
			callback: () -> Unit
		) {
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