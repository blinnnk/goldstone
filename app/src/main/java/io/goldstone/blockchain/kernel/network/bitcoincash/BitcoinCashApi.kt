package io.goldstone.blockchain.kernel.network.bitcoincash

import android.support.annotation.WorkerThread
import com.blinnnk.extension.isNull
import io.goldstone.blockchain.common.error.RequestError
import io.goldstone.blockchain.crypto.multichain.ChainType
import io.goldstone.blockchain.crypto.multichain.CoinSymbol
import io.goldstone.blockchain.kernel.commonmodel.BTCSeriesTransactionTable
import io.goldstone.blockchain.kernel.network.BTCSeriesApiUtils
import io.goldstone.blockchain.kernel.network.bitcoin.model.UnspentModel
import org.json.JSONObject

/**
 * @date 2018/8/15 1:40 PM
 * @author KaySaith
 */

object BitcoinCashApi {

	fun getBalance(
		address: String,
		isMainThread: Boolean,
		hold: (balance: Double?, error: RequestError) -> Unit
	) {
		BTCSeriesApiUtils.getDoubleBalance(BitcoinCashUrl.getBalance(address), isMainThread, hold)
	}

	fun getUnspentListByAddress(
		address: String,
		@WorkerThread hold: (unspents: List<UnspentModel>?, error: RequestError) -> Unit
	) {
		BTCSeriesApiUtils.getUnspentListByAddress(
			BitcoinCashUrl.getUnspentInfo(address),
			hold
		)
	}

	fun getTransactions(
		address: String,
		from: Int,
		to: Int,
		hold: (transactions: List<JSONObject>?, error: RequestError) -> Unit
	) {
		BTCSeriesApiUtils.getTransactions(
			BitcoinCashUrl.getTransactions(address, from, to),
			hold
		)
	}

	fun getTransactionCount(
		address: String,
		hold: (count: Int?, error: RequestError) -> Unit
	) {
		// `From` 值传巨大的目的是获取 `Count` 而不是拉取数据
		BTCSeriesApiUtils.getTransactionCount(
			BitcoinCashUrl.getTransactions(address, 999999999, 0),
			hold
		)
	}

	fun getTransactionByHash(
		hash: String,
		address: String,
		targetNet: String,
		hold: (data: BTCSeriesTransactionTable?, error: RequestError) -> Unit
	) {
		BTCSeriesApiUtils.getTransactionByHash(
			BitcoinCashUrl.getTransactionByHash(targetNet, hash)
		) { transaction, error ->
			if (!transaction.isNull() && error.isNone()) {
				hold(
					BTCSeriesTransactionTable(
						transaction!!,
						// 这里拉取的数据只在通知中心展示并未插入数据库 , 所以 DataIndex 随便设置即可
						0,
						address,
						CoinSymbol.bch,
						false,
						ChainType.BCH.id
					),
					error
				)
			} else hold(null, error)
		}
	}
}