package io.goldstone.blockchain.kernel.network

import com.blinnnk.extension.orZero
import com.blinnnk.extension.safeGet
import io.goldstone.blockchain.common.error.RequestError
import io.goldstone.blockchain.common.utils.LogUtil
import io.goldstone.blockchain.common.value.DataValue
import io.goldstone.blockchain.common.value.PageInfo
import io.goldstone.blockchain.kernel.network.bitcoin.model.BlockInfoUnspentModel
import io.goldstone.blockchain.kernel.network.bitcoin.model.UnspentModel
import io.goldstone.blockchain.kernel.network.common.GoldStoneAPI
import io.goldstone.blockchain.kernel.network.common.RequisitionUtil
import org.jetbrains.anko.runOnUiThread
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
		errorCallback: (RequestError) -> Unit,
		hold: (count: Int) -> Unit
	) {
		RequisitionUtil.requestUnCryptoData<String>(
			api,
			"totalItems",
			true,
			errorCallback
		) {
			hold(this.firstOrNull()?.toIntOrNull().orZero())
		}
	}

	fun getBalance(
		api: String,
		isMainThread: Boolean,
		hold: (balance: Long?, error: RequestError) -> Unit
	) {
		RequisitionUtil.requestUnCryptoData<String>(
			api,
			"",
			true,
			{ hold(null, it) }
		) {
			val result = firstOrNull()?.toLongOrNull()
			if (isMainThread) GoldStoneAPI.context.runOnUiThread { hold(result, RequestError.None) }
			else hold(result, RequestError.None)
		}
	}

	// `Insight` 不稳定的时候用 `BlockChainInfo` 做备份
	fun getBalanceFromBlockInfo(
		api: String,
		address: String,
		hold: (balance: Long?, error: RequestError) -> Unit
	) {
		RequisitionUtil.requestUnCryptoData<String>(
			api,
			address,
			true,
			{ hold(null, it) }
		) {
			if (isNotEmpty()) hold(JSONObject(first()).safeGet("final_balance").toLong(), RequestError.None)
		}
	}

	fun getDoubleBalance(
		api: String,
		isMainThread: Boolean,
		hold: (balance: Double?, error: RequestError) -> Unit
	) {
		RequisitionUtil.requestUnCryptoData<String>(
			api,
			"",
			true,
			{ hold(null, it) }
		) {
			val balance = firstOrNull()?.toDoubleOrNull() ?: 0.0
			if (isMainThread) GoldStoneAPI.context.runOnUiThread {
				hold(balance, RequestError.None)
			} else hold(balance, RequestError.None)
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
		errorCallback: (Throwable) -> Unit,
		hold: (List<UnspentModel>) -> Unit
	) {
		RequisitionUtil.requestUnCryptoData<UnspentModel>(
			api,
			"",
			false,
			{
				errorCallback(it)
				LogUtil.error("getUnspentListByAddress", it)
			}
		) {
			hold(if (isNotEmpty()) this else listOf())
		}
	}

	// `Insight` 接口挂掉的时候向 `BlockInfo` 发起请求
	fun getUnspentListByAddressFromBlockInfo(
		api: String,
		errorCallback: (Throwable) -> Unit,
		hold: (List<BlockInfoUnspentModel>) -> Unit
	) {
		RequisitionUtil.requestUnCryptoData<BlockInfoUnspentModel>(
			api,
			"unspent_outputs",
			false,
			{
				errorCallback(it)
				LogUtil.error("getUnspentListByAddressFromBlockInfo", it)
			}
		) {
			hold(if (isNotEmpty()) this else listOf())
		}
	}

	fun getPageInfo(transactionCount: Int, localDataMaxIndex: Int): PageInfo {
		val willGetDataCount =
			if (transactionCount - localDataMaxIndex > DataValue.pageCount) DataValue.pageCount
			else transactionCount - localDataMaxIndex
		// 网络接口数据默认都是从 `0` 开始拉取最新的
		// TODO 这里需要增加复杂的补充翻页逻辑
		return PageInfo(0, willGetDataCount, localDataMaxIndex)
	}
}