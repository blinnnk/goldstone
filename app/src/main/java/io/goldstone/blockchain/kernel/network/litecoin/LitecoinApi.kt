package io.goldstone.blockchain.kernel.network.litecoin

import android.support.annotation.WorkerThread
import com.blinnnk.extension.getTargetChild
import com.blinnnk.extension.isNull
import com.blinnnk.extension.orElse
import com.blinnnk.extension.orZero
import io.goldstone.blockchain.common.error.RequestError
import io.goldstone.blockchain.crypto.multichain.Amount
import io.goldstone.blockchain.crypto.multichain.ChainType
import io.goldstone.blockchain.crypto.multichain.CoinSymbol
import io.goldstone.blockchain.kernel.commonmodel.BTCSeriesTransactionTable
import io.goldstone.blockchain.kernel.network.bitcoin.model.UnspentModel
import io.goldstone.blockchain.kernel.network.common.GoldStoneAPI
import io.goldstone.blockchain.kernel.network.common.RequisitionUtil
import org.jetbrains.anko.runOnUiThread
import org.json.JSONArray
import org.json.JSONObject

/**
 * @date 2018/8/13 12:07 PM
 * @author KaySaith
 */

object LitecoinApi {
	fun getBalance(
		address: String,
		isMainThread: Boolean,
		hold: (balance: Amount<Long>?, error: RequestError) -> Unit
	) {
		RequisitionUtil.requestData<String>(
			LitecoinUrl.getBalance(address),
			"",
			true,
			null,
			true
		) { result, error ->
			if (!result.isNull() && error.isNone()) {
				val data = result?.firstOrNull()?.toLongOrNull().orElse(0L)
				if (isMainThread) GoldStoneAPI.context.runOnUiThread { hold(Amount(data), RequestError.None) }
				else hold(Amount(data), RequestError.None)
			} else if (isMainThread) GoldStoneAPI.context.runOnUiThread { hold(null, error) }
			else hold(null, error)
		}
	}

	fun getBalanceFromChainSo(
		address: String,
		@WorkerThread hold: (balance: Double?, error: RequestError) -> Unit
	) {
		RequisitionUtil.requestUnCryptoData<String>(
			LitecoinUrl.getBalanceFromChainSo(address),
			"",
			true
		) { result, error ->
			if (result != null && error.isNone()) {
				val data = JSONObject(result.firstOrNull())
				val balance = data.getTargetChild("data", "confirmed_balance").toDoubleOrNull().orZero()
				hold(balance, error)
			} else hold(null, error)
		}
	}

	fun getUnspentListByAddress(
		address: String,
		@WorkerThread hold: (unspentList: List<UnspentModel>?, error: RequestError) -> Unit
	) {
		RequisitionUtil.requestData(
			LitecoinUrl.getUnspentInfo(address),
			"",
			false,
			null,
			true,
			hold = hold
		)
	}

	fun getTransactions(
		address: String,
		from: Int,
		to: Int,
		@WorkerThread hold: (transactions: List<JSONObject>?, error: RequestError) -> Unit
	) {
		RequisitionUtil.requestData<String>(
			LitecoinUrl.getTransactions(address, from, to),
			"items",
			true,
			null,
			true
		) { result, error ->
			if (!result.isNull() && error.isNone()) {
				val jsonArray = JSONArray(result?.firstOrNull())
				var data = listOf<JSONObject>()
				(0 until jsonArray.length()).forEach {
					data += JSONObject(jsonArray[it].toString())
				}
				hold(data, error)
			} else hold(null, error)
		}
	}

	fun getTransactionCount(
		address: String,
		hold: (count: Int?, error: RequestError) -> Unit
	) {
		RequisitionUtil.requestData<String>(
			LitecoinUrl.getTransactions(address, 999999999, 0),
			"totalItems",
			true,
			null,
			true
		) { result, error ->
			hold(result?.firstOrNull()?.toIntOrNull(), error)
		}
	}

	// 因为通知中心是混合主网测试网的查账所以, 相关接口设计为需要传入网络头的参数头
	fun getTransactionByHash(
		hash: String,
		address: String,
		targetNet: String,
		@WorkerThread hold: (transaction: BTCSeriesTransactionTable?, error: RequestError) -> Unit
	) {
		RequisitionUtil.requestData<String>(
			LitecoinUrl.getTransactionByHash(targetNet, hash),
			"",
			true,
			null,
			true
		) { result, error ->
			if (!result.isNull() && error.isNone()) {
				val data = JSONObject(result?.firstOrNull())
				hold(
					BTCSeriesTransactionTable(
						data,
						// 这里拉取的数据只在通知中心展示并未插入数据库 , 所以 DataIndex 随便设置即可
						0,
						address,
						CoinSymbol.LTC.symbol!!,
						false,
						ChainType.LTC.id
					),
					error
				)
			} else hold(null, error)
		}
	}
}