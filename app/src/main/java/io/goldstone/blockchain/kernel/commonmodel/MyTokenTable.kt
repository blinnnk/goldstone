package io.goldstone.blockchain.kernel.commonmodel

import android.arch.persistence.room.*
import com.blinnnk.extension.*
import com.blinnnk.util.coroutinesTask
import io.goldstone.blockchain.common.utils.ConcurrentAsyncCombine
import io.goldstone.blockchain.common.utils.NetworkUtil
import io.goldstone.blockchain.common.value.Config
import io.goldstone.blockchain.crypto.CryptoUtils
import io.goldstone.blockchain.crypto.CryptoValue
import io.goldstone.blockchain.crypto.toEthCount
import io.goldstone.blockchain.kernel.database.GoldStoneDataBase
import io.goldstone.blockchain.kernel.network.GoldStoneAPI
import io.goldstone.blockchain.kernel.network.GoldStoneEthCall
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
		
		fun insert(model: MyTokenTable, chainID: String = Config.getCurrentChain()) {
			GoldStoneDataBase.database.myTokenDao().apply {
				// 防止重复添加
				if (getCurrentChainTokenByContractAndAddress(
						model.contract,
						model.ownerAddress,
						chainID
					).isNull()
				) {
					insert(model)
				}
			}
		}
		
		fun getCurrentChainTokensWithAddress(
			walletAddress: String = Config.getCurrentAddress(),
			callback: (ArrayList<MyTokenTable>) -> Unit = {}
		) {
			coroutinesTask(
				{
					GoldStoneDataBase.database.myTokenDao().getCurrentChainTokensBy(walletAddress)
				}) {
				callback(it.toArrayList())
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
				MyTokenTable.getCurrentChainTokensWithAddress { myTokens ->
					hold(myTokens, defaultTokens)
				}
			}
		}
		
		fun getCurrentChainTokenByContract(
			contract: String,
			callback: (MyTokenTable?) -> Unit
		) {
			coroutinesTask(
				{
					GoldStoneDataBase
						.database
						.myTokenDao()
						.getCurrentChainTokenByContractAndAddress(
							contract,
							Config.getCurrentAddress()
						)
				}) {
				callback(it)
			}
		}
		
		fun getCurrentChainTokenBalanceByContract(
			contract: String,
			callback: (Double?) -> Unit
		) {
			coroutinesTask(
				{
					GoldStoneDataBase
						.database
						.myTokenDao()
						.getCurrentChainTokenByContractAndAddress(
							contract,
							Config.getCurrentAddress()
						)
				}) { token ->
				if (token.isNull()) callback(null)
				else {
					DefaultTokenTable.getCurrentChainTokenByContract(contract) {
						callback(CryptoUtils.toCountByDecimal(token!!.balance, it!!.decimals))
					}
				}
			}
		}
		
		fun deleteByContract(
			contract: String,
			address: String = Config.getCurrentAddress(),
			callback: () -> Unit = {}
		) {
			doAsync {
				GoldStoneDataBase.database.myTokenDao().apply {
					getCurrentChainTokenByContractAndAddress(contract, address).let {
						it?.let { delete(it) }
						GoldStoneAPI.context.runOnUiThread {
							callback()
						}
					}
				}
			}
		}
		
		fun deleteByAddress(address: String, callback: () -> Unit) {
			GoldStoneDataBase.database.myTokenDao().apply {
				val allTokens = getAllTokensBy(address)
				if (allTokens.isEmpty()) {
					GoldStoneAPI.context.runOnUiThread {
						callback()
					}
					return
				}
				object : ConcurrentAsyncCombine() {
					override var asyncCount: Int = allTokens.size
					override fun concurrentJobs() {
						allTokens.forEach {
							delete(it)
							completeMark()
						}
					}
					
					override fun mergeCallBack() = callback()
				}.start()
			}
		}
		
		fun insertBySymbolAndContract(
			symbol: String,
			contract: String,
			errorCallback: (error: Exception?, reason: String?) -> Unit,
			ownerAddress: String = Config.getCurrentAddress(),
			callback: () -> Unit
		) {
			doAsync {
				GoldStoneDataBase.database.apply {
					// 安全判断, 如果钱包里已经有这个 `Symbol` 则不添加
					myTokenDao().getCurrentChainTokensBy(ownerAddress).find {
						it.contract.equals(contract, true)
					}.isNull() isTrue {
						if (NetworkUtil.hasNetwork(GoldStoneAPI.context)) {
							getBalanceAndInsertWithSymbolAndContract(
								symbol,
								contract,
								ownerAddress,
								errorCallback
							) {
								GoldStoneAPI.context.runOnUiThread {
									callback()
								}
							}
						} else {
							insert(
								MyTokenTable(
									0,
									ownerAddress,
									symbol,
									0.0,
									contract,
									Config.getCurrentChain()
								)
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
		
		private fun getBalanceAndInsertWithSymbolAndContract(
			symbol: String,
			contract: String,
			ownerAddress: String,
			errorCallback: (error: Exception?, reason: String?) -> Unit,
			callback: (balance: Double) -> Unit
		) {
			// 获取选中的 `Symbol` 的 `Token` 对应 `WalletAddress` 的 `Balance`
			if (contract == CryptoValue.ethContract) {
				GoldStoneEthCall.getEthBalance(ownerAddress, { error, reason ->
					errorCallback(error, reason)
				}) {
					insert(
						MyTokenTable(
							0,
							ownerAddress,
							symbol,
							it,
							CryptoValue.ethContract,
							Config.getCurrentChain()
						)
					)
					callback(it)
				}
			} else {
				GoldStoneEthCall.getTokenBalanceWithContract(
					contract,
					ownerAddress, { _, _ ->
						// error callback if need alert
					}
				) {
					insert(
						MyTokenTable(
							0,
							ownerAddress,
							symbol,
							it,
							contract,
							Config.getCurrentChain()
						)
					)
					callback(it)
				}
			}
		}
		
		fun getBalanceWithContract(
			contract: String,
			ownerAddress: String,
			convertByDecimal: Boolean = false,
			errorCallback: (error: Exception?, reason: String?) -> Unit,
			callback: (balance: Double) -> Unit = {}
		) {
			// 获取选中的 `Symbol` 的 `Token` 对应 `WalletAddress` 的 `Balance`
			if (contract == CryptoValue.ethContract) {
				GoldStoneEthCall.getEthBalance(ownerAddress, { error, reason ->
					errorCallback(error, reason)
				}) {
					val balance = if (convertByDecimal) it.toEthCount() else it
					callback(balance)
				}
			} else {
				DefaultTokenTable.getCurrentChainTokenByContract(contract) { token ->
					GoldStoneEthCall.getTokenBalanceWithContract(
						token?.contract.orEmpty(),
						ownerAddress, { _, _ ->
							// error callback if need do something
						}
					) {
						val balance = if (convertByDecimal) CryptoUtils.toCountByDecimal(
							it, token?.decimals.orElse(0.0)
						) else it
						callback(balance)
					}
				}
			}
		}
		
		fun updateCurrentWalletBalanceWithContract(
			balance: Double,
			contract: String
		) {
			doAsync {
				GoldStoneDataBase.database.myTokenDao().apply {
					getCurrentChainTokenByContractAndAddress(contract, Config.getCurrentAddress()).let {
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
	
	@Query("SELECT * FROM myTokens WHERE contract LIKE :contract AND ownerAddress LIKE :walletAddress AND chainID Like :chainID ")
	fun getCurrentChainTokenByContractAndAddress(
		contract: String,
		walletAddress: String,
		chainID: String = Config.getCurrentChain()
	): MyTokenTable?
	
	@Query("SELECT * FROM myTokens WHERE ownerAddress LIKE :walletAddress AND chainID Like :chainID ")
	fun getCurrentChainTokensBy(
		walletAddress: String,
		chainID: String = Config.getCurrentChain()
	): List<MyTokenTable>
	
	@Query("SELECT * FROM myTokens WHERE ownerAddress LIKE :walletAddress")
	fun getAllTokensBy(
		walletAddress: String
	): List<MyTokenTable>
	
	@Insert
	fun insert(token: MyTokenTable)
	
	@Update
	fun update(token: MyTokenTable)
	
	@Delete
	fun delete(token: MyTokenTable)
}