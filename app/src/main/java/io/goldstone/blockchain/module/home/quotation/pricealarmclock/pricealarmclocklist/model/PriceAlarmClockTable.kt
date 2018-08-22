package io.goldstone.blockchain.module.home.quotation.pricealarmclock.pricealarmclocklist.model

import android.arch.persistence.room.*
import com.blinnnk.extension.toArrayList
import com.blinnnk.util.coroutinesTask
import io.goldstone.blockchain.common.utils.load
import io.goldstone.blockchain.common.utils.then
import io.goldstone.blockchain.common.value.ValueTag
import io.goldstone.blockchain.kernel.database.GoldStoneDataBase
import io.goldstone.blockchain.kernel.network.GoldStoneAPI
import io.goldstone.blockchain.module.home.quotation.quotation.model.QuotationModel
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
	var alarmType: Int = 0, // 0为永久
	var pairDisplay: String,
	var position: Int = -1,
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
		-1,
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
		quotationModel.price,
		false,
		quotationModel.pair,
		0,
		0,
		quotationModel.pairDisplay,
		-1,
		quotationModel.symbol,
		quotationModel.name
	)

	companion object {
		fun insertPriceAlarm(
			priceAlarmClockTable: PriceAlarmClockTable,
			callback: () -> Unit
		) {
			load {
				GoldStoneDataBase.database.priceAlarmClockDao().apply {
					insertPriceAlarmClock(priceAlarmClockTable)
				}
			} then {
				GoldStoneAPI.context.runOnUiThread {
					callback()
				}
			}
		}

		fun updatePriceAlarm(
			priceAlarmClockTable: PriceAlarmClockTable,
			callback: () -> Unit
		) {
			load {
				GoldStoneDataBase.database.priceAlarmClockDao().apply {
					updatePriceAlarmClock(priceAlarmClockTable)
				}
			} then {
				GoldStoneAPI.context.runOnUiThread {
					callback()
				}
			}
		}

		fun deleteAllAlarm(callback: () -> Unit) {
			load {
				GoldStoneDataBase.database.priceAlarmClockDao().apply {
					deleteAllPriceAlarmClock()
				}
			} then {
				GoldStoneAPI.context.runOnUiThread {
					callback()
				}
			}
		}

		fun deleteAlarm(
			priceAlarmClockTable: PriceAlarmClockTable,
			callback: () -> Unit
		) {
			load {
				GoldStoneDataBase.database.priceAlarmClockDao().apply {
					deletePriceAlarmClock(priceAlarmClockTable)
				}
			} then {
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
				val size = it.size
				for (index: Int in 0 until size) {
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
		val onOff: String,
		val name: String,
		val value: String
	)
}

data class AddAlarmClockModel(
	val code: Int,
	val id: String
)

data class DeleteAlarmClockModel(val code: Int)

data class PricePairModel(
	var pair: String,
	var price: String,
	var marketName: String = "",
	var pairDisplay: String = ""
)
