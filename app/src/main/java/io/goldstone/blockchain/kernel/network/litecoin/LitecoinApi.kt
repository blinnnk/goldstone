package io.goldstone.blockchain.kernel.network.litecoin

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
 * @date 2018/8/13 12:07 PM
 * @author KaySaith
 */

object LitecoinApi {
	fun getBalance(address: String, hold: (Long) -> Unit) {
		BTCSeriesApiUtils.getBalance(LitecoinUrl.getBalance(address), hold)
	}

	fun getUnspentListByAddress(
		address: String,
		hold: (List<UnspentModel>) -> Unit
	) {
		BTCSeriesApiUtils.getUnspentListByAddress(LitecoinUrl.getUnspentInfo(address), hold)
	}

	fun getTransactions(
		address: String,
		errorCallback: (Throwable) -> Unit,
		hold: (List<JSONObject>) -> Unit
	) {
		BTCSeriesApiUtils.getTransactions(
			LitecoinUrl.getTransactions(address),
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
					address,
					CryptoSymbol.ltc,
					false,
					ChainType.LTC.id
				)
			)
		}
	}
}