package io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.model

import android.arch.persistence.room.*
import com.blinnnk.extension.safeGet
import com.blinnnk.extension.toArrayList
import com.blinnnk.util.coroutinesTask
import com.google.gson.annotations.SerializedName
import io.goldstone.blockchain.kernel.database.GoldStoneDataBase
import io.goldstone.blockchain.kernel.network.GoldStoneAPI
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenSearch.model.TokenSearchModel
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.runOnUiThread
import org.json.JSONObject

/**
 * @date 25/03/2018 5:11 PM
 * @author KaySaith
 */
enum class TinyNumber(val value: Int) {
	
	True(1),
	False(0)
}

@Entity(tableName = "defaultTokens")
data class DefaultTokenTable(
	@PrimaryKey(autoGenerate = true)
	var id: Int,
	@SerializedName("address")
	var contract: String,
	@SerializedName("url")
	var iconUrl: String,
	@SerializedName("symbol")
	var symbol: String,
	@SerializedName("force_show")
	var forceShow: Int,
	@SerializedName("price")
	var price: Double,
	@SerializedName("name")
	var name: String,
	@SerializedName("decimals")
	var decimals: Double,
	var totalSupply: String? = null,
	// 个人通过 `Contract` 搜索到的, 和 `Server` 与 `Local` Json 数据都不同的部分.
	var isDefault: Boolean = true,
	@Ignore
	var isUsed: Boolean = false,
	@SerializedName("weight")
	var weight: Int
) {
	
	/** 默认的 `constructor` */
	@Ignore
	constructor() : this(
		0,
		"",
		"",
		"",
		0,
		0.0,
		"",
		0.0,
		"",
		true,
		false,
		0
	)
	
	constructor(
		data: TokenSearchModel,
		isUsed: Boolean = false
	) : this(
		0,
		data.contract,
		data.iconUrl,
		data.symbol,
		0,
		data.price.toDouble(),
		data.name,
		data.decimal.toDouble(),
		"",
		isUsed,
		isUsed,
		data.weight
	)
	
	constructor(
		localData: JSONObject,
		isUsed: Boolean = false
	) : this(
		0,
		localData.safeGet("address"),
		localData.safeGet("url"),
		localData.safeGet("symbol"),
		localData.safeGet("force_show").toInt(),
		localData.safeGet("price").toDouble(),
		localData.safeGet("name"),
		localData.safeGet("decimals").toDouble(),
		localData.safeGet("total_supply"),
		localData.safeGet("is_default").toInt() == TinyNumber.True.value,
		isUsed,
		if (localData.safeGet("weight").isEmpty()) 0
		else localData.safeGet("weight").toInt()
	)
	
	constructor(
		contract: String,
		symbol: String,
		decimals: Double
	) : this(
		0,
		contract,
		"",
		symbol,
		0,
		0.0,
		"",
		decimals,
		"",
		false,
		false,
		0
	)
	
	companion object {
		
		fun getTokens(hold: (ArrayList<DefaultTokenTable>) -> Unit) {
			coroutinesTask(
				{
					GoldStoneDataBase.database.defaultTokenDao()
						.getAllTokens()
				}) {
				hold(it.toArrayList())
			}
		}
		
		fun getDefaultTokens(hold: (ArrayList<DefaultTokenTable>) -> Unit) {
			coroutinesTask(
				{
					GoldStoneDataBase.database.defaultTokenDao()
						.getDefaultTokens()
				}) {
				hold(it.toArrayList())
			}
		}
		
		fun getTokenByContract(
			contract: String,
			hold: (DefaultTokenTable?) -> Unit
		) {
			coroutinesTask(
				{
					GoldStoneDataBase.database.defaultTokenDao().getTokenByContract(contract)
				}) {
				hold(it)
			}
		}
		
		fun updateTokenPrice(
			contract: String,
			newPrice: Double,
			callback: () -> Unit = {}
		) {
			doAsync {
				GoldStoneDataBase.database.defaultTokenDao()
					.apply {
						getTokenByContract(contract)?.let {
							update(it.apply { price = newPrice })
							GoldStoneAPI.context.runOnUiThread { callback() }
						}
					}
			}
		}
		
		fun updateTokenDefaultStatus(
			contract: String,
			isDefault: Boolean,
			callback: () -> Unit = {}
		) {
			doAsync {
				GoldStoneDataBase.database.defaultTokenDao()
					.apply {
						getTokenByContract(contract)?.let {
							update(it.apply { this.isDefault = isDefault })
							GoldStoneAPI.context.runOnUiThread { callback() }
						}
					}
			}
		}
		
		fun insertToken(
			token: DefaultTokenTable,
			callback: () -> Unit
		) {
			coroutinesTask(
				{ GoldStoneDataBase.database.defaultTokenDao().insert(token) }
			) {
				callback()
			}
		}
	}
}

@Dao
interface DefaultTokenDao {
	
	@Query("SELECT * FROM defaultTokens")
	fun getAllTokens(): List<DefaultTokenTable>
	
	@Query("SELECT * FROM defaultTokens WHERE isDefault LIKE :isDefault")
	fun getDefaultTokens(isDefault: Boolean = true): List<DefaultTokenTable>
	
	@Query("SELECT * FROM defaultTokens WHERE contract LIKE :contract")
	fun getTokenByContract(contract: String): DefaultTokenTable?
	
	@Insert
	fun insert(token: DefaultTokenTable)
	
	@Update
	fun update(token: DefaultTokenTable)
	
	@Delete
	fun delete(token: DefaultTokenTable)
}