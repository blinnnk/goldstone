package io.goldstone.blockchain.kernel.commonmodel

import android.arch.persistence.room.*
import com.blinnnk.extension.*
import io.goldstone.blockchain.common.utils.ConcurrentAsyncCombine
import io.goldstone.blockchain.common.utils.load
import io.goldstone.blockchain.common.utils.then
import io.goldstone.blockchain.common.value.Config
import io.goldstone.blockchain.crypto.CryptoValue
import io.goldstone.blockchain.crypto.utils.CryptoUtils
import io.goldstone.blockchain.crypto.utils.toBTCCount
import io.goldstone.blockchain.crypto.utils.toEthCount
import io.goldstone.blockchain.kernel.database.GoldStoneDataBase
import io.goldstone.blockchain.kernel.network.GoldStoneAPI
import io.goldstone.blockchain.kernel.network.GoldStoneEthCall
import io.goldstone.blockchain.kernel.network.bitcoin.BitcoinApi
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.model.DefaultTokenTable
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.runOnUiThread

/**
 * @date 01/04/2018 12:38 AM
 * @author KaySaith
 */
@Entity(tableName = "myTokens")
data class MyTokenTable(
	@PrimaryKey(autoGenerate = true)
	var id: Int,
	var ownerAddress: String,
	var symbol: String,
	var balance: Double,
	var contract: String,
	var chainID: String
) {

	constructor(data: DefaultTokenTable, address: String) : this(
		0,
		address,
		data.symbol,
		0.0,
		data.contract,
		data.chain_id
	)

	companion object {

		fun insert(model: MyTokenTable, chainID: String) {
			GoldStoneDataBase.database.myTokenDao().apply {
				// 防止重复添加
				if (getCurrentChainTokenByContractAndAddress(
						model.contract,
						model.ownerAddress,
						chainID,
						chainID,
						chainID
					).isNull()
				) {
					insert(model)
				}
			}
		}

		fun getMyTokens(
			callback: (ArrayList<MyTokenTable>) -> Unit
		) {
			WalletTable.getCurrentAddresses { addresses ->
				doAsync {
					var allTokens = listOf<MyTokenTable>()
					addresses.forEachOrEnd { item, isEnd ->
						allTokens += GoldStoneDataBase
							.database
							.myTokenDao()
							.getCurrentChainTokensBy(item)
						if (isEnd) {
							GoldStoneAPI.context.runOnUiThread {
								callback(allTokens.toArrayList())
							}
						}
					}
				}
			}
		}

		fun getMyTokensByAddress(
			addresses: List<String>,
			hold: (ArrayList<MyTokenTable>) -> Unit
		) {
			doAsync {
				var allTokens = listOf<MyTokenTable>()
				addresses.forEachOrEnd { item, isEnd ->
					allTokens += GoldStoneDataBase.database.myTokenDao().getCurrentChainTokensBy(item)
					if (isEnd) {
						GoldStoneAPI.context.runOnUiThread {
							hold(allTokens.toArrayList())
						}
					}
				}
			}
		}

		fun getCurrentChainDefaultAndMyTokens(
			hold: (
				myTokens: ArrayList<MyTokenTable>,
				defaultTokens: ArrayList<DefaultTokenTable>
			) -> Unit
		) {
			DefaultTokenTable.getCurrentChainTokens { defaultTokens ->
				// Check current wallet has more than on token or not
				MyTokenTable.getMyTokens { myTokens ->
					hold(myTokens, defaultTokens)
				}
			}
		}

		fun getTokenBalance(
			contract: String,
			walletAddress: String,
			convertByDecimal: Boolean = true,
			callback: (Double?) -> Unit
		) {
			load {
				GoldStoneDataBase
					.database
					.myTokenDao()
					.getCurrentChainTokenByContractAndAddress(
						contract,
						walletAddress
					)
			} then { token ->
				if (token.isNull()) callback(null)
				else {
					if (!convertByDecimal) {
						callback(token!!.balance)
					} else {
						DefaultTokenTable.getCurrentChainToken(contract) {
							it?.apply {
								callback(CryptoUtils.toCountByDecimal(token!!.balance, it.decimals))
							}
						}
					}
				}
			}
		}

		fun deleteByContract(
			contract: String,
			address: String,
			callback: () -> Unit
		) {
			doAsync {
				GoldStoneDataBase.database.myTokenDao().apply {
					getCurrentChainTokenByContractAndAddress(contract, address).let { it ->
						it?.let { delete(it) }
						GoldStoneAPI.context.runOnUiThread { callback() }
					}
				}
			}
		}

		fun deleteByAddress(address: String, callback: () -> Unit) {
			doAsync {
				GoldStoneDataBase.database.myTokenDao().apply {
					val allTokens = getAllTokensBy(address)
					if (allTokens.isEmpty()) {
						callback()
						return@doAsync
					}
					object : ConcurrentAsyncCombine() {
						override var asyncCount: Int = allTokens.size
						override fun concurrentJobs() {
							allTokens.forEach {
								delete(it)
								completeMark()
							}
						}

						override fun getResultInMainThread() = false
						override fun mergeCallBack() = callback()
					}.start()
				}
			}
		}

		fun insertBySymbolAndContract(
			symbol: String,
			contract: String,
			chainID: String,
			callback: () -> Unit = {}
		) {
			WalletTable.getCurrentWallet {
				doAsync {
					val currentAddress = WalletTable.getAddressBySymbol(symbol)
					GoldStoneDataBase.database.apply {
						// 安全判断, 如果钱包里已经有这个 `Symbol` 则不添加
						myTokenDao().getCurrentChainTokensBy(currentAddress).find {
							it.contract.equals(contract, true)
						}.isNull() isTrue {
							insert(
								MyTokenTable(
									0,
									currentAddress,
									symbol,
									0.0,
									contract,
									CryptoValue.chainID(contract)
								),
								chainID
							)
							// 没有网络不用检查间隔直接插入数据库
							GoldStoneAPI.context.runOnUiThread {
								callback()
							}
						}
					}
				}
			}
		}

		fun getBalanceWithContract(
			contract: String,
			ownerAddress: String,
			convertByDecimal: Boolean = false,
			errorCallback: (error: Throwable?, reason: String?) -> Unit,
			callback: (balance: Double) -> Unit
		) {
			// 获取选中的 `Symbol` 的 `Token` 对应 `WalletAddress` 的 `Balance`
			when {
				contract.equals(CryptoValue.ethContract, true) -> {
					GoldStoneEthCall.getEthBalance(
						ownerAddress,
						errorCallback,
						Config.getCurrentChainName()
					) {
						val balance = if (convertByDecimal) it.toEthCount() else it
						callback(balance)
					}
				}

				contract.equals(CryptoValue.etcContract, true) -> {
					GoldStoneEthCall.getEthBalance(
						ownerAddress,
						errorCallback,
						Config.getETCCurrentChainName()
					) {
						val balance = if (convertByDecimal) it.toEthCount() else it
						callback(balance)
					}
				}

				contract.equals(CryptoValue.btcContract, true) -> {
					BitcoinApi.getBalanceByAddress(ownerAddress) {
						val balance = if (convertByDecimal) it.toBTCCount() else it.toDouble()
						callback(balance)
					}
				}

				else -> DefaultTokenTable.getCurrentChainToken(contract) { token ->
					GoldStoneEthCall.getTokenBalanceWithContract(
						token?.contract.orEmpty(),
						ownerAddress, errorCallback,
						Config.getCurrentChainName()
					) {
						val balance =
							if (convertByDecimal)
								CryptoUtils.toCountByDecimal(it, token?.decimals.orElse(0.0))
							else it
						callback(balance)
					}
				}
			}
		}

		fun updateBalanceWithContract(
			balance: Double,
			address: String,
			contract: String
		) {
			doAsync {
				GoldStoneDataBase.database.myTokenDao().apply {
					getCurrentChainTokenByContractAndAddress(contract, address).let { it ->
						it?.let {
							update(it.apply { this.balance = balance })
						}
					}
				}
			}
		}
	}
}

@Dao
interface MyTokenDao {

	@Query("SELECT * FROM myTokens WHERE contract LIKE :contract AND ownerAddress LIKE :walletAddress AND (chainID Like :ercChain OR chainID Like :etcChain OR chainID Like :btcChain) ")
	fun getCurrentChainTokenByContractAndAddress(
		contract: String,
		walletAddress: String,
		ercChain: String = Config.getCurrentChain(),
		etcChain: String = Config.getETCCurrentChain(),
		btcChain: String = Config.getBTCCurrentChain()
	): MyTokenTable?

	@Query("SELECT * FROM myTokens WHERE ownerAddress LIKE :walletAddress AND (chainID Like :ercChain OR chainID Like :etcChain OR chainID Like :btcChain) ORDER BY balance DESC ")
	fun getCurrentChainTokensBy(
		walletAddress: String,
		ercChain: String = Config.getCurrentChain(),
		etcChain: String = Config.getETCCurrentChain(),
		btcChain: String = Config.getBTCCurrentChain()
	): List<MyTokenTable>

	@Query("SELECT * FROM myTokens WHERE ownerAddress LIKE :walletAddress")
	fun getAllTokensBy(walletAddress: String): List<MyTokenTable>

	@Query("SELECT * FROM myTokens")
	fun getAll(): List<MyTokenTable>

	@Insert
	fun insert(token: MyTokenTable)

	@Update
	fun update(token: MyTokenTable)

	@Delete
	fun delete(token: MyTokenTable)
}