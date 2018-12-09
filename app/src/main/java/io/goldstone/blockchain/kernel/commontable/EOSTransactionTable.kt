package io.goldstone.blockchain.kernel.commontable

import android.arch.persistence.room.*
import android.support.annotation.WorkerThread
import com.blinnnk.extension.getTargetObject
import com.blinnnk.extension.safeGet
import com.blinnnk.extension.toIntOrZero
import io.goldstone.blockchain.common.sharedpreference.SharedAddress
import io.goldstone.blockchain.common.sharedpreference.SharedChain
import io.goldstone.blockchain.crypto.eos.EOSTransactionMethod
import io.goldstone.blockchain.crypto.eos.EOSUtils
import io.goldstone.blockchain.crypto.eos.account.EOSAccount
import io.goldstone.blockchain.crypto.eos.base.EOSResponse
import io.goldstone.blockchain.crypto.eos.transaction.EOSTransactionInfo
import io.goldstone.blockchain.crypto.multichain.ChainID
import io.goldstone.blockchain.crypto.multichain.TokenContract
import io.goldstone.blockchain.kernel.commonmodel.eos.EOSTransactionData
import io.goldstone.blockchain.kernel.database.GoldStoneDataBase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.Serializable
import java.math.BigInteger

@Entity(tableName = "eosTransactions", primaryKeys = ["txID", "recordAccountName"])
data class EOSTransactionTable(
	var dataIndex: Int,
	var serverID: Long,
	var txID: String,
	var symbol: String,
	var codeName: String,
	var cupUsage: BigInteger,
	var netUsage: BigInteger,
	var transactionData: EOSTransactionData,
	var blockNumber: Int,
	var time: Long,
	var actionName: String,
	var recordAccountName: String,
	var recordPublicKey: String, // 删除钱包的时候用这个标记来删除, 一个公钥下的全部
	var chainID: String,
	var isPending: Boolean
) : Serializable {
	constructor(
		info: EOSTransactionInfo,
		response: EOSResponse,
		dataIndex: Int
	) : this(
		dataIndex,
		0L,
		response.transactionID,
		info.contract.symbol,
		info.contract.contract,
		response.cupUsageByte,
		response.netUsageByte,
		EOSTransactionData(info),
		0, // 需要在数块的时候获取
		System.currentTimeMillis(),
		EOSTransactionMethod.transfer().value,
		// 这个构造方法用于插入 `Pending Data` 是本地发起才用到, 所以 `RecordAccount` 就是 `FromAccount `
		info.fromAccount.name,
		SharedAddress.getCurrentEOS(),
		SharedChain.getEOSCurrent().chainID.id,
		true
	)

	// act part json
	// {"account":"eosio.token","name":"transfer","authorization":[{"actor":"huaxingziben","permission":"active"}],"data":{"from":"huaxingziben","to":"googletumblr","quantity":"2.0000 EOS","memo":""},"hex_data":"30d5719f4dd78d6e70e3913aabc82865204e00000000000004454f530000000000"}
	constructor(data: JSONObject, recordAccountName: String) : this(
		dataIndex = 0,
		serverID = data.safeGet("id").toLongOrNull() ?: 0L,
		txID = data.safeGet("txid"),
		symbol = EOSTransactionData(data.getTargetObject("data")).quantity.substringAfter(" "),
		codeName = data.safeGet("account"),
		cupUsage = BigInteger(data.safeGet("cpu_usage_us")),
		netUsage = BigInteger(data.safeGet("net_usage_words")) * BigInteger.valueOf(8),
		transactionData = EOSTransactionData(data.getTargetObject("data")),
		blockNumber = data.safeGet("block_num").toIntOrZero(),
		time = EOSUtils.getUTCTimeStamp(data.safeGet("timestamp")),
		actionName = data.safeGet("action_name"),
		recordAccountName = recordAccountName,
		recordPublicKey = SharedAddress.getCurrentEOS(),
		chainID = SharedChain.getEOSCurrent().chainID.id,
		isPending = false
	)

	companion object {

		@JvmField val dao = GoldStoneDataBase.database.eosTransactionDao()

		fun getMaxDataIndexTable(
			account: EOSAccount,
			contract: TokenContract,
			chainID: ChainID,
			hold: (EOSTransactionTable?) -> Unit
		) = GlobalScope.launch(Dispatchers.Default) {
			val data =
				GoldStoneDataBase.database.eosTransactionDao().getMaxDataIndex(
					account.name,
					contract.contract,
					contract.symbol,
					chainID.id
				)
			hold(data)
		}

		@WorkerThread
		fun getRangeData(
			account: EOSAccount,
			start: Int,
			end: Int,
			symbol: String,
			codeName: String,
			hold: (List<EOSTransactionTable>) -> Unit
		) {
			// `Server` 返回的 数据 `Memo` 中会带有不确定的 `SqlLite` 不支持的特殊符号,
			// 这里用 `Try Catch` 兼容一下
			val data = try {
				GoldStoneDataBase.database.eosTransactionDao().getDataByRange(
					account.name,
					start,
					end,
					symbol,
					codeName
				)
			} catch (error: Exception) {
				listOf<EOSTransactionTable>()
			}
			hold(data)
		}
	}
}

@Dao
interface EOSTransactionDao {

	@Query("UPDATE eosTransactions SET cupUsage = :cpuUsage, netUsage = :netUsage WHERE txID LIKE :txID")
	fun updateBandWidthAndStatusByTxID(txID: String, cpuUsage: BigInteger, netUsage: BigInteger)

	@Query("UPDATE eosTransactions SET blockNumber = :blockNumber, isPending = :isPending WHERE txID LIKE :txID")
	fun updateBlockNumberByTxID(txID: String, blockNumber: Int, isPending: Boolean)

	@Query("UPDATE eosTransactions SET isPending = :pendingStatus, serverID = :serverID WHERE txID LIKE :txID")
	fun updatePendingDataByTxID(txID: String, serverID: Long, pendingStatus: Boolean = false)

	@Query("SELECT * FROM eosTransactions WHERE dataIndex = (SELECT MAX(dataIndex) FROM eosTransactions WHERE recordAccountName LIKE :accountName AND codeName = :codeName AND symbol = :symbol AND chainID = :chainID)")
	fun getMaxDataIndex(accountName: String, codeName: String, symbol: String, chainID: String): EOSTransactionTable?

	@Query("SELECT * FROM eosTransactions WHERE recordAccountName LIKE :recordAccountName")
	fun getDataByRecordAccount(recordAccountName: String): List<EOSTransactionTable>

	@Query("SELECT * FROM eosTransactions WHERE recordAccountName LIKE :recordAccountName AND dataIndex LIKE :dataIndex AND symbol LIKE :symbol AND codeName LIKE :codeName")
	fun getDataByDataIndex(recordAccountName: String, dataIndex: Int, symbol: String, codeName: String): EOSTransactionTable?

	@Query("SELECT * FROM eosTransactions WHERE recordAccountName LIKE :recordAccountName AND symbol LIKE :symbol AND codeName LIKE :codeName AND dataIndex BETWEEN :start AND :end  ORDER BY time DESC")
	fun getDataByRange(recordAccountName: String, start: Int, end: Int, symbol: String, codeName: String): List<EOSTransactionTable>

	@Query("SELECT * FROM eosTransactions WHERE recordAccountName LIKE :recordAddress")
	fun getDataByRecordAddress(recordAddress: String): List<EOSTransactionTable>

	@Query("DELETE FROM eosTransactions WHERE recordPublicKey LIKE :recordAddress")
	fun deleteDataByRecordAddress(recordAddress: String)

	@Query("SELECT * FROM eosTransactions WHERE recordAccountName LIKE :recordAccountName AND chainID LIKE :chainID AND symbol LIKE :symbol AND codeName LIKE :codeName")
	fun getDataByRecordAccount(recordAccountName: String, symbol: String, codeName: String, chainID: String): List<EOSTransactionTable>

	@Query("SELECT * FROM eosTransactions WHERE recordAccountName LIKE :recordAccountName AND txID LIKE :txID")
	fun getDataByTxIDAndRecordName(txID: String, recordAccountName: String): EOSTransactionTable?

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	fun insert(transaction: EOSTransactionTable)

	@Query("SELECT * FROM eosTransactions WHERE recordAccountName Like :recordAccountName")
	fun getAll(recordAccountName: String): List<EOSTransactionTable>

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	fun insertAll(transactions: List<EOSTransactionTable>)

	@Update
	fun update(transaction: EOSTransactionTable)

	@Delete
	fun delete(transaction: EOSTransactionTable)

	@Delete
	fun deleteAll(transactions: List<EOSTransactionTable>)
}