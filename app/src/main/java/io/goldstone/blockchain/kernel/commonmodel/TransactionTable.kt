package io.goldstone.blockchain.kernel.commonmodel

import android.arch.persistence.room.*
import com.blinnnk.extension.forEachOrEnd
import com.blinnnk.extension.isNull
import com.blinnnk.extension.safeGet
import com.blinnnk.extension.toArrayList
import com.blinnnk.util.coroutinesTask
import com.google.gson.annotations.SerializedName
import io.goldstone.blockchain.GoldStoneApp
import io.goldstone.blockchain.crypto.CryptoUtils
import io.goldstone.blockchain.crypto.toDecimalFromHex
import io.goldstone.blockchain.kernel.database.GoldStoneDataBase
import io.goldstone.blockchain.kernel.network.GoldStoneAPI
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable
import io.goldstone.blockchain.module.home.wallet.transactions.transactionlist.model.ERC20TransactionModel
import io.goldstone.blockchain.module.home.wallet.transactions.transactionlist.model.TransactionListModel
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.runOnUiThread
import org.json.JSONObject
import java.math.BigInteger
import kotlin.math.max

/**
 * @date 07/04/2018 7:32 PM
 * @author KaySaith
 */
@Entity(tableName = "transactionList")
data class TransactionTable(
	@PrimaryKey(autoGenerate = true)
	var id: Int,
	@SerializedName("blockNumber")
	var blockNumber: String,
	@SerializedName("timeStamp")
	var timeStamp: String,
	@SerializedName("hash")
	var hash: String,
	@SerializedName("nonce")
	var nonce: String,
	@SerializedName("blockHash")
	var blockHash: String,
	@Ignore
	@SerializedName("transactionIndex")
	private var transactionIndex: String,
	@SerializedName("from")
	var fromAddress: String,
	@SerializedName("to")
	var to: String,
	@SerializedName("value")
	var value: String,
	@SerializedName("gas")
	var gas: String,
	@SerializedName("gasPrice")
	var gasPrice: String,
	@SerializedName("isError")
	var hasError: String,
	@SerializedName("txreceipt_status")
	var txreceipt_status: String,
	@SerializedName("input")
	var input: String,
	@SerializedName("contractAddress")
	var contractAddress: String,
	@Ignore
	@SerializedName("cumulativeGasUsed")
	private var cumulativeGasUsed: String,
	@SerializedName("gasUsed")
	var gasUsed: String,
	@Ignore
	@SerializedName("confirmations")
	private var confirmations: String,
	var isReceive: Boolean,
	var isERC20: Boolean,
	var symbol: String,
	var recordOwnerAddress: String,
	var tokenReceiveAddress: String? = null,
	var isPending: Boolean = false,
	var logIndex: String = "",
	var chainID: String = GoldStoneApp.currentChain
) {
	
	/** 默认的 `constructor` */
	constructor() : this(
		0,
		"",
		"",
		"",
		"",
		"",
		"",
		"",
		"",
		"",
		"",
		"",
		"",
		"",
		"",
		"",
		"",
		"",
		"",
		false,
		false,
		"",
		""
	)
	
	// 这个是专门为入账的 `ERC20 Token` 准备的
	constructor(data: ERC20TransactionModel) : this(
		0,
		data.blockNumber,
		data.timeStamp,
		data.transactionHash,
		"",
		"",
		data.transactionIndex,
		data.from,
		data.to,
		data.value,
		"",
		data.gasPrice,
		"0",
		"1",
		"",
		data.contract,
		"",
		data.gasUsed,
		"",
		data.isReceive,
		true,
		data.symbol,
		WalletTable.current.address,
		data.to,
		false,
		data.logIndex
	)
	
	// 这个是专门为入账的 `ERC20 Token` 准备的
	constructor(data: JSONObject) : this(
		0,
		data.safeGet("blockNumber").toDecimalFromHex(),
		"",
		data.safeGet("hash"),
		data.safeGet("nonce").toDecimalFromHex(),
		data.safeGet("blockHash"),
		data.safeGet("transactionIndex").toDecimalFromHex(),
		data.safeGet("from"),
		data.safeGet("to"),
		data.safeGet("value").toDecimalFromHex(),
		data.safeGet("gas").toDecimalFromHex(),
		data.safeGet("gasPrice").toDecimalFromHex(),
		"0", "1",
		data.safeGet("input"),
		if (CryptoUtils.isERC20TransferByInputCode(data.safeGet("input")))
			data.safeGet("to") else "0x0",
		"",
		"",
		"",
		data.safeGet("from") != WalletTable.current.address,
		CryptoUtils.isERC20TransferByInputCode(data.safeGet("input")),
		"",
		WalletTable.current.address
	)
	
	companion object {
		fun updateModelInfoFromChain(
			transaction: TransactionTable,
			isERC20: Boolean,
			symbol: String,
			value: String,
			tokenReceiveAddress: String?
		) {
			transaction.apply {
				this.isReceive = WalletTable.current.address.equals(tokenReceiveAddress, true)
				this.isERC20 = isERC20
				this.symbol = symbol
				this.value = value
				this.tokenReceiveAddress = tokenReceiveAddress
				this.recordOwnerAddress = WalletTable.current.address
			}
		}
		
		fun getTransactionListModelsByAddress(
			address: String,
			hold: (ArrayList<TransactionListModel>) -> Unit
		) {
			coroutinesTask(
				{
					GoldStoneDataBase.database.transactionDao().getTransactionsByAddress(address)
				}) {
				hold(it.map { TransactionListModel(it) }.toArrayList())
			}
		}
		
		fun getTransactionsByAddress(
			address: String,
			hold: (ArrayList<TransactionTable>) -> Unit
		) {
			coroutinesTask({
				               GoldStoneDataBase.database.transactionDao().getTransactionsByAddress(address)
			               }) {
				hold(it.toArrayList())
			}
		}
		
		/**
		 * 分别从本地数据库以及 `Etherscan` 查询目前成功的最大的 `nounce` 值来生成
		 * 最近可用的 `Nounce`
		 */
		fun getLatestValidNounce(hold: (BigInteger) -> Unit) {
			GoldStoneAPI.getTransactionListByAddress {
				TransactionTable.getLocalLatestNounce { localNounce ->
					val myLatestNonce = firstOrNull {
						it.fromAddress.equals(WalletTable.current.address, true)
					}?.nonce?.toLong()
					val chainNounce = if (myLatestNonce.isNull()) 0L
					else myLatestNonce!! + 1
					BigInteger.valueOf(
						max(chainNounce, if (localNounce.isNull()) 0 else localNounce!! + 1)
					).let {
						hold(it)
					}
				}
			}
		}
		
		private fun getLocalLatestNounce(hold: (Long?) -> Unit) {
			doAsync {
				GoldStoneDataBase.database.transactionDao().apply {
					getTransactionsByAddress(WalletTable.current.address).let {
						GoldStoneAPI.context.runOnUiThread {
							if (it.isEmpty()) {
								hold(null)
								return@runOnUiThread
							}
							// 获取最大的 nonce
							it.filter {
								it.hasError == "0"
							}.filter {
								!it.isReceive
							}.filter {
								it.blockNumber.isNotEmpty()
							}.maxBy {
								it.nonce
							}.let {
								hold(it?.nonce?.toLongOrNull())
							}
						}
					}
				}
			}
		}
		
		fun getTransactionsByAddressAndSymbol(
			address: String,
			symbol: String,
			hold: (ArrayList<TransactionTable>) -> Unit
		) {
			coroutinesTask({
				               GoldStoneDataBase.database.transactionDao()
					               .getTransactionsByAddressAndSymbol(address, symbol)
			               }) {
				hold(it.toArrayList())
			}
		}
		
		fun getMyLatestStartBlock(
			address: String = WalletTable.current.address,
			hold: (String) -> Unit
		) {
			coroutinesTask({
				               GoldStoneDataBase.database.transactionDao().getTransactionsByAddress(address)
			               }) {
				// 获取到当前最近的一个 `BlockNumber` 若获取不到返回 `0`
				hold(
					(it.maxBy { it.blockNumber }?.blockNumber
					 ?: "0") + 1
				)
			}
		}
		
		fun deleteByAddress(
			address: String,
			callback: () -> Unit
		) {
			coroutinesTask({
				               GoldStoneDataBase.database.transactionDao().apply {
					               getTransactionsByAddress(address).forEach { delete(it) }
				               }
			               }) {
				callback()
			}
		}
		
		// 异步方法
		fun deleteByTaxHash(taxHash: String) {
			GoldStoneDataBase.database.transactionDao().apply {
				getTransactionByTaxHash(taxHash).let {
					it.forEach {
						delete(it)
					}
				}
			}
		}
		
		fun updateInputCodeByHash(
			taxHash: String,
			input: String,
			callback: () -> Unit = {}
		) {
			GoldStoneDataBase.database.transactionDao().apply {
				getTransactionByTaxHash(taxHash).let {
					it.forEachOrEnd { item, isEnd ->
						update(item.apply { this.input = input })
						if (isEnd) callback()
					}
				}
			}
		}
		
		fun getTransactionByHash(
			taxHash: String,
			hold: (List<TransactionTable>) -> Unit
		) {
			GoldStoneDataBase.database.transactionDao().apply {
				getTransactionByTaxHash(taxHash).let {
					hold(it)
				}
			}
		}
		
		fun getTransactionByHashAndReceivedStatus(
			hash: String,
			isReceived: Boolean,
			hold: (TransactionTable?) -> Unit
		) {
			coroutinesTask({
				               GoldStoneDataBase.database.transactionDao()
					               .getTransactionByTaxHashAndReceivedStatus(hash, isReceived)
			               }) {
				hold(it)
			}
		}
	}
}

@Dao
interface TransactionDao {
	
	@Query(
		"SELECT * FROM transactionList WHERE recordOwnerAddress LIKE :walletAddress AND chainID LIKE :chainID ORDER BY timeStamp DESC"
	)
	fun getTransactionsByAddress(walletAddress: String, chainID: String = GoldStoneApp.currentChain):
		List<TransactionTable>
	
	@Query("SELECT * FROM transactionList WHERE hash LIKE :taxHash AND chainID LIKE :chainID")
	fun getTransactionByTaxHash(taxHash: String, chainID: String = GoldStoneApp.currentChain):
		List<TransactionTable>
	
	@Query("SELECT * FROM transactionList WHERE hash LIKE :taxHash AND isReceive LIKE :isReceive AND chainID LIKE :chainID")
	fun getTransactionByTaxHashAndReceivedStatus(
		taxHash: String,
		isReceive: Boolean,
		chainID: String = GoldStoneApp.currentChain
	): TransactionTable?
	
	@Query(
		"SELECT * FROM transactionList WHERE recordOwnerAddress LIKE :walletAddress AND symbol LIKE :targetSymbol AND chainID LIKE :chainID ORDER BY timeStamp DESC"
	)
	fun getTransactionsByAddressAndSymbol(
		walletAddress: String,
		targetSymbol: String,
		chainID: String = GoldStoneApp.currentChain
	): List<TransactionTable>
	
	@Insert
	fun insert(token: TransactionTable)
	
	@Update
	fun update(token: TransactionTable)
	
	@Delete
	fun delete(token: TransactionTable)
}