package io.goldstone.blockchain.kernel.network

import io.goldstone.blockchain.common.utils.LogUtil
import io.goldstone.blockchain.kernel.network.bitcoin.model.UnspentModel
import org.json.JSONArray
import org.json.JSONObject

/**
 * @date 2018/8/14 4:13 PM
 * @author KaySaith
 */

object BTCSeriesApiUtils {
	fun getTransactions(
		api: String,
		errorCallback: (Throwable) -> Unit,
		hold: (List<JSONObject>) -> Unit
	) {
		RequisitionUtil.requestUncryptoData<String>(
			api,
			"txs",
			true,
			{
				errorCallback(it)
				LogUtil.error("getTransactions", it)
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

	fun getBalance(api: String, hold: (Long) -> Unit) {
		RequisitionUtil.requestUncryptoData<String>(
			api,
			"",
			true,
			{
				LogUtil.error("getBalance", it)
			}
		) {
			val count = first().toLongOrNull() ?: 0L
			hold(count)
		}
	}

	fun getDoubleBalance(api: String, hold: (Double) -> Unit) {
		RequisitionUtil.requestUncryptoData<String>(
			api,
			"",
			true,
			{
				LogUtil.error("getDoubleBalance", it)
			}
		) {
			val count = first().toDoubleOrNull() ?: 0.0
			hold(count)
		}
	}

	fun getTransactionByHash(
		api: String,
		errorCallback: (Throwable) -> Unit,
		hold: (JSONObject?) -> Unit
	) {
		RequisitionUtil.requestUncryptoData<String>(
			api,
			"",
			true,
			{
				errorCallback(it)
				LogUtil.error("Bitcoin getTransactionByHash", it)
			}
		) {
			hold(if (isNotEmpty()) JSONObject(this[0]) else null)
		}
	}

	fun getUnspentListByAddress(
		api: String,
		hold: (List<UnspentModel>) -> Unit
	) {
		RequisitionUtil.requestUncryptoData<UnspentModel>(
			api,
			"",
			false,
			{
				hold(listOf())
				LogUtil.error("getUnspentListByAddress", it)
			}
		) {
			hold(if (isNotEmpty()) this else listOf())
		}
	}
}