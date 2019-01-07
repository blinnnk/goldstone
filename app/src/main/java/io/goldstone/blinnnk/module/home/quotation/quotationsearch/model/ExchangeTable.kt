package io.goldstone.blinnnk.module.home.quotation.quotationsearch.model

import android.arch.persistence.room.*
import com.blinnnk.extension.orZero
import com.blinnnk.extension.safeGet
import com.google.gson.annotations.SerializedName
import io.goldstone.blinnnk.kernel.database.GoldStoneDataBase
import org.json.JSONObject

/**
 * @date: 2018/8/29.
 * @author: yanglihai
 */
@Entity(tableName = "exchangeTable", primaryKeys = ["marketId"])
data class ExchangeTable(
	@SerializedName("market_id")
	var marketId: Int,
	@SerializedName("icon")
	var iconUrl: String,
	@SerializedName("market")
	var exchangeName: String,
	var isSelected: Boolean = false
) {

	constructor(data: ExchangeTable) : this(
		data.marketId,
		data.iconUrl,
		data.exchangeName,
		false
	)

	constructor(localData: JSONObject) : this(
		localData.safeGet("market_id").toInt().orZero(),
		localData.safeGet("icon"),
		localData.safeGet("market"),
		false
	)

	companion object {
		@JvmField
		val dao = GoldStoneDataBase.database.exchangeTableDao()
	}

}

@Dao
interface ExchangeDao {
	@Query("select * from exchangeTable")
	fun getAll(): List<ExchangeTable>

	@Query("SELECT count(*) FROM exchangeTable")
	fun rowCount(): Int

	@Query("select * from exchangeTable where isSelected = 1")
	fun getSelected(): List<ExchangeTable>

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	fun insertAll(exchange: List<ExchangeTable>)

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	fun insert(exchangeTable: ExchangeTable)

	@Query("UPDATE exchangeTable SET isSelected = :isSelected WHERE marketId = :id ")
	fun updateSelectedStatus(id: Int, isSelected: Boolean)

	@Update
	fun update(exchangeTable: ExchangeTable)

	@Delete
	fun delete(exchangeTable: ExchangeTable)

}