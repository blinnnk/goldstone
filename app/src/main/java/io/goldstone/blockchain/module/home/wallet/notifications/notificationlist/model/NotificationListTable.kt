package io.goldstone.blockchain.module.home.wallet.notifications.notificationlist.model

import android.arch.persistence.room.*
import com.blinnnk.extension.toArrayList
import com.blinnnk.util.coroutinesTask
import com.google.gson.annotations.SerializedName
import io.goldstone.blockchain.common.utils.ConcurrentAsyncCombine
import io.goldstone.blockchain.kernel.database.GoldStoneDataBase
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.model.TinyNumber

/**
 * @date 25/03/2018 1:49 AM
 * @author KaySaith
 */
enum class NotificationType(val code: Int) {
  Transaction(0), System(1)
}

@Entity(tableName = "notification")
data class NotificationTable(
  @PrimaryKey(autoGenerate = true) var id: Int,
  @SerializedName("content") val content: String = "",
  @SerializedName("title") val title: String = "",
  @SerializedName("create_on") val createTIme: Long = 0L,
  @SerializedName("hash") val transactionHash: String  = "",
  @SerializedName("type") val type: Int  = 0,
  @SerializedName("from_or_to") val isTo: Int  = 1,
  val isReceived: Boolean = false
) {

  constructor(data: NotificationTable) : this(
    0,
    data.content,
    data.title,
    data.createTIme,
    data.transactionHash,
    data.type,
    data.isTo,
    data.isTo == TinyNumber.True.value // `to` 是我收到
  )

  companion object {
    fun getAllNotifications(hold: (ArrayList<NotificationTable>) -> Unit) {
      coroutinesTask({
        GoldStoneDataBase.database.notificationDao().getAllNotifications()
      }) {
        hold(it.sortedByDescending { it.createTIme }.toArrayList())
      }
    }

    fun insertData(tables: ArrayList<NotificationTable>, callback: () -> Unit) {
      object : ConcurrentAsyncCombine() {
        override var asyncCount: Int = tables.size
        override fun concurrentJobs() {
          tables.forEach {
            GoldStoneDataBase.database.notificationDao().insert(it)
            completeMark()
          }
        }
        override fun mergeCallBack() = callback()
      }.start()
    }
  }
}

@Dao
interface NotificationDao {

  @Query("SELECT * FROM notification")
  fun getAllNotifications(): List<NotificationTable>

  @Insert
  fun insert(notification: NotificationTable)

  @Delete
  fun delete(notification: NotificationTable)

  @Update
  fun update(notification: NotificationTable)
}