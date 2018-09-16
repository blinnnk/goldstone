package io.goldstone.blockchain.kernel.commonmodel

import android.arch.persistence.room.*
import android.support.annotation.UiThread
import com.blinnnk.extension.*
import io.goldstone.blockchain.common.utils.ConcurrentAsyncCombine
import io.goldstone.blockchain.common.utils.load
import io.goldstone.blockchain.common.utils.then
import io.goldstone.blockchain.common.value.Config
import io.goldstone.blockchain.crypto.eos.EOSWalletUtils
import io.goldstone.blockchain.crypto.multichain.CoinSymbol
import io.goldstone.blockchain.crypto.multichain.TokenContract
import io.goldstone.blockchain.crypto.utils.CryptoUtils
import io.goldstone.blockchain.crypto.utils.toBTCCount
import io.goldstone.blockchain.crypto.utils.toEthCount
import io.goldstone.blockchain.crypto.utils.toSatoshi
import io.goldstone.blockchain.kernel.database.GoldStoneDataBase
import io.goldstone.blockchain.kernel.network.GoldStoneAPI
import io.goldstone.blockchain.kernel.network.GoldStoneEthCall
import io.goldstone.blockchain.kernel.network.bitcoin.BitcoinApi
import io.goldstone.blockchain.kernel.network.bitcoincash.BitcoinCashApi
import io.goldstone.blockchain.kernel.network.eos.EOSAPI
import io.goldstone.blockchain.kernel.network.litecoin.LitecoinApi
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
	var ownerName: String, // `EOS` 的 `account name` 会复用这个值作为 `Token` 唯一标识
	var ownerAddress: String,
	var symbol: String,
	var balance: Double,
	var contract: String,
	var chainID: String
) {

	constructor(
		data: DefaultTokenTable,
		address: String
	) : this(
		0,
		address,
		address,
		data.symbol,
		0.0,
		data.contract,
		data.chainID
	)

	constructor(
		data: DefaultTokenTable,
		name: String,
		publicKey: String
	) : this(
		0,
		name,
		publicKey,
		data.symbol,
		0.0,
		data.contract,
		data.chainID
	)

	fun insert() {
		doAsync {
			GoldStoneDataBase.database.myTokenDao().apply {
				// 防止重复添加
				if (getTargetChainTokenByContractAndAddress(
						contract,
						ownerAddress,
						chainID
					).isNull()
				) {
					insert(this@MyTokenTable)
				}
			}
		}
	}

	companion object {
		fun updateEOSAccountName(name: String, address: String) {
			doAsync {
				GoldStoneDataBase.database.myTokenDao().updateEOSAccountName(name, address)
			}
		}

		fun getMyTokens(callback: (List<MyTokenTable>) -> Unit) {
			WalletTable.getCurrentAddresses { addresses ->
				doAsync {
					var allTokens = listOf<MyTokenTable>()
					addresses.forEachOrEnd { item, isEnd ->
						allTokens += GoldStoneDataBase.database.myTokenDao()
							.getCurrentChainTokensBy(item)
						if (isEnd) {
							GoldStoneAPI.context.runOnUiThread {
								callback(allTokens)
							}
						}
					}
				}
			}
		}

		fun getMyTokensByAddress(
			addresses: List<String>,
			hold: (List<MyTokenTable>) -> Unit
		) {
			doAsync {
				var allTokens = listOf<MyTokenTable>()
				addresses.forEachOrEnd { item, isEnd ->
					allTokens += GoldStoneDataBase.database.myTokenDao().getCurrentChainTokensBy(item)
					if (isEnd) {
						GoldStoneAPI.context.runOnUiThread {
							hold(allTokens)
						}
					}
				}
			}
		}

		fun getCurrentChainDefaultAndMyTokens(
			hold: (
				myTokens: List<MyTokenTable>,
				defaultTokens: List<DefaultTokenTable>
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
			ownerName: String,
			convertByDecimal: Boolean = true,
			callback: (Double?) -> Unit
		) {
			load {
				GoldStoneDataBase.database.myTokenDao()
					.getCurrentChainTokenByContractAndAddress(contract, ownerName)
			} then { token ->
				if (token.isNull()) callback(null)
				else {
					if (!convertByDecimal) {
						callback(token?.balance.orZero())
					} else {
						DefaultTokenTable.getCurrentChainToken(contract) {
							it?.apply {
								callback(CryptoUtils.toCountByDecimal(token?.balance.orZero(), it.decimals))
							}
						}
					}
				}
			}
		}

		fun getMyTokenByContractAndWalletAddress(
			contract: String,
			walletAddress: String,
			callback: (MyTokenTable?) -> Unit
		) {
			load {
				GoldStoneDataBase
					.database.myTokenDao()
					.getCurrentChainTokenByContractAndAddress(contract, walletAddress)
			} then { token ->
				if (token.isNull()) callback(null)
				else callback(token)
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

		fun deleteByAddress(
			address: String,
			callback: () -> Unit
		) {
			doAsync {
				GoldStoneDataBase.database.myTokenDao().apply {
					val allTokens = getAll(address)
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
			@UiThread callback: () -> Unit
		) {
			WalletTable.getCurrentWallet {
				doAsync {
					val currentAddress = CoinSymbol(symbol).getAddress()
					GoldStoneDataBase.database.apply {
						// 安全判断, 如果钱包里已经有这个 `Symbol` 则不添加
						myTokenDao().getCurrentChainTokensBy(currentAddress).find {
							it.contract.equals(contract, true)
						}.isNull() isTrue {
							MyTokenTable(
								0,
								currentAddress,
								currentAddress,
								symbol,
								0.0,
								contract,
								TokenContract(contract).getCurrentChainID()
							).insert()
							// 没有网络不用检查间隔直接插入数据库
							GoldStoneAPI.context.runOnUiThread {
								callback()
							}
						}
					}
				}
			}
		}

		fun getBalanceByContract(
			contract: TokenContract,
			ownerName: String,
			convertByDecimal: Boolean = false,
			errorCallback: (error: Throwable?, reason: String?) -> Unit,
			callback: (balance: Double) -> Unit
		) {
			// 获取选中的 `Symbol` 的 `Token` 对应 `WalletAddress` 的 `Balance`
			when {
				contract.isETH() ->
					GoldStoneEthCall.getEthBalance(
						ownerName,
						errorCallback,
						contract.getCurrentChainName()
					) {
						val balance = if (convertByDecimal) it.toEthCount() else it
						callback(balance)
					}
				contract.isETC() ->
					GoldStoneEthCall.getEthBalance(
						ownerName,
						errorCallback,
						contract.getCurrentChainName()
					) {
						val balance = if (convertByDecimal) it.toEthCount() else it
						callback(balance)
					}
				contract.isBTC() ->
					BitcoinApi.getBalance(ownerName) {
						val balance = if (convertByDecimal) it.toBTCCount() else it.toDouble()
						callback(balance)
					}
				contract.isLTC() ->
					LitecoinApi.getBalance(ownerName) {
						val balance = if (convertByDecimal) it.toBTCCount() else it.toDouble()
						callback(balance)
					}

				contract.isBCH() ->
					BitcoinCashApi.getBalance(ownerName) {
						val balance = if (convertByDecimal) it else it.toSatoshi().toDouble()
						callback(balance)
					}

				contract.isEOS() -> {
					// 在激活和设置默认账号之前这个存储有可能存储了是地址, 防止无意义的
					// 网络请求在这额外校验一次.
					if (EOSWalletUtils.isValidAccountName(Config.getCurrentEOSName())) {
						EOSAPI.getAccountEOSBalance(Config.getCurrentEOSName(), callback)
					}
				}

				else -> DefaultTokenTable.getCurrentChainToken(contract.contract.orEmpty()) { token ->
					GoldStoneEthCall.getTokenBalanceWithContract(
						token?.contract.orEmpty(),
						ownerName,
						errorCallback,
						Config.getCurrentChainName()
					) {
						val balance =
							if (convertByDecimal)
								CryptoUtils.toCountByDecimal(it, token?.decimals.orZero())
							else it
						callback(balance)
					}
				}
			}
		}

		fun updateBalanceByContract(
			balance: Double,
			address: String,
			contract: TokenContract
		) {
			doAsync {
				GoldStoneDataBase.database.myTokenDao().apply {
					getCurrentChainTokenByContractAndAddress(contract.contract.orEmpty(), address).let { it ->
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

	@Query("SELECT * FROM myTokens WHERE contract LIKE :contract AND ownerName LIKE :ownerName AND (chainID Like :ercChain OR chainID Like :eosChain OR chainID Like :bchChain OR chainID Like :ltcChain OR chainID Like :etcChain OR chainID Like :btcChain) ")
	fun getCurrentChainTokenByContractAndAddress(
		contract: String,
		ownerName: String,
		ercChain: String = Config.getCurrentChain(),
		etcChain: String = Config.getETCCurrentChain(),
		btcChain: String = Config.getBTCCurrentChain(),
		ltcChain: String = Config.getLTCCurrentChain(),
		bchChain: String = Config.getBCHCurrentChain(),
		eosChain: String = Config.getEOSCurrentChain()
	): MyTokenTable?

	@Query("SELECT * FROM myTokens WHERE contract LIKE :contract AND ownerName LIKE :walletAddress AND chainID Like :chainID ")
	fun getTargetChainTokenByContractAndAddress(
		contract: String,
		walletAddress: String,
		chainID: String
	): MyTokenTable?

	@Query("SELECT * FROM myTokens WHERE ownerAddress LIKE :walletAddress AND (chainID Like :ercChain OR chainID Like :bchChain OR chainID Like :eosChain OR chainID Like :ltcChain  OR chainID Like :etcChain OR chainID Like :btcChain) ORDER BY balance DESC ")
	fun getCurrentChainTokensBy(
		walletAddress: String,
		ercChain: String = Config.getCurrentChain(),
		etcChain: String = Config.getETCCurrentChain(),
		btcChain: String = Config.getBTCCurrentChain(),
		ltcChain: String = Config.getLTCCurrentChain(),
		bchChain: String = Config.getBCHCurrentChain(),
		eosChain: String = Config.getEOSCurrentChain()
	): List<MyTokenTable>

	@Query("SELECT * FROM myTokens WHERE ownerName LIKE :walletAddress")
	fun getAll(walletAddress: String): List<MyTokenTable>

	@Query("SELECT * FROM myTokens")
	fun getAll(): List<MyTokenTable>

	@Query("UPDATE myTokens SET ownerName = :name  WHERE ownerAddress = :address")
	fun updateEOSAccountName(name: String, address: String)

	@Insert
	fun insert(token: MyTokenTable)

	@Update
	fun update(token: MyTokenTable)

	@Delete
	fun delete(token: MyTokenTable)
}