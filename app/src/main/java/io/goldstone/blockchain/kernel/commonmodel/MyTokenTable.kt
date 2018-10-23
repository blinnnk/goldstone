package io.goldstone.blockchain.kernel.commonmodel

import android.arch.persistence.room.*
import android.support.annotation.UiThread
import android.support.annotation.WorkerThread
import com.blinnnk.extension.isNull
import com.blinnnk.extension.orEmpty
import com.blinnnk.extension.orZero
import io.goldstone.blockchain.common.error.GoldStoneError
import io.goldstone.blockchain.common.error.RequestError
import io.goldstone.blockchain.common.sharedpreference.SharedAddress
import io.goldstone.blockchain.common.sharedpreference.SharedChain
import io.goldstone.blockchain.common.utils.isEmptyThen
import io.goldstone.blockchain.common.utils.load
import io.goldstone.blockchain.common.utils.then
import io.goldstone.blockchain.common.value.Current
import io.goldstone.blockchain.crypto.eos.account.EOSAccount
import io.goldstone.blockchain.crypto.multichain.*
import io.goldstone.blockchain.crypto.utils.CryptoUtils
import io.goldstone.blockchain.crypto.utils.toBTCCount
import io.goldstone.blockchain.crypto.utils.toEthCount
import io.goldstone.blockchain.kernel.database.GoldStoneDataBase
import io.goldstone.blockchain.kernel.network.bitcoin.BitcoinApi
import io.goldstone.blockchain.kernel.network.bitcoincash.BitcoinCashApi
import io.goldstone.blockchain.kernel.network.common.GoldStoneAPI
import io.goldstone.blockchain.kernel.network.eos.EOSAPI
import io.goldstone.blockchain.kernel.network.ethereum.GoldStoneEthCall
import io.goldstone.blockchain.kernel.network.litecoin.LitecoinApi
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

	fun preventDuplicateInsert() {
		doAsync {
			// 防止重复添加
			GoldStoneDataBase.database.myTokenDao().apply {
				if (getTokenByContractAndAddress(contract, symbol, ownerName, chainID).isNull()) {
					insert(this@MyTokenTable)
				}
			}
		}
	}

	companion object {
		fun updateOrInsertOwnerName(name: String, address: String) {
			doAsync {
				GoldStoneDataBase.database.myTokenDao().apply {
					val chainID = SharedChain.getEOSCurrent().id
					// 如果存在 OwnerName 和 OwnerAddress 一样的 EOS 记录, 那么就更新这条数据
					// 如果不存在则, 查询 Name 是否已经存在了, 如果还是不存在, 那么就插入一条全新的
					val pendingAccount = getPendingEOSAccount(address, chainID)
					val existingAccount = getByOwnerName(name, chainID)
					if (pendingAccount.isNull() && existingAccount.isEmpty()) {
						val defaultToken =
							GoldStoneDataBase.database.defaultTokenDao().getTokenByContract(
								TokenContract.EOS.contract!!,
								CoinSymbol.EOS.symbol!!,
								SharedChain.getEOSCurrent().id
							)
						defaultToken?.let {
							MyTokenTable(it, name, address).preventDuplicateInsert()
						}
					} else if (!pendingAccount.isNull()) {
						updatePendingAccountName(name, address, chainID)
					}
				}
			}
		}

		fun getMyTokens(inMainThread: Boolean = true, hold: (List<MyTokenTable>) -> Unit) {
			doAsync {
				GoldStoneDataBase.database.walletDao().findWhichIsUsing(true)?.getCurrentAddresses(true)?.let { addresses ->
					GoldStoneDataBase.database.myTokenDao().getTokensByAddress(addresses).apply {
						if (inMainThread) GoldStoneAPI.context.runOnUiThread { hold(this@apply) }
						else hold(this)
					}
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

		fun getTokensByAddress(
			addresses: List<String>,
			@UiThread hold: (List<MyTokenTable>) -> Unit
		) {
			load {
				GoldStoneDataBase.database.myTokenDao().getTokensByAddress(addresses)
			} then (hold)
		}

		fun getTokenBalance(
			contract: TokenContract,
			ownerName: String,
			callback: (Double?) -> Unit
		) {
			load {
				GoldStoneDataBase.database.myTokenDao()
					.getTokenByContractAndAddress(contract.contract.orEmpty(), contract.symbol, ownerName, contract.getCurrentChainID().id)
			} then { token ->
				if (token.isNull()) callback(null) else callback(token?.balance.orZero())
			}
		}

		@WorkerThread
		fun addNew(contract: TokenContract, chainID: String) {
			val currentAddress = contract.getAddress(false)
			val accountName =
				if (contract.isEOSSeries()) contract.getAddress() isEmptyThen currentAddress else currentAddress
			// 安全判断, 如果钱包里已经有这个 `Symbol` 则不添加
			MyTokenTable(
				0,
				accountName,
				currentAddress,
				contract.symbol,
				0.0,
				contract.contract.orEmpty(),
				chainID
			).preventDuplicateInsert()
		}

		fun getBalanceByContract(
			contract: TokenContract,
			ownerName: String,
			@WorkerThread hold: (balance: Double?, error: GoldStoneError) -> Unit
		) {
			// 获取选中的 `Symbol` 的 `Token` 对应 `WalletAddress` 的 `Balance`
			when {
				contract.isETH() ->
					GoldStoneEthCall.getEthBalance(
						ownerName,
						{ hold(null, it) },
						contract.getCurrentChainName()
					) {
						val balance = it.toEthCount()
						hold(balance, RequestError.None)
					}
				contract.isETC() ->
					GoldStoneEthCall.getEthBalance(
						ownerName,
						{ hold(null, it) },
						contract.getCurrentChainName()
					) {
						val balance = it.toEthCount()
						hold(balance, RequestError.None)
					}
				contract.isBTC() ->
					BitcoinApi.getBalance(ownerName, false) { balance, error ->
						hold(balance?.toBTCCount(), error)
					}
				contract.isLTC() ->
					LitecoinApi.getBalanceFromChainSo(ownerName) { balance, error ->
						hold(balance, error)
					}

				contract.isBCH() ->
					BitcoinCashApi.getBalance(ownerName, false) { balance, error ->
						hold(balance, error)
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
							contract.contract.orEmpty(),
							hold
						)
					else hold(null, RequestError.None)
				}

				else -> DefaultTokenTable.getCurrentChainToken(contract) { token ->
					GoldStoneEthCall.getTokenBalanceWithContract(
						token?.contract.orEmpty(),
						ownerName,
						{ hold(null, it) },
						contract.getCurrentChainName()
					) {
						val balance = CryptoUtils.toCountByDecimal(it, token?.decimals.orZero())
						hold(balance, RequestError.None)
					}
				}
			}
		}

		fun updateBalanceByContract(balance: Double, address: String, contract: TokenContract) {
			doAsync {
				GoldStoneDataBase.database.myTokenDao().updateBalanceByContract(balance, contract.contract!!, contract.symbol, address)
			}
		}
	}
}

@Dao
interface MyTokenDao {

	@Query("SELECT * FROM myTokens WHERE contract LIKE :contract AND symbol LIKE :symbol AND (ownerName = :ownerName OR ownerAddress = :ownerName) AND chainID Like :chainID ")
	fun getTokenByContractAndAddress(contract: String, symbol: String, ownerName: String, chainID: String): MyTokenTable?

	@Query("SELECT * FROM myTokens WHERE ownerName IN (:addresses)  AND chainID IN (:currentChainIDS) ORDER BY balance DESC ")
	fun getTokensByAddress(addresses: List<String>, currentChainIDS: List<String> = Current.chainIDs()): List<MyTokenTable>

	@Query("SELECT * FROM myTokens WHERE ownerAddress LIKE :walletAddress")
	fun getAll(walletAddress: String): List<MyTokenTable>

	@Query("SELECT * FROM myTokens WHERE ownerAddress LIKE :walletAddress AND chainID LIKE :chainID")
	fun getEOSData(walletAddress: String, chainID: String = SharedChain.getEOSCurrent().id): List<MyTokenTable>

	@Query("DELETE FROM myTokens WHERE ownerAddress LIKE :walletAddress OR ownerName LIKE :walletAddress")
	fun deleteAllByAddress(walletAddress: String)

	@Query("SELECT * FROM myTokens")
	fun getAll(): List<MyTokenTable>

	@Query("UPDATE myTokens SET balance = :balance WHERE contract = :contract AND symbol = :symbol AND ownerName LIKE :address AND chainID IN (:currentChainIDS)")
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

	@Insert
	fun insert(token: MyTokenTable)

	@Update
	fun update(token: MyTokenTable)

	@Delete
	fun delete(token: MyTokenTable)

	@Delete
	fun deleteAll(token: List<MyTokenTable>)
}