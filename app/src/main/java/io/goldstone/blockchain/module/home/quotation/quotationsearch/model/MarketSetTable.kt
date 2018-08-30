package io.goldstone.blockchain.module.home.quotation.quotationsearch.model

import android.arch.persistence.room.*
import android.widget.ImageView
import com.google.gson.annotations.SerializedName
import io.goldstone.blockchain.kernel.database.GoldStoneDataBase
import io.goldstone.blockchain.kernel.network.GoldStoneAPI
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.runOnUiThread

/**
 * @date: 2018/8/29.
 * @author: yanglihai
 * @description:
 */
@Entity(tableName = "marketSet") data class MarketSetTable(
	@PrimaryKey @SerializedName("market_id") var id: Int,
	@SerializedName("icon") var url: String,
	@SerializedName("market") var name: String,
	var status: Int = 0  // 1 选中，0 未选中
	) {
	
	companion object {
		fun insert(
			marketSetTable: List<MarketSetTable>,
			callback: () -> Unit
		) {
			doAsync {
				GoldStoneDataBase.database.marketSetTableDao().insertMarkets(marketSetTable)
				GoldStoneAPI.context.runOnUiThread { callback() }
			}
		}
		
		fun getAllMarkets(callback: (List<MarketSetTable>) -> Unit) {
			doAsync {
				val marketSet = GoldStoneDataBase.database.marketSetTableDao().queryAll()
				GoldStoneAPI.context.runOnUiThread { callback(marketSet) }
			}
		}
		
		fun getMarketsByStatus(
			status: Int,
			callback: (List<MarketSetTable>) -> Unit
		) {
			doAsync {
				val marketList = GoldStoneDataBase.database.marketSetTableDao().queryByStatus(status)
				GoldStoneAPI.context.runOnUiThread { callback(marketList) }
			}
		}
		
		fun updateStatusById(
			id: Int,
			status: Int
		) {
			doAsync {
				GoldStoneDataBase.database.marketSetTableDao().updateStatusById(id, status)
			}
		}
		
		
		fun clearData(callback: () -> Unit) {
			doAsync {
				GoldStoneDataBase.database.marketSetTableDao().clearData()
				GoldStoneAPI.context.runOnUiThread { callback() }
			}
		}
		
	}
	
}

@Dao interface MarketSetDao {
	@Query("select * from marketSet")
	fun queryAll(): List<MarketSetTable>
	
	@Query("select * from marketSet where status = :status")
	fun queryByStatus(status: Int): List<MarketSetTable>
	
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	fun insertMarkets(marketSet: List<MarketSetTable>)
	
	@Query("UPDATE marketSet SET status = :newStatus WHERE id = :rowId ")
	fun updateStatusById(
		rowId: Int,
		newStatus: Int
	)
	
	@Update
	fun update(marketSetTable: MarketSetTable)
	
	@Delete
	fun delete(marketSetTable: MarketSetTable)
	
	@Query("delete from marketSet")
	fun clearData()
	
}