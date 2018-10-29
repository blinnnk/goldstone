package io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.model

import android.arch.persistence.room.*
import android.support.annotation.WorkerThread
import com.blinnnk.extension.*
import com.blinnnk.util.TinyNumberUtils
import com.google.gson.annotations.SerializedName
import io.goldstone.blockchain.common.sharedpreference.SharedChain
import io.goldstone.blockchain.common.sharedpreference.SharedWallet
import io.goldstone.blockchain.common.utils.load
import io.goldstone.blockchain.common.utils.then
import io.goldstone.blockchain.common.value.Current
import io.goldstone.blockchain.crypto.multichain.ChainID
import io.goldstone.blockchain.crypto.multichain.TokenContract
import io.goldstone.blockchain.crypto.multichain.getCurrentChainID
import io.goldstone.blockchain.kernel.database.GoldStoneDataBase
import io.goldstone.blockchain.kernel.network.common.GoldStoneAPI
import io.goldstone.blockchain.kernel.network.ethereum.GoldStoneEthCall
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
	var decimals: Int,
	var totalSupply: String? = null,
	// 个人通过 `Contract` 搜索到的, 和 `Server` 与 `Local` Json 数据都不同的部分.
	var isDefault: Boolean = true,
	@SerializedName("weight")
	var weight: Int,
	var chainID: String,
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
		0,
		"",
		true,
		0,
		SharedChain.getCurrentETH().id
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
		data.decimal,
		"",
		isDefault,
		data.weight,
		data.chainID
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
		localData.safeGet("decimals").toIntOrZero(),
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
		data.contract.contract.orEmpty(),
		"",
		data.symbol,
		0,
		0.0,
		"",
		0,
		data.supply,
		false,
		0,
		data.chainID,
		"${SharedWallet.getCurrentLanguageCode()}${data.description}",
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
		decimals: Int,
		chainID: ChainID,
		iconUrl: String,
		isDefault: Boolean = true
	) : this(
		0,
		"",
		contract,
		iconUrl,
		symbol,
		0,
		0.0,
		"",
		decimals,
		"",
		isDefault,
		0,
		chainID.id
	)

	infix fun insertThen(callback: () -> Unit) {
		doAsync {
			GoldStoneDataBase.database.defaultTokenDao().insert(this@DefaultTokenTable)
			GoldStoneAPI.context.runOnUiThread { callback() }
		}
	}

	@WorkerThread
	fun preventDuplicateInsert() {
		GoldStoneDataBase.database.defaultTokenDao().apply {
			if (getTokenByContract(contract, symbol, chainID).isNull()) {
				insert(this@DefaultTokenTable)
			}
		}
	}

	fun updateDefaultStatus(
		contract: TokenContract,
		isDefault: Boolean,
		name: String,
		iconUrl: String,
		callback: () -> Unit
	) {
		load {
			GoldStoneDataBase.database.defaultTokenDao().update(apply {
				this.isDefault = isDefault
				this.name = name
				this.contract = contract.contract.orEmpty()
				this.chainID = contract.getCurrentChainID().id
				this.iconUrl = iconUrl
			})
		} then { callback() }
	}

	fun updateTokenNameFromChain() {
		GoldStoneEthCall.getTokenName(
			contract,
			{ },
			SharedChain.getCurrentETHName()
		) {
			val name = if (it.isEmpty()) symbol else it
			DefaultTokenTable.updateTokenName(TokenContract(this), name)
		}
	}

	companion object {

		fun getAllTokens(isMainThread: Boolean = true, hold: (ArrayList<DefaultTokenTable>) -> Unit) {
			doAsync {
				val data = GoldStoneDataBase.database.defaultTokenDao().getAllTokens()
				if (isMainThread) GoldStoneAPI.context.runOnUiThread { hold(data.toArrayList()) }
				else hold(data.toArrayList())
			}
		}

		fun getCurrentChainTokens(hold: (List<DefaultTokenTable>) -> Unit) {
			load {
				GoldStoneDataBase.database.defaultTokenDao().getCurrentChainTokens()
			} then (hold)
		}

		fun getDefaultTokens(hold: (ArrayList<DefaultTokenTable>) -> Unit) {
			load {
				GoldStoneDataBase.database.defaultTokenDao().getDefaultTokens()
			} then {
				hold(it.toArrayList())
			}
		}

		fun getTokenByContractFromAllChains(contract: String, hold: (DefaultTokenTable?) -> Unit
		) {
			load {
				GoldStoneDataBase.database.defaultTokenDao().getTokenByContractFromAllChains(contract)
			} then { hold(it.firstOrNull()) }
		}

		fun getCurrentChainToken(
			contract: TokenContract,
			hold: (DefaultTokenTable?) -> Unit
		) {
			load {
				GoldStoneDataBase.database.defaultTokenDao()
					.getTokenByContract(contract.contract.orEmpty(), contract.symbol, contract.getCurrentChainID().id)
			} then (hold)
		}

		fun updateOrInsertCoinInfo(
			data: CoinInfoModel,
			@WorkerThread callback: () -> Unit
		) {
			doAsync {
				GoldStoneDataBase.database.defaultTokenDao()
					.apply {
						getTokenByContractFromAllChains(data.contract.contract.orEmpty()).let { targetTokens ->
							if (targetTokens.isEmpty()) {
								insert(DefaultTokenTable(data))
								callback()
							} else {
								// 插入行情的 `TokenInformation` 只需要插入主链数据即可
								update(targetTokens.asSequence().filterNot { default ->
									ChainID.getTestChains().any { it.equals(default.chainID, true) }
								}.first().apply {
									exchange = data.exchange
									website = data.website
									marketCap = data.marketCap
									whitePaper = data.whitePaper
									socialMedia = data.socialMedia
									rank = data.rank
									totalSupply = data.supply
									startDate = data.startDate
									description = "${SharedWallet.getCurrentLanguageCode()}${data.description}"
								})
								callback()
							}
						}
					}
			}
		}

		fun updateTokenPrice(contract: TokenContract, newPrice: Double, callback: () -> Unit = {}) {
			load {
				GoldStoneDataBase.database.defaultTokenDao()
					.updateTokenPrice(newPrice, contract.contract.orEmpty(), contract.symbol, contract.getCurrentChainID().id)
			} then { callback() }
		}

		fun updateTokenName(contract: TokenContract, name: String) {
			doAsync {
				GoldStoneDataBase.database.defaultTokenDao()
					.updateTokenName(name, contract.contract.orEmpty(), contract.symbol, contract.getCurrentChainID().id)
			}
		}
	}
}

@Dao
interface DefaultTokenDao {

	@Query("SELECT * FROM defaultTokens")
	fun getAllTokens(): List<DefaultTokenTable>

	@Query("UPDATE defaultTokens SET price = :newPrice WHERE contract LIKE :contract AND symbol LIKE :symbol AND chainID LIKE :chainID")
	fun updateTokenPrice(newPrice: Double, contract: String, symbol: String, chainID: String)

	@Query("UPDATE defaultTokens SET name = :newName WHERE contract LIKE :contract AND chainID LIKE :chainID AND symbol LIKE :symbol")
	fun updateTokenName(newName: String, contract: String, symbol: String, chainID: String)

	@Query("SELECT * FROM defaultTokens WHERE chainID IN (:currentChainIDs)")
	fun getCurrentChainTokens(currentChainIDs: List<String> = Current.chainIDs()): List<DefaultTokenTable>

	@Query("SELECT * FROM defaultTokens WHERE isDefault LIKE :isDefault AND chainID IN (:currentChainIDs)")
	fun getDefaultTokens(currentChainIDs: List<String> = Current.chainIDs(), isDefault: Boolean = true): List<DefaultTokenTable>

	@Query("SELECT * FROM defaultTokens WHERE contract LIKE :contract AND symbol LIKE :symbol  AND chainID LIKE :chainID")
	fun getTokenByContract(contract: String, symbol: String, chainID: String): DefaultTokenTable?

	@Query("SELECT * FROM defaultTokens WHERE contract LIKE :contract")
	fun getTokenByContractFromAllChains(contract: String): List<DefaultTokenTable>

	@Insert
	fun insert(token: DefaultTokenTable)

	@Insert
	fun insertAll(token: List<DefaultTokenTable>)

	@Update
	fun update(token: DefaultTokenTable)

	@Delete
	fun delete(token: DefaultTokenTable)
}