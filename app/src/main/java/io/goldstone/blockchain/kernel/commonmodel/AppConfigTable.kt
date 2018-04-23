package io.goldstone.blockchain.kernel.commonmodel

import android.arch.persistence.room.*
import com.blinnnk.extension.isTrue
import com.blinnnk.util.coroutinesTask
import io.goldstone.blockchain.kernel.database.GoldStoneDataBase
import io.goldstone.blockchain.kernel.network.GoldStoneAPI
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.runOnUiThread

/**
 * @date 23/04/2018 2:42 PM
 * @author KaySaith
 */

@Entity(tableName = "appConfig")
data class AppConfigTable(
  @PrimaryKey(autoGenerate = true)
  var id: Int,
  var pincode: Int? = null,
  var showPincode: Boolean = false
) {
  companion object {
    fun getAppConfig(hold: (AppConfigTable) -> Unit) {
      coroutinesTask({
        GoldStoneDataBase.database.appConfigDao().getAppConfig()
      }) {
        it.isNotEmpty() isTrue {
          hold(it[0])
        }
      }
    }

    fun updatePinCode(newPinCode: Int, callback: () -> Unit) {
      doAsync {
        GoldStoneDataBase.database.appConfigDao().apply {
          getAppConfig().let {
            it.isNotEmpty() isTrue {
              update(it[0].apply { it[0].pincode = newPinCode })
              GoldStoneAPI.context.runOnUiThread {
                callback()
              }
            }
          }
        }
      }
    }

    fun setShowPinCodeStatus(status: Boolean, callback: () -> Unit) {
      AppConfigTable.getAppConfig {
        doAsync {
          GoldStoneDataBase.database.appConfigDao().update(it.apply { showPincode = status })
          GoldStoneAPI.context.runOnUiThread {
            callback()
          }
        }
      }
    }

    fun insertAppConfig() {
      doAsync {
        GoldStoneDataBase.database.appConfigDao().insert(AppConfigTable(0, null))
      }
    }
  }
}

@Dao
interface AppConfigDao {

  @Query("SELECT * FROM appConfig")
  fun getAppConfig(): List<AppConfigTable>

  @Insert
  fun insert(appConfigTable: AppConfigTable)

  @Update
  fun update(appConfigTable: AppConfigTable)

  @Delete
  fun delete(appConfigTable: AppConfigTable)
}