package io.goldstone.blockchain.module.home.quotation.quotationsearch.model

import android.arch.persistence.room.*
import com.google.gson.annotations.SerializedName
import io.goldstone.blockchain.common.utils.load
import io.goldstone.blockchain.common.utils.then
import io.goldstone.blockchain.kernel.database.GoldStoneDataBase
import io.goldstone.blockchain.kernel.network.GoldStoneAPI
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.runOnUiThread

/**
 * @date: 2018/8/29.
 * @author: yanglihai
 * @description:
 */
@Entity(tableName = "exchangeTable")
data class ExchangeTable(
	@PrimaryKey(autoGenerate = true)
	var id: Int,
	@SerializedName("market_id")
	var exchangeId: Int,
	@SerializedName("icon")
	var iconUrl: String,
	@SerializedName("market")
	var exchangeName: String,
	var isSelected: Boolean = false
) {
	
	companion object {
		fun insertAll(
			exchangeTables: List<ExchangeTable>
		) {
			doAsync {
				GoldStoneDataBase.database.exchangeTableDao().insertAll(exchangeTables)
			}
		}
		
		fun insert(exchangeTable: ExchangeTable) {
			doAsync { GoldStoneDataBase.database.exchangeTableDao().insert(exchangeTable) }
		}
		
		
		fun getMarketsBySelectedStatus(
			isSelected: Boolean,
			hold: (List<ExchangeTable>) -> Unit
		) {
			doAsync {
				val marketList = GoldStoneDataBase.database.exchangeTableDao().getByStatus(isSelected)
				GoldStoneAPI.context.runOnUiThread { hold(marketList) }
			}
		}
		
		fun update(exchangeTable: ExchangeTable) {
			doAsync {
				GoldStoneDataBase.database.exchangeTableDao().update(exchangeTable)
			}
		}
		
		fun updateSelectedStatusById(
			id: Int,
			isSelected: Boolean
		) {
			doAsync {
				GoldStoneDataBase.database.exchangeTableDao().updateSelectedStatusByExchangeId(id, isSelected)
			}
		}
		
		fun delete( exchangeTable: ExchangeTable) {
			doAsync {
				GoldStoneDataBase.database.exchangeTableDao().delete(exchangeTable)
			}
		}
		
		fun deleteAll(callback:() -> Unit) {
			doAsync {
				GoldStoneDataBase.database.exchangeTableDao().deleteAll()
				callback()
			}
		}
		
	}
	
}

@Dao interface ExchangeDao {
	@Query("select * from exchangeTable")
	fun getAll(): List<ExchangeTable>
	
	@Query("select * from exchangeTable where isSelected = :isSelected")
	fun getByStatus(isSelected: Boolean): List<ExchangeTable>
	
	@Insert
	fun insertAll(exchange: List<ExchangeTable>)
	
	@Insert
	fun insert(exchangeTable: ExchangeTable)
	
	@Query("UPDATE exchangeTable SET isSelected = :isSelected WHERE exchangeId = :id ")
	fun updateSelectedStatusByExchangeId(
		id: Int,
		isSelected: Boolean
	)
	
	@Update
	fun update(exchangeTable: ExchangeTable)
	
	@Delete
	fun delete(exchangeTable: ExchangeTable)
	
	@Query("delete from exchangeTable ")
	fun deleteAll()
	
}