package io.goldstone.blockchain.module.home.wallet.notifications.notificationlist.model

import android.arch.persistence.room.*
import com.blinnnk.extension.safeGet
import com.blinnnk.extension.toArrayList
import com.blinnnk.util.coroutinesTask
import io.goldstone.blockchain.common.utils.ConcurrentAsyncCombine
import io.goldstone.blockchain.kernel.database.GoldStoneDataBase
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.model.TinyNumber
import org.json.JSONObject

/**
 * @date 25/03/2018 1:49 AM
 * @author KaySaith
 */
enum class NotificationType(val code: Int) {
	
	Transaction(0), System(1)
}

@Entity(tableName = "notification")
data class NotificationTable(
	@PrimaryKey(autoGenerate = true)
	var id: Int,
	val content: String = "",
	val title: String = "",
	val createTime: Long = 0L,
	val action: String = "",
	val actionContent: String = "", // hash or weburl
	val type: Int = 0,
	val extra: String? = ""
) {
	
	constructor(data: NotificationTable) : this(
		0,
		data.content,
		data.title,
		data.createTime,
		data.action,
		data.actionContent,
		data.type,
		data.extra
	)
	
	constructor(data: JSONObject) : this(
		0,
		data.safeGet("content"),
		data.safeGet("title"),
		data.safeGet("create_on").toLong(),
		data.safeGet("action"),
		data.safeGet("action_content"),
		data.safeGet("type").toInt(),
		data.safeGet("extra")
	)
	
	companion object {
		fun getAllNotifications(hold: (ArrayList<NotificationTable>) -> Unit) {
			coroutinesTask(
				{
					GoldStoneDataBase.database.notificationDao().getAllNotifications()
				}) {
				hold(it.sortedByDescending { it.createTime }.toArrayList())
			}
		}
		
		fun getChianID(extra: String): String {
			return if (extra.isNotEmpty()) {
				JSONObject(extra).safeGet("chainid")
			} else ""
		}
		
		fun getReceiveStatus(extra: String): Boolean? {
			return if (extra.isNotEmpty()) {
				JSONObject(extra).safeGet("from_or_to").toIntOrNull() == TinyNumber.True.value
			} else null
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