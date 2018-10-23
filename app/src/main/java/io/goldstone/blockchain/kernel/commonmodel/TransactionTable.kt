package io.goldstone.blockchain.kernel.commonmodel

import android.arch.persistence.room.*
import android.support.annotation.UiThread
import com.blinnnk.extension.isNull
import com.blinnnk.extension.safeGet
import com.google.gson.annotations.SerializedName
import io.goldstone.blockchain.common.error.EthereumRPCError
import io.goldstone.blockchain.common.sharedpreference.SharedAddress
import io.goldstone.blockchain.common.sharedpreference.SharedChain
import io.goldstone.blockchain.common.utils.load
import io.goldstone.blockchain.common.utils.then
import io.goldstone.blockchain.crypto.multichain.*
import io.goldstone.blockchain.crypto.utils.CryptoUtils
import io.goldstone.blockchain.crypto.utils.hexToDecimal
import io.goldstone.blockchain.crypto.utils.toDecimalFromHex
import io.goldstone.blockchain.kernel.database.GoldStoneDataBase
import io.goldstone.blockchain.kernel.network.common.GoldStoneAPI
import io.goldstone.blockchain.kernel.network.ethereum.GoldStoneEthCall
import io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.model.ETCTransactionModel
import io.goldstone.blockchain.module.home.wallet.transactions.transactionlist.ethereumtransactionlist.model.ERC20TransactionModel
import io.goldstone.blockchain.module.home.wallet.transactions.transactionlist.ethereumtransactionlist.model.TransactionListModel
import io.goldstone.blockchain.module.home.wallet.transactions.transactionlist.ethereumtransactionlist.model.getMemoFromInputCode
import org.jetbrains.anko.runOnUiThread
import org.json.JSONObject
import java.io.Serializable

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
	var txReceiptStatus: String,
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
	var chainID: String = SharedChain.getCurrentETH().id,
	var isFee: Boolean = false,
	var isFailed: Boolean = false,
	var minerFee: String = ""
) : Serializable {

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
		data.txReceiptStatus,
		data.input,
		// 奇怪的情况, 极其少见的情况下自己给自己转账解析为 Contract 转账, 待研究
		// eg: https://ropsten.etherscan.io/tx/0xbda369f2ddc689d6039bf2dc96fc73af2bee1be8a87d88ceb4a9b552066f92ff
		if (CryptoUtils.isERC20TransferByInputCode(data.input)) data.to
		else TokenContract.ethContract,
		data.cumulativeGasUsed,
		data.gasUsed,
		data.confirmations,
		data.fromAddress != SharedAddress.getCurrentEthereum(),
		CryptoUtils.isERC20TransferByInputCode(data.input),
		data.symbol,
		SharedAddress.getCurrentEthereum()
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
		SharedAddress.getCurrentEthereum(),
		data.to,
		false,
		data.logIndex
	)

	constructor(
		data: JSONObject,
		isETC: Boolean = false,
		chainID: String = SharedChain.getCurrentETH().id
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
			isETC -> TokenContract.etcContract
			CryptoUtils.isERC20TransferByInputCode(data.safeGet("input")) -> data.safeGet("to")
			else -> TokenContract.ethContract
		},
		"",
		"",
		"",
		!data.safeGet("from").equals(
			if (isETC) SharedAddress.getCurrentETC() else SharedAddress.getCurrentEthereum(),
			true
		),
		CryptoUtils.isERC20TransferByInputCode(data.safeGet("input")),
		"",
		if (isETC) SharedAddress.getCurrentETC() else SharedAddress.getCurrentEthereum(),
		minerFee = CryptoUtils.toGasUsedEther(data.safeGet("gas"), data.safeGet("gasPrice")),
		chainID = chainID
	)

	constructor(data: ETCTransactionModel) : this(
		0,
		data.blockNumber.hexToDecimal().toString(),
		data.timestamp.hexToDecimal().toString(),
		data.hash,
		data.nonce.hexToDecimal().toString(),
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
		TokenContract.etcContract,
		"",
		"0",
		"",
		!data.from.equals(SharedAddress.getCurrentETC(), true),
		false,
		CoinSymbol.etc,
		SharedAddress.getCurrentETC(),
		tokenReceiveAddress = data.to,
		chainID = SharedChain.getETCCurrent().id,
		isFee = data.isFee,
		minerFee = CryptoUtils.toGasUsedEther(data.gas, data.gasPrice)
	)

	fun updateModelInfo(
		isERC20Token: Boolean,
		symbol: String,
		value: String,
		tokenReceiveAddress: String?
	) {
		this.isReceive = SharedAddress.getCurrentEthereum().equals(tokenReceiveAddress, true)
		this.isERC20Token = isERC20Token
		this.symbol = symbol
		this.value = value
		this.tokenReceiveAddress = tokenReceiveAddress
		this.recordOwnerAddress = SharedAddress.getCurrentEthereum()
		this.minerFee = CryptoUtils.toGasUsedEther(gas, gasPrice, false)
	}

	companion object {
		// `ERC` 类型的 `Transactions` 专用
		fun getTokenTransactions(address: String, @UiThread hold: (List<TransactionListModel>) -> Unit) {
			load {
				GoldStoneDataBase.database.transactionDao().getTransactionsByAddress(address, SharedChain.getCurrentETH().id)
			} then { it ->
				hold(it.map { TransactionListModel(it) })
			}
		}

		fun getETCTransactions(address: String, hold: (List<TransactionListModel>) -> Unit) {
			load {
				GoldStoneDataBase.database.transactionDao().getETCTransactionsByAddress(address)
			} then { data ->
				hold(data.map { TransactionListModel(it) })
			}
		}

		fun getByAddressAndContract(
			walletAddress: String,
			contract: TokenContract,
			hold: (List<TransactionListModel>) -> Unit
		) {
			val chainID = contract.getCurrentChainID().id
			load {
				val dao = GoldStoneDataBase.database.transactionDao()
				var transactions = dao.getByAddressAndContract(walletAddress, contract.contract.orEmpty(), chainID)
				// 如果是 `ETH` or `ETC` 需要查询出所有相关的 `Miner` 作为账单记录
				var fee = listOf<TransactionTable>()
				if (!contract.isERC20Token()) {
					fee = dao.getCurrentChainFee(walletAddress, true, chainID)
				}
				transactions += fee.filter { TokenContract(it.contractAddress).isERC20Token() }
				transactions
			} then { transactions ->
				hold(
					if (contract.isERC20Token()) transactions.asSequence().filter { !it.isFee }.map {
						TransactionListModel(it)
					}.sortedByDescending { it.timeStamp }.toList()
					else transactions.asSequence().map { TransactionListModel(it) }.sortedByDescending { it.timeStamp }.toList()
				)
			}
		}

		fun getMemoByHashAndReceiveStatus(
			hash: String,
			isReceive: Boolean,
			chainName: String,
			errorCallback: (EthereumRPCError) -> Unit,
			callback: (memo: String) -> Unit
		) {
			TransactionTable.getByHashAndReceivedStatus(hash, isReceive) { transaction ->
				if (transaction?.memo?.isNotEmpty() == true) {
					callback(transaction.memo)
				} else {
					GoldStoneEthCall.apply {
						getInputCodeByHash(
							hash,
							errorCallback,
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

		fun getTransactionByHash(taxHash: String, hold: (List<TransactionTable>) -> Unit) {
			GoldStoneDataBase.database.transactionDao().apply {
				hold(getTransactionByTaxHash(taxHash))
			}
		}

		fun getByHashAndReceivedStatus(hash: String, isReceived: Boolean, hold: (TransactionTable?) -> Unit) {
			load {
				GoldStoneDataBase.database.transactionDao().getByTaxHashAndReceivedStatus(hash, isReceived)
			} then (hold)
		}
	}
}

@Dao
interface TransactionDao {

	@Query("SELECT * FROM transactionList")
	fun getAll(): List<TransactionTable>

	@Query("SELECT * FROM transactionList WHERE recordOwnerAddress LIKE :walletAddress AND chainID LIKE :chainID ORDER BY timeStamp DESC")
	fun getTransactionsByAddress(walletAddress: String, chainID: String): List<TransactionTable>

	@Query("SELECT * FROM transactionList WHERE recordOwnerAddress LIKE :walletAddress AND chainID LIKE :chainID AND symbol LIKE :symbol ORDER BY timeStamp DESC")
	fun getETCTransactionsByAddress(walletAddress: String, symbol: String = CoinSymbol.etc, chainID: String = SharedChain.getETCCurrent().id): List<TransactionTable>

	@Query("SELECT * FROM transactionList WHERE recordOwnerAddress LIKE :walletAddress ORDER BY timeStamp DESC")
	fun getAllTransactionsByAddress(walletAddress: String): List<TransactionTable>

	@Query("SELECT * FROM transactionList WHERE hash LIKE :taxHash")
	fun getTransactionByTaxHash(taxHash: String): List<TransactionTable>

	@Query("SELECT * FROM transactionList WHERE hash LIKE :taxHash AND isReceive LIKE :isReceive")
	fun getByTaxHashAndReceivedStatus(taxHash: String, isReceive: Boolean): TransactionTable?

	@Query("SELECT * FROM transactionList WHERE recordOwnerAddress LIKE :walletAddress AND contractAddress LIKE :contract ORDER BY timeStamp DESC")
	fun getByAddressAndContract(walletAddress: String, contract: String): List<TransactionTable>

	@Query("SELECT * FROM transactionList WHERE recordOwnerAddress LIKE :walletAddress AND contractAddress LIKE :contract AND chainID LIKE :chainID ORDER BY timeStamp DESC")
	fun getByAddressAndContract(walletAddress: String, contract: String, chainID: String): List<TransactionTable>

	@Query("SELECT * FROM transactionList WHERE recordOwnerAddress LIKE :walletAddress AND isFee LIKE :isFee AND chainID LIKE :chainID ORDER BY timeStamp DESC")
	fun getCurrentChainFee(walletAddress: String, isFee: Boolean, chainID: String): List<TransactionTable>

	@Insert
	fun insert(token: TransactionTable)

	@Insert
	fun insertAll(tokens: List<TransactionTable>)

	@Update
	fun update(token: TransactionTable)

	@Delete
	fun delete(token: TransactionTable)

	@Delete
	fun deleteAll(token: List<TransactionTable>)

	@Query("DELETE FROM transactionList WHERE recordOwnerAddress IN (:addresses)")
	fun deleteAllByAddress(addresses: List<String>)

	@Query("DELETE FROM transactionList WHERE recordOwnerAddress = :recordAddress")
	fun deleteRecordAddressData(recordAddress: String)
}