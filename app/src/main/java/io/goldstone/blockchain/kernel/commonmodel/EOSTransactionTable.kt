package io.goldstone.blockchain.kernel.commonmodel

import android.arch.persistence.room.*
import io.goldstone.blockchain.common.utils.load
import io.goldstone.blockchain.common.utils.then
import io.goldstone.blockchain.kernel.database.GoldStoneDataBase
import org.jetbrains.anko.doAsync
import java.io.Serializable

@Entity(tableName = "eosTransactions")
data class EOSTransactionTable(
	@PrimaryKey(autoGenerate = true)
	var id: Int,
	var dataIndex: Int,
	var txId: String,
	var cupUsage: String,
	var netUsage: String,
	var fromAccount: String,
	var toAccount: String,
	var transferCount: String,
	var memo: String,
	var blockNumber: String,
	var time: String,
	var status: String,
	var recordAccount: String,
	var isPending: Boolean
) : Serializable {
	constructor() : this(
		0,
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
		false
	)

	companion object {

		fun getTransactionByAccountName(name: String, hold: (List<EOSTransactionTable>) -> Unit) {
			load {
				GoldStoneDataBase.database.eosTransactionDao().getDataByRecordAccount(name)
			} then (hold)
		}

	    fun deleteByAddress(address: String) {
				doAsync {
					GoldStoneDataBase.database.eosTransactionDao().apply {
						val data = getDataByRecordAccount(address)
						data.forEach { delete(it) }
					}
				}
			}
	}
}

@Dao
interface EOSTransactionDao {

	@Query("SELECT * FROM eosTransactions WHERE recordAccount LIKE :recordAccount")
	fun getDataByRecordAccount(recordAccount: String): List<EOSTransactionTable>

	@Insert
	fun insert(transaction: EOSTransactionTable)

	@Update
	fun update(transaction: EOSTransactionTable)

	@Delete
	fun delete(transaction: EOSTransactionTable)
}