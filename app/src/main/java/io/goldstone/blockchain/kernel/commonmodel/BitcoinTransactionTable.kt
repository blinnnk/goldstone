package io.goldstone.blockchain.kernel.commonmodel

import android.arch.persistence.room.*
import com.blinnnk.extension.isNull
import com.blinnnk.extension.orZero
import com.blinnnk.extension.safeGet
import io.goldstone.blockchain.common.utils.ConcurrentAsyncCombine
import io.goldstone.blockchain.common.utils.load
import io.goldstone.blockchain.common.utils.then
import io.goldstone.blockchain.crypto.ChainType
import io.goldstone.blockchain.crypto.bitcoin.BTCUtils
import io.goldstone.blockchain.crypto.bitcoincash.BCHUtil
import io.goldstone.blockchain.crypto.bitcoincash.BCHWalletUtils
import io.goldstone.blockchain.kernel.database.GoldStoneDataBase
import org.bitcoinj.params.MainNetParams
import org.bitcoinj.params.TestNet3Params
import org.jetbrains.anko.doAsync
import org.json.JSONArray
import org.json.JSONObject

/**
 * @date 2018/7/26 11:20 PM
 * @author KaySaith
 */
@Entity(tableName = "bitcoinTransactionList")
data class BTCSeriesTransactionTable(
	@PrimaryKey(autoGenerate = true)
	val id: Int,
	val dataIndex: Int, // 复杂的翻页机制需要和服务器映射的抽象角标
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
	var confirmations: Int,
	var isFee: Boolean,
	var isPending: Boolean,
	var chainType: Int
) {

	constructor(
		data: JSONObject,
		dataIndex: Int,
		myAddress: String,
		symbol: String,
		isFee: Boolean,
		chainType: Int
	) : this(
		0,
		dataIndex,
		symbol,
		data.safeGet("blockheight"),
		0,
		data.safeGet("time"),
		data.safeGet("txid"),
		getFromAddress(data),
		getToAddresses(data).toString(),
		myAddress,
		isReceive(getFromAddress(data), myAddress),
		getTransactionValue(data, myAddress),
		data.safeGet("fees"),
		data.safeGet("size"),
		data.safeGet("confirmations").toIntOrNull().orZero(),
		isFee,
		false,
		chainType
	)

	companion object {

		private fun isReceive(fromAddress: String, toAddress: String): Boolean {
			val formatToAddress =
				if (BCHWalletUtils.isNewCashAddress(fromAddress)) {
					if (!BCHWalletUtils.isNewCashAddress(toAddress))
						BCHUtil.instance
							.encodeCashAddressByLegacy(toAddress)
							.substringAfter(":")
					else {
						if (toAddress.contains(":"))
							toAddress.substringAfter(":")
						else toAddress
					}
				} else {
					if (BTCUtils.isValidTestnetAddress(fromAddress))
						BCHWalletUtils.formattedToLegacy(toAddress, TestNet3Params.get())
					else BCHWalletUtils.formattedToLegacy(toAddress, MainNetParams.get())
				}
			return !fromAddress.equals(formatToAddress, true)
		}

		private fun convertToBCHOrDefaultAddress(myAddress: String, targetAddress: String): String {
			return if (BCHWalletUtils.isNewCashAddress(targetAddress)) {
				val myAddressIsLegacy = !BCHWalletUtils.isNewCashAddress(myAddress)
				if (myAddressIsLegacy)
					BCHUtil.instance
						.encodeCashAddressByLegacy(myAddress)
						.substringAfter(":")
				else {
					if (myAddress.contains(":")) myAddress.substringAfter(":")
					else myAddress
				}
			} else {
				if (BTCUtils.isValidTestnetAddress(targetAddress))
					BCHWalletUtils.formattedToLegacy(myAddress, TestNet3Params.get())
				else BCHWalletUtils.formattedToLegacy(myAddress, MainNetParams.get())
			}
		}

		private fun getFromAddress(data: JSONObject): String {
			val inputs = JSONArray(data.safeGet("vin"))
			return JSONObject(inputs[0].toString()).safeGet("addr")
		}

		private fun getToAddresses(
			data: JSONObject
		): List<String> {
			val out = JSONArray(data.safeGet("vout"))
			var toAddresses = listOf<String>()
			(0 until out.length()).forEach {
				toAddresses += JSONArray(JSONObject(JSONObject(out[it].toString()).safeGet("scriptPubKey")).safeGet("addresses"))[0].toString()
			}
			// 如果发起地址里面有我的地址, 那么接收地址就是 `Out` 里面不等于我的及找零地址的地址.
			val finalToAddress = toAddresses.filterNot {
				it.equals(getFromAddress(data), true)
			}
			return if (finalToAddress.isEmpty()) toAddresses.subList(0, 1) else finalToAddress
		}

		private fun getTransactionValue(data: JSONObject, myAddress: String): String {
			val totalWithoutFee = data.safeGet("valueOut").toDoubleOrNull().orZero()
			return (
				totalWithoutFee - getChangeValue(myAddress, data).toDoubleOrNull().orZero()).toString()
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
			val out = JSONArray(data.safeGet("vout"))
			var changeValue = 0.0
			val formatToAddress = convertToBCHOrDefaultAddress(toAddress, getFromAddress(data))
			val mineIsTo = getFromAddress(data).equals(formatToAddress, true)
			val outCount = out.length()
			var countForCalculateTransferToMySelf = out.length()
			(0 until outCount).forEach {
				val child = JSONObject(out[it].toString())
				val childAddress = JSONArray(JSONObject(child.safeGet("scriptPubKey")).safeGet("addresses"))[0].toString()
				changeValue +=
					if (childAddress.equals(formatToAddress, true) == mineIsTo) {
						countForCalculateTransferToMySelf -= 1
						// 如果 `countForCalculateTransferToMySelf` 的值是 `0` 那么意味着是自己转给自己
						//  这种情况留一个值作为 `TransferValue`
						if (countForCalculateTransferToMySelf != 0) {
							child.safeGet("value").toDoubleOrNull().orZero()
						} else 0.0
					} else {
						0.0
					}
			}
			return changeValue.toString()
		}

		fun getTransactionsByAddressAndChainType(
			address: String,
			chainType: Int,
			hold: (List<BTCSeriesTransactionTable>) -> Unit
		) {
			load {
				GoldStoneDataBase
					.database
					.btcSeriesTransactionDao()
					.getDataByAddressAndChainType(address, chainType)
			} then (hold)
		}

		fun getTransactionsByHash(
			hash: String,
			isReceive: Boolean,
			hold: (BTCSeriesTransactionTable?) -> Unit
		) {
			load {
				GoldStoneDataBase
					.database
					.btcSeriesTransactionDao()
					.getDataByHash(hash, isReceive)
			} then (hold)
		}

		fun updateLocalDataByHash(
			hash: String,
			newData: BTCSeriesTransactionTable,
			isFee: Boolean,
			isPending: Boolean
		) {
			GoldStoneDataBase
				.database
				.btcSeriesTransactionDao()
				.apply {
					getTransactionByHash(hash, isFee, newData.recordAddress)
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

		fun preventRepeatedInsert(
			hash: String,
			isFee: Boolean,
			transaction: BTCSeriesTransactionTable
		) {
			GoldStoneDataBase.database.btcSeriesTransactionDao().apply {
				if (getTransactionByHash(hash, isFee, transaction.recordAddress).isNull()) {
					insert(transaction)
				}
			}
		}

		fun deleteByAddress(address: String, chainType: Int, callback: () -> Unit = {}) {
			doAsync {
				GoldStoneDataBase.database.btcSeriesTransactionDao().apply {
					// `BCH` 的 `insight` 账单是新地址格式, 本地的测试网是公用的 `BTCTest Legacy` 格式,
					// 删除多链钱包的时候需要额外处理一下这种情况的地址比对
					val formattedAddress =
						if (
							chainType == ChainType.BCH.id &&
							(
								address.substring(0, 1).equals("m", true) ||
									address.substring(0, 1).equals("n", true)
								)
						) BCHWalletUtils.formattedToLegacy(address, TestNet3Params.get())
						else address

					val data =
						getDataByAddressAndChainType(formattedAddress, chainType)
					if (data.isEmpty()) {
						callback()
						return@doAsync
					}
					object : ConcurrentAsyncCombine() {
						override var asyncCount: Int = data.size
						override fun concurrentJobs() {
							data.forEach {
								delete(it)
								completeMark()
							}
						}

						override fun getResultInMainThread() = false
						override fun mergeCallBack() = callback()
					}.start()
				}
			}
		}

	}
}

@Dao
interface BTCSeriesTransactionDao {

	@Query("SELECT * FROM bitcoinTransactionList")
	fun getAll(): List<BTCSeriesTransactionTable>

	@Query("SELECT * FROM bitcoinTransactionList WHERE recordAddress LIKE :address AND chainType LIKE :chainType ORDER BY timeStamp DESC")
	fun getDataByAddressAndChainType(address: String, chainType: Int): List<BTCSeriesTransactionTable>

	@Query("SELECT * FROM bitcoinTransactionList WHERE hash LIKE :hash AND isReceive LIKE :isReceive")
	fun getDataByHash(hash: String, isReceive: Boolean): BTCSeriesTransactionTable?

	@Query("SELECT * FROM bitcoinTransactionList WHERE hash LIKE :hash AND recordAddress LIKE :recordAddress AND isFee LIKE :isFee")
	fun getTransactionByHash(hash: String, isFee: Boolean, recordAddress: String): BTCSeriesTransactionTable?

	@Insert
	fun insert(table: BTCSeriesTransactionTable)

	@Update
	fun update(table: BTCSeriesTransactionTable)

	@Delete
	fun delete(table: BTCSeriesTransactionTable)
}