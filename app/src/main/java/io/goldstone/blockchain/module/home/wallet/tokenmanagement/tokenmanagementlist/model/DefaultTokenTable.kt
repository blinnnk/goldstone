package io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.model

import android.arch.persistence.room.*
import com.blinnnk.util.coroutinesTask
import com.google.gson.annotations.SerializedName
import io.goldstone.blockchain.common.utils.toArrayList
import io.goldstone.blockchain.kernel.database.GoldStoneDataBase

/**
 * @date 25/03/2018 5:11 PM
 * @author KaySaith
 */

enum class TinyNumber(val value: Int) {
  True(1), False(0)
}

@Entity(tableName = "defaultTokens")
data class DefaultTokenTable(
  @PrimaryKey(autoGenerate = true)
  var id: Int,
  @SerializedName("address") var contract: String,
  @SerializedName("url") var iconUrl: String,
  @SerializedName("symbol") var symbol: String,
  @SerializedName("force_show") var forceShow: Int,
  @SerializedName("price") var price: Double,
  @SerializedName("name") var name: String,
  var decimal: String? = null,
  var totalSupply: String? = null,
  var isUsed: Boolean = false,
  var isDefault: Boolean = true
) {
  companion object {

    fun getTokens(hold: (ArrayList<DefaultTokenTable>) -> Unit) {
      coroutinesTask({
        GoldStoneDataBase.database.defaultTokenDao().getAllTokens()
      }) {
        hold(it.toArrayList())
      }
    }

    fun updateUsedStatusBySymbol(symbol: String, status: Boolean, callback: () -> Unit = {}) {
      coroutinesTask({
        GoldStoneDataBase.database.defaultTokenDao().apply {
          getTokenBySymbol(symbol).let {
            update(it.apply { isUsed = status })
          }
        }
      }) {
        callback()
      }
    }
  }
}

@Dao
interface DefaultTokenDao {
  @Query("SELECT * FROM defaultTokens")
  fun getAllTokens(): List<DefaultTokenTable>

  @Query("SELECT * FROM defaultTokens WHERE symbol LIKE :symbol")
  fun getTokenBySymbol(symbol: String): DefaultTokenTable

  @Insert
  fun insert(token: DefaultTokenTable)

  @Update
  fun update(token: DefaultTokenTable)

  @Delete
  fun delete(token: DefaultTokenTable)
}