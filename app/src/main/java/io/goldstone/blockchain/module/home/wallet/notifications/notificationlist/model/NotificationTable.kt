package io.goldstone.blockchain.module.home.wallet.notifications.notificationlist.model

import android.arch.persistence.room.*
import com.blinnnk.extension.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.goldstone.blockchain.common.utils.load
import io.goldstone.blockchain.common.utils.then
import io.goldstone.blockchain.kernel.database.GoldStoneDataBase
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
	val id: Int,
	val content: String,
	val title: String,
	val createTime: Long,
	val action: String,
	val actionContent: String, // hash or WebURL
	val type: Int,
	val extra: NotificationExtraModel
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
		data.safeGet("create_on").toLongOrZero(),
		data.safeGet("action"),
		data.safeGet("action_content"),
		data.safeGet("type").toIntOrZero(),
		NotificationExtraModel(data.getTargetObject("extra"))
	)

	companion object {
		fun getAllNotifications(hold: (ArrayList<NotificationTable>) -> Unit) {
			load {
				GoldStoneDataBase.database.notificationDao().getAllNotifications()
			} then { list ->
				hold(list.sortedByDescending { it.createTime }.toArrayList())
			}
		}

		fun getBTCTransactionData(extra: NotificationExtraModel, isFrom: Boolean): List<ExtraTransactionModel> {
			val gson = Gson()
			val collectionType = object : TypeToken<Collection<ExtraTransactionModel>>() {}.type
			val jsonData = if (isFrom) extra.fromAddress else extra.toAddress
			return gson.fromJson(jsonData, collectionType)
		}
	}
}

@Dao
interface NotificationDao {

	@Query("SELECT * FROM notification")
	fun getAllNotifications(): List<NotificationTable>

	@Insert
	fun insertAll(notification: List<NotificationTable>)

	@Insert
	fun insert(notification: NotificationTable)

	@Delete
	fun delete(notification: NotificationTable)

	@Delete
	fun deleteAll(notifications: List<NotificationTable>)

	@Update
	fun update(notification: NotificationTable)
}
