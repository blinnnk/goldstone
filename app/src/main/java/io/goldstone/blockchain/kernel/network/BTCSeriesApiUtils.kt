package io.goldstone.blockchain.kernel.network

import android.support.annotation.WorkerThread
import com.blinnnk.extension.isNull
import com.blinnnk.extension.orZero
import com.blinnnk.extension.safeGet
import io.goldstone.blockchain.common.error.RequestError
import io.goldstone.blockchain.common.value.DataValue
import io.goldstone.blockchain.common.value.PageInfo
import io.goldstone.blockchain.crypto.multichain.Amount
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
		@WorkerThread hold: (transactions: List<JSONObject>?, error: RequestError) -> Unit
	) {
		RequisitionUtil.requestUnCryptoData<String>(
			api,
			"items",
			true
		) { result, error ->
			if (!result.isNull() && error.isNone()) {
				val jsonArray = JSONArray(result!!.firstOrNull())
				var data = listOf<JSONObject>()
				(0 until jsonArray.length()).forEach {
					data += JSONObject(jsonArray[it].toString())
				}
				hold(data, error)
			} else hold(null, error)
		}
	}

	fun getTransactionCount(
		api: String,
		@WorkerThread hold: (count: Int?, error: RequestError) -> Unit
	) {
		RequisitionUtil.requestUnCryptoData<String>(
			api,
			"totalItems",
			true
		) { data, error ->
			if (!data.isNull() && error.isNone()) {
				hold(data!!.firstOrNull()?.toIntOrNull(), error)
			} else hold(null, error)
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
			true
		) { data, error ->
			if (!data.isNull() && error.isNone()) {
				val result = data!!.firstOrNull()?.toLongOrNull()
				if (isMainThread) GoldStoneAPI.context.runOnUiThread { hold(result, error) }
				else hold(result, error)
			} else hold(null, error)
		}
	}

	// `Insight` 不稳定的时候用 `BlockChainInfo` 做备份
	fun getBalanceFromBlockInfo(
		api: String,
		address: String,
		hold: (balance: Amount<Long>?, error: RequestError) -> Unit
	) {
		RequisitionUtil.requestUnCryptoData<String>(
			api,
			address,
			true
		) { data, error ->
			if (!data.isNull() && error.isNone()) {
				hold(Amount(JSONObject(data!!.firstOrNull()).safeGet("final_balance").toLong()), error)
			} else hold(null, error)
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
			true
		) { data, error ->
			if (!data.isNull() && error.isNone()) {
				val balance = data!!.firstOrNull()?.toDoubleOrNull().orZero()
				if (isMainThread) GoldStoneAPI.context.runOnUiThread {
					hold(balance, error)
				} else hold(balance, error)
			} else hold(null, error)
		}
	}

	fun getTransactionByHash(
		api: String,
		hold: (transaction: JSONObject?, error: RequestError) -> Unit
	) {
		RequisitionUtil.requestUnCryptoData<String>(
			api,
			"",
			true
		) { result, error ->
			if (!result.isNull() && error.isNone()) {
				hold(JSONObject(result!!.firstOrNull()), error)
			} else hold(null, error)
		}
	}

	fun getUnspentListByAddress(
		api: String,
		hold: (unspentList: List<UnspentModel>?, error: RequestError) -> Unit
	) {
		RequisitionUtil.requestUnCryptoData(
			api,
			"",
			false,
			hold
		)
	}

	// `Insight` 接口挂掉的时候向 `BlockInfo` 发起请求
	fun getUnspentListByAddressFromBlockInfo(
		api: String,
		hold: (unspents: List<BlockInfoUnspentModel>?, error: RequestError) -> Unit
	) {
		RequisitionUtil.requestUnCryptoData(
			api,
			"unspent_outputs",
			false,
			hold
		)
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