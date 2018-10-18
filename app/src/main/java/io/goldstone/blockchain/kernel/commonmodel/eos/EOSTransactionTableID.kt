package io.goldstone.blockchain.kernel.commonmodel.eos

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Query
import java.io.Serializable


/**
 * @author KaySaith
 * @date  2018/10/16
 */
data class EOSTransactionTableID(
	val serverID: Long,
	val dataIndex: Int
) : Serializable {
}

@Dao
interface EOSTransactionIDDao {
	@Query("SELECT eosTransactions.serverID AS serverID, eosTransactions.dataIndex AS dataIndex FROM eosTransactions  WHERE eosTransactions.recordAccountName = :accountName AND eosTransactions.symbol = :symbol AND eosTransactions.codeName = :codeName")
	fun getData(accountName: String, symbol: String, codeName: String): List<EOSTransactionTableID>
}