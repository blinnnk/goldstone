package io.goldstone.blockchain.kernel.commonmodel

import android.arch.persistence.room.*
import android.support.annotation.UiThread
import android.support.annotation.WorkerThread
import com.blinnnk.extension.orZero
import com.blinnnk.extension.safeGet
import com.blinnnk.extension.toIntOrZero
import io.goldstone.blockchain.common.error.RequestError
import io.goldstone.blockchain.common.sharedpreference.SharedAddress
import io.goldstone.blockchain.common.sharedpreference.SharedChain
import io.goldstone.blockchain.common.utils.load
import io.goldstone.blockchain.common.utils.then
import io.goldstone.blockchain.crypto.multichain.ChainID
import io.goldstone.blockchain.crypto.multichain.CoinSymbol
import io.goldstone.blockchain.crypto.multichain.CryptoValue
import io.goldstone.blockchain.crypto.multichain.TokenContract
import io.goldstone.blockchain.crypto.multichain.node.ChainURL
import io.goldstone.blockchain.crypto.utils.CryptoUtils
import io.goldstone.blockchain.crypto.utils.hexToDecimal
import io.goldstone.blockchain.crypto.utils.toDecimalFromHex
import io.goldstone.blockchain.kernel.database.GoldStoneDataBase
import io.goldstone.blockchain.kernel.network.ethereum.GoldStoneEthCall
import io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.model.ETCTransactionModel
import io.goldstone.blockchain.module.home.wallet.transactions.transactionlist.ethereumtransactionlist.model.ERC20TransactionModel
import io.goldstone.blockchain.module.home.wallet.transactions.transactionlist.ethereumtransactionlist.model.ETHTransactionModel
import io.goldstone.blockchain.module.home.wallet.transactions.transactionlist.ethereumtransactionlist.model.TransactionListModel
import io.goldstone.blockchain.module.home.wallet.transactions.transactionlist.ethereumtransactionlist.model.getMemoFromInputCode
import org.jetbrains.anko.doAsync
import org.json.JSONObject
import java.io.Serializable
import java.math.BigInteger

/**
 * @date 07/04/2018 7:32 PM
 * @author KaySaith
 */
@Entity(tableName = "transactionList", primaryKeys = ["hash", "fromAddress", "isFee"])
data class TransactionTable(
	var blockNumber: String,
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
		data.blockNumber,
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
		CoinSymbol.ETH.symbol!!,
		SharedAddress.getCurrentEthereum(),
		false,
		"",
		SharedChain.getCurrentETH().chainID.id,
		CryptoUtils.isERC20Transfer(data.input), // 是 `ERC20` 就是燃气费
		CryptoUtils.toGasUsedEther(data.gas, data.gasPrice, false)
	)

	// 这个是专门为入账的 `ERC20 Token` 准备的
	constructor(data: ERC20TransactionModel) : this(
		data.blockNumber,
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
		data.safeGet("blockNumber").toDecimalFromHex(),
		"",
		data.safeGet("hash"),
		data.safeGet("nonce").toDecimalFromHex(),
		data.safeGet("blockHash"),
		data.safeGet("transactionIndex").toDecimalFromHex(),
		data.safeGet("from"),
		data.safeGet("to"),
		data.safeGet("value").toDecimalFromHex(),
		CryptoUtils.toCountByDecimal(BigInteger(data.safeGet("value").toDecimalFromHex()), decimal),
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
		memo = "",
		chainID = chainID.id,
		isFee = false,
		minerFee = CryptoUtils.toGasUsedEther(data.safeGet("gas"), data.safeGet("gasPrice"), true)
	)

	constructor(data: ETCTransactionModel) : this(
		data.blockNumber.hexToDecimal().toString(),
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
		// `ERC` 类型的 `Transactions` 专用
		fun getTokenTransactions(address: String, @UiThread hold: (List<TransactionListModel>) -> Unit) {
			load {
				GoldStoneDataBase.database.transactionDao().getTransactionsByAddress(address, SharedChain.getCurrentETH().chainID.id)
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

		fun getMemoByHashAndReceiveStatus(
			hash: String,
			isReceive: Boolean,
			isFee: Boolean,
			chainURL: ChainURL,
			@WorkerThread hold: (memo: String?, error: RequestError) -> Unit
		) {
			doAsync {
				val transactionDao = GoldStoneDataBase.database.transactionDao()
				val transaction =
					transactionDao.getByTaxHashAndReceivedStatus(hash, isReceive, isFee) ?: return@doAsync
				if (transaction.memo.isNotEmpty()) {
					hold(transaction.memo, RequestError.None)
				} else GoldStoneEthCall.getInputCodeByHash(hash, chainURL) { inputCode, error ->
					if (inputCode != null && error.isNone()) {
						val isErc20Token = CryptoUtils.isERC20Transfer(inputCode)
						val memo = getMemoFromInputCode(inputCode, isErc20Token)
						// 如果数据库有这条数据那么更新 `Memo` 和 `Input`
						transactionDao.update(
							transaction.apply {
								this.input = inputCode
								this.memo = memo
							}
						)
						transactionDao.updateFeeMemo(hash, memo)
						hold(memo, error)
					} else hold(null, error)
				}
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

	@Query("UPDATE transactionList SET memo =:memo WHERE hash = :txHash AND isFee = :isFee")
	fun updateFeeMemo(txHash: String, memo: String, isFee: Boolean = true)

	@Query("SELECT * FROM transactionList WHERE recordOwnerAddress LIKE :walletAddress AND chainID LIKE :chainID ORDER BY timeStamp DESC")
	fun getTransactionsByAddress(walletAddress: String, chainID: String): List<TransactionTable>

	@Query("SELECT * FROM transactionList WHERE recordOwnerAddress LIKE :walletAddress AND chainID LIKE :chainID AND symbol LIKE :symbol ORDER BY timeStamp DESC")
	fun getETCTransactionsByAddress(walletAddress: String, symbol: String = CoinSymbol.etc, chainID: String = SharedChain.getETCCurrent().chainID.id): List<TransactionTable>

	@Query("SELECT * FROM transactionList WHERE recordOwnerAddress LIKE :walletAddress ORDER BY timeStamp DESC")
	fun getAllTransactionsByAddress(walletAddress: String): List<TransactionTable>

	@Query("SELECT * FROM transactionList WHERE hash = :taxHash")
	fun getTransactionByTaxHash(taxHash: String): List<TransactionTable>

	@Query("SELECT * FROM transactionList WHERE hash = :taxHash AND isReceive = :isReceive AND isFee = :isFee")
	fun getByTaxHashAndReceivedStatus(taxHash: String, isReceive: Boolean, isFee: Boolean): TransactionTable?

	@Query("SELECT * FROM transactionList WHERE recordOwnerAddress LIKE :walletAddress AND contractAddress LIKE :contract AND chainID LIKE :chainID AND isFee = :isFee ORDER BY timeStamp DESC")
	fun getByAddressAndContract(walletAddress: String, contract: String, chainID: String, isFee: Boolean = false): List<TransactionTable>

	@Query("SELECT * FROM transactionList WHERE recordOwnerAddress LIKE :walletAddress AND chainID LIKE :chainID AND (contractAddress LIKE :contract OR isFee = :isFee) ORDER BY timeStamp DESC")
	fun getETHAndAllFee(walletAddress: String, contract: String, chainID: String, isFee: Boolean = true): List<TransactionTable>

	@Insert
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