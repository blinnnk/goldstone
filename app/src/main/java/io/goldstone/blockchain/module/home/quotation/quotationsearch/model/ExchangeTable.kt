package io.goldstone.blockchain.module.home.quotation.quotationsearch.model

import android.arch.persistence.room.*
import com.blinnnk.extension.orZero
import com.blinnnk.extension.safeGet
import com.google.gson.annotations.SerializedName
import io.goldstone.blockchain.kernel.database.GoldStoneDataBase
import io.goldstone.blockchain.kernel.network.common.GoldStoneAPI
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.runOnUiThread
import org.json.JSONObject

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
	var marketId: Int,
	@SerializedName("icon")
	var iconUrl: String,
	@SerializedName("market")
	var exchangeName: String,
	var isSelected: Boolean = false
) {
	constructor(
		localData: JSONObject
	) : this(
		0,
		localData.safeGet("market_id").toInt().orZero(),
		localData.safeGet("icon"),
		localData.safeGet("market"),
		false
	)
	companion object {
		
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
	
	@Query("UPDATE exchangeTable SET isSelected = :isSelected WHERE marketId = :id ")
	fun updateSelectedStatusByExchangeId(id: Int, isSelected: Boolean)
	
	@Update
	fun update(exchangeTable: ExchangeTable)
	
	@Delete
	fun delete(exchangeTable: ExchangeTable)
	
	@Query("delete from exchangeTable ")
	fun deleteAll()
	
}