package io.goldstone.blinnnk.kernel.commontable

import android.arch.persistence.room.*
import com.blinnnk.extension.isNotNull
import com.blinnnk.extension.orZero
import com.blinnnk.extension.safeGet
import com.blinnnk.extension.toIntOrZero
import com.blinnnk.util.load
import com.blinnnk.util.then
import io.goldstone.blinnnk.common.sharedpreference.SharedAddress
import io.goldstone.blinnnk.common.sharedpreference.SharedChain
import io.goldstone.blinnnk.common.value.DataValue
import io.goldstone.blinnnk.crypto.multichain.ChainID
import io.goldstone.blinnnk.crypto.multichain.CoinSymbol
import io.goldstone.blinnnk.crypto.multichain.CryptoValue
import io.goldstone.blinnnk.crypto.multichain.TokenContract
import io.goldstone.blinnnk.crypto.utils.CryptoUtils
import io.goldstone.blinnnk.crypto.utils.hexToDecimal
import io.goldstone.blinnnk.crypto.utils.toDecimalFromHex
import io.goldstone.blinnnk.kernel.commontable.model.ETCTransactionModel
import io.goldstone.blinnnk.kernel.database.GoldStoneDataBase
import io.goldstone.blinnnk.module.home.wallet.transactions.transactionlist.ethereumtransactionlist.model.ERC20TransactionModel
import io.goldstone.blinnnk.module.home.wallet.transactions.transactionlist.ethereumtransactionlist.model.ETHTransactionModel
import io.goldstone.blinnnk.module.home.wallet.transactions.transactionlist.ethereumtransactionlist.model.TransactionListModel
import io.goldstone.blinnnk.module.home.wallet.transactions.transactionlist.ethereumtransactionlist.model.getMemoFromInputCode
import org.json.JSONObject
import java.io.Serializable
import java.math.BigInteger

/**
 * @date 07/04/2018 7:32 PM
 * @author KaySaith
 */
@Entity(tableName = "transactionList", primaryKeys = ["hash", "fromAddress", "isFee"])
data class TransactionTable(
	var blockNumber: Int,
	var timeStamp: String,
	var hash: String,
	var nonce: String,
	var blockHash: String,
	var transactionIndex: String,
	var fromAddress: String,
	var to: String,
	var value: String,
	var count: Double, // 精度处理后的 `Count`
	var gas: String,
	var gasPrice: String,
	var hasError: String,
	var txReceiptStatus: String,
	var input: String,
	var contractAddress: String,
	var cumulativeGasUsed: String,
	var gasUsed: String,
	var confirmations: String,
	var isReceive: Boolean,
	var isERC20Token: Boolean,
	var symbol: String,
	var recordOwnerAddress: String,
	var isPending: Boolean,
	var memo: String,
	var chainID: String,
	var isFee: Boolean,
	var minerFee: String
) : Serializable {

	constructor(data: TransactionTable) : this(
		data.blockNumber,
		data.timeStamp,
		data.hash,
		data.nonce,
		data.blockHash,
		data.transactionIndex,
		data.fromAddress,
		data.to,
		data.value,
		CryptoUtils.toCountByDecimal(BigInteger(data.value), CryptoValue.ethDecimal),
		data.gas,
		data.gasPrice,
		data.hasError,
		data.txReceiptStatus,
		data.input,
		// 奇怪的情况, 极其少见的情况下自己给自己转账解析为 Contract 转账, 待研究
		// eg: https://ropsten.etherscan.io/tx/0xbda369f2ddc689d6039bf2dc96fc73af2bee1be8a87d88ceb4a9b552066f92ff
		if (CryptoUtils.isERC20Transfer(data.input)) data.to
		else TokenContract.ethContract,
		data.cumulativeGasUsed,
		data.gasUsed,
		data.confirmations,
		data.fromAddress != SharedAddress.getCurrentEthereum(),
		CryptoUtils.isERC20Transfer(data.input),
		data.symbol,
		SharedAddress.getCurrentEthereum(),
		false,
		"", // `EtherScan` 的 `Input` 只有 `138` 位需要额外去查询 Memo
		SharedChain.getCurrentETH().chainID.id,
		false,
		CryptoUtils.toGasUsedEther(data.gas, data.gasPrice, true)
	)

	// 这个是专门为入账的 `ETH Token` 准备的
	constructor(data: ETHTransactionModel) : this(
		data.blockNumber.toIntOrZero(),
		data.timeStamp,
		data.transactionHash,
		data.nonce,
		data.blockHash,
		data.transactionIndex,
		data.from,
		if (CryptoUtils.isERC20Transfer(data.input))
			CryptoUtils.getTransferInfoFromInputData(data.input)?.address.orEmpty()
		else data.to,
		if (CryptoUtils.isERC20Transfer(data.input))
			CryptoUtils.getTransferInfoFromInputData(data.input)?.amount.orZero().toString()
		else data.value,
		if (CryptoUtils.isERC20Transfer(data.input)) 0.0
		else CryptoUtils.toCountByDecimal(BigInteger(data.value), CryptoValue.ethDecimal),
		data.gasUsed,
		data.gasPrice,
		data.isError,
		data.txReceiptStatus,
		data.input,
		if (CryptoUtils.isERC20Transfer(data.input)) data.to else TokenContract.ethContract,
		data.cumulativeGasUsed,
		data.gasUsed,
		data.confirmations,
		data.to == SharedAddress.getCurrentEthereum(),
		CryptoUtils.isERC20Transfer(data.input),
		CoinSymbol.ETH.symbol,
		SharedAddress.getCurrentEthereum(),
		false,
		"",
		SharedChain.getCurrentETH().chainID.id,
		CryptoUtils.isERC20Transfer(data.input), // 是 `ERC20` 就是燃气费
		CryptoUtils.toGasUsedEther(data.gas, data.gasPrice, false)
	)

	// 这个是专门为入账的 `ERC20 Token` 准备的
	constructor(data: ERC20TransactionModel) : this(
		data.blockNumber.toIntOrZero(),
		data.timeStamp,
		data.transactionHash,
		data.nonce,
		data.blockHash,
		data.transactionIndex,
		data.from,
		data.to,
		data.value,
		CryptoUtils.toCountByDecimal(BigInteger(data.value), data.tokenDecimal.toIntOrZero()),
		data.gasUsed,
		data.gasPrice,
		"0",
		"1",
		"",
		data.contract,
		data.cumulativeGasUsed,
		data.gasUsed,
		data.confirmations,
		data.to == SharedAddress.getCurrentEthereum(),
		true,
		data.tokenSymbol,
		SharedAddress.getCurrentEthereum(),
		false,
		"",
		SharedChain.getCurrentETH().chainID.id,
		false,
		CryptoUtils.toGasUsedEther(data.gas, data.gasPrice, false)
	)

	constructor(
		data: JSONObject,
		decimal: Int,
		chainID: ChainID
	) : this(
		data.safeGet("blockNumber").toDecimalFromHex().toIntOrZero(),
		"",
		data.safeGet("hash"),
		data.safeGet("nonce").toDecimalFromHex(),
		data.safeGet("blockHash"),
		data.safeGet("transactionIndex").toDecimalFromHex(),
		data.safeGet("from"),
		data.safeGet("to"),
		data.safeGet("value").toDecimalFromHex(),
		if (CryptoUtils.isERC20Transfer(data.safeGet("input"))) {
			val amount = CryptoUtils.getTransferInfoFromInputData(data.safeGet("input"))?.amount
			if (amount.isNotNull()) CryptoUtils.toCountByDecimal(amount, decimal) else 0.0
		} else {
			CryptoUtils.toCountByDecimal(BigInteger(data.safeGet("value").toDecimalFromHex()), decimal)
		},
		data.safeGet("gas").toDecimalFromHex(),
		data.safeGet("gasPrice").toDecimalFromHex(),
		"0",
		"1",
		data.safeGet("input"),
		when {
			chainID.isETCSeries() -> TokenContract.etcContract
			CryptoUtils.isERC20Transfer(data.safeGet("input")) -> data.safeGet("to")
			else -> TokenContract.ethContract
		},
		cumulativeGasUsed = "",
		gasUsed = "",
		confirmations = "",
		isReceive = !data.safeGet("from").equals(
			if (chainID.isETCSeries()) SharedAddress.getCurrentETC()
			else SharedAddress.getCurrentEthereum(),
			true
		),
		isERC20Token = CryptoUtils.isERC20Transfer(data.safeGet("input")),
		symbol = "",
		recordOwnerAddress =
		if (chainID.isETCSeries()) SharedAddress.getCurrentETC()
		else SharedAddress.getCurrentEthereum(),
		isPending = false,
		memo = getMemoFromInputCode(data.safeGet("input")),
		chainID = chainID.id,
		isFee = false,
		minerFee = CryptoUtils.toGasUsedEther(data.safeGet("gas"), data.safeGet("gasPrice"), true)
	)

	constructor(data: ETCTransactionModel) : this(
		data.blockNumber.hexToDecimal().toInt(),
		data.timestamp.hexToDecimal().toString(),
		data.hash,
		data.nonce.hexToDecimal().toString(),
		data.blockHash,
		data.transactionIndex.hexToDecimal().toString(),
		data.from,
		data.to,
		"${data.value.hexToDecimal()}",
		CryptoUtils.toCountByDecimal(
			data.value.hexToDecimal(),
			CryptoValue.ethDecimal
		),
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
		false,
		"",
		chainID = SharedChain.getETCCurrent().chainID.id,
		isFee = data.isFee,
		minerFee = CryptoUtils.toGasUsedEther(data.gas, data.gasPrice, true)
	)

	companion object {
		@JvmField
		val dao = GoldStoneDataBase.database.transactionDao()

		fun getETCTransactions(address: String, hold: (List<TransactionListModel>) -> Unit) {
			load {
				GoldStoneDataBase.database.transactionDao().getETCTransactionsByAddress(address)
			} then { data ->
				hold(data.map { TransactionListModel(it) })
			}
		}
	}
}

@Dao
interface TransactionDao {

	@Query("SELECT * FROM transactionList")
	fun getAll(): List<TransactionTable>

	@Query("UPDATE transactionList SET symbol =:symbol, count = :count WHERE hash = :txHash AND isFee = :isFee")
	fun updateFeeInfo(symbol: String, count: Double, txHash: String, isFee: Boolean = true)

	@Query("UPDATE transactionList SET hasError =:hasError, isPending = :isPending WHERE hash = :txHash AND fromAddress = :fromAddress")
	fun updateErrorStatus(hasError: String, txHash: String, fromAddress: String, isPending: Boolean = false)

	@Query("UPDATE transactionList SET blockNumber =:blockNumber, isPending = :isPending WHERE hash = :txHash AND fromAddress = :fromAddress")
	fun updateBlockNumber(blockNumber: Int, txHash: String, fromAddress: String, isPending: Boolean)

	@Query("UPDATE transactionList SET confirmations = :confirmationCount, isPending = :isPending WHERE hash = :txHash AND fromAddress = :fromAddress")
	fun updateConfirmationCount(confirmationCount: Int, txHash: String, fromAddress: String, isPending: Boolean)

	@Query("UPDATE transactionList SET input =:input, memo = :memo WHERE hash = :hash AND isReceive = :isReceive AND isFee = :isFee")
	fun updateInputCodeAndMemo(input: String, memo: String, hash: String, isReceive: Boolean, isFee: Boolean)

	@Query("UPDATE transactionList SET memo =:memo WHERE hash = :txHash AND isFee = :isFee")
	fun updateFeeMemo(txHash: String, memo: String, isFee: Boolean = true)

	@Query("SELECT * FROM transactionList WHERE recordOwnerAddress = :walletAddress AND chainID = :chainID ORDER BY timeStamp DESC")
	fun getTransactionsByAddress(walletAddress: String, chainID: String): List<TransactionTable>

	@Query("SELECT MAX(blockNumber) FROM transactionList WHERE recordOwnerAddress = :address AND chainID = :chainID")
	fun getMyMaxBlockNumber(address: String, chainID: String): Int?

	@Query("SELECT * FROM transactionList WHERE recordOwnerAddress = :walletAddress AND chainID = :chainID AND symbol = :symbol ORDER BY timeStamp DESC")
	fun getETCTransactionsByAddress(walletAddress: String, symbol: String = CoinSymbol.etc, chainID: String = SharedChain.getETCCurrent().chainID.id): List<TransactionTable>

	@Query("SELECT * FROM transactionList WHERE recordOwnerAddress = :walletAddress ORDER BY timeStamp DESC")
	fun getAllTransactionsByAddress(walletAddress: String): List<TransactionTable>

	@Query("SELECT * FROM transactionList WHERE hash = :taxHash AND isReceive = :isReceive AND isFee = :isFee")
	fun getByTaxHashAndReceivedStatus(taxHash: String, isReceive: Boolean, isFee: Boolean): TransactionTable?

	@Query("SELECT * FROM transactionList WHERE recordOwnerAddress = :walletAddress AND contractAddress LIKE :contract AND chainID LIKE :chainID AND isFee = :isFee ORDER BY timeStamp DESC")
	fun getByAddressAndContract(walletAddress: String, contract: String, chainID: String, isFee: Boolean = false): List<TransactionTable>

	@Query("SELECT * FROM transactionList WHERE recordOwnerAddress = :walletAddress AND contractAddress LIKE :contract AND chainID LIKE :chainID AND blockNumber <= :blockNumber ORDER BY timeStamp DESC")
	fun getDataWithFee(walletAddress: String, contract: String, chainID: String, blockNumber: Int): List<TransactionTable>

	@Query("SELECT * FROM transactionList WHERE recordOwnerAddress = :walletAddress AND chainID = :chainID AND (contractAddress LIKE :contract OR isFee = 1) AND blockNumber <= :endBlock ORDER BY timeStamp DESC LIMIT :pageCount")
	fun getETHAndAllFee(walletAddress: String, contract: String, endBlock: Int, chainID: String, pageCount: Int = DataValue.pageCount): List<TransactionTable>

	@Query("SELECT MAX(blockNumber) FROM transactionList WHERE recordOwnerAddress = :address AND (contractAddress = :contract OR isFee = 1) AND chainID = :chainID")
	fun getMaxBlockNumber(address: String, contract: String, chainID: String): Int?

	@Query("SELECT timeStamp FROM transactionList WHERE blockNumber = (SELECT MAX(blockNumber) FROM transactionList WHERE recordOwnerAddress = :address AND (contractAddress = :contract OR isFee = 1) AND chainID = :chainID)")
	fun getLatestTimeStamp(address: String, contract: String, chainID: String): String?

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	fun insert(token: TransactionTable)

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	fun insertAll(tokens: List<TransactionTable>)

	@Update
	fun update(token: TransactionTable)

	@Delete
	fun delete(token: TransactionTable)

	@Delete
	fun deleteAll(token: List<TransactionTable>)

	@Query("DELETE FROM transactionList WHERE recordOwnerAddress = :recordAddress")
	fun deleteRecordAddressData(recordAddress: String)
}