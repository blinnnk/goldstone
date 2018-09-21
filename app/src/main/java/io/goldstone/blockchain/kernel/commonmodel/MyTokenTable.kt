package io.goldstone.blockchain.kernel.commonmodel

import android.arch.persistence.room.*
import android.support.annotation.UiThread
import android.support.annotation.WorkerThread
import com.blinnnk.extension.isNull
import com.blinnnk.extension.orEmpty
import com.blinnnk.extension.orZero
import io.goldstone.blockchain.common.error.GoldStoneError
import io.goldstone.blockchain.common.utils.load
import io.goldstone.blockchain.common.utils.then
import io.goldstone.blockchain.common.value.Config
import io.goldstone.blockchain.crypto.eos.EOSWalletUtils
import io.goldstone.blockchain.crypto.multichain.*
import io.goldstone.blockchain.crypto.utils.CryptoUtils
import io.goldstone.blockchain.crypto.utils.toBTCCount
import io.goldstone.blockchain.crypto.utils.toEthCount
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
	var balance: Double, // 含有精度的余额 eg: `1.02` BTC
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
			// 防止重复添加
			GoldStoneDataBase.database.myTokenDao().apply {
				if (getTokenByContractAndAddress(contract, ownerAddress, chainID).isNull()) {
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

		fun getMyTokens(@UiThread hold: (List<MyTokenTable>) -> Unit) {
			WalletTable.getCurrentAddresses { addresses ->
				getTokensByAddress(addresses, hold)
			}
		}

		fun getTokensByAddress(
			addresses: List<String>,
			@UiThread hold: (List<MyTokenTable>) -> Unit
		) {
			load {
				addresses.map { address ->
					GoldStoneDataBase.database.myTokenDao()
						.getTokensBy(address).filter { ChainID(it.chainID).isCurrent() }
				}.flatten()
			} then (hold)
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
			contract: TokenContract,
			ownerName: String,
			callback: (Double?) -> Unit
		) {
			load {
				GoldStoneDataBase.database.myTokenDao()
					.getTokenByContractAndAddress(contract.contract.orEmpty(), ownerName, contract.getCurrentChainID().id)
			} then { token ->
				if (token.isNull()) callback(null) else callback(token?.balance.orZero())
			}
		}

		fun getMyTokenByContractAndWalletAddress(
			contract: TokenContract,
			walletAddress: String,
			callback: (MyTokenTable?) -> Unit
		) {
			load {
				GoldStoneDataBase.database.myTokenDao()
					.getTokenByContractAndAddress(
						contract.contract.orEmpty(),
						walletAddress,
						contract.getCurrentChainID().id
					)
			} then (callback)
		}

		fun deleteByContract(
			contract: TokenContract,
			address: String,
			callback: () -> Unit
		) {
			load {
				GoldStoneDataBase.database.myTokenDao()
					.deleteByContractAndAddress(address, contract.contract.orEmpty())
			} then { callback() }
		}

		fun deleteByAddress(
			address: String,
			callback: () -> Unit
		) {
			doAsync {
				GoldStoneDataBase.database.myTokenDao().apply {
					deleteAll(getAll(address))
					callback()
				}
			}
		}

		fun insertBySymbolAndContract(
			symbol: String,
			contract: TokenContract,
			@UiThread callback: () -> Unit
		) {
			doAsync {
				val currentAddress = CoinSymbol(symbol).getAddress()
				// 安全判断, 如果钱包里已经有这个 `Symbol` 则不添加
				if (
					GoldStoneDataBase.database.myTokenDao()
						.getTokenByContractAndAddress(currentAddress, currentAddress, contract.getMainnetChainID()).isNull()
				) {
					MyTokenTable(
						0,
						currentAddress,
						currentAddress,
						symbol,
						0.0,
						contract.contract.orEmpty(),
						contract.getCurrentChainID().id
					).insert()
					// 没有网络不用检查间隔直接插入数据库
					GoldStoneAPI.context.runOnUiThread {
						callback()
					}
				}
			}
		}

		fun getBalanceByContract(
			contract: TokenContract,
			ownerName: String,
			errorCallback: (GoldStoneError) -> Unit,
			@WorkerThread callback: (balance: Double) -> Unit
		) {
			// 获取选中的 `Symbol` 的 `Token` 对应 `WalletAddress` 的 `Balance`
			when {
				contract.isETH() ->
					GoldStoneEthCall.getEthBalance(
						ownerName,
						errorCallback,
						contract.getCurrentChainName()
					) {
						val balance = it.toEthCount()
						callback(balance)
					}
				contract.isETC() ->
					GoldStoneEthCall.getEthBalance(
						ownerName,
						errorCallback,
						contract.getCurrentChainName()
					) {
						val balance = it.toEthCount()
						callback(balance)
					}
				contract.isBTC() ->
					BitcoinApi.getBalance(ownerName) {
						val balance = it.toBTCCount()
						callback(balance)
					}
				contract.isLTC() ->
					LitecoinApi.getBalance(ownerName) {
						val balance = it.toBTCCount()
						callback(balance)
					}

				contract.isBCH() ->
					BitcoinCashApi.getBalance(ownerName) {
						val balance = it
						callback(balance)
					}

				contract.isEOS() -> {
					// 在激活和设置默认账号之前这个存储有可能存储了是地址, 防止无意义的
					// 网络请求在这额外校验一次.
					if (EOSWalletUtils.isValidAccountName(Config.getCurrentEOSName())) {
						EOSAPI.getAccountEOSBalance(Config.getCurrentEOSName(), errorCallback, callback)
					}
				}

				else -> DefaultTokenTable.getCurrentChainToken(contract) { token ->
					GoldStoneEthCall.getTokenBalanceWithContract(
						token?.contract.orEmpty(),
						ownerName,
						errorCallback,
						contract.getCurrentChainName()
					) {
						val balance = CryptoUtils.toCountByDecimal(it, token?.decimals.orZero())
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
					getTokenByContractAndAddress(contract.contract.orEmpty(), address, contract.getCurrentChainID().id).let { it ->
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

	@Query("SELECT * FROM myTokens WHERE contract LIKE :contract AND ownerName LIKE :ownerName AND chainID Like :chainID ")
	fun getTokenByContractAndAddress(contract: String, ownerName: String, chainID: String): MyTokenTable?

	@Query("SELECT * FROM myTokens WHERE ownerAddress LIKE :walletAddress ORDER BY balance DESC ")
	fun getTokensBy(walletAddress: String): List<MyTokenTable>

	@Query("SELECT * FROM myTokens WHERE ownerName LIKE :walletAddress")
	fun getAll(walletAddress: String): List<MyTokenTable>

	@Query("SELECT * FROM myTokens")
	fun getAll(): List<MyTokenTable>

	@Query("UPDATE myTokens SET ownerName = :name  WHERE ownerAddress = :address")
	fun updateEOSAccountName(name: String, address: String)

	@Query("DELETE FROM myTokens  WHERE ownerAddress LIKE :address AND contract LIKE :contract")
	fun deleteByContractAndAddress(address: String, contract: String)

	@Insert
	fun insert(token: MyTokenTable)

	@Update
	fun update(token: MyTokenTable)

	@Delete
	fun delete(token: MyTokenTable)

	@Delete
	fun deleteAll(token: List<MyTokenTable>)
}