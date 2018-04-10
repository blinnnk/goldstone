package io.goldstone.blockchain.kernel.commonmodel

import android.arch.persistence.room.*
import com.blinnnk.util.coroutinesTask
import com.google.gson.annotations.SerializedName
import io.goldstone.blockchain.common.utils.toArrayList
import io.goldstone.blockchain.kernel.database.GoldStoneDataBase
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable

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
  @Ignore @SerializedName("nonce")
  var nonce: String, // Ignore
  @SerializedName("blockHash")
  var blockHash: String,
  @Ignore @SerializedName("transactionIndex")
  private var transactionIndex: String, // Ignore
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
  var input: String, // Ignore
  @SerializedName("contractAddress")
  var contractAddress: String, @Ignore
  @SerializedName("cumulativeGasUsed")
  private var cumulativeGasUsed: String, // Ignore
  @SerializedName("gasUsed")
  var gasUsed: String,
  @Ignore @SerializedName("confirmations")
  private var confirmations: String, // Ignore
  var isReceive: Boolean,
  var isERC20: Boolean,
  var symbol: String,
  var recordOwnerAddress: String,
  var tokenReceiveAddress: String? = null,
  var transactionVolume: Int? = null
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

  companion object {
    fun getAllTransactionsByAddress(address: String, hold: (ArrayList<TransactionTable>) -> Unit) {
      coroutinesTask({
        GoldStoneDataBase.database.transactionDao().getTransactionsByAddress(address)
      }) {
        hold(it.toArrayList())
      }
    }

    fun getTransactionsByAddressAndSymbol(address: String, symbol: String, hold: (ArrayList<TransactionTable>) -> Unit) {
      coroutinesTask({
        GoldStoneDataBase.database.transactionDao().getTransactionsByAddressAndSymbol(address, symbol)
      }) {
        hold(it.toArrayList())
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
  }
}

@Dao
interface TransactionDao {
  @Query("SELECT * FROM transactionList WHERE recordOwnerAddress LIKE :walletAddress")
  fun getTransactionsByAddress(walletAddress: String): List<TransactionTable>

  @Query("SELECT * FROM transactionList WHERE recordOwnerAddress LIKE :walletAddress AND symbol LIKE :targetSymbol")
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