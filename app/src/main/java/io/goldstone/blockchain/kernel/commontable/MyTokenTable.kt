package io.goldstone.blockchain.kernel.commontable

import android.arch.persistence.room.*
import android.support.annotation.UiThread
import android.support.annotation.WorkerThread
import com.blinnnk.extension.isNotNull
import com.blinnnk.extension.isNull
import com.blinnnk.extension.orEmpty
import com.blinnnk.extension.orZero
import com.blinnnk.util.load
import com.blinnnk.util.then
import io.goldstone.blockchain.common.error.GoldStoneError
import io.goldstone.blockchain.common.error.RequestError
import io.goldstone.blockchain.common.sharedpreference.SharedAddress
import io.goldstone.blockchain.common.sharedpreference.SharedChain
import io.goldstone.blockchain.common.utils.isEmptyThen
import io.goldstone.blockchain.common.value.Current
import io.goldstone.blockchain.crypto.eos.account.EOSAccount
import io.goldstone.blockchain.crypto.multichain.*
import io.goldstone.blockchain.crypto.utils.CryptoUtils
import io.goldstone.blockchain.crypto.utils.toETHCount
import io.goldstone.blockchain.kernel.database.GoldStoneDataBase
import io.goldstone.blockchain.kernel.network.btcseries.insight.InsightApi
import io.goldstone.blockchain.kernel.network.eos.EOSAPI
import io.goldstone.blockchain.kernel.network.ethereum.ETHJsonRPC
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.model.DefaultTokenTable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * @date 01/04/2018 12:38 AM
 * @author KaySaith
 */
@Entity(tableName = "myTokens", primaryKeys = ["ownerName", "chainID", "symbol", "contract"])
data class MyTokenTable(
	var ownerName: String, // `EOS` 的 `account name` 会复用这个值作为 `Token` 唯一标识
	var ownerAddress: String,
	var symbol: String,
	var balance: Double, // 含有精度的余额 eg: `1.02` BTC
	var contract: String,
	var chainID: String,
	var isClosed: Boolean // 用户手动关闭的 `Token` 用来防止 `Server` 给出 `EOS` 潜在资产的重复添加
) {

	constructor(
		data: DefaultTokenTable,
		address: String
	) : this(
		address,
		address,
		data.symbol,
		0.0,
		data.contract,
		data.chainID,
		false
	)

	constructor(
		data: DefaultTokenTable,
		name: String,
		publicKey: String
	) : this(
		name,
		publicKey,
		data.symbol,
		0.0,
		data.contract,
		data.chainID,
		false
	)

	companion object {
		@JvmField
		val dao = GoldStoneDataBase.database.myTokenDao()

		fun updateOrInsertOwnerName(name: String, address: String) {
			GlobalScope.launch(Dispatchers.Default) {
				dao.apply {
					val chainID = SharedChain.getEOSCurrent().chainID.id
					// 如果存在 OwnerName 和 OwnerAddress 一样的 EOS 记录, 那么就更新这条数据
					// 如果不存在则, 查询 Name 是否已经存在了, 如果还是不存在, 那么就插入一条全新的
					val pendingAccount = getPendingEOSAccount(address, chainID)
					val existingAccount = getByOwnerName(name, chainID)
					if (pendingAccount.isNull() && existingAccount.isEmpty()) {
						val defaultToken =
							DefaultTokenTable.dao.getToken(
								TokenContract.EOS.contract,
								CoinSymbol.EOS.symbol,
								SharedChain.getEOSCurrent().chainID.id
							)
						defaultToken?.let {
							dao.insert(MyTokenTable(it, name, address))
						}
					} else if (!pendingAccount.isNull()) {
						// TODO 有可能出现 existing Account 不为空, 导入同一个 AccountName 但权限不同的私钥
						updatePendingAccountName(name, address, chainID)
					}
				}
			}
		}

		fun getMyTokens(@WorkerThread hold: (List<MyTokenTable>) -> Unit) {
			GlobalScope.launch(Dispatchers.Default) {
				WalletTable.dao
					.findWhichIsUsing(true)?.getCurrentAddresses(true)?.let { addresses ->
						dao.getTokensByAddress(addresses).let(hold)
					}
			}
		}

		fun getEOSAccountNamesByAddress(address: String, @UiThread hold: (List<String>) -> Unit) {
			load {
				GoldStoneDataBase.database.myTokenDao().getEOSData(address)
			} then { myTokens ->
				hold(
					myTokens.asSequence().distinctBy { it.ownerName }.filter {
						EOSAccount(it.ownerName).isValid()
					}.map {
						it.ownerName
					}.toList()
				)
			}
		}


		fun getTokenBalance(
			contract: TokenContract,
			@UiThread callback: (Double?) -> Unit
		) {
			load {
				GoldStoneDataBase.database.myTokenDao()
					.getTokenByContractAndAddress(
						contract.contract,
						contract.symbol,
						contract.getAddress(true),
						contract.getCurrentChainID().id
					)
			} then { token ->
				callback(token?.balance.orZero())
			}
		}

		@WorkerThread
		fun addNewOrOpen(contract: TokenContract, chainID: String) {
			val currentAddress = contract.getAddress(false)
			val accountName =
				if (contract.isEOSSeries()) contract.getAddress() isEmptyThen currentAddress else currentAddress
			// 安全判断, 如果钱包里已经有这个 `Symbol` 则不添加
			dao.insert(MyTokenTable(
				accountName,
				currentAddress,
				contract.symbol,
				0.0,
				contract.contract,
				chainID,
				false
			))
		}

		fun getBalanceByContract(
			contract: TokenContract,
			@WorkerThread hold: (balance: Double?, error: GoldStoneError) -> Unit
		) {
			// 获取选中的 `Symbol` 的 `Token` 对应 `WalletAddress` 的 `Balance`
			when {
				contract.isETH() || contract.isETC() -> ETHJsonRPC.getEthBalance(
					contract.getAddress(),
					contract.getChainURL()
				) { amount, error ->
					if (amount.isNotNull() && error.isNone()) {
						val balance = amount.toBigDecimal().toETHCount().toDouble()
						hold(balance, RequestError.None)
					} else hold(null, error)
				}

				contract.isBTCSeries() -> InsightApi.getBalance(
					contract.getChainType(),
					!contract.isBCH(), // 目前 BCH 的自有加密节点暂时不能使用, 这里用的第三方非加密
					contract.getAddress()
				) { balance, error ->
					hold(balance?.toBTC(), error)
				}

				// 在激活和设置默认账号之前这个存储有可能存储了是地址, 防止无意义的
				// 网络请求在这额外校验一次.
				contract.isEOS() -> {
					if (SharedAddress.getCurrentEOSAccount().isValid(false)) {
						EOSAPI.getAccountEOSBalance(SharedAddress.getCurrentEOSAccount(), hold)
					} else hold(null, RequestError.None)
				}

				contract.isEOSToken() -> {
					if (SharedAddress.getCurrentEOSAccount().isValid(false))
						EOSAPI.getAccountBalanceBySymbol(
							SharedAddress.getCurrentEOSAccount(),
							CoinSymbol(contract.symbol),
							contract.contract,
							hold
						)
					else hold(null, RequestError.None)
				}

				else -> DefaultTokenTable.getCurrentChainToken(contract) { token ->
					ETHJsonRPC.getTokenBalanceWithContract(
						token?.contract.orEmpty(),
						contract.getAddress(),
						SharedChain.getCurrentETH()
					) { amount, error ->
						if (amount.isNotNull() && error.isNone()) {
							val balance = CryptoUtils.toCountByDecimal(amount, token?.decimals.orZero())
							hold(balance, RequestError.None)
						} else hold(null, error)
					}
				}
			}
		}

	}
}

@Dao
interface MyTokenDao {

	@Query("SELECT * FROM myTokens WHERE contract LIKE :contract AND symbol LIKE :symbol AND (ownerName = :ownerName OR ownerAddress = :ownerName) AND chainID Like :chainID ")
	fun getTokenByContractAndAddress(contract: String, symbol: String, ownerName: String, chainID: String): MyTokenTable?

	@Query("SELECT * FROM myTokens WHERE ownerName IN (:addresses)  AND isClosed = :isClose AND chainID IN (:currentChainIDS) ORDER BY balance DESC ")
	fun getTokensByAddress(addresses: List<String>, isClose: Boolean = false, currentChainIDS: List<String> = Current.chainIDs()): List<MyTokenTable>

	@Query("SELECT * FROM myTokens WHERE ownerAddress LIKE :walletAddress")
	fun getAll(walletAddress: String): List<MyTokenTable>

	@Query("SELECT * FROM myTokens WHERE ownerAddress LIKE :walletAddress AND chainID LIKE :chainID")
	fun getEOSData(walletAddress: String, chainID: String = SharedChain.getEOSCurrent().chainID.id): List<MyTokenTable>

	@Query("DELETE FROM myTokens WHERE ownerAddress LIKE :walletAddress OR ownerName LIKE :walletAddress")
	fun deleteAllByAddress(walletAddress: String)

	@Query("SELECT * FROM myTokens")
	fun getAll(): List<MyTokenTable>

	@Query("UPDATE myTokens SET balance = :balance WHERE contract = :contract AND symbol = :symbol AND (ownerName LIKE :address OR ownerAddress LIKE :address) AND chainID IN (:currentChainIDS)")
	fun updateBalanceByContract(balance: Double, contract: String, symbol: String, address: String, currentChainIDS: List<String> = Current.chainIDs())

	// `OwnerName` 和 `OwnerAddress` 都是地址的情况, 是 `EOS` 的未激活或为设置默认 AccountName 的状态
	@Query("UPDATE myTokens SET ownerName = :name  WHERE ownerAddress = :address AND ownerName = :address AND chainID = :chainID")
	fun updatePendingAccountName(name: String, address: String, chainID: String)

	@Query("SELECT * FROM myTokens WHERE ownerName = :address AND ownerAddress = :address  AND chainID = :chainID")
	fun getPendingEOSAccount(address: String, chainID: String): MyTokenTable?

	@Query("SELECT * FROM myTokens WHERE ownerName = :name AND chainID = :chainID")
	fun getByOwnerName(name: String, chainID: String): List<MyTokenTable>

	@Query("DELETE FROM myTokens  WHERE ownerName LIKE :address AND contract LIKE :contract AND symbol = :symbol")
	fun deleteByContractAndAddress(contract: String, symbol: String, address: String)

	@Query("UPDATE myTokens SET isClosed = :isClose  WHERE ownerName LIKE :address AND contract LIKE :contract AND symbol = :symbol AND chainID = :chainID")
	fun updateCloseStatus(contract: String, symbol: String, address: String, chainID: String, isClose: Boolean)

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	fun insert(token: MyTokenTable)

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	fun insertAll(tokens: List<MyTokenTable>)

	@Update
	fun update(token: MyTokenTable)

	@Delete
	fun delete(token: MyTokenTable)

	@Delete
	fun deleteAll(token: List<MyTokenTable>)
}