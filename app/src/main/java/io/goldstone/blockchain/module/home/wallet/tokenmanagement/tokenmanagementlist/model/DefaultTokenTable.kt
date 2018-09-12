package io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.model

import android.arch.persistence.room.*
import com.blinnnk.extension.orZero
import com.blinnnk.extension.safeGet
import com.blinnnk.extension.toArrayList
import com.blinnnk.util.TinyNumberUtils
import com.google.gson.annotations.SerializedName
import io.goldstone.blockchain.common.utils.load
import io.goldstone.blockchain.common.utils.then
import io.goldstone.blockchain.common.value.ChainID
import io.goldstone.blockchain.common.value.Config
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
@Entity(tableName = "defaultTokens")
data class DefaultTokenTable(
	@PrimaryKey(autoGenerate = true)
	var id: Int,
	@SerializedName("_id")
	var serverTokenID: String,
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
	@SerializedName("weight")
	var weight: Int,
	var chain_id: String,
	var description: String = "",
	var exchange: String = "",
	var whitePaper: String = "",
	var socialMedia: String = "",
	var startDate: String = "",
	var website: String = "",
	var rank: String = "",
	var marketCap: String = "",
	@Ignore
	var isUsed: Boolean = false
) {

	/** 默认的 `constructor` */
	constructor() : this(
		0,
		"",
		"",
		"",
		"",
		0,
		0.0,
		"",
		0.0,
		"",
		true,
		0,
		Config.getCurrentChain()
	)

	constructor(
		data: TokenSearchModel,
		isDefault: Boolean = false
	) : this(
		0,
		"",
		data.contract,
		data.iconUrl.orEmpty(),
		data.symbol,
		0,
		data.price.toDoubleOrNull().orZero(),
		data.name,
		data.decimal.toDouble(),
		"",
		isDefault,
		data.weight,
		Config.getCurrentChain()
	)

	constructor(
		localData: JSONObject
	) : this(
		0,
		"",
		localData.safeGet("address"),
		localData.safeGet("url"),
		localData.safeGet("symbol"),
		localData.safeGet("force_show").toInt(),
		localData.safeGet("price").toDouble(),
		localData.safeGet("name"),
		localData.safeGet("decimals").toDouble(),
		localData.safeGet("total_supply"),
		TinyNumberUtils.isTrue(localData.safeGet("is_default")),
		if (localData.safeGet("weight").isEmpty()) 0
		else localData.safeGet("weight").toInt(),
		localData.safeGet("chain_id"),
		localData.safeGet("description"),
		localData.safeGet("website"),
		localData.safeGet("exchange"),
		localData.safeGet("white_paper"),
		localData.safeGet("social_media"),
		localData.safeGet("start_date")
	)

	constructor(data: CoinInfoModel) : this(
		0,
		"",
		data.contract,
		"",
		data.symbol,
		0,
		0.0,
		"",
		0.0,
		data.supply,
		false,
		0,
		data.chainID,
		"${Config.getCurrentLanguageCode()}${data.description}",
		data.exchange,
		data.whitePaper,
		data.socialMedia,
		data.startDate,
		data.website,
		data.rank,
		data.marketCap
	)

	@Ignore
	constructor(
		contract: String,
		symbol: String,
		decimals: Double
	) : this(
		0,
		"",
		contract,
		"",
		symbol,
		0,
		0.0,
		"",
		decimals,
		"",
		false,
		0,
		ChainID.getChainIDBySymbol(symbol)
	)

	companion object {

		fun getAllTokens(hold: (ArrayList<DefaultTokenTable>) -> Unit) {
			load {
				GoldStoneDataBase.database.defaultTokenDao().getAllTokens()
			} then {
				hold(it.toArrayList())
			}
		}

		fun getCurrentChainTokens(hold: (List<DefaultTokenTable>) -> Unit) {
			load {
				GoldStoneDataBase.database.defaultTokenDao().getCurrentChainTokens()
			} then(hold)
		}

		fun getDefaultTokens(hold: (ArrayList<DefaultTokenTable>) -> Unit) {
			load {
				GoldStoneDataBase.database.defaultTokenDao().getDefaultTokens()
			} then {
				hold(it.toArrayList())
			}
		}

		fun getTokenBySymbolAndContractFromAllChains(
			symbol: String,
			contract: String,
			hold: (DefaultTokenTable?) -> Unit
		) {
			load {
				GoldStoneDataBase
					.database
					.defaultTokenDao()
					.getTokenBySymbolAndContractFromAllChains(symbol, contract)
			} then {
				if (it.isNotEmpty()) {
					hold(it[0])
				} else {
					hold(null)
				}
			}
		}

		fun getCurrentChainToken(
			contract: String,
			hold: (DefaultTokenTable?) -> Unit
		) {
			load {
				GoldStoneDataBase
					.database
					.defaultTokenDao()
					.getCurrentChainTokenByContract(contract)
			} then (hold)
		}

		fun updateOrInsertCoinInfo(
			data: CoinInfoModel,
			callback: () -> Unit
		) {
			doAsync {
				GoldStoneDataBase.database.defaultTokenDao()
					.apply {
						getTokenBySymbolAndContractFromAllChains(data.symbol, data.contract).let {
							if (it.isEmpty()) {
								insert(DefaultTokenTable(data))
								callback()
							} else {
								// 插入行情的 `TokenInformation` 只需要插入主链数据即可
								it.filterNot { default ->
									ChainID.getTestChains().any { it.equals(default.chain_id, true) }
								}[0].let {
									update(it.apply {
										exchange = data.exchange
										website = data.website
										marketCap = data.marketCap
										whitePaper = data.whitePaper
										socialMedia = data.socialMedia
										rank = data.rank
										totalSupply = data.supply
										startDate = data.startDate
										description = "${Config.getCurrentLanguageCode()}${data.description}"
									})
								}
								callback()
							}
						}
					}
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
						getCurrentChainTokenByContract(contract)?.let {
							update(it.apply { price = newPrice })
							GoldStoneAPI.context.runOnUiThread { callback() }
						}
					}
			}
		}

		fun updateTokenName(contract: String, name: String) {
			doAsync {
				GoldStoneDataBase.database.defaultTokenDao()
					.apply {
						getCurrentChainTokenByContract(contract)?.let {
							update(it.apply { this.name = name })
						}
					}
			}
		}

		fun updateTokenDefaultStatus(
			contract: String,
			isDefault: Boolean,
			name: String,
			callback: () -> Unit
		) {
			doAsync {
				GoldStoneDataBase.database.defaultTokenDao()
					.apply {
						getCurrentChainTokenByContract(contract)?.let {
							update(it.apply {
								this.isDefault = isDefault
								this.name = name
							})
							callback()
						}
					}
			}
		}

		fun updateDefaultStatusInCurrentChain(
			contract: String,
			symbol: String,
			isDefault: Boolean
		) {
			GoldStoneDataBase.database.defaultTokenDao().apply {
				getTokenBySymbolContractAndChainID(
					symbol,
					contract,
					Config.getCurrentChain()
				)?.let {
					update(it.apply { this.isDefault = isDefault })
				}
			}
		}

		fun insertToken(
			token: DefaultTokenTable,
			callback: () -> Unit
		) {
			doAsync {
				GoldStoneDataBase.database.defaultTokenDao().insert(token)
				GoldStoneAPI.context.runOnUiThread {
					callback()
				}
			}
		}
	}
}

@Dao
interface DefaultTokenDao {

	@Query("SELECT * FROM defaultTokens")
	fun getAllTokens(): List<DefaultTokenTable>

	@Query("SELECT * FROM defaultTokens WHERE chain_id LIKE :ercChain OR chain_id LIKE :eosChain OR chain_id LIKE :bchChain OR chain_id LIKE :ltcChain OR chain_id LIKE :etcChain OR chain_id LIKE :btcChain")
	fun getCurrentChainTokens(
		ercChain: String = Config.getCurrentChain(),
		etcChain: String = Config.getETCCurrentChain(),
		btcChain: String = Config.getBTCCurrentChain(),
		ltcChain: String = Config.getLTCCurrentChain(),
		bchChain: String = Config.getBCHCurrentChain(),
		eosChain: String = Config.getEOSCurrentChain()
	): List<DefaultTokenTable>

	@Query("SELECT * FROM defaultTokens WHERE isDefault LIKE :isDefault AND (chain_id LIKE :ercChain OR chain_id LIKE :eosChain OR chain_id LIKE :bchChain OR chain_id LIKE :ltcChain OR chain_id LIKE :etcChain OR chain_id LIKE :btcChain)")
	fun getDefaultTokens(
		isDefault: Boolean = true,
		ercChain: String = Config.getCurrentChain(),
		etcChain: String = Config.getETCCurrentChain(),
		btcChain: String = Config.getBTCCurrentChain(),
		ltcChain: String = Config.getLTCCurrentChain(),
		bchChain: String = Config.getBCHCurrentChain(),
		eosChain: String = Config.getEOSCurrentChain()
	): List<DefaultTokenTable>

	@Query("SELECT * FROM defaultTokens WHERE contract LIKE :contract  AND (chain_id LIKE :ercChain OR chain_id LIKE :eosChain OR chain_id LIKE :bchChain OR chain_id LIKE :ltcChain OR chain_id LIKE :etcChain OR chain_id LIKE :btcChain)")
	fun getCurrentChainTokenByContract(
		contract: String,
		ercChain: String = Config.getCurrentChain(),
		etcChain: String = Config.getETCCurrentChain(),
		btcChain: String = Config.getBTCCurrentChain(),
		ltcChain: String = Config.getLTCCurrentChain(),
		bchChain: String = Config.getBCHCurrentChain(),
		eosChain: String = Config.getEOSCurrentChain()
	): DefaultTokenTable?

	@Query("SELECT * FROM defaultTokens WHERE symbol LIKE :symbol AND contract LIKE :contract")
	fun getTokenBySymbolAndContractFromAllChains(
		symbol: String,
		contract: String
	): List<DefaultTokenTable>

	@Query("SELECT * FROM defaultTokens WHERE symbol LIKE :symbol AND chain_id LIKE :chainID AND  contract LIKE :contract")
	fun getTokenBySymbolContractAndChainID(
		symbol: String,
		contract: String,
		chainID: String
	): DefaultTokenTable?

	@Insert
	fun insert(token: DefaultTokenTable)

	@Update
	fun update(token: DefaultTokenTable)

	@Delete
	fun delete(token: DefaultTokenTable)
}