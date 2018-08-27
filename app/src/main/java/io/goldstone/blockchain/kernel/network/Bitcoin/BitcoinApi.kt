package io.goldstone.blockchain.kernel.network.bitcoin

import com.blinnnk.extension.isNull
import io.goldstone.blockchain.common.utils.LogUtil
import io.goldstone.blockchain.crypto.ChainType
import io.goldstone.blockchain.crypto.CryptoSymbol
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

	fun getBalance(address: String, hold: (Long) -> Unit) {
		// 向 `Insight` 发起余额查询请求
		BTCSeriesApiUtils.getBalance(
			BitcoinUrl.getBalance(address),
			{ error ->
				// 如果 `Insight` 的接口出错那么向 `BlockInfo` 发起请求
				BTCSeriesApiUtils.getBalanceFromBlockInfo(
					BitcoinUrl.getBalanceFromBlockInfo(address),
					address,
					{ blockInfoError ->
						LogUtil.error("BitcoinApi GetBalance", blockInfoError)
					},
					hold
				)
				LogUtil.error("BitcoinApi GetBalance", error)
			},
			hold
		)
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

	fun getTransactionsCount(
		address: String,
		errorCallback: (Throwable) -> Unit,
		hold: (count: Int) -> Unit
	) {
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
					// 这里拉取的数据只在通知中心展示并未插入数据库 , 所以 DataIndex 随便设置即可
					0,
					address,
					CryptoSymbol.pureBTCSymbol,
					false,
					ChainType.BTC.id
				)
			)
		}
	}

}