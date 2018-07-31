package io.goldstone.blockchain.kernel.commonmodel

import android.arch.persistence.room.*
import com.blinnnk.extension.safeGet
import io.goldstone.blockchain.common.utils.load
import io.goldstone.blockchain.common.utils.then
import io.goldstone.blockchain.kernel.database.GoldStoneDataBase
import org.json.JSONArray
import org.json.JSONObject

/**
 * @date 2018/7/26 11:20 PM
 * @author KaySaith
 */
@Entity(tableName = "bitcoinTransactionList")
data class BitcoinSeriesTransactionTable(
	@PrimaryKey(autoGenerate = true)
	val id: Int,
	var symbol: String,
	var blockNumber: String,
	var transactionIndex: Int,
	var timeStamp: String,
	val hash: String,
	val fromAddress: String,
	val to: String,
	var recordAddress: String,
	var isReceive: Boolean,
	val value: String,
	val fee: String,
	var size: String,
	var isFee: Boolean,
	var isPending: Boolean
) {
	
	constructor(
		data: JSONObject,
		symbol: String,
		myAddress: String,
		isFee: Boolean
	) : this(
		0,
		symbol,
		data.safeGet("block_height"),
		data.safeGet("tx_index").toInt(),
		data.safeGet("time"),
		data.safeGet("hash"),
		getFromAddress(data),
		getToAddresses(data).toString(),
		myAddress,
		!getFromAddress(data).equals(myAddress, true),
		getTransactionValue(data, myAddress),
		getFeeSatoshi(data),
		data.safeGet("size"),
		isFee,
		false
	)
	
	companion object {
		private fun getFromAddress(data: JSONObject): String {
			val inputs = JSONArray(data.safeGet("inputs"))
			return JSONObject(
				JSONObject(inputs[0].toString()).safeGet("prev_out")
			).safeGet("addr")
		}
		
		private fun getToAddresses(
			data: JSONObject
		): List<String> {
			val out = JSONArray(data.safeGet("out"))
			var toAddresses = listOf<String>()
			(0 until out.length()).forEach {
				toAddresses += JSONObject(out[it].toString()).safeGet("addr")
			}
			// 如果发起地址里面有我的地址, 那么接收地址就是 `Out` 里面不等于我的及找零地址的地址.
			return toAddresses.filterNot { it.equals(getFromAddress(data), true) }
		}
		
		private fun getTransactionValue(data: JSONObject, myAddress: String): String {
			return (getTotalValue(data) - getFeeSatoshi(data).toLong() - getChangeValue(
				myAddress,
				data
			).toLong()).toString()
		}
		
		/**
		 * 理论上, 比特币的转账地址都可以定义为找零地址, 而若当用户更改不为人所知的自己可以控制的 `ChangeAddress`
		 * 我们是无从得知的。这里我们假定输出地址就是发起转账的地址为找零地址。并把对应的 `Value` 定义为 `ChangeValue`、
		 * 或发起地址不为我自己, `Out` 地址中去除我的地址的部分为 `ChangeValue`
		 */
		private fun getChangeValue(
			toAddress: String,
			data: JSONObject
		): String {
			val out = JSONArray(data.safeGet("out"))
			var changeValue = 0L
			val mineIsTo = getFromAddress(data).equals(toAddress, true)
			(0 until out.length()).forEach {
				val child = JSONObject(out[it].toString())
				changeValue +=
					if (child.safeGet("addr").equals(toAddress, true) == mineIsTo) {
						child.safeGet("value").toLong()
					} else {
						0L
					}
			}
			return changeValue.toString()
		}
		
		fun getTransactionsByAddress(
			address: String,
			hold: (List<BitcoinSeriesTransactionTable>) -> Unit
		) {
			load {
				GoldStoneDataBase
					.database
					.bitcoinTransactionDao()
					.getDataByAddress(address)
			} then (hold)
		}
		
		fun getTransactionsByHash(
			hash: String,
			isReceive: Boolean,
			hold: (BitcoinSeriesTransactionTable?) -> Unit
		) {
			load {
				GoldStoneDataBase
					.database
					.bitcoinTransactionDao()
					.getDataByHash(hash, isReceive)
			} then (hold)
		}
		
		fun updateLocalDataByHash(
			hash: String,
			newData: BitcoinSeriesTransactionTable,
			isPending: Boolean
		) {
			GoldStoneDataBase
				.database
				.bitcoinTransactionDao()
				.apply {
					getTransactionByHash(hash)
						?.let {
							update(it.apply {
								blockNumber = newData.blockNumber
								transactionIndex = newData.transactionIndex
								timeStamp = newData.timeStamp
								size = newData.size
								this.isPending = isPending
							})
						}
				}
		}
		
		private fun getTotalValue(data: JSONObject): Long {
			val inputs = JSONArray(data.safeGet("inputs"))
			var totalValue = 0L
			(0 until inputs.length()).forEach {
				totalValue += JSONObject(
					JSONObject(inputs[it].toString()).safeGet("prev_out")
				).safeGet("value").toLong()
			}
			return totalValue
		}
		
		private fun getTotalOutValue(data: JSONObject): Long {
			val out = JSONArray(data.safeGet("out"))
			var totalValue = 0L
			(0 until out.length()).forEach {
				totalValue += JSONObject(out[it].toString()).safeGet("value").toLong()
			}
			return totalValue
		}
		
		private fun getFeeSatoshi(data: JSONObject): String {
			return (getTotalValue(data) - getTotalOutValue(data)).toString()
		}
	}
}

@Dao
interface BitcoinTransactionDao {
	
	@Query("SELECT * FROM bitcoinTransactionList")
	fun getAll(): List<BitcoinSeriesTransactionTable>
	
	@Query("SELECT * FROM bitcoinTransactionList WHERE recordAddress LIKE :address  ORDER BY timeStamp DESC")
	fun getDataByAddress(address: String): List<BitcoinSeriesTransactionTable>
	
	@Query("SELECT * FROM bitcoinTransactionList WHERE hash LIKE :hash AND isReceive LIKE :isReceive")
	fun getDataByHash(hash: String, isReceive: Boolean): BitcoinSeriesTransactionTable?
	
	@Query("SELECT * FROM bitcoinTransactionList WHERE hash LIKE :hash")
	fun getTransactionByHash(hash: String): BitcoinSeriesTransactionTable?
	
	@Insert
	fun insert(table: BitcoinSeriesTransactionTable)
	
	@Update
	fun update(table: BitcoinSeriesTransactionTable)
	
	@Delete
	fun delete(table: BitcoinSeriesTransactionTable)
}