package io.goldstone.blockchain.kernel.commonmodel

import android.arch.persistence.room.*
import com.blinnnk.extension.safeGet
import com.blinnnk.extension.toArrayList
import io.goldstone.blockchain.common.utils.load
import io.goldstone.blockchain.common.utils.then
import io.goldstone.blockchain.kernel.database.GoldStoneDataBase
import io.goldstone.blockchain.kernel.network.common.GoldStoneAPI
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.runOnUiThread
import org.json.JSONObject

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
) {

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

		fun updateUsedStatus(symbol: String, callback: () -> Unit) {
			doAsync {
				GoldStoneDataBase.database.currencyDao()
					.apply {
						getSupportCurrencies().find { it.isUsed }
							?.let {
								update(it.apply { isUsed = false })
							}
						getCurrencyBySymbol(symbol)?.let {
							update(it.apply { isUsed = true })
						}
						GoldStoneAPI.context.runOnUiThread { callback() }
					}
			}
		}

		fun getSupportCurrencies(hold: (ArrayList<SupportCurrencyTable>) -> Unit) {
			load {
				GoldStoneDataBase.database.currencyDao().getSupportCurrencies()
			} then {
				hold(it.toArrayList())
			}
		}
	}
}

@Dao
interface SupportCurrencyDao {

	@Query("SELECT * FROM supportCurrency")
	fun getSupportCurrencies(): List<SupportCurrencyTable>

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