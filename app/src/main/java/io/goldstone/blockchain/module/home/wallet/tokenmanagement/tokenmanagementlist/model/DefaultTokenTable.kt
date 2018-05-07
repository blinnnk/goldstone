package io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.model

import android.arch.persistence.room.*
import com.blinnnk.extension.forEachOrEnd
import com.blinnnk.extension.toArrayList
import com.blinnnk.util.coroutinesTask
import com.google.gson.annotations.SerializedName
import io.goldstone.blockchain.kernel.database.GoldStoneDataBase
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenSearch.model.TokenSearchModel

/**
 * @date 25/03/2018 5:11 PM
 * @author KaySaith
 */

enum class TinyNumber(val value: Int) {
	True(1), False(0)
}

@Entity(tableName = "defaultTokens")
data class DefaultTokenTable(
	@PrimaryKey(autoGenerate = true) var id: Int, @SerializedName("address") var contract: String,
	@SerializedName("url") var iconUrl: String, @SerializedName("symbol") var symbol: String,
	@SerializedName("force_show") var forceShow: Int, @SerializedName("price") var price: Double,
	@SerializedName("name") var name: String, @SerializedName("decimals")
	var decimals: Double, var totalSupply: String? = null, var isDefault: Boolean = true, @Ignore
	var isUsed: Boolean = false
) {
	/** 默认的 `constructor` */
	constructor() : this(
		0, "", "", "", 0, 0.0, "", 0.0, "", true, false
	)

	constructor(data: TokenSearchModel, isUsed: Boolean = false) : this(
		0,
		data.contract,
		data.iconUrl,
		data.symbol,
		0,
		data.price.toDouble(),
		data.name,
		data.decimal.toDouble(),
		"",
		false,
		isUsed
	)

	companion object {

		fun getTokens(hold: (ArrayList<DefaultTokenTable>) -> Unit) {
			coroutinesTask({
				GoldStoneDataBase.database.defaultTokenDao().getAllTokens()
			}) {
				hold(it.toArrayList())
			}
		}

		fun forEachDefaultTokensToEnd(hold: (token: DefaultTokenTable, isEnd: Boolean) -> Unit) {
			getTokens {
				it.forEachOrEnd { item, isEnd ->
					hold(item, isEnd)
				}
			}
		}

		fun getTokenBySymbol(symbol: String, hold: (DefaultTokenTable) -> Unit) {
			coroutinesTask({
				GoldStoneDataBase.database.defaultTokenDao().getTokenBySymbol(symbol)
			}) {
				hold(it)
			}
		}

		fun getTokenByContractAddress(contractAddress: String, hold: (DefaultTokenTable?) -> Unit) {
			coroutinesTask({
				GoldStoneDataBase.database.defaultTokenDao().getSymbolByContractAddress(contractAddress)
			}) {
				hold(it)
			}
		}

		fun updateUsedStatusBySymbol(symbol: String, status: Boolean, callback: () -> Unit = {}) {
			coroutinesTask({
				GoldStoneDataBase.database.defaultTokenDao().apply {
					getTokenBySymbol(symbol).let {
						update(it.apply { isUsed = status })
					}
				}
			}) {
				callback()
			}
		}

		fun getContractAddressBySymbol(symbol: String, hold: (String) -> Unit) {
			coroutinesTask({
				GoldStoneDataBase.database.defaultTokenDao().getTokenBySymbol(symbol)
			}) {
				hold(it.contract)
			}
		}

		fun insertTokenInfo(token: DefaultTokenTable, callback: () -> Unit) {
			coroutinesTask({
				GoldStoneDataBase.database.defaultTokenDao().insert(token)
			}) {
				callback()
			}
		}
	}
}

@Dao
interface DefaultTokenDao {
	@Query("SELECT * FROM defaultTokens")
	fun getAllTokens(): List<DefaultTokenTable>

	@Query("SELECT * FROM defaultTokens WHERE symbol LIKE :symbol")
	fun getTokenBySymbol(symbol: String): DefaultTokenTable

	@Query("SELECT * FROM defaultTokens WHERE contract LIKE :contract")
	fun getSymbolByContractAddress(contract: String): DefaultTokenTable?

	@Insert
	fun insert(token: DefaultTokenTable)

	@Update
	fun update(token: DefaultTokenTable)

	@Delete
	fun delete(token: DefaultTokenTable)
}