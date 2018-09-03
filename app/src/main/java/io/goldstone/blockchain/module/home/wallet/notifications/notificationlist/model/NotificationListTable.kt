package io.goldstone.blockchain.module.home.wallet.notifications.notificationlist.model

import android.arch.persistence.room.*
import com.blinnnk.extension.orElse
import com.blinnnk.extension.safeGet
import com.blinnnk.extension.toArrayList
import com.blinnnk.util.TinyNumberUtils
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import io.goldstone.blockchain.common.utils.ConcurrentAsyncCombine
import io.goldstone.blockchain.common.utils.load
import io.goldstone.blockchain.common.utils.then
import io.goldstone.blockchain.kernel.database.GoldStoneDataBase
import org.json.JSONObject
import java.io.Serializable

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
	val actionContent: String = "", // hash or WebURL
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
			load {
				GoldStoneDataBase.database.notificationDao().getAllNotifications()
			} then { it ->
				hold(it.sortedByDescending { it.createTime }.toArrayList())
			}
		}

		fun getChianID(extra: String): String {
			return if (extra.isNotEmpty()) {
				JSONObject(extra).safeGet("chainid")
			} else ""
		}

		fun getFromAddress(extra: String): String {
			return if (extra.isNotEmpty()) {
				JSONObject(extra).safeGet("from")
			} else ""
		}

		fun getBTCTransactionData(extra: String, isFrom: Boolean): List<ExtraTransactionModel> {
			val option = if (isFrom) "from" else "to"
			return if (extra.isNotEmpty()) {
				val gson = Gson()
				val collectionType = object : TypeToken<Collection<ExtraTransactionModel>>() {}.type
				val jsonData = JSONObject(extra).safeGet(option)
				gson.fromJson(jsonData, collectionType)
			} else listOf()
		}

		fun getToAddress(extra: String): String {
			return if (extra.isNotEmpty()) {
				JSONObject(extra).safeGet("to")
			} else ""
		}

		fun getSymbol(extra: String): String {
			return if (extra.isNotEmpty()) {
				JSONObject(extra).safeGet("symbol")
			} else ""
		}

		fun getValue(extra: String): Double {
			return if (extra.isNotEmpty()) {
				JSONObject(extra).safeGet("value").toDoubleOrNull().orElse(0.0)
			} else 0.0
		}

		fun getReceiveStatus(extra: String): Boolean? {
			return if (extra.isNotEmpty()) {
				TinyNumberUtils.isTrue(JSONObject(extra).safeGet("from_or_to"))
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

data class NotificationTransactionInfo(
	val hash: String,
	val chainID: String,
	val isReceived: Boolean,
	val symbol: String,
	val value: Double,
	val timeStamp: Long,
	val toAddress: String,
	val fromAddress: String
) : Serializable

data class ExtraTransactionModel(
	@SerializedName("value")
	val value: String,
	@SerializedName("address")
	val address: String
) {
	constructor() : this(
		"",
		""
	)
}
