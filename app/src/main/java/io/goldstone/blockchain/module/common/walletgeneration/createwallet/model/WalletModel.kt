package io.goldstone.blockchain.module.common.walletgeneration.createwallet.model

import android.arch.persistence.room.*
import android.content.Context
import com.blinnnk.extension.isTrue
import com.blinnnk.extension.orFalse
import com.blinnnk.util.coroutinesTask
import io.goldstone.blockchain.common.utils.toArrayList
import io.goldstone.blockchain.common.value.AlertText
import io.goldstone.blockchain.common.value.HoneyLanguage
import io.goldstone.blockchain.kernel.database.GoldStoneDataBase
import io.goldstone.blockchain.kernel.network.GoldStoneAPI
import org.jetbrains.anko.alert
import org.jetbrains.anko.appcompat.v7.Appcompat

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
  var address: String,
  var isUsing: Boolean,
  var isWatchOnly: Boolean = false,
  var passwordHint: String? = null,
  var language: Int = HoneyLanguage.English.code,
  var balance: Double? = null
) {
  companion object {

    var myBalance: Double? = null
    var walletCount: Int? = null
    var isWatchingWallet: Boolean? = null

    fun insert(model: WalletTable, callback: () -> Unit = {}) {
      coroutinesTask({
        GoldStoneDataBase.database.walletDao().apply {
          findWhichIsUsing(true)?.let {
            update(it.apply { isUsing = false })
          }
          insert(model)
        }.findWhichIsUsing(true)
      }) {
        isWatchingWallet = it?.isWatchOnly.orFalse()
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

    fun getCurrentWalletInfo(hold: (WalletTable?) -> Unit) {
      coroutinesTask({
        GoldStoneDataBase.database.walletDao().findWhichIsUsing(true)?.apply {
          balance =  myBalance
        }
      }) {
        hold(it)
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

    fun updateLanguage(code: Int, callback: () -> Unit) {
      coroutinesTask({
        GoldStoneDataBase.database.walletDao().apply {
          findWhichIsUsing(true)?.let {
            update(it.apply { language = code })
          }
        }
      }) {
        callback()
      }
    }

    fun switchCurrentWallet(walletAddress: String, callback: (WalletTable?) -> Unit) {
      coroutinesTask({
        GoldStoneDataBase.database.walletDao().apply {
          findWhichIsUsing(true)?.let {
            update(it.apply { it.isUsing = false })
          }
          getWalletByAddress(walletAddress)?.let {
            update(it.apply { it.isUsing = true })
          }
        }.findWhichIsUsing(true)
      }) {
        isWatchingWallet = it?.isWatchOnly.orFalse()
        callback(it)
      }
    }

    fun deleteCurrentWallet(callback: () -> Unit) {
      coroutinesTask({
        GoldStoneDataBase.database.walletDao().apply {
          findWhichIsUsing(true)?.let { delete(it) }
          getAllWallets().let {
            it.isNotEmpty().isTrue {
              update(it.first().apply { isUsing = true })
            }
          }
        }
      }) {
        callback()
      }
    }

    fun isWatchOnlyWalletShowAlertOrElse(context: Context, callback: () -> Unit) {
      isWatchingWallet?.isTrue {
        context.alert(Appcompat, AlertText.watchOnly).show()
        return
      }
      callback()
    }

  }
}

@Dao
interface WalletDao {
  @Query("SELECT * FROM wallet WHERE isUsing LIKE :status")
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