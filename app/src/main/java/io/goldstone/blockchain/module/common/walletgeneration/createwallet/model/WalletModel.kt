package io.goldstone.blockchain.module.common.walletgeneration.createwallet.model

import android.arch.persistence.room.*
import android.content.Context
import com.blinnnk.extension.*
import com.blinnnk.util.coroutinesTask
import io.goldstone.blockchain.R
import io.goldstone.blockchain.common.component.GoldStoneDialog
import io.goldstone.blockchain.common.value.AlertText
import io.goldstone.blockchain.common.value.Config
import io.goldstone.blockchain.common.value.DialogText
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
	var currentEthSeriesAddress: String, // currentEthSeriesAddress
	var isUsing: Boolean,
	var hint: String? = null,
	var isWatchOnly: Boolean = false,
	var balance: Double? = 0.0,
	var encryptMnemonic: String? = null,
	var hasBackUpMnemonic: Boolean = false,
	var currentBtcSeriesAddress: String = "",
	var ethSeriesAddresses: String = "",
	var btcSeriesAddresses: String = "",
	var ethPath: String = "",
	var btcPath: String = ""
) : Serializable {
	
	companion object {
		
		fun insert(
			model: WalletTable,
			callback: () -> Unit
		) {
			coroutinesTask(
				{
					GoldStoneDataBase.database.walletDao().apply {
						findWhichIsUsing(true)?.let {
							update(it.apply { isUsing = false })
						}
						insert(model)
					}.findWhichIsUsing(true)
				}) {
				Config.updateCurrentIsWatchOnlyOrNot(it?.isWatchOnly.orFalse())
				callback()
			}
		}
		
		fun saveEncryptMnemonicIfUserSkip(
			encryptMnemonic: String,
			address: String = Config.getCurrentAddress(),
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
			coroutinesTask(
				{
					GoldStoneDataBase.database.walletDao().getAllWallets()
				}) {
				callback(it.toArrayList())
			}
		}
		
		fun getAllAddresses(callback: ArrayList<String>.() -> Unit = {}) {
			coroutinesTask(
				{
					GoldStoneDataBase.database.walletDao().getAllWallets()
				}) {
				callback(it.map { it.currentEthSeriesAddress }.toArrayList())
			}
		}
		
		fun getCurrentWallet(hold: (WalletTable?) -> Unit) {
			coroutinesTask(
				{
					GoldStoneDataBase.database.walletDao().findWhichIsUsing(true)?.apply {
						balance = Config.getCurrentBalance()
					}
				}) { hold(it) }
		}
		
		fun getWalletWithLatestChildAddressIndex(
			hold: (
				wallet: WalletTable,
				ethereumChildAddressIndex: Int
			) -> Unit
		) {
			WalletTable.getCurrentWallet {
				it?.apply {
					// 清理数据格式
					val pureAddresses = if (ethSeriesAddresses.contains(",")) {
						ethSeriesAddresses.replace(",", "")
					} else {
						ethSeriesAddresses
					}
					// 获取最近的 `Address Index` 数值
					hold(this, pureAddresses.substringAfterLast("|").toInt())
				}
			}
		}
		
		fun getCurrentWalletAddress(hold: String.() -> Unit) {
			WalletTable.getCurrentWallet {
				hold(it!!.currentEthSeriesAddress)
			}
		}
		
		fun updateName(
			newName: String,
			callback: () -> Unit
		) {
			coroutinesTask(
				{
					GoldStoneDataBase.database.walletDao().apply {
						findWhichIsUsing(true)?.let {
							update(it.apply { name = newName })
						}
					}
				}) {
				callback()
			}
		}
		
		fun updateHint(
			newHint: String,
			callback: () -> Unit = {}
		) {
			coroutinesTask(
				{
					GoldStoneDataBase.database.walletDao().apply {
						findWhichIsUsing(true)?.let {
							update(it.apply { hint = newHint })
						}
					}
				}) {
				callback()
			}
		}
		
		fun updateEthereumSeriesAddresses(
			newAddress: String,
			newAddressIndex: Int,
			callback: (ethereumSeriesAddresses: String) -> Unit
		) {
			doAsync {
				GoldStoneDataBase.database.walletDao().apply {
					findWhichIsUsing(true)?.let {
						it.apply {
							val addresses = this.ethSeriesAddresses + "," + newAddress + "|$newAddressIndex"
							update(this.apply {
								ethSeriesAddresses = addresses
							})
							GoldStoneAPI.context.runOnUiThread {
								callback(addresses)
							}
						}
					}
				}
			}
		}
		
		private fun updateHasBackedUpStatus(callback: () -> Unit) {
			coroutinesTask(
				{
					GoldStoneDataBase.database.walletDao().apply {
						findWhichIsUsing(true)?.let {
							update(it.apply {
								hasBackUpMnemonic = true
							})
						}
					}
				}) {
				callback()
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
						GoldStoneAPI.context.runOnUiThread {
							callback(wallet)
						}
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
							GoldStoneAPI.context.runOnUiThread { callback() }
						} otherwise {
							update(wallets.first().apply { isUsing = true })
							GoldStoneAPI.context.runOnUiThread {
								Config.updateCurrentIsWatchOnlyOrNot(wallets.first().isWatchOnly.orFalse())
								callback()
							}
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
			callback: (WalletTable?) -> Unit
		) {
			coroutinesTask(
				{
					GoldStoneDataBase.database.walletDao().getWalletByAddress(address)
				}) {
				callback(it)
			}
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
				it?.apply {
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
}

@Dao
interface WalletDao {
	
	@Query("SELECT * FROM wallet WHERE isUsing LIKE :status ORDER BY id DESC")
	fun findWhichIsUsing(status: Boolean): WalletTable?
	
	@Query("SELECT * FROM wallet WHERE currentEthSeriesAddress LIKE :walletAddress")
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