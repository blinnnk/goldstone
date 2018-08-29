package io.goldstone.blockchain.module.home.quotation.quotationsearch.model

import android.arch.persistence.room.*
import android.support.annotation.WorkerThread
import com.blinnnk.extension.isNull
import com.blinnnk.extension.orElse
import com.google.gson.annotations.SerializedName
import io.goldstone.blockchain.common.utils.load
import io.goldstone.blockchain.common.utils.then
import io.goldstone.blockchain.kernel.database.GoldStoneDataBase
import io.goldstone.blockchain.kernel.network.GoldStoneAPI
import io.goldstone.blockchain.module.home.quotation.markettokendetail.view.PriceHistoryModel
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

		fun insertSelection(
			table: QuotationSelectionTable,
			callback: () -> Unit
		) {
			doAsync {
				GoldStoneDataBase.database.quotationSelectionDao().apply {
					// 添加的时候赋予新的最大的 `orderID`
					getQuotationSelfSelections().let { it ->
						val currentID = it.maxBy { it.orderID }?.orderID
						val newOrderID = if (currentID.isNull()) 1.0 else currentID.orElse(0.0) + 1
						insert(table.apply {
							orderID = newOrderID
						})
						GoldStoneAPI.context.runOnUiThread { callback() }
					}
				}
			}
		}

		fun getSelectionByPair(
			pair: String,
			hold: (QuotationSelectionTable) -> Unit
		) {
			load {
				GoldStoneDataBase.database.quotationSelectionDao().getSelectionByPair(pair)
			} then {
				it?.let(hold)
			}
		}

		fun removeSelectionBy(
			pair: String,
			callback: () -> Unit = {}
		) {
			doAsync {
				GoldStoneDataBase.database.quotationSelectionDao().apply {
					getSelectionByPair(pair).let {
						if (it.isNull()) {
							GoldStoneAPI.context.runOnUiThread { callback() }
						} else {
							delete(it!!)
							GoldStoneAPI.context.runOnUiThread { callback() }
						}
					}
				}
			}
		}

		fun getMySelections(@WorkerThread hold: (List<QuotationSelectionTable>) -> Unit) {
			doAsync {
				hold(GoldStoneDataBase.database.quotationSelectionDao().getQuotationSelfSelections())
			}
		}

		fun updateSelectionOrderIDBy(
			fromPair: String, newOrderID: Double, callback: () -> Unit = {}
		) {
			doAsync {
				GoldStoneDataBase.database.quotationSelectionDao().apply {
					getSelectionByPair(fromPair)?.let {
						update(it.apply {
							this.orderID = newOrderID
						})
						GoldStoneAPI.context.runOnUiThread {
							callback()
						}
					}
				}
			}
		}

		fun updateLineChartDataBy(
			pair: String, lineChart: String, callback: () -> Unit = {}
		) {
			doAsync {
				GoldStoneDataBase.database.quotationSelectionDao().apply {
					getSelectionByPair(pair)?.let {
						update(it.apply {
							this.lineChartDay = lineChart
						})
						GoldStoneAPI.context.runOnUiThread {
							callback()
						}
					}
				}
			}
		}

		fun updateLineChartWeekBy(
			pair: String, chartData: String, callback: () -> Unit = {}
		) {
			doAsync {
				GoldStoneDataBase.database.quotationSelectionDao().apply {
					getSelectionByPair(pair)?.let {
						update(it.apply {
							this.lineChartWeek = chartData
						})
						GoldStoneAPI.context.runOnUiThread {
							callback()
						}
					}
				}
			}
		}

		fun updateLineChartMontyBy(
			pair: String, chartData: String, callback: () -> Unit = {}
		) {
			doAsync {
				GoldStoneDataBase.database.quotationSelectionDao().apply {
					getSelectionByPair(pair)?.let {
						update(it.apply {
							this.lineChartMonth = chartData
						})
						GoldStoneAPI.context.runOnUiThread {
							callback()
						}
					}
				}
			}
		}

		fun updateLineChartHourBy(
			pair: String, chartData: String, callback: () -> Unit = {}
		) {
			doAsync {
				GoldStoneDataBase.database.quotationSelectionDao().apply {
					getSelectionByPair(pair)?.let {
						update(it.apply {
							this.lineChartHour = chartData
						})
						GoldStoneAPI.context.runOnUiThread {
							callback()
						}
					}
				}
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

	@Query("SELECT * FROM quotationSelection WHERE orderID LIKE :orderID")
	fun getSelectionByOrderID(orderID: Int): QuotationSelectionTable?

	@Insert
	fun insert(table: QuotationSelectionTable)

	@Update
	fun update(table: QuotationSelectionTable)

	@Delete
	fun delete(table: QuotationSelectionTable)
}
