package io.goldstone.blockchain.module.common.walletgeneration.createwallet.model

import android.arch.persistence.room.*
import com.blinnnk.util.coroutinesTask
import io.goldstone.blockchain.common.utils.toArrayList
import io.goldstone.blockchain.common.value.HoneyLanguage
import io.goldstone.blockchain.kernel.database.GoldStoneDataBase

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
  var passwordHint: String? = null,
  var language: Int = HoneyLanguage.English.code,
  var balance: Double? = null
) {
  companion object {

    var myBalance: Double? = null
    var walletCount: Int? = null

    fun insert(model: WalletTable, callback: () -> Unit = {}) {
      coroutinesTask({
        GoldStoneDataBase.database.walletDao().apply {
          findWhichIsUsing(true)?.let {
            update(it.apply { isUsing = false })
          }
          insert(model)
        }
      }) {
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

    fun switchCurrentWallet(walletAddress: String, callback: () -> Unit) {
      coroutinesTask({
        GoldStoneDataBase.database.walletDao().apply {
          findWhichIsUsing(true)?.let {
            update(it.apply { it.isUsing = false })
          }
          getWalletByAddress(walletAddress)?.let {
            update(it.apply {
              it.isUsing = true
            })
          }
        }
      }) {
        callback()
      }
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

  @Update
  fun update(wallet: WalletTable)
}