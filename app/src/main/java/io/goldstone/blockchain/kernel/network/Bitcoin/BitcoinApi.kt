package io.goldstone.blockchain.kernel.network.bitcoin

import android.support.annotation.WorkerThread
import com.blinnnk.extension.isNull
import io.goldstone.blockchain.common.error.RequestError
import io.goldstone.blockchain.common.utils.LogUtil
import io.goldstone.blockchain.crypto.multichain.ChainType
import io.goldstone.blockchain.crypto.multichain.CoinSymbol
import io.goldstone.blockchain.kernel.commonmodel.BTCSeriesTransactionTable
import io.goldstone.blockchain.kernel.network.BTCSeriesApiUtils
import io.goldstone.blockchain.kernel.network.bitcoin.model.UnspentModel
import org.json.JSONObject

/**
 * @date 2018/7/19 1:46 AM
 * @author KaySaith
 * @important
 * `Insight` 的 `BTCSeries` 数据集比较全面, 所以我们默认采集数据是通过它来做,
 * 但 `Insight` 的 `BTC` 接口不稳定, 固这里的设计为嵌套请求,
 * 当 `Insight` 的数据结果出现问题的时候, 则向 `BlockInfo` 重新发起请求
 * 确保数据尽可能有结果.
 */
object BitcoinApi {

	fun getBalance(
		address: String,
		@WorkerThread hold: (balance: Long?, error: RequestError) -> Unit
	) {
		// 向 `Insight` 发起余额查询请求
		BTCSeriesApiUtils.getBalance(
			BitcoinUrl.getBalance(address)
		) { balance, error ->
			if (!balance.isNull() && error.isNone()) {
				hold(balance!!, error)
			} else BTCSeriesApiUtils.getBalanceFromBlockInfo(
				BitcoinUrl.getBalanceFromBlockInfo(address),
				address,
				hold
			)
		}
	}

	fun getBTCTransactions(
		address: String,
		from: Int,
		to: Int,
		errorCallback: (Throwable) -> Unit,
		hold: (List<JSONObject>) -> Unit
	) {
		BTCSeriesApiUtils.getTransactions(
			BitcoinUrl.getTransactions(address, from, to),
			errorCallback,
			hold
		)
	}

	fun getTransactionCount(
		address: String,
		errorCallback: (RequestError) -> Unit,
		hold: (count: Int) -> Unit
	) {
		// `from` 传入一个特大值, `to` 传入 `0` 确保返回的数据只有 `count` 参数而不包含子集
		BTCSeriesApiUtils.getTransactionCount(
			BitcoinUrl.getTransactions(address, 999999999, 0),
			errorCallback,
			hold
		)
	}

	fun getUnspentListByAddress(
		address: String,
		hold: (List<UnspentModel>) -> Unit
	) {
		// 向 `Insight` 发起请求
		BTCSeriesApiUtils.getUnspentListByAddress(
			BitcoinUrl.getUnspentInfo(address),
			{ error ->
				// 当 `Insight` 接口出现问题的时候向 `BlockInfo` 重发请求
				BTCSeriesApiUtils.getUnspentListByAddressFromBlockInfo(
					BitcoinUrl.getUnspentInfoFromBlockInfo(address),
					{ blockInfoError ->
						LogUtil.error("getUnspentListByAddressFromBlockInfo", blockInfoError)
					}
				) { blockInfoUnspentModels ->
					hold(blockInfoUnspentModels.map { UnspentModel(it) })
				}
				LogUtil.error("getUnspentListByAddress", error)
			},
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
			BitcoinUrl.getTransactionByHash(targetNet, hash),
			errorCallback
		) {
			hold(
				if (isNull()) null
				else BTCSeriesTransactionTable(
					it!!,
					// 这里拉取的数据只在通知中心展示并未插入数据库 , 所以 `DataIndex` 随便设置即可
					0,
					address,
					CoinSymbol.pureBTCSymbol,
					false,
					ChainType.BTC.id
				)
			)
		}
	}

}