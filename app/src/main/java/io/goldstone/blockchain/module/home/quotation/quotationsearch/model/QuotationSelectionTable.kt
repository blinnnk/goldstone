package io.goldstone.blockchain.module.home.quotation.quotationsearch.model

import android.arch.persistence.room.*
import android.support.annotation.UiThread
import com.blinnnk.extension.isNull
import com.blinnnk.extension.orElse
import com.google.gson.annotations.SerializedName
import io.goldstone.blockchain.common.utils.load
import io.goldstone.blockchain.common.utils.then
import io.goldstone.blockchain.kernel.database.GoldStoneDataBase
import io.goldstone.blockchain.module.home.quotation.markettokendetail.view.PriceHistoryModel
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
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

		fun updatePrice(
			quotationData: QuotationSelectionTable,
			priceData: PriceHistoryModel?
		): QuotationSelectionTable {
			return quotationData.apply {
				priceData?.apply {
					high24 = dayHighest
					low24 = dayLow
					highTotal = totalHighest
					lowTotal = totalLow
				}
			}
		}

		fun insertSelection(table: QuotationSelectionTable) {
			doAsync {
				GoldStoneDataBase.database.quotationSelectionDao().apply {
					// 添加的时候赋予新的最大的 `orderID`
					getQuotationSelfSelections().let { it ->
						val currentID = it.maxBy { it.orderID }?.orderID
						val newOrderID = if (currentID.isNull()) 1.0 else currentID.orElse(0.0) + 1
						insert(table.apply { orderID = newOrderID })
					}
				}
			}
		}

		fun getSelectionByPair(pair: String, hold: (QuotationSelectionTable) -> Unit) {
			load {
				GoldStoneDataBase.database.quotationSelectionDao().getSelectionByPair(pair)
			} then {
				it?.let(hold)
			}
		}

		fun getMySelections(@UiThread hold: (List<QuotationSelectionTable>) -> Unit) {
			load { GoldStoneDataBase.database.quotationSelectionDao().getQuotationSelfSelections() } then (hold)
		}

		fun updateSelectionOrderIDBy(fromPair: String, newOrderID: Double, callback: () -> Unit) {
			doAsync {
				GoldStoneDataBase.database.quotationSelectionDao().updateOrderIDByPair(fromPair, newOrderID)
				uiThread { callback() }
			}
		}

		fun updateLineChartDataBy(pair: String, lineChart: String, callback: () -> Unit) {
			doAsync {
				GoldStoneDataBase.database.quotationSelectionDao().updateDayLineChartByPair(pair, lineChart)
				uiThread { callback() }
			}
		}

		fun updateLineChartWeekBy(pair: String, weekLineChart: String, callback: () -> Unit) {
			doAsync {
				GoldStoneDataBase.database.quotationSelectionDao().updateWeekLineChartByPair(pair, weekLineChart)
				uiThread { callback() }
			}
		}

		fun updateLineChartMontyBy(pair: String, monthChart: String, callback: () -> Unit) {
			doAsync {
				GoldStoneDataBase.database.quotationSelectionDao().updateMonthLineChartByPair(pair, monthChart)
				uiThread { callback() }
			}
		}

		fun updateLineChartHourBy(pair: String, hourChart: String, callback: () -> Unit) {
			doAsync {
				GoldStoneDataBase.database.quotationSelectionDao().updateHourLineChartByPair(pair, hourChart)
				uiThread { callback() }
			}
		}
	}
}

@Dao
interface QuotationSelectionDao {

	@Query("SELECT * FROM quotationSelection")
	fun getQuotationSelfSelections(): List<QuotationSelectionTable>

	@Query("SELECT * FROM quotationSelection WHERE pair LIKE :pair")
	fun getSelectionByPair(pair: String): QuotationSelectionTable?

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
