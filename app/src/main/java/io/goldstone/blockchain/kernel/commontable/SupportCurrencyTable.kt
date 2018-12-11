package io.goldstone.blockchain.kernel.commontable

import android.arch.persistence.room.*
import com.blinnnk.extension.safeGet
import com.blinnnk.util.load
import com.blinnnk.util.then
import io.goldstone.blockchain.kernel.database.GoldStoneDataBase
import org.json.JSONObject
import java.io.Serializable

/**
 * @date 2018/5/11 2:15 PM
 * @author KaySaith
 */
@Entity(tableName = "supportCurrency")
data class SupportCurrencyTable(
	@PrimaryKey(autoGenerate = true)
	var id: Int,
	var countrySymbol: String,
	var currencySymbol: String,
	var isUsed: Boolean,
	var rate: Double
): Serializable {

	@Ignore
	constructor() : this(
		0,
		"",
		"",
		false,
		0.0
	)

	constructor(data: JSONObject) : this(
		0,
		data.safeGet("countrySymbol"),
		data.safeGet("currencySymbol"),
		data.safeGet("isUsed").toBoolean(),
		data.safeGet("rate").toDouble()
	)

	companion object {

		@JvmField
		val dao = GoldStoneDataBase.database.currencyDao()

		fun getSupportCurrencies(hold: (List<SupportCurrencyTable>) -> Unit) {
			load { dao.getSupportCurrencies() } then (hold)
		}
	}
}

@Dao
interface SupportCurrencyDao {

	@Query("SELECT * FROM supportCurrency")
	fun getSupportCurrencies(): List<SupportCurrencyTable>

	@Query("SELECT count(*) FROM supportCurrency")
	fun rowCount(): Int

	@Query("SELECT * FROM supportCurrency WHERE isUsed = :isUsed")
	fun getCurrentCurrency(isUsed: Boolean = true): SupportCurrencyTable

	@Query("UPDATE supportCurrency set isUsed = :unused WHERE isUsed = :isUsed")
	fun setCurrentCurrencyUnused(unused: Boolean = false, isUsed: Boolean = true)

	@Query("UPDATE supportCurrency set isUsed = :isUsed WHERE currencySymbol = :symbol")
	fun setCurrencyInUse(symbol: String, isUsed: Boolean = true)

	@Query("SELECT * FROM supportCurrency WHERE currencySymbol LIKE :symbol")
	fun getCurrencyBySymbol(symbol: String): SupportCurrencyTable?

	@Query("UPDATE supportCurrency SET rate = :rate WHERE isUsed LIKE :isUsed")
	fun updateUsedRate(rate: Double, isUsed: Boolean = true)

	@Insert
	fun insert(token: SupportCurrencyTable)

	@Insert
	fun insertAll(tokens: List<SupportCurrencyTable>)

	@Update
	fun update(token: SupportCurrencyTable)

	@Delete
	fun delete(token: SupportCurrencyTable)
}