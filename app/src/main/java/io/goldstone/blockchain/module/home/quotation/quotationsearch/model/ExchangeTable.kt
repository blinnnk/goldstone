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
	@PrimaryKey
	@SerializedName("market_id")
	var id: Int,
	@SerializedName("icon")
	var iconUrl: String,
	@SerializedName("market")
	var exchangeName: String,
	var isSelected: Boolean = false
	) {
	
	companion object {
		fun insertOrReplace(
			exchangeTables: List<ExchangeTable>,
			callback: () -> Unit
		) {
			load {
				GoldStoneDataBase.database.exchangeTableDao().insertOrReplace(exchangeTables)
			} then {
				GoldStoneAPI.context.runOnUiThread { callback() }
			}
		}
		
		fun getAll(hold: (List<ExchangeTable>) -> Unit) {
			doAsync {
				val exchangeSet = GoldStoneDataBase.database.exchangeTableDao().getAll()
				hold(exchangeSet)
			}
		}
		
		fun getMarketsBySelectedStatus(
			isSelected: Boolean,
			hold: (List<ExchangeTable>) -> Unit
		) {
			doAsync {
				val marketList = GoldStoneDataBase.database.exchangeTableDao().queryByStatus(isSelected)
				GoldStoneAPI.context.runOnUiThread { hold(marketList) }
			}
		}
		
		fun updateSelectedStatusById(
			id: Int,
			isSelected: Boolean
		) {
			doAsync {
				GoldStoneDataBase.database.exchangeTableDao().updateSelectedStatusById(id, isSelected)
			}
		}
		
		fun delete( exchangeTable: ExchangeTable) {
			doAsync {
				GoldStoneDataBase.database.exchangeTableDao().delete(exchangeTable)
			}
		}
		
	}
	
}

@Dao interface ExchangeDao {
	@Query("select * from exchangeTable")
	fun getAll(): List<ExchangeTable>
	
	@Query("select * from exchangeTable where isSelected = :isSelected")
	fun queryByStatus(isSelected: Boolean): List<ExchangeTable>
	
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	fun insertOrReplace(exchange: List<ExchangeTable>)
	
	@Query("UPDATE exchangeTable SET isSelected = :isSelected WHERE id = :id ")
	fun updateSelectedStatusById(
		id: Int,
		isSelected: Boolean
	)
	
	@Update
	fun update(exchangeTable: ExchangeTable)
	
	@Delete
	fun delete(exchangeTable: ExchangeTable)
	
}