package io.goldstone.blockchain.kernel.network.bitcoincash

import android.support.annotation.WorkerThread
import com.blinnnk.extension.isNull
import io.goldstone.blockchain.common.utils.LogUtil
import io.goldstone.blockchain.crypto.multichain.CoinSymbol
import io.goldstone.blockchain.crypto.multichain.MultiChainType
import io.goldstone.blockchain.kernel.commonmodel.BTCSeriesTransactionTable
import io.goldstone.blockchain.kernel.network.BTCSeriesApiUtils
import io.goldstone.blockchain.kernel.network.bitcoin.model.UnspentModel
import org.json.JSONObject

/**
 * @date 2018/8/15 1:40 PM
 * @author KaySaith
 */

object BitcoinCashApi {

	fun getBalance(address: String, hold: (Double) -> Unit) {
		BTCSeriesApiUtils.getDoubleBalance(BitcoinCashUrl.getBalance(address), hold)
	}

	fun getUnspentListByAddress(
		address: String,
		@WorkerThread hold: (List<UnspentModel>) -> Unit
	) {
		BTCSeriesApiUtils.getUnspentListByAddress(
			BitcoinCashUrl.getUnspentInfo(address),
			{
				LogUtil.error("getUnspentListByAddress", it)
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
			BitcoinCashUrl.getTransactions(address, from, to),
			errorCallback,
			hold
		)
	}

	fun getTransactionCount(
		address: String,
		errorCallback: (Throwable) -> Unit,
		hold: (count: Int) -> Unit
	) {
		// `From` 值传巨大的目的是获取 `Count` 而不是拉取数据
		BTCSeriesApiUtils.getTransactionCount(
			BitcoinCashUrl.getTransactions(address, 999999999, 0),
			errorCallback,
			hold
		)
	}

	fun getTransactionByHash(
		hash: String,
		address: String,
		targetNet: String,
		errorCallback: (Throwable) -> Unit,
		hold: (BTCSeriesTransactionTable?) -> Unit
	) {
		BTCSeriesApiUtils.getTransactionByHash(
			BitcoinCashUrl.getTransactionByHash(targetNet, hash),
			errorCallback
		) {
			hold(
				if (isNull()) null
				else BTCSeriesTransactionTable(
					it!!,
					// 这里拉取的数据只在通知中心展示并未插入数据库 , 所以 DataIndex 随便设置即可
					0,
					address,
					CoinSymbol.bch,
					false,
					MultiChainType.BCH.id
				)
			)
		}
	}
}