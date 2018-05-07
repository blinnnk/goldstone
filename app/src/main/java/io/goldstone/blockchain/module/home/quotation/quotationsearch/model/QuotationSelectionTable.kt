package io.goldstone.blockchain.module.home.quotation.quotationsearch.model

import android.arch.persistence.room.*
import com.blinnnk.extension.isNull
import com.blinnnk.extension.orElse
import com.blinnnk.extension.toArrayList
import com.blinnnk.util.coroutinesTask
import com.google.gson.annotations.SerializedName
import io.goldstone.blockchain.common.utils.toUpperCaseFirstLetter
import io.goldstone.blockchain.kernel.database.GoldStoneDataBase
import io.goldstone.blockchain.kernel.network.GoldStoneAPI
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.runOnUiThread

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
	var baseSymnbol: String,
	@SerializedName("quote")
	var quoteSymbol: String,
	@SerializedName("pair")
	var pair: String,
	@SerializedName("market")
	var market: String,
	@SerializedName("name")
	var name: String,
	var infoTitle: String,
	var orderID: Double = 0.0,
	var lineChart: String,
	var isSelecting: Boolean = false
) {
	constructor(data: QuotationSelectionTable, lineChart: String) : this(
		0,
		data.marketID,
		data.pairDisplay,
		data.baseSymnbol,
		data.quoteSymbol,
		data.pair,
		data.market,
		data.name,
		data.pairDisplay + " " + data.market.toUpperCaseFirstLetter(),
		data.orderID,
		lineChart,
		data.isSelecting
	)

	companion object {
		fun insertSelection(table: QuotationSelectionTable, callback: () -> Unit) {
			doAsync {
				GoldStoneDataBase.database.quotationSelectionDao().apply {
					// 添加的时候赋予新的最大的 `orderID`
					getQuotationSelfSelections().let {
						val currentID = it.maxBy { it.orderID }?.orderID
						val newOrderID = if (currentID.isNull()) 1.0 else currentID.orElse(0.0) + 1
						insert(table.apply { orderID = newOrderID })
						GoldStoneAPI.context.runOnUiThread { callback() }
					}
				}
			}
		}

		fun removeSelectionBy(pair: String, callback: () -> Unit = {}) {
			doAsync {
				GoldStoneDataBase.database.quotationSelectionDao().apply {
					getSelectionByPair(pair)?.let {
						delete(it)
						GoldStoneAPI.context.runOnUiThread { callback() }
					}
				}
			}
		}

		fun getMySelections(hold: (ArrayList<QuotationSelectionTable>) -> Unit) {
			coroutinesTask({
				GoldStoneDataBase.database.quotationSelectionDao().getQuotationSelfSelections()
			}) {
				hold(it.toArrayList())
			}
		}

		fun updateSelectionOrderIDBy(fromPair: String, newOrderID: Double, callback: () -> Unit = {}) {
			doAsync {
				GoldStoneDataBase.database.quotationSelectionDao().apply {
					getSelectionByPair(fromPair)?.let {
						update(it.apply { this.orderID = newOrderID })
						GoldStoneAPI.context.runOnUiThread {
							callback()
						}
					}
				}
			}
		}

		fun updateLineChartDataBy(pair: String, lineChart: String, callback: () -> Unit = {}) {
			doAsync {
				GoldStoneDataBase.database.quotationSelectionDao().apply {
					getSelectionByPair(pair)?.let {
						update(it.apply { this.lineChart = lineChart })
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