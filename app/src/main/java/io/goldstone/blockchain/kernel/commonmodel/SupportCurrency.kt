package io.goldstone.blockchain.kernel.commonmodel

import android.arch.persistence.room.*
import com.blinnnk.extension.toArrayList
import com.blinnnk.util.coroutinesTask
import io.goldstone.blockchain.common.utils.safeGet
import io.goldstone.blockchain.kernel.database.GoldStoneDataBase
import io.goldstone.blockchain.kernel.network.GoldStoneAPI
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.runOnUiThread
import org.json.JSONObject
import javax.security.auth.callback.Callback

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
	var isUsed: Boolean
) {

	constructor() : this(
		0,
		"",
		"",
		false
	)

	constructor(data: JSONObject) : this(
		0,
		data.safeGet("countrySymbol"),
		data.safeGet("currencySymbol"),
		data.safeGet("isUsed").toBoolean()
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
			coroutinesTask({
				GoldStoneDataBase.database.currencyDao()
					.getSupportCurrencies()
			}) {
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

	@Insert
	fun insert(token: SupportCurrencyTable)

	@Update
	fun update(token: SupportCurrencyTable)

	@Delete
	fun delete(token: SupportCurrencyTable)
}