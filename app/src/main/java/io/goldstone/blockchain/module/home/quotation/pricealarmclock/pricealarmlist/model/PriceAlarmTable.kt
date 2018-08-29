package io.goldstone.blockchain.module.home.quotation.pricealarmclock.pricealarmlist.model

import android.arch.persistence.room.*
import com.blinnnk.extension.toArrayList
import com.blinnnk.util.coroutinesTask
import com.google.gson.annotations.SerializedName
import io.goldstone.blockchain.common.utils.load
import io.goldstone.blockchain.common.utils.then
import io.goldstone.blockchain.common.value.ArgumentKey
import io.goldstone.blockchain.common.value.ValueTag
import io.goldstone.blockchain.kernel.database.GoldStoneDataBase
import io.goldstone.blockchain.kernel.network.GoldStoneAPI
import io.goldstone.blockchain.module.home.quotation.quotation.model.QuotationModel
import org.jetbrains.anko.runOnUiThread
import java.io.Serializable
import java.util.*

/**
 * @data 07/23/2018 10/21
 * @author wcx
 * @description 价格闹钟提醒bean类
 */
@Entity(tableName = "price_alarm_clock")
data class PriceAlarmTable(
	@PrimaryKey(autoGenerate = true)
	var id: Int,
	var addId: String,
	var createTime: String,
	var marketName: String,
	var currencyName: String,
	var marketPrice: String,
	var price: String,
	var status: Boolean,
	var pair: String,
	var priceType: Int,
	var alarmType: Int = ArgumentKey.repeatingForAlarm, // 0为永久
	var pairDisplay: String,
	var symbol: String = "",
	var name: String?
) : Serializable {
	@Ignore
	constructor() : this(
		0,
		"0",
		"",
		"",
		"",
		"0",
		"0",
		true,
		"",
		0,
		0,
		"",
		"",
		null
	)

	constructor(quotationModel: QuotationModel) : this(
		0,
		"0",
		"",
		quotationModel.exchangeName,
		quotationModel.quoteSymbol.toUpperCase(),
		if (quotationModel.price == ValueTag.emptyPrice) "0" else quotationModel.price,
		if (quotationModel.price == ValueTag.emptyPrice) "0" else quotationModel.price,
		false,
		quotationModel.pair,
		0,
		0,
		quotationModel.pairDisplay,
		quotationModel.symbol,
		quotationModel.name
	)

	companion object {
		fun insertPriceAlarm(
			priceAlarmTable: PriceAlarmTable,
			callback: () -> Unit
		) {
			load {
				GoldStoneDataBase.database.priceAlarmDao().insertPriceAlarm(priceAlarmTable)
			} then {
				GoldStoneAPI.context.runOnUiThread {
					callback()
				}
			}
		}

		fun updatePriceAlarm(
			priceAlarmTable: PriceAlarmTable,
			callback: () -> Unit
		) {
			load {
				GoldStoneDataBase.database.priceAlarmDao().updatePriceAlarm(priceAlarmTable)
			} then {
				GoldStoneAPI.context.runOnUiThread {
					callback()
				}
			}
		}

		fun deleteAllAlarm(callback: () -> Unit) {
			load {
				GoldStoneDataBase.database.priceAlarmDao().deleteAllPriceAlarm()
			} then {
				GoldStoneAPI.context.runOnUiThread {
					callback()
				}
			}
		}

		fun deleteAlarm(
			priceAlarmTable: PriceAlarmTable,
			callback: () -> Unit
		) {
			load {
				GoldStoneDataBase.database.priceAlarmDao().deletePriceAlarm(priceAlarmTable)
			} then {
				GoldStoneAPI.context.runOnUiThread {
					callback()
				}
			}
		}

		fun getAll(hold: (ArrayList<PriceAlarmTable>) -> Unit) {
			load {
				GoldStoneDataBase.database.priceAlarmDao().selectPriceAlarms()
			} then {
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
interface PriceAlarmDao {

	@Insert
	fun insertPriceAlarm(user: PriceAlarmTable)

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	fun insertPriceAlarms(arrayList: ArrayList<PriceAlarmTable>)

	@Delete
	fun deletePriceAlarm(priceAlarmTable: PriceAlarmTable)

	@Query("DELETE FROM price_alarm_clock")
	fun deleteAllPriceAlarm()

	@Query("select * from price_alarm_clock")
	fun selectPriceAlarms(): List<PriceAlarmTable>

	@Update
	fun updatePriceAlarm(priceAlarmTable: PriceAlarmTable)

	@Update
	fun updatePriceAlarms(arrayList: ArrayList<PriceAlarmTable>)
}

data class AlarmConfigListModel(
	@SerializedName("code")
	val code: Int,
	@SerializedName("list")
	val list: List<ListModel>
) {
	data class ListModel(
		@SerializedName("on_off")
		val onOff: String,
		@SerializedName("name")
		val name: String,
		@SerializedName("value")
		val value: String
	)
}

data class AddAlarmModel(
	@SerializedName("code")
	val code: Int,
	@SerializedName("id")
	val id: String
)

data class DeleteAlarmModel(
	@SerializedName("code")
	val code: Int
)

data class PricePairModel(
	@SerializedName("pair")
	var pair: String,
	@SerializedName("price")
	var price: String,
	@SerializedName("marketName")
	var marketName: String = "",
	@SerializedName("pairDisplay")
	var pairDisplay: String = ""
)
