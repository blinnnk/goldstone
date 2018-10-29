package io.goldstone.blockchain.kernel.network.litecoin

import android.support.annotation.WorkerThread
import com.blinnnk.extension.*
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
			{ hold(null, it) },
			null,
			true
		) {
			val result = firstOrNull()?.toLongOrNull().orElse(0L)
			if (isMainThread) GoldStoneAPI.context.runOnUiThread { hold(Amount(result), RequestError.None) }
			else hold(Amount(result), RequestError.None)
		}
	}

	fun getBalanceFromChainSo(
		address: String,
		@WorkerThread hold: (balance: Double?, error: RequestError) -> Unit
	) {
		RequisitionUtil.requestUnCryptoData<String>(
			LitecoinUrl.getBalanceFromChainSo(address),
			"",
			true,
			{ hold(null, it) }
		) {
			val result = JSONObject(firstOrNull())
			val balance = result.getTargetObject("data").safeGet("confirmed_balance").toDoubleOrNull().orZero()
			hold(balance, RequestError.None)
		}
	}

	fun getUnspentListByAddress(
		address: String,
		@WorkerThread hold: (unspentList: List<UnspentModel>?, error: RequestError) -> Unit
	) {
		RequisitionUtil.requestData<UnspentModel>(
			LitecoinUrl.getUnspentInfo(address),
			"",
			false,
			{ hold(null, it) },
			null,
			true
		) {
			hold(this, RequestError.None)
		}
	}

	fun getTransactions(
		address: String,
		from: Int,
		to: Int,
		errorCallback: (Throwable) -> Unit,
		hold: (List<JSONObject>) -> Unit
	) {
		RequisitionUtil.requestData<String>(
			LitecoinUrl.getTransactions(address, from, to),
			"items",
			true,
			{ errorCallback(it) },
			null,
			true
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
		address: String,
		errorCallback: (RequestError) -> Unit,
		hold: (count: Int) -> Unit
	) {
		RequisitionUtil.requestData<String>(
			LitecoinUrl.getTransactions(address, 999999999, 0),
			"totalItems",
			true,
			errorCallback,
			null,
			true
		) {
			hold(this.firstOrNull()?.toIntOrNull().orZero())
		}
	}

	// 因为通知中心是混合主网测试网的查账所以, 相关接口设计为需要传入网络头的参数头
	fun getTransactionByHash(
		hash: String,
		address: String,
		targetNet: String,
		errorCallback: (Throwable) -> Unit,
		hold: (BTCSeriesTransactionTable?) -> Unit
	) {
		RequisitionUtil.requestData<String>(
			LitecoinUrl.getTransactionByHash(targetNet, hash),
			"",
			true,
			{ errorCallback(it) },
			null,
			true
		) {
			val result = if (isNotEmpty()) JSONObject(this[0]) else null
			hold(
				if (isNull()) null
				else BTCSeriesTransactionTable(
					result!!,
					// 这里拉取的数据只在通知中心展示并未插入数据库 , 所以 DataIndex 随便设置即可
					0,
					address,
					CoinSymbol.LTC.symbol!!,
					false,
					ChainType.LTC.id
				)
			)
		}
	}
}