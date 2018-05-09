package io.goldstone.blockchain.module.common.walletgeneration.createwallet.model

import android.arch.persistence.room.*
import android.content.Context
import com.blinnnk.extension.*
import com.blinnnk.util.coroutinesTask
import com.blinnnk.util.observing
import io.goldstone.blockchain.common.value.AlertText
import io.goldstone.blockchain.kernel.database.GoldStoneDataBase
import io.goldstone.blockchain.kernel.network.GoldStoneAPI
import io.goldstone.blockchain.module.home.wallet.transactions.transactionlist.presenter.localTransactions
import org.jetbrains.anko.alert
import org.jetbrains.anko.appcompat.v7.Appcompat
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.runOnUiThread
import java.util.*

/**
 * @date 29/03/2018 10:35 PM
 * @author KaySaith
 */

@Entity(tableName = "wallet")
data class WalletTable(
	//@PrimaryKey autoGenerate 自增
	@PrimaryKey(autoGenerate = true) var id: Int, var name: String,
	var address: String,
	var isUsing: Boolean,
	var hint: String? = null,
	var isWatchOnly: Boolean = false,
	var passwordHint: String? = null,
	var balance: Double? = 0.0
) {
	companion object {

		var current: WalletTable by observing(WalletTable(0, "", "", false)) {
			// 每次切换账户需要清空放在内存里面的当前账户的信息.
			localTransactions = null
		}
		var walletCount: Int? = null

		fun insert(model: WalletTable, callback: () -> Unit = {}) {
			coroutinesTask({
				GoldStoneDataBase.database.walletDao().apply {
					findWhichIsUsing(true)?.let {
						update(it.apply { isUsing = false })
					}
					insert(model)
				}.findWhichIsUsing(true)
			}) {
				current.isWatchOnly = it?.isWatchOnly.orFalse()
				callback()
			}
		}

		fun getAll(callback: ArrayList<WalletTable>.() -> Unit = {}) {
			coroutinesTask({
				GoldStoneDataBase.database.walletDao().getAllWallets()
			}) {
				callback(it.toArrayList())
			}
		}

		fun getAllAddresses(callback: ArrayList<String>.() -> Unit = {}) {
			coroutinesTask({
				GoldStoneDataBase.database.walletDao().getAllWallets()
			}) {
				callback(it.map { it.address }.toArrayList())
			}
		}

		fun getCurrentWalletInfo(hold: (WalletTable?) -> Unit) {
			coroutinesTask({
				GoldStoneDataBase.database.walletDao().findWhichIsUsing(true)?.apply {
					balance = current.balance
				}
			}) { hold(it) }
		}

		fun getCurrentWalletAddress(hold: String.() -> Unit) {
			WalletTable.getCurrentWalletInfo {
				hold(it!!.address)
			}
		}

		fun updateName(newName: String, callback: () -> Unit) {
			coroutinesTask({
				GoldStoneDataBase.database.walletDao().apply {
					findWhichIsUsing(true)?.let {
						update(it.apply { name = newName })
					}
				}
			}) {
				callback()
			}
		}

		fun updateHint(newHint: String, callback: () -> Unit) {
			coroutinesTask({
				GoldStoneDataBase.database.walletDao().apply {
					findWhichIsUsing(true)?.let {
						update(it.apply { hint = newHint })
					}
				}
			}) {
				callback()
			}
		}

		fun switchCurrentWallet(walletAddress: String, callback: (WalletTable?) -> Unit) {
			doAsync {
				GoldStoneDataBase.database.walletDao().apply {
					findWhichIsUsing(true)?.let {
						update(it.apply { it.isUsing = false })
					}
					getWalletByAddress(walletAddress)?.let {
						update(it.apply { it.isUsing = true })
						GoldStoneAPI.context.runOnUiThread {
							current = it
							callback(it)
						}
					}
				}
			}
		}

		fun deleteCurrentWallet(callback: () -> Unit) {
			doAsync {
				GoldStoneDataBase.database.walletDao().apply {
					findWhichIsUsing(true)?.let { delete(it) }
					getAllWallets().let {
						it.isEmpty() isTrue {
							GoldStoneAPI.context.runOnUiThread { callback() }
						} otherwise {
							update(it.first().apply { isUsing = true })
							GoldStoneAPI.context.runOnUiThread {
								current.isWatchOnly = it.first().isWatchOnly.orFalse()
								callback()
							}
						}
					}
				}
			}
		}

		fun isWatchOnlyWalletShowAlertOrElse(context: Context, callback: () -> Unit) {
			current.isWatchOnly.isTrue {
				context.alert(Appcompat, AlertText.watchOnly).show()
				return
			}
			callback()
		}

		fun getWalletByAddress(address: String, callback: (WalletTable?) -> Unit) {
			coroutinesTask({
				GoldStoneDataBase.database.walletDao().getWalletByAddress(address)
			}) {
				callback(it)
			}
		}

		fun insertAddress(address: String, name: String, hint: String? = null, callback: () -> Unit) {
			coroutinesTask({
				GoldStoneDataBase.database.walletDao().findWhichIsUsing(true).let {
					it.isNull().isFalse {
						GoldStoneDataBase.database.walletDao().update(it!!.apply { isUsing = false })
					}
					WalletTable.insert(WalletTable(0, name, address, true, hint))
				}
			}) {
				callback()
			}
		}

	}
}

@Dao
interface WalletDao {
	@Query("SELECT * FROM wallet WHERE isUsing LIKE :status ORDER BY id DESC")
	fun findWhichIsUsing(status: Boolean): WalletTable?

	@Query("SELECT * FROM wallet WHERE address LIKE :walletAddress")
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