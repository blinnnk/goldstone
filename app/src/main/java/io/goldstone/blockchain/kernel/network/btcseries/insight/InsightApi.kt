package io.goldstone.blockchain.kernel.network.btcseries.insight

import android.support.annotation.WorkerThread
import io.goldstone.blockchain.common.error.RequestError
import io.goldstone.blockchain.common.value.DataValue
import io.goldstone.blockchain.common.value.PageInfo
import io.goldstone.blockchain.crypto.multichain.Amount
import io.goldstone.blockchain.crypto.multichain.ChainID
import io.goldstone.blockchain.crypto.multichain.ChainType
import io.goldstone.blockchain.crypto.multichain.isBCH
import io.goldstone.blockchain.crypto.utils.toSatoshi
import io.goldstone.blockchain.kernel.commonmodel.BTCSeriesTransactionTable
import io.goldstone.blockchain.kernel.network.bitcoin.model.UnspentModel
import io.goldstone.blockchain.kernel.network.common.RequisitionUtil
import org.json.JSONArray
import org.json.JSONObject

/**
 * @date 2018/8/13 12:07 PM
 * @author KaySaith
 */

object InsightApi {
	fun getBalance(
		chainType: ChainType,
		isEncrypt: Boolean,
		address: String,
		@WorkerThread hold: (balance: Amount<Long>?, error: RequestError) -> Unit
	) {
		RequisitionUtil.requestData<String>(
			InsightUrl.getBalance(chainType, address),
			"",
			true,
			null,
			isEncrypt
		) { result, error ->
			// Insight BCH 的 getBalance 接口返回的是 Double 信息, BTC
			// 和 LTC 返回的是 Satoshi
			if (result != null && error.isNone()) {
				val data =
					if (chainType.isBCH()) result.firstOrNull()?.toDoubleOrNull()?.toSatoshi() ?: 0
					else result.firstOrNull()?.toLongOrNull() ?: 0
				hold(Amount(data), RequestError.None)
			} else hold(null, error)
		}
	}

	fun getUnspents(
		chainType: ChainType,
		isEncrypt: Boolean,
		address: String,
		@WorkerThread hold: (unspentList: List<UnspentModel>?, error: RequestError) -> Unit
	) {
		RequisitionUtil.requestData(
			InsightUrl.getUnspentInfo(chainType, address),
			"",
			false,
			null,
			isEncrypt,
			hold = hold
		)
	}

	fun getTransactions(
		chainType: ChainType,
		isEncrypt: Boolean,
		address: String,
		from: Int,
		to: Int,
		@WorkerThread hold: (transactions: List<JSONObject>?, error: RequestError) -> Unit
	) {
		RequisitionUtil.requestData<String>(
			InsightUrl.getTransactions(chainType, address, from, to),
			"items",
			true,
			null,
			isEncrypt
		) { result, error ->
			if (result != null && error.isNone()) {
				val jsonArray = JSONArray(result.firstOrNull())
				var data = listOf<JSONObject>()
				(0 until jsonArray.length()).forEach {
					data += JSONObject(jsonArray[it].toString())
				}
				hold(data, error)
			} else hold(null, error)
		}
	}

	fun getTransactionCount(
		chainType: ChainType,
		isEncrypt: Boolean,
		address: String,
		hold: (count: Int?, error: RequestError) -> Unit
	) {
		RequisitionUtil.requestData<String>(
			InsightUrl.getTransactions(chainType, address, 999999999, 0),
			"totalItems",
			true,
			null,
			isEncrypt
		) { result, error ->
			hold(result?.firstOrNull()?.toIntOrNull(), error)
		}
	}

	// 因为通知中心是混合主网测试网的查账所以, 相关接口设计为需要传入网络头的参数头
	fun getTransactionByHash(
		chainID: ChainID,
		isEncrypt: Boolean,
		hash: String,
		address: String,
		@WorkerThread hold: (transaction: BTCSeriesTransactionTable?, error: RequestError) -> Unit
	) {
		RequisitionUtil.requestData<String>(
			InsightUrl.getTransactionByHash(chainID, hash),
			"",
			true,
			null,
			isEncrypt
		) { result, error ->
			if (result != null && error.isNone()) {
				val data = JSONObject(result.firstOrNull())
				hold(
					BTCSeriesTransactionTable(
						data,
						// 这里拉取的数据只在通知中心展示并未插入数据库 , 所以 DataIndex 随便设置即可
						0,
						address,
						chainID.getContract().symbol,
						false,
						chainID.getChainType().id
					),
					error
				)
			} else hold(null, error)
		}
	}

	fun getPageInfo(transactionCount: Int, localDataMaxIndex: Int): PageInfo {
		val willGetDataCount =
			if (transactionCount - localDataMaxIndex > DataValue.pageCount) DataValue.pageCount
			else transactionCount - localDataMaxIndex
		// 网络接口数据默认都是从 `0` 开始拉取最新的
		return PageInfo(0, willGetDataCount, localDataMaxIndex)
	}
}