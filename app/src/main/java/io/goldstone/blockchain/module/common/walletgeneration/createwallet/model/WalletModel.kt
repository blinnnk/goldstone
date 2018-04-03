package io.goldstone.blockchain.module.common.walletgeneration.createwallet.model

import android.arch.persistence.room.*
import com.blinnnk.util.coroutinesTask
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
  var isUsing: Boolean
) {
  companion object {
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

    fun getAll(callback: List<WalletTable>.() -> Unit = {}) {
      coroutinesTask({
        GoldStoneDataBase.database.walletDao().getAllWallets()
      }) {
        callback(it)
      }
    }

    fun getDataByIndex(index: Int, callback: WalletTable.() -> Unit = {}) {
      coroutinesTask({
        GoldStoneDataBase.database.walletDao().getAllWallets()
      }) {
        callback(it[index])
      }
    }

    fun getCurrentWalletAddress(hold: (WalletTable?) -> Unit) {
      coroutinesTask({
        GoldStoneDataBase.database.walletDao().findWhichIsUsing(true)
      }) {
        hold(it)
      }
    }
  }

}

@Dao
interface WalletDao {
  @Query("SELECT * FROM wallet WHERE isUsing LIKE :status")
  fun findWhichIsUsing(status: Boolean): WalletTable?

  @Query("SELECT * FROM wallet")
  fun getAllWallets(): List<WalletTable>

  @Insert
  fun insert(wallet: WalletTable)

  @Update
  fun update(wallet: WalletTable)
}