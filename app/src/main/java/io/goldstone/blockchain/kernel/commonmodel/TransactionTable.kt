package io.goldstone.blockchain.kernel.commonmodel

import android.arch.persistence.room.*
import com.blinnnk.extension.isNull
import com.blinnnk.extension.safeGet
import com.blinnnk.extension.toArrayList
import com.blinnnk.util.ConcurrentCombine
import com.blinnnk.util.coroutinesTask
import com.google.gson.annotations.SerializedName
import io.goldstone.blockchain.common.utils.LogUtil
import io.goldstone.blockchain.common.value.Config
import io.goldstone.blockchain.crypto.CryptoSymbol
import io.goldstone.blockchain.crypto.CryptoValue
import io.goldstone.blockchain.crypto.utils.CryptoUtils
import io.goldstone.blockchain.crypto.utils.hexToDecimal
import io.goldstone.blockchain.crypto.utils.toDecimalFromHex
import io.goldstone.blockchain.crypto.utils.toIntFromHex
import io.goldstone.blockchain.kernel.database.GoldStoneDataBase
import io.goldstone.blockchain.kernel.network.GoldStoneAPI
import io.goldstone.blockchain.kernel.network.GoldStoneEthCall
import io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.model.ETCTransactionModel
import io.goldstone.blockchain.module.home.wallet.transactions.transactionlist.ethereumtransactionlist.model.ERC20TransactionModel
import io.goldstone.blockchain.module.home.wallet.transactions.transactionlist.ethereumtransactionlist.model.TransactionListModel
import io.goldstone.blockchain.module.home.wallet.transactions.transactionlist.ethereumtransactionlist.model.getMemoFromInputCode
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.runOnUiThread
import org.json.JSONObject

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
	var isERC20Token: Boolean,
	var symbol: String,
	var recordOwnerAddress: String,
	var tokenReceiveAddress: String? = null, // `Contract` 内的真实接收地址
	var isPending: Boolean = false,
	var logIndex: String = "",
	var memo: String = "",
	var chainID: String = Config.getCurrentChain(),
	var isFee: Boolean = false,
	var isFailed: Boolean = false,
	var minerFee: String = ""
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
		data.gasUsed,
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
	
	constructor(
		data: JSONObject,
		isETC: Boolean = false,
		chainID: String = Config.getCurrentChain()
	) : this(
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
		when {
			isETC -> CryptoValue.etcContract
			CryptoUtils.isERC20TransferByInputCode(
				data.safeGet
				("input")
			) -> data.safeGet("to")
			else -> CryptoValue.ethContract
		},
		"",
		"",
		"",
		data.safeGet("from") != Config.getCurrentAddress(),
		CryptoUtils.isERC20TransferByInputCode(data.safeGet("input")),
		"",
		Config.getCurrentAddress(),
		minerFee = CryptoUtils.toGasUsedEther(data.safeGet("gas"), data.safeGet("gasPrice")),
		chainID = chainID
	)
	
	constructor(data: ETCTransactionModel) : this(
		0,
		data.blockNumber.toIntFromHex().toString(),
		data.timestamp.hexToDecimal().toString(),
		data.hash,
		data.nonce.toIntFromHex().toString(),
		data.blockHash,
		data.transactionIndex.hexToDecimal().toString(),
		data.from,
		data.to,
		CryptoUtils.toCountByDecimal(
			data.value.hexToDecimal(),
			CryptoValue.ethDecimal
		).toString(),
		data.gas.hexToDecimal().toString(),
		data.gasPrice.hexToDecimal().toString(),
		"0",
		"1",
		data.input,
		CryptoValue.etcContract,
		"",
		"0",
		"",
		data.from != Config.getCurrentAddress(),
		false,
		CryptoSymbol.etc,
		Config.getCurrentAddress(),
		tokenReceiveAddress = data.to,
		chainID = Config.getETCCurrentChain(),
		isFee = data.isFee,
		minerFee = CryptoUtils.toGasUsedEther(data.gas, data.gasPrice)
	)
	
	companion object {
		
		fun updateModelInfo(
			transaction: TransactionTable,
			isERC20Token: Boolean,
			symbol: String,
			value: String,
			tokenReceiveAddress: String?
		) {
			transaction.apply {
				this.isReceive = Config.getCurrentAddress().equals(tokenReceiveAddress, true)
				this.isERC20Token = isERC20Token
				this.symbol = symbol
				this.value = value
				this.tokenReceiveAddress = tokenReceiveAddress
				this.recordOwnerAddress = Config.getCurrentAddress()
				this.minerFee = CryptoUtils.toGasUsedEther(gas, gasPrice, false)
			}
		}
		
		// `ERC` 类型的 `Transactions` 专用
		fun getERCTransactionsByAddress(
			address: String,
			hold: (ArrayList<TransactionListModel>) -> Unit
		) {
			coroutinesTask(
				{
					GoldStoneDataBase
						.database
						.transactionDao()
						.getTransactionsByAddress(address, chainID = Config.getCurrentChain())
				}) {
				val result = if (it.isEmpty()) {
					arrayListOf()
				} else {
					it.map { TransactionListModel(it) }.toArrayList()
				}
				hold(result)
			}
		}
		
		fun getETCTransactionsByAddress(
			address: String,
			hold: (ArrayList<TransactionListModel>) -> Unit
		) {
			coroutinesTask(
				{
					GoldStoneDataBase
						.database
						.transactionDao()
						.getETCTransactionsByAddress(address)
				}) {
				val result = if (it.isEmpty()) {
					arrayListOf()
				} else {
					it.map { TransactionListModel(it) }.toArrayList()
				}
				hold(result)
			}
		}
		
		fun getCurrentChainByAddressAndContract(
			walletAddress: String,
			contract: String,
			chainID: String,
			hold: (ArrayList<TransactionListModel>) -> Unit
		) {
			doAsync {
				var transactions = GoldStoneDataBase
					.database
					.transactionDao()
					.getCurrentChainByAddressAndContract(walletAddress, contract, chainID)
				// 如果是 `ETH` or `ETC` 需要查询出所有相关的 `Miner` 作为账单记录
				var fee = listOf<TransactionTable>()
				if (!CryptoValue.isToken(contract)) {
					fee = GoldStoneDataBase
						.database
						.transactionDao()
						.getCurrentChainFee(walletAddress, true, chainID)
				}
				transactions += fee.filter { CryptoValue.isToken(it.contractAddress) }
				GoldStoneAPI.context.runOnUiThread {
					hold(
						if (
							CryptoValue.isToken(contract)
						) {
							transactions.filter {
								!it.isFee
							}.map {
								TransactionListModel(it)
							}.sortedByDescending {
								it.timeStamp
							}.toArrayList()
						} else {
							transactions.map {
								TransactionListModel(it)
							}.sortedByDescending {
								it.timeStamp
							}.toArrayList()
						}
					)
				}
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
		
		fun getMemoByHashAndReceiveStatus(
			hash: String,
			isReceive: Boolean,
			chainName: String,
			callback: (memo: String) -> Unit
		) {
			TransactionTable.getByHashAndReceivedStatus(hash, isReceive) { transaction ->
				if (transaction?.memo?.isNotEmpty() == true) {
					callback(transaction.memo)
				} else {
					GoldStoneEthCall.apply {
						getInputCodeByHash(
							hash,
							{ error, reason ->
								LogUtil.error("getMemoByHashAndReceiveStatus $reason", error)
							},
							chainName
						) { input ->
							val isErc20Token = CryptoUtils.isERC20TransferByInputCode(input)
							val memo = getMemoFromInputCode(input, isErc20Token)
							// 如果数据库有这条数据那么更新 `Memo` 和 `Input`
							// TODO ETC 类型的数据需要维护数据库插入
							if (!transaction.isNull()) {
								GoldStoneDataBase.database.transactionDao().update(transaction!!.apply {
									this.input = input
									this.memo = memo
								})
							}
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
		chainID: String
	): List<TransactionTable>
	
	@Query(
		"SELECT * FROM transactionList WHERE recordOwnerAddress LIKE :walletAddress AND chainID LIKE :chainID AND symbol LIKE :symbol ORDER BY timeStamp DESC"
	)
	fun getETCTransactionsByAddress(
		walletAddress: String,
		symbol: String = CryptoSymbol.etc,
		chainID: String = Config.getETCCurrentChain()
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
	
	@Query(
		"SELECT * FROM transactionList WHERE recordOwnerAddress LIKE :walletAddress AND isFee LIKE :isFee AND chainID LIKE :chainID ORDER BY timeStamp DESC"
	)
	fun getCurrentChainFee(
		walletAddress: String,
		isFee: Boolean,
		chainID: String
	): List<TransactionTable>
	
	@Insert
	fun insert(token: TransactionTable)
	
	@Update
	fun update(token: TransactionTable)
	
	@Delete
	fun delete(token: TransactionTable)
}