package io.goldstone.blockchain.kernel.network.bitcoin

import com.blinnnk.extension.safeGet
import io.goldstone.blockchain.common.utils.LogUtil
import io.goldstone.blockchain.kernel.commonmodel.BitcoinTransactionTable
import io.goldstone.blockchain.kernel.network.RequisitionUtil
import io.goldstone.blockchain.kernel.network.bitcoin.model.UnspentModel
import org.json.JSONArray
import org.json.JSONObject

/**
 * @date 2018/7/19 1:46 AM
 * @author KaySaith
 */
object BitcoinApi {
	
	fun getBalanceByAddress(address: String, hold: (Long) -> Unit) {
		RequisitionUtil.requestUncryptoData<String>(
			BitcoinUrl.getBalance(BitcoinUrl.currentUrl(), address),
			address,
			true,
			{
				LogUtil.error("getBitcoinBalance", it)
			}
		) {
			val count = JSONObject(this[0]).safeGet("final_balance").toLongOrNull() ?: 0L
			hold(count)
		}
	}
	
	fun getBTCTransactions(
		address: String,
		pageSize: Int,
		offset: Int,
		errorCallback: (Exception) -> Unit,
		hold: (List<JSONObject>) -> Unit
	) {
		RequisitionUtil.requestUncryptoData<String>(
			BitcoinUrl.getTransactions(BitcoinUrl.currentUrl(), address, pageSize, offset),
			"txs",
			true,
			{
				errorCallback(it)
				LogUtil.error("getBTCTransactions", it)
			}
		) {
			val jsonArray = JSONArray(this[0])
			var data = listOf<JSONObject>()
			(0 until jsonArray.length()).forEach {
				data += JSONObject(jsonArray[it].toString())
			}
			hold(data)
		}
	}
	
	fun getUnspentListByAddress(address: String, hold: (List<UnspentModel>) -> Unit) {
		RequisitionUtil.requestUncryptoData<UnspentModel>(
			BitcoinUrl.getUnspentInfo(BitcoinUrl.currentUrl(), address),
			"unspent_outputs",
			false,
			{
				LogUtil.error("getRawtxByHash", it)
			}
		) {
			hold(if (isNotEmpty()) this else listOf())
		}
	}
	
	fun getTransactionByHash(
		hash: String,
		address: String,
		errorCallback: (Exception) -> Unit,
		hold: (BitcoinTransactionTable?) -> Unit
	) {
		RequisitionUtil.requestUncryptoData<String>(
			BitcoinUrl.getTransactionByHash(BitcoinUrl.currentUrl(), hash),
			"",
			true,
			{
				errorCallback(it)
				LogUtil.error("Bitcoin getTransactionByHash", it)
			}
		) {
			hold(
				if (isNotEmpty()) {
					BitcoinTransactionTable(JSONObject(this[0]), address)
				} else null
			)
		}
	}
	
	fun getBlockNumberByTransactionHash(
		hash: String,
		errorCallback: (Exception) -> Unit,
		hold: (Int?) -> Unit
	) {
		RequisitionUtil.requestUncryptoData<String>(
			BitcoinUrl.getTransactionByHash(BitcoinUrl.currentUrl(), hash),
			"",
			true,
			{
				hold(null)
				errorCallback(it)
				LogUtil.error("getBlockNumberByTransactionHash", it)
			}
		) {
			hold(
				if (isNotEmpty()) {
					JSONObject(this[0]).safeGet("block_height").toIntOrNull()
				} else null
			)
		}
	}
}