package io.goldstone.blockchain.kernel.commonmodel

import android.arch.persistence.room.*
import com.blinnnk.extension.safeGet
import org.json.JSONArray
import org.json.JSONObject

/**
 * @date 2018/7/26 11:20 PM
 * @author KaySaith
 */
@Entity(tableName = "bitcoinTransactionList")
data class BitcoinTransactionTable(
	@PrimaryKey(autoGenerate = true)
	val id: Int,
	val blockNumber: String,
	val transactionIndex: Int,
	val timeStamp: String,
	val hash: String,
	val fromAddress: String,
	val to: String,
	val recordAddress: String,
	val changeAddress: String,
	val value: String,
	val fee: String,
	val size: String
) {
	
	constructor(
		data: JSONObject,
		myAddress: String
	) : this(
		0,
		data.safeGet("block_height"),
		data.safeGet("tx_index").toInt(),
		data.safeGet("time"),
		data.safeGet("hash"),
		getFromAddress(data),
		getToAddress(data, myAddress),
		myAddress,
		getChangeAddress(data),
		getTransactionValue(data, myAddress),
		getFeeSatoshi(data, myAddress),
		data.safeGet("size")
	)
	
	companion object {
		private fun getFromAddress(data: JSONObject): String {
			val inputs = JSONArray(data.safeGet("inputs"))
			return JSONObject(
				JSONObject(inputs[0].toString()).safeGet("prev_out")
			).safeGet("addr")
		}
		
		private fun getToAddress(
			data: JSONObject,
			myAddress: String
		): String {
			val out = JSONArray(data.safeGet("out"))
			var toAddress = ""
			(0 until out.length()).forEach {
				// 如果发起地址里面有我的地址, 那么接收地址就是 `Out` 里面不等于我的及找零地址的地址.
				if (getFromAddress(data).equals(myAddress, true)) {
					val outAddress = JSONObject(out[0].toString()).safeGet("addr")
					if (
						!outAddress.equals(myAddress, true)
						&& !outAddress.equals(getChangeAddress(data), true)
					) {
						toAddress = outAddress
					}
				} else {
					// 如果发起地址里面没有我的地址, 那么接收地址就是我
					toAddress = myAddress
				}
			}
			return toAddress
		}
		
		private fun getTransactionValue(data: JSONObject, myAddress: String): String {
			val out = JSONArray(data.safeGet("out"))
			return if (
				!getFromAddress(data).equals(myAddress, true)
				&& getChangeAddress(data).equals(myAddress, true)
			) {
				getChangeValue(data)
			} else {
				JSONObject(out[0].toString()).safeGet("value")
			}
		}
		
		private fun getChangeValue(
			data: JSONObject
		): String {
			val out = JSONArray(data.safeGet("out"))
			var changeValue = ""
			(0 until out.length()).forEach {
				changeValue =
					if (JSONObject(out[it].toString()).safeGet("n").toIntOrNull() == 1) {
						JSONObject(out[it].toString()).safeGet("value")
					} else {
						"0"
					}
			}
			return changeValue
		}
		
		private fun getChangeAddress(
			data: JSONObject
		): String {
			val out = JSONArray(data.safeGet("out"))
			var changeAddress = ""
			(0 until out.length()).forEach {
				changeAddress =
					if (JSONObject(out[it].toString()).safeGet("n").toIntOrNull() == 1) {
						JSONObject(out[it].toString()).safeGet("addr")
					} else {
						""
					}
			}
			// 如果用户没有设置找零那么意味着没有第二个输出, 其余的都被当作燃气费
			return changeAddress
		}
		
		private fun getFeeSatoshi(data: JSONObject, myAddress: String): String {
			val inputs = JSONArray(data.safeGet("inputs"))
			var totalValue = 0L
			(0 until inputs.length()).forEach {
				totalValue += JSONObject(
					JSONObject(inputs[it].toString()).safeGet("prev_out")
				).safeGet("value").toLong()
			}
			return (totalValue - getTransactionValue(
				data,
				myAddress
			).toLong() - getChangeValue(data).toLong()).toString()
		}
	}
}

@Dao
interface BitcoinTransactionDao {
	
	@Query("SELECT * FROM bitcoinTransactionList")
	fun getAll(): List<BitcoinTransactionTable>
	
	@Insert
	fun insert(table: BitcoinTransactionTable)
	
	@Update
	fun update(table: BitcoinTransactionTable)
	
	@Delete
	fun delete(table: BitcoinTransactionTable)
}