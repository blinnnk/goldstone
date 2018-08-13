package io.goldstone.blockchain.module.common.walletgeneration.createwallet.model

import android.arch.persistence.room.*
import android.content.Context
import com.blinnnk.extension.*
import io.goldstone.blockchain.R
import io.goldstone.blockchain.common.component.overlay.GoldStoneDialog
import io.goldstone.blockchain.common.language.AlertText
import io.goldstone.blockchain.common.language.DialogText
import io.goldstone.blockchain.common.utils.load
import io.goldstone.blockchain.common.utils.then
import io.goldstone.blockchain.common.value.Config
import io.goldstone.blockchain.common.value.WalletType
import io.goldstone.blockchain.crypto.ChainType
import io.goldstone.blockchain.crypto.CryptoSymbol
import io.goldstone.blockchain.kernel.database.GoldStoneDataBase
import io.goldstone.blockchain.kernel.network.GoldStoneAPI
import org.jetbrains.anko.alert
import org.jetbrains.anko.appcompat.v7.Appcompat
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.runOnUiThread
import java.io.Serializable
import java.util.*

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
	var ethAddresses: String, // format - "address|index,0x288832ds23...|0"
	var btcAddresses: String,
	var btcSeriesTestAddresses: String,
	var etcAddresses: String,
	var ltcAddresses: String,
	var ethPath: String,
	var etcPath: String,
	var btcPath: String,
	var btcTestPath: String,
	var ltcPath: String,
	var isUsing: Boolean,
	var hint: String? = null,
	var isWatchOnly: Boolean = false,
	var balance: Double? = 0.0,
	var encryptMnemonic: String? = null,
	var hasBackUpMnemonic: Boolean = false
) : Serializable {

	companion object {

		fun getWalletAddressCount(hold: (Int) -> Unit) {
			WalletTable.getCurrentWallet {
				when (Config.getCurrentWalletType()) {
					WalletType.MultiChain.content -> {
						val ethAddressCount = ethAddresses.split(",").size
						val etcAddressCount = etcAddresses.split(",").size
						val btcAddressCount = btcAddresses.split(",").size
						val btcTestAddressCount = btcSeriesTestAddresses.split(",").size
						val ltcAddressCount = ltcAddresses.split(",").size
						hold(ethAddressCount + etcAddressCount + btcAddressCount + btcTestAddressCount + ltcAddressCount)
					}

					WalletType.ETHERCAndETCOnly.content -> hold(1)
					WalletType.BTCTestOnly.content -> hold(1)
					WalletType.BTCOnly.content -> hold(1)
				}
			}
		}

		fun getAddressBySymbol(symbol: String?): String {
			return when {
				symbol.equals(CryptoSymbol.btc(), true) -> {
					if (Config.isTestEnvironment()) {
						Config.getCurrentBTCSeriesTestAddress()
					} else {
						Config.getCurrentBTCAddress()
					}
				}
				symbol.equals(CryptoSymbol.ltc, true) -> {
					if (Config.isTestEnvironment()) Config.getCurrentBTCSeriesTestAddress()
					else Config.getCurrentLTCAddress()
				}
				symbol.equals(CryptoSymbol.etc, true) ->
					Config.getCurrentETCAddress()
				else ->
					Config.getCurrentEthereumAddress()
			}
		}

		fun insert(
			model: WalletTable,
			callback: () -> Unit
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
				callback()
			}
		}

		fun saveEncryptMnemonicIfUserSkip(
			encryptMnemonic: String,
			address: String,
			callback: () -> Unit
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

		fun getAll(callback: ArrayList<WalletTable>.() -> Unit = {}) {
			load {
				GoldStoneDataBase.database.walletDao().getAllWallets()
			} then {
				callback(it.toArrayList())
			}
		}

		fun getAllETHAndERCAddresses(callback: ArrayList<String>.() -> Unit) {
			load {
				GoldStoneDataBase.database.walletDao().getAllWallets()
			} then { it ->
				callback(
					it.map {
						it.currentETHAndERCAddress
					}.toArrayList()
				)
			}
		}

		fun getAllBTCMainnetAddresses(callback: ArrayList<String>.() -> Unit) {
			load {
				GoldStoneDataBase.database.walletDao().getAllWallets()
			} then { it ->
				callback(
					it.map {
						it.currentBTCAddress
					}.toArrayList()
				)
			}
		}

		fun getAllLTCAddresses(callback: ArrayList<String>.() -> Unit) {
			load {
				GoldStoneDataBase.database.walletDao().getAllWallets()
			} then { it ->
				callback(
					it.map {
						it.currentLTCAddress
					}.toArrayList()
				)
			}
		}

		fun getAllBTCSeriesTestnetAddresses(callback: ArrayList<String>.() -> Unit) {
			load {
				GoldStoneDataBase.database.walletDao().getAllWallets()
			} then { it ->
				callback(
					it.map {
						it.currentBTCSeriesTestAddress
					}.toArrayList()
				)
			}
		}

		fun getCurrentWallet(hold: WalletTable.() -> Unit) {
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
						hold(it[0])
					}
				} else {
					hold(null)
				}
			}
		}

		fun getCurrentAddresses(hold: (List<String>) -> Unit) {
			WalletTable.getCurrentWallet {
				listOf(
					currentBTCAddress,
					currentBTCSeriesTestAddress,
					currentETCAddress,
					currentETHAndERCAddress,
					currentLTCAddress
				).filter {
					it.isNotEmpty()
				}.apply {
					if (isEmpty()) hold(this)
					else distinctBy { it }.let(hold)
				}
			}
		}

		fun getAddressesByWallet(wallet: WalletTable): List<String> {
			return listOf(
				wallet.currentBTCAddress,
				wallet.currentBTCSeriesTestAddress,
				wallet.currentETCAddress,
				wallet.currentETHAndERCAddress,
				wallet.currentLTCAddress
			).filter { it.isNotEmpty() }.distinctBy { it }
		}

		fun getTargetWalletType(walletTable: WalletTable): WalletType {
			val types = listOf(
				Pair(WalletType.BTCOnly, walletTable.currentBTCAddress),
				Pair(WalletType.BTCTestOnly, walletTable.currentBTCAddress),
				Pair(WalletType.ETHERCAndETCOnly, walletTable.currentBTCAddress),
				Pair(WalletType.LTCOnly, walletTable.currentLTCAddress)
			).filter {
				it.second.isNotEmpty()
			}
			return when (types.size) {
				4 -> WalletType.MultiChain
				else -> types[0].first
			}
		}

		fun getWalletType(hold: (WalletType) -> Unit) {
			WalletTable.getCurrentWallet {
				hold(getTargetWalletType(this))
			}
		}

		fun getETHAndERCWalletLatestChildAddressIndex(
			hold: (wallet: WalletTable, ethChildAddressIndex: Int) -> Unit
		) {
			WalletTable.getCurrentWallet {
				// 清理数据格式
				val pureAddresses = if (ethAddresses.contains(",")) {
					ethAddresses.replace(",", "")
				} else {
					ethAddresses
				}
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
				} else {
					etcAddresses
				}
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
				} else {
					btcAddresses
				}
				// 获取最近的 `Address Index` 数值
				hold(this, pureAddresses.substringAfterLast("|").toInt())
			}
		}

		fun getBTCTestWalletLatestChildAddressIndex(
			hold: (
				wallet: WalletTable,
				btcTestChildAddressIndex: Int
			) -> Unit
		) {
			WalletTable.getCurrentWallet {
				// 清理数据格式
				val pureAddresses = if (btcSeriesTestAddresses.contains(",")) {
					btcSeriesTestAddresses.replace(",", "")
				} else {
					btcSeriesTestAddresses
				}
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
				} else {
					ltcAddresses
				}
				// 获取最近的 `Address Index` 数值
				hold(this, pureAddresses.substringAfterLast("|").toInt())
			}
		}

		fun updateName(newName: String, callback: () -> Unit) {
			load {
				GoldStoneDataBase.database.walletDao().apply {
					findWhichIsUsing(true)?.let {
						update(it.apply { name = newName })
					}
				}
			} then {
				callback()
			}
		}

		fun updateHint(
			newHint: String,
			callback: () -> Unit = {}
		) {
			load {
				GoldStoneDataBase.database.walletDao().apply {
					findWhichIsUsing(true)?.let {
						update(it.apply { hint = newHint })
					}
				}
			} then {
				callback()
			}
		}

		fun updateHasBackupMnemonic(callback: () -> Unit) {
			load {
				GoldStoneDataBase.database.walletDao().apply {
					findWhichIsUsing(true)?.let {
						update(it.apply { hasBackUpMnemonic = true })
					}
				}
			} then {
				callback()
			}
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

		fun updateCurrentAddressByChainType(
			chainType: Int,
			newAddress: String,
			callback: () -> Unit
		) {
			WalletTable.getCurrentWallet wallet@{
				doAsync {
					when (chainType) {
						ChainType.ETH.id -> {
							GoldStoneDataBase.database.walletDao().update(
								this@wallet.apply {
									currentETHAndERCAddress = newAddress
									Config.updateCurrentEthereumAddress(newAddress)
								}
							)
							GoldStoneAPI.context.runOnUiThread {
								callback()
							}
						}

						ChainType.ETC.id -> {
							GoldStoneDataBase.database.walletDao().update(
								this@wallet.apply {
									currentETCAddress = newAddress
									Config.updateCurrentETCAddress(newAddress)
								}
							)
							GoldStoneAPI.context.runOnUiThread {
								callback()
							}
						}

						ChainType.LTC.id -> {
							GoldStoneDataBase.database.walletDao().update(
								this@wallet.apply {
									currentLTCAddress = newAddress
									Config.updateCurrentLTCAddress(newAddress)
								}
							)
							GoldStoneAPI.context.runOnUiThread {
								callback()
							}
						}

						ChainType.BTC.id -> {
							if (Config.isTestEnvironment()) {
								GoldStoneDataBase.database.walletDao().update(
									this@wallet.apply {
										currentBTCSeriesTestAddress = newAddress
										Config.updateCurrentBTCTestAddress(newAddress)
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
						callback(wallet)
					}
				}
			}
		}

		fun deleteCurrentWallet(callback: () -> Unit) {
			doAsync {
				GoldStoneDataBase.database.walletDao().apply {
					findWhichIsUsing(true)?.let { delete(it) }
					getAllWallets().let { wallets ->
						wallets.isEmpty() isTrue {
							callback()
						} otherwise {
							update(wallets.first().apply { isUsing = true })
							Config.updateCurrentIsWatchOnlyOrNot(wallets.first().isWatchOnly.orFalse())
							callback()
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
				context.alert(Appcompat, AlertText.watchOnly).show()
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

	@Query("SELECT * FROM wallet WHERE isUsing LIKE :status ORDER BY id DESC")
	fun findWhichIsUsing(status: Boolean): WalletTable?

	@Query("SELECT * FROM wallet WHERE currentETHAndERCAddress LIKE :walletAddress OR currentLTCAddress LIKE :walletAddress OR currentBTCAddress LIKE :walletAddress OR currentBTCSeriesTestAddress LIKE :walletAddress")
	fun getWalletByAddress(walletAddress: String): WalletTable?

	@Query("SELECT * FROM wallet")
	fun getAllWallets(): List<WalletTable>

	@Insert
	fun insert(wallet: WalletTable)

	@Delete
	fun delete(wallet: WalletTable)

	@Update
	fun update(wallet: WalletTable)
}