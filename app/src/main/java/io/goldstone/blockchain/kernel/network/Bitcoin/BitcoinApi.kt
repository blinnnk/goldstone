package io.goldstone.blockchain.kernel.network.bitcoin

import com.blinnnk.extension.isNull
import com.blinnnk.extension.orZero
import com.blinnnk.extension.safeGet
import io.goldstone.blockchain.crypto.ChainType
import io.goldstone.blockchain.crypto.CryptoSymbol
import io.goldstone.blockchain.kernel.commonmodel.BTCSeriesTransactionTable
import io.goldstone.blockchain.kernel.network.BTCSeriesApiUtils
import io.goldstone.blockchain.kernel.network.bitcoin.model.UnspentModel
import org.json.JSONObject

/**
 * @date 2018/7/19 1:46 AM
 * @author KaySaith
 */
object BitcoinApi {

	fun getBalance(address: String, hold: (Long) -> Unit) {
		BTCSeriesApiUtils.getBalance(BitcoinUrl.getBalance(address), hold)
	}

	fun getBTCTransactions(
		address: String,
		errorCallback: (Throwable) -> Unit,
		hold: (List<JSONObject>) -> Unit
	) {
		BTCSeriesApiUtils.getTransactions(
			BitcoinUrl.getTransactionList(address),
			errorCallback,
			hold
		)
	}

	fun getUnspentListByAddress(
		address: String,
		hold: (List<UnspentModel>) -> Unit
	) {
		BTCSeriesApiUtils.getUnspentListByAddress(
			BitcoinUrl.getUnspentInfo(address),
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
					address,
					CryptoSymbol.pureBTCSymbol,
					false,
					ChainType.BTC.id
				)
			)
		}
	}

}