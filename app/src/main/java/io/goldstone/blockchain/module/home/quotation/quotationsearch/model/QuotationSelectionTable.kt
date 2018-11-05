package io.goldstone.blockchain.module.home.quotation.quotationsearch.model

import android.arch.persistence.room.*
import android.support.annotation.UiThread
import android.support.annotation.WorkerThread
import com.blinnnk.extension.isNull
import com.blinnnk.extension.orElse
import com.google.gson.annotations.SerializedName
import io.goldstone.blockchain.common.utils.load
import io.goldstone.blockchain.common.utils.then
import io.goldstone.blockchain.kernel.database.GoldStoneDataBase
import io.goldstone.blockchain.kernel.network.common.GoldStoneAPI
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.runOnUiThread
import java.io.Serializable

/**
 * @date 26/04/2018 10:47 AM
 * @author KaySaith
 */
@Entity(tableName = "quotationSelection")
data class QuotationSelectionTable(
	@PrimaryKey(autoGenerate = true)
	var id: Int,
	@SerializedName("market_id")
	var marketID: Int,
	@SerializedName("pair_display")
	var pairDisplay: String,
	@SerializedName("base")
	var baseSymbol: String,
	@SerializedName("quote")
	var quoteSymbol: String,
	@SerializedName("pair")
	var pair: String,
	@SerializedName("market")
	var market: String,
	@SerializedName("name")
	var name: String,
	@SerializedName("address")
	var contract: String,
	var infoTitle: String,
	var orderID: Double = 0.0,
	var lineChartDay: String,
	var isSelecting: Boolean = false,
	var lineChartWeek: String? = "",
	var lineChartMonth: String? = "",
	var lineChartHour: String? = "",
	var highTotal: String = "",
	var lowTotal: String = "",
	var high24: String = "",
	var low24: String = ""
) : Serializable {

	constructor(
		data: QuotationSelectionTable,
		lineChart: String
	) : this(
		0,
		data.marketID,
		data.pairDisplay,
		data.baseSymbol,
		data.quoteSymbol,
		data.pair,
		data.market,
		data.name,
		data.contract,
		data.pairDisplay + " " + data.market,
		data.orderID,
		lineChart,
		data.isSelecting,
		data.lineChartWeek,
		data.lineChartMonth,
		data.lineChartHour
	)

	companion object {

		@WorkerThread
		fun insertSelection(table: QuotationSelectionTable) {
			val quotationDao = GoldStoneDataBase.database.quotationSelectionDao()
			// 添加的时候赋予新的最大的 `orderID`
			val currentID = quotationDao.getMaxOrderIDTable()?.orderID
			val newOrderID = if (currentID.isNull()) 1.0 else currentID.orElse(0.0) + 1
			quotationDao.insert(table.apply { orderID = newOrderID })
		}

		fun getSelectionByPair(pair: String, hold: (QuotationSelectionTable) -> Unit) {
			load {
				GoldStoneDataBase.database.quotationSelectionDao().getSelectionByPair(pair)
			} then {
				it?.let(hold)
			}
		}

		fun getAll(@UiThread hold: (List<QuotationSelectionTable>) -> Unit) {
			load {
				GoldStoneDataBase.database.quotationSelectionDao().getAll()
			} then (hold)
		}

		fun updateSelectionOrderIDBy(fromPair: String, newOrderID: Double, callback: () -> Unit) {
			doAsync {
				GoldStoneDataBase.database.quotationSelectionDao().updateOrderIDByPair(fromPair, newOrderID)
				GoldStoneAPI.context.runOnUiThread { callback() }
			}
		}

		fun updateLineChartDataBy(pair: String, lineChart: String, callback: () -> Unit) {
			load {
				GoldStoneDataBase.database.quotationSelectionDao().updateDayLineChartByPair(pair, lineChart)
			} then {
				callback()
			}
		}

		fun updateLineChartWeekBy(pair: String, weekLineChart: String, callback: () -> Unit) {
			doAsync {
				GoldStoneDataBase.database.quotationSelectionDao().updateWeekLineChartByPair(pair, weekLineChart)
				GoldStoneAPI.context.runOnUiThread { callback() }
			}
		}

		fun updateLineChartMontyBy(pair: String, monthChart: String, callback: () -> Unit) {
			doAsync {
				GoldStoneDataBase.database.quotationSelectionDao().updateMonthLineChartByPair(pair, monthChart)
				GoldStoneAPI.context.runOnUiThread { callback() }
			}
		}

		fun updateLineChartHourBy(pair: String, hourChart: String, callback: () -> Unit) {
			doAsync {
				GoldStoneDataBase.database.quotationSelectionDao().updateHourLineChartByPair(pair, hourChart)
				GoldStoneAPI.context.runOnUiThread { callback() }
			}
		}
	}
}

@Dao
interface QuotationSelectionDao {

	@Query("SELECT * FROM quotationSelection")
	fun getAll(): List<QuotationSelectionTable>

	@Query("SELECT * FROM quotationSelection WHERE marketID IN (:marketIDs)")
	fun getTargetMarketTables(marketIDs: List<Int>): List<QuotationSelectionTable>

	@Query("SELECT * FROM quotationSelection WHERE orderID = (SELECT MAX(orderID) FROM quotationSelection)")
	fun getMaxOrderIDTable(): QuotationSelectionTable?

	@Query("SELECT * FROM quotationSelection WHERE pair LIKE :pair")
	fun getSelectionByPair(pair: String): QuotationSelectionTable?

	@Query("UPDATE quotationSelection SET high24 = :highPrice, low24 = :lowPrice, highTotal = :highTotal, lowTotal = :lowTotal WHERE pair = :pair")
	fun updatePriceInfo(highPrice: String, lowPrice: String, highTotal: String, lowTotal: String, pair: String)

	@Query("UPDATE quotationSelection SET lineChartDay = :lineChartDay WHERE pair LIKE :pair")
	fun updateDayLineChartByPair(pair: String, lineChartDay: String)

	@Query("UPDATE quotationSelection SET lineChartWeek = :lineChartWeek WHERE pair LIKE :pair")
	fun updateWeekLineChartByPair(pair: String, lineChartWeek: String)

	@Query("UPDATE quotationSelection SET lineChartMonth = :lineChartMonth WHERE pair LIKE :pair")
	fun updateMonthLineChartByPair(pair: String, lineChartMonth: String)

	@Query("UPDATE quotationSelection SET lineChartHour = :lineChartHour WHERE pair LIKE :pair")
	fun updateHourLineChartByPair(pair: String, lineChartHour: String)

	@Query("UPDATE quotationSelection SET orderID = :orderID WHERE pair LIKE :pair")
	fun updateOrderIDByPair(pair: String, orderID: Double)

	@Query("SELECT * FROM quotationSelection WHERE orderID LIKE :orderID")
	fun getSelectionByOrderID(orderID: Int): QuotationSelectionTable?

	@Insert
	fun insert(table: QuotationSelectionTable)

	@Update
	fun update(table: QuotationSelectionTable)

	@Delete
	fun delete(table: QuotationSelectionTable)

	@Delete
	fun deleteAll(tables: List<QuotationSelectionTable>)

	@Query("DELETE FROM quotationSelection WHERE pair IN (:pairs)")
	fun deleteByPairs(pairs: List<String>)

	@Query("DELETE FROM quotationSelection WHERE pair LIKE :pair")
	fun deleteByPairs(pair: String)
}
