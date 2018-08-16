package io.goldstone.blockchain.module.home.quotation.pricealarmclock.pricealarmclocklist.model

import android.arch.persistence.room.*
import com.blinnnk.extension.toArrayList
import com.blinnnk.util.coroutinesTask
import io.goldstone.blockchain.kernel.database.GoldStoneDataBase
import io.goldstone.blockchain.kernel.network.GoldStoneAPI
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.runOnUiThread
import java.io.Serializable
import java.util.*

/**
 * @data 07/23/2018 10/21
 * @author wcx
 * @description 价格闹钟提醒bean类
 */
@Entity(tableName = "price_alarm_clock")
data class PriceAlarmClockTable(
  @PrimaryKey(autoGenerate = true)
  @ColumnInfo(name = "_id")
  var id: Int,
  @ColumnInfo(name = "add_id")
  var addId: String,
  @ColumnInfo(name = "create_time")
  var createTime: String?,
  @ColumnInfo(name = "market_name")
  var marketName: String?,
  @ColumnInfo(name = "currency_name")
  var currencyName: String?,
  @ColumnInfo(name = "market_price")
  var marketPrice: String?,
  @ColumnInfo(name = "price")
  var price: String?,
  @ColumnInfo(name = "status")
  var status: Boolean,
  @ColumnInfo(name = "pair")
  var pair: String?,
  @ColumnInfo(name = "price_type")
  var priceType: Int?,
  @ColumnInfo(name = "alarm_type")
  var alarmType: Int?, // 0为永久
  var pairDisplay: String?,
  var position: Int = -1,
  var symbol: String?,
  var name: String?
) : Serializable {
  @Ignore
  constructor() : this(
    0,
    "0",
    null,
    null,
    null,
    "0",
    "0",
    true,
    null,
    null,
    null,
    null,
    -1,
    null,
    null
  )

  companion object {
    fun insertPriceAlarm(
      priceAlarmClockTable: PriceAlarmClockTable,
      callback: () -> Unit
    ) {
      doAsync {
        GoldStoneDataBase.database.priceAlarmClockDao().apply {
          insertPriceAlarmClock(priceAlarmClockTable)
        }
        GoldStoneAPI.context.runOnUiThread {
          callback()
        }
      }
    }

    fun updatePriceAlarm(
      priceAlarmClockTable: PriceAlarmClockTable,
      callback: () -> Unit
    ) {
      doAsync {
        GoldStoneDataBase.database.priceAlarmClockDao().apply {
          updatePriceAlarmClock(priceAlarmClockTable)
        }
        GoldStoneAPI.context.runOnUiThread {
          callback()
        }
      }
    }

    fun deleteAllAlarm(callback: () -> Unit) {
      doAsync {
        GoldStoneDataBase.database.priceAlarmClockDao().apply {
          deleteAllPriceAlarmClock()
        }

        GoldStoneAPI.context.runOnUiThread {
          callback()
        }
      }
    }

    fun deleteAlarm(
      priceAlarmClockTable: PriceAlarmClockTable,
      callback: () -> Unit
    ) {
      doAsync {
        GoldStoneDataBase.database.priceAlarmClockDao().apply {
          deletePriceAlarmClock(priceAlarmClockTable)
        }
        GoldStoneAPI.context.runOnUiThread {
          callback()
        }
      }
    }

    fun getAllPriceAlarm(hold: (ArrayList<PriceAlarmClockTable>) -> Unit) {
      coroutinesTask(
        {
          GoldStoneDataBase.database.priceAlarmClockDao().selectPriceAlarmClocks()
        }) {
        val size = it.size - 1
        for (index: Int in 0..size) {
          it[index].position = index
        }
        hold(it.toArrayList())
      }
    }
  }
}

/**
 * @data 07/23/2018 10/41
 * @author wcx
 * @description 价格闹钟提醒数据库操作接口
 */
@Dao
interface PriceAlarmClockDao {

  @Insert
  fun insertPriceAlarmClock(user: PriceAlarmClockTable)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun insertPriceAlarmClocks(arrayList: ArrayList<PriceAlarmClockTable>)

  @Delete
  fun deletePriceAlarmClock(priceAlarmClockTable: PriceAlarmClockTable)

  @Query("DELETE FROM price_alarm_clock")
  fun deleteAllPriceAlarmClock()

  @Query("select * from price_alarm_clock")
  fun selectPriceAlarmClocks(): List<PriceAlarmClockTable>

  @Update
  fun updatePriceAlarmClock(priceAlarmClockTable: PriceAlarmClockTable)

  @Update
  fun updatePriceAlarmClocks(arrayList: ArrayList<PriceAlarmClockTable>)
}

data class AlarmConfigListModel(
  val code: Int,
  val list: List<ListBean>
) {
  data class ListBean(
    val on_off: String,
    val name: String,
    val value: String
  ) {
  }
}

data class AddAlarmClockModel(
  val code: Int,
  val id: String
) {
}

data class DeleteAlarmClockModel(val code: Int) {
}

