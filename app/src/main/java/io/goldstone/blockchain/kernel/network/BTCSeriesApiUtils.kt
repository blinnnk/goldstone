package io.goldstone.blockchain.kernel.network

import com.blinnnk.extension.orZero
import io.goldstone.blockchain.common.utils.LogUtil
import io.goldstone.blockchain.common.value.DataValue
import io.goldstone.blockchain.common.value.PageInfo
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
		RequisitionUtil.requestUnCryptoData<String>(
			api,
			"items",
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

	fun getTransactionCount(
		api: String,
		errorCallback: (Throwable) -> Unit,
		hold: (count: Int) -> Unit
	) {
		RequisitionUtil.requestUnCryptoData<String>(
			api,
			"totalItems",
			true,
			{
				errorCallback(it)
				LogUtil.error("getTransactionCount", it)
			}
		) {
			hold(this.firstOrNull()?.toIntOrNull().orZero())
		}
	}

	fun getBalance(api: String, hold: (Long) -> Unit) {
		RequisitionUtil.requestUnCryptoData<String>(
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
		RequisitionUtil.requestUnCryptoData<String>(
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
		RequisitionUtil.requestUnCryptoData<String>(
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
		RequisitionUtil.requestUnCryptoData<UnspentModel>(
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

	fun getPageInfo(transactionCount: Int, localDataMaxIndex: Int): PageInfo {
		val willGetDataCount =
			if (transactionCount - localDataMaxIndex > DataValue.pageCount) DataValue.pageCount
			else transactionCount - localDataMaxIndex
		return PageInfo(0, willGetDataCount, transactionCount)
	}
}