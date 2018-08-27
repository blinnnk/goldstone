package io.goldstone.blockchain.kernel.network.litecoin

import com.blinnnk.extension.isNull
import io.goldstone.blockchain.common.utils.LogUtil
import io.goldstone.blockchain.crypto.ChainType
import io.goldstone.blockchain.crypto.CryptoSymbol
import io.goldstone.blockchain.kernel.commonmodel.BTCSeriesTransactionTable
import io.goldstone.blockchain.kernel.network.BTCSeriesApiUtils
import io.goldstone.blockchain.kernel.network.bitcoin.model.UnspentModel
import org.json.JSONObject

/**
 * @date 2018/8/13 12:07 PM
 * @author KaySaith
 */

object LitecoinApi {
	fun getBalance(address: String, hold: (Long) -> Unit) {
		BTCSeriesApiUtils.getBalance(
			LitecoinUrl.getBalance(address),
			{
				// 当 Insight 接口挂掉的时候向其他备份 Litecoin 接口发起请求
				// TODO
				LogUtil.error("getBalance Litecoin", it)
			},
			hold
		)
	}

	fun getUnspentListByAddress(
		address: String,
		hold: (List<UnspentModel>) -> Unit
	) {
		BTCSeriesApiUtils.getUnspentListByAddress(
			LitecoinUrl.getUnspentInfo(address),
			{
				LogUtil.error("getUnspentListByAddress Litecoin", it)
			},
			hold
		)
	}

	fun getTransactions(
		address: String,
		from: Int,
		to: Int,
		errorCallback: (Throwable) -> Unit,
		hold: (List<JSONObject>) -> Unit
	) {
		BTCSeriesApiUtils.getTransactions(
			LitecoinUrl.getTransactions(address, from, to),
			errorCallback,
			hold
		)
	}

	fun getTransactionsCount(
		address: String,
		errorCallback: (Throwable) -> Unit,
		hold: (count: Int) -> Unit
	) {
		BTCSeriesApiUtils.getTransactionCount(
			LitecoinUrl.getTransactions(address, 999999999, 0),
			errorCallback,
			hold
		)
	}

	// 因为通知中心是混合主网测试网的查账所以, 相关接口设计为需要传入网络头的参数头
	fun getTransactionByHash(
		hash: String,
		address: String,
		targetNet: String,
		errorCallback: (Throwable) -> Unit,
		hold: (BTCSeriesTransactionTable?) -> Unit
	) {
		BTCSeriesApiUtils.getTransactionByHash(
			LitecoinUrl.getTransactionByHash(targetNet, hash),
			errorCallback
		) {
			hold(
				if (isNull()) null
				else BTCSeriesTransactionTable(
					it!!,
					// 这里拉取的数据只在通知中心展示并未插入数据库 , 所以 DataIndex 随便设置即可
					0,
					address,
					CryptoSymbol.ltc,
					false,
					ChainType.LTC.id
				)
			)
		}
	}
}