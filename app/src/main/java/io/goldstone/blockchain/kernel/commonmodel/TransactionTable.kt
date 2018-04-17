package io.goldstone.blockchain.kernel.commonmodel

import android.arch.persistence.room.*
import com.blinnnk.extension.toArrayList
import com.blinnnk.util.coroutinesTask
import com.google.gson.annotations.SerializedName
import io.goldstone.blockchain.kernel.database.GoldStoneDataBase
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable
import io.goldstone.blockchain.module.home.wallet.transactions.transactionlist.model.ERC20TransactionModel
import io.goldstone.blockchain.module.home.wallet.transactions.transactionlist.model.TransactionListModel

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
  @Ignore @SerializedName("transactionIndex")
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
  @Ignore @SerializedName("input")
  var input: String,
  @SerializedName("contractAddress")
  var contractAddress: String, @Ignore
  @SerializedName("cumulativeGasUsed")
  private var cumulativeGasUsed: String,
  @SerializedName("gasUsed")
  var gasUsed: String,
  @Ignore @SerializedName("confirmations")
  private var confirmations: String,
  var isReceive: Boolean,
  var isERC20: Boolean,
  var symbol: String,
  var recordOwnerAddress: String,
  var tokenReceiveAddress: String? = null,
  var isPending: Boolean = false,
  var logIndex: String = ""
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
    data.to,
    data.to,
    false,
    data.logIndex
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

    fun getAllTransactionsByAddress(address: String, hold: (ArrayList<TransactionTable>) -> Unit) {
      coroutinesTask({
        GoldStoneDataBase.database.transactionDao().getTransactionsByAddress(address)
      }) {
        hold(it.toArrayList())
      }
    }

    fun getTransactionListModelsByAddress(address: String, hold: (ArrayList<TransactionListModel>) -> Unit) {
      coroutinesTask({
        GoldStoneDataBase.database.transactionDao().getTransactionsByAddress(address)
      }) {
        hold(it.map { TransactionListModel(it) }.toArrayList())
      }
    }

    fun getTransactionListModelsByAddressAndModel(address: String, symbol: String, hold: (ArrayList<TransactionListModel>) -> Unit) {
      coroutinesTask({
        GoldStoneDataBase.database.transactionDao().getTransactionsByAddressAndSymbol(address, symbol)
      }) {
        hold(it.map { TransactionListModel(it) }.toArrayList())
      }
    }

    fun getTransactionsByAddressAndSymbol(address: String, symbol: String, hold: (ArrayList<TransactionTable>) -> Unit) {
      coroutinesTask({
        GoldStoneDataBase.database.transactionDao().getTransactionsByAddressAndSymbol(address, symbol)
      }) {
        hold(it.toArrayList())
      }
    }

    fun getMyLatestStartBlock(address: String = WalletTable.current.address, hold: (String) -> Unit) {
      coroutinesTask({
        GoldStoneDataBase.database.transactionDao().getTransactionsByAddress(address)
      }) {
        // 获取到当前最近的一个 `BlockNumber` 若获取不到返回 `0`
        hold((it.maxBy { it.blockNumber }?.blockNumber ?: "0") + 1)
      }
    }

    fun deleteByAddress(address: String, callback: () -> Unit) {
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
        getTransactionsByTaxHash(taxHash)?.let {
          delete(it)
        }
      }
    }
  }
}

@Dao
interface TransactionDao {
  @Query("SELECT * FROM transactionList WHERE recordOwnerAddress LIKE :walletAddress ORDER BY timeStamp DESC")
  fun getTransactionsByAddress(walletAddress: String): List<TransactionTable>

  @Query("SELECT * FROM transactionList WHERE hash LIKE :taxHash")
  fun getTransactionsByTaxHash(taxHash: String): TransactionTable?

  @Query("SELECT * FROM transactionList WHERE recordOwnerAddress LIKE :walletAddress AND symbol LIKE :targetSymbol ORDER BY timeStamp DESC")
  fun getTransactionsByAddressAndSymbol(
    walletAddress: String,
    targetSymbol: String
  ): List<TransactionTable>

  @Insert
  fun insert(token: TransactionTable)

  @Update
  fun update(token: TransactionTable)

  @Delete
  fun delete(token: TransactionTable)
}