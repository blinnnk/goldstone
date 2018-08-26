package io.goldstone.blockchain.kernel.network.bitcoincash

import com.blinnnk.extension.isNull
import io.goldstone.blockchain.crypto.ChainType
import io.goldstone.blockchain.crypto.CryptoSymbol
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
		hold: (List<UnspentModel>) -> Unit
	) {
		BTCSeriesApiUtils.getUnspentListByAddress(BitcoinCashUrl.getUnspentInfo(address), hold)
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
		hold: (List<JSONObject>) -> Unit
	) {
		BTCSeriesApiUtils.getTransactions(
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
					CryptoSymbol.bch,
					false,
					ChainType.BCH.id
				)
			)
		}
	}
}