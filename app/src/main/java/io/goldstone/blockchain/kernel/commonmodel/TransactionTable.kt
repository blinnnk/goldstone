package io.goldstone.blockchain.kernel.commonmodel

import android.arch.persistence.room.*
import com.blinnnk.extension.isNull
import com.blinnnk.extension.safeGet
import com.blinnnk.extension.toArrayList
import com.blinnnk.util.ConcurrentCombine
import com.blinnnk.util.coroutinesTask
import com.google.gson.annotations.SerializedName
import io.goldstone.blockchain.common.utils.LogUtil
import io.goldstone.blockchain.common.utils.alert
import io.goldstone.blockchain.common.value.Config
import io.goldstone.blockchain.crypto.CryptoValue
import io.goldstone.blockchain.crypto.utils.CryptoUtils
import io.goldstone.blockchain.crypto.utils.toDecimalFromHex
import io.goldstone.blockchain.kernel.database.GoldStoneDataBase
import io.goldstone.blockchain.kernel.network.GoldStoneAPI
import io.goldstone.blockchain.kernel.network.GoldStoneEthCall
import io.goldstone.blockchain.module.home.wallet.transactions.transactionlist.model.ERC20TransactionModel
import io.goldstone.blockchain.module.home.wallet.transactions.transactionlist.model.TransactionListModel
import io.goldstone.blockchain.module.home.wallet.transactions.transactionlist.model.getMemoFromInputCode
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
	@SerializedName("transactionIndex")
	var transactionIndex: String,
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
	@SerializedName("cumulativeGasUsed")
	var cumulativeGasUsed: String,
	@SerializedName("gasUsed")
	var gasUsed: String,
	@SerializedName("confirmations")
	var confirmations: String,
	var isReceive: Boolean,
	var isERC20: Boolean,
	var symbol: String,
	var recordOwnerAddress: String,
	var tokenReceiveAddress: String? = null,
	var isPending: Boolean = false,
	var logIndex: String = "",
	var memo: String = "",
	var chainID: String = Config.getCurrentChain(),
	var isFee: Boolean = false,
	var isFailed: Boolean = false
) {
	
	/** 默认的 `constructor` */
	@Ignore
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
	
	constructor(data: TransactionTable) : this(
		0,
		data.blockNumber,
		data.timeStamp,
		data.hash,
		data.nonce,
		data.blockHash,
		data.transactionIndex,
		data.fromAddress,
		data.to,
		data.value,
		data.gas,
		data.gasPrice,
		data.hasError,
		data.txreceipt_status,
		data.input,
		if (CryptoUtils.isERC20TransferByInputCode(data.input)) data.to
		else CryptoValue.ethContract,
		data.cumulativeGasUsed,
		data.gasUsed,
		data.confirmations,
		data.fromAddress != Config.getCurrentAddress(),
		CryptoUtils.isERC20TransferByInputCode(data.input),
		data.symbol,
		Config.getCurrentAddress()
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
		Config.getCurrentAddress(),
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
		"0",
		"1",
		data.safeGet("input"),
		if (CryptoUtils.isERC20TransferByInputCode(data.safeGet("input")))
			data.safeGet("to") else "0x0",
		"",
		"",
		"",
		data.safeGet("from") != Config.getCurrentAddress(),
		CryptoUtils.isERC20TransferByInputCode(data.safeGet("input")),
		"",
		Config.getCurrentAddress()
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
				this.isReceive = Config.getCurrentAddress().equals(tokenReceiveAddress, true)
				this.isERC20 = isERC20
				this.symbol = symbol
				this.value = value
				this.tokenReceiveAddress = tokenReceiveAddress
				this.recordOwnerAddress = Config.getCurrentAddress()
			}
		}
		
		fun getListModelsByAddress(
			address: String,
			hold: (ArrayList<TransactionListModel>) -> Unit
		) {
			coroutinesTask(
				{
					GoldStoneDataBase
						.database
						.transactionDao()
						.getTransactionsByAddress(address)
				}) {
				val result = if (it.isEmpty()) {
					arrayListOf()
				} else {
					it.map { TransactionListModel(it) }.toArrayList()
				}
				hold(result)
			}
		}
		
		/**
		 * 分别从本地数据库以及 `Etherscan` 查询目前成功的最大的 `nonce` 值来生成
		 * 最近可用的 `nonce`
		 */
		fun getLatestValidNonce(
			errorCallback: (Exception) -> Unit,
			hold: (BigInteger) -> Unit
		) {
			GoldStoneAPI.getTransactionListByAddress(
				"0",
				errorCallback
			) {
				TransactionTable.getLocalLatestNonce { localNonce ->
					val myLatestNonce = firstOrNull {
						it.fromAddress.equals(Config.getCurrentAddress(), true)
					}?.nonce?.toLong()
					val chainNonce = if (myLatestNonce.isNull()) 0L
					else myLatestNonce!! + 1
					BigInteger.valueOf(
						max(chainNonce, if (localNonce.isNull()) 0 else localNonce!! + 1)
					).let {
						hold(it)
					}
				}
			}
		}
		
		private fun getLocalLatestNonce(hold: (Long?) -> Unit) {
			doAsync {
				GoldStoneDataBase
					.database
					.transactionDao()
					.getTransactionsByAddress(Config.getCurrentAddress()).let {
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
		
		fun getCurrentChainByAddressAndContract(
			walletAddress: String,
			contract: String,
			hold: (ArrayList<TransactionTable>) -> Unit
		) {
			coroutinesTask(
				{
					GoldStoneDataBase
						.database
						.transactionDao()
						.getCurrentChainByAddressAndContract(walletAddress, contract)
				}) {
				hold(it.toArrayList())
			}
		}
		
		fun getMyLatestStartBlock(
			address: String = Config.getCurrentAddress(),
			hold: (String) -> Unit
		) {
			GoldStoneDataBase.database.transactionDao().getTransactionsByAddress(address).let {
				// 获取到当前最近的一个 `BlockNumber` 若获取不到返回 `0`
				hold((it.maxBy { it.blockNumber }?.blockNumber ?: "0") + 1)
			}
		}
		
		fun deleteByAddress(address: String, callback: () -> Unit) {
			GoldStoneDataBase.database.transactionDao().apply {
				val data = getAllTransactionsByAddress(address)
				if (data.isEmpty()) {
					GoldStoneAPI.context.runOnUiThread { callback() }
					return
				}
				object : ConcurrentCombine() {
					override var asyncCount: Int = data.size
					override fun concurrentJobs() {
						data.forEach {
							delete(it)
							completeMark()
						}
					}
					
					override fun mergeCallBack() = callback()
				}.start()
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
		
		fun getMemoByHashAndReceiveStatus(
			hash: String,
			isReceive: Boolean,
			chainID: String = Config.getCurrentChain(),
			callback: (memo: String) -> Unit
		) {
			TransactionTable.getByHashAndReceivedStatus(hash, isReceive) { transaction ->
				if (transaction.isNull() || transaction?.memo?.isNotEmpty() == true) {
					callback(transaction?.memo.orEmpty())
				} else {
					GoldStoneEthCall.apply {
						getInputCodeByHash(
							hash,
							{ error, reason ->
								reason?.let { context.alert(it) }
								LogUtil.error("getByHashAndReceivedStatus", error)
							},
							chainID
						) { input ->
							val isErc20 = CryptoUtils
								.isERC20TransferByInputCode(input)
							val memo = getMemoFromInputCode(input, isErc20)
							GoldStoneDataBase.database.transactionDao().update(transaction!!.apply {
								this.input = input
								this.memo = memo
							})
							GoldStoneAPI.context.runOnUiThread {
								callback(memo)
							}
						}
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
		
		fun getByHashAndReceivedStatus(
			hash: String,
			isReceived: Boolean,
			hold: (TransactionTable?) -> Unit
		) {
			coroutinesTask(
				{
					GoldStoneDataBase.database.transactionDao()
						.getByTaxHashAndReceivedStatus(hash, isReceived)
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
	fun getTransactionsByAddress(
		walletAddress: String,
		chainID: String = Config.getCurrentChain()
	): List<TransactionTable>
	
	@Query(
		"SELECT * FROM transactionList WHERE recordOwnerAddress LIKE :walletAddress ORDER BY timeStamp DESC"
	)
	fun getAllTransactionsByAddress(walletAddress: String): List<TransactionTable>
	
	@Query("SELECT * FROM transactionList WHERE hash LIKE :taxHash")
	fun getTransactionByTaxHash(
		taxHash: String
	): List<TransactionTable>
	
	@Query("SELECT * FROM transactionList WHERE hash LIKE :taxHash AND isReceive LIKE :isReceive")
	fun getByTaxHashAndReceivedStatus(
		taxHash: String,
		isReceive: Boolean
	): TransactionTable?
	
	@Query(
		"SELECT * FROM transactionList WHERE recordOwnerAddress LIKE :walletAddress AND contractAddress LIKE :contract ORDER BY timeStamp DESC"
	)
	fun getByAddressAndContract(
		walletAddress: String,
		contract: String
	): List<TransactionTable>
	
	@Query(
		"SELECT * FROM transactionList WHERE recordOwnerAddress LIKE :walletAddress AND contractAddress LIKE :contract AND chainID LIKE :chainID ORDER BY timeStamp DESC"
	)
	fun getCurrentChainByAddressAndContract(
		walletAddress: String,
		contract: String,
		chainID: String = Config.getCurrentChain()
	): List<TransactionTable>
	
	@Insert
	fun insert(token: TransactionTable)
	
	@Update
	fun update(token: TransactionTable)
	
	@Delete
	fun delete(token: TransactionTable)
}