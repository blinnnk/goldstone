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

	fun getTransactionByHash(
		hash: String,
		address: String,
		errorCallback: (Throwable) -> Unit,
		hold: (BTCSeriesTransactionTable?) -> Unit
	) {
		BTCSeriesApiUtils.getTransactionByHash(
			BitcoinUrl.getTransactionByHash(hash),
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

	fun getBlockNumberByTransactionHash(
		hash: String,
		errorCallback: (Throwable) -> Unit,
		hold: (Int?) -> Unit
	) {
		BTCSeriesApiUtils.getTransactionByHash(
			BitcoinUrl.getTransactionByHash(hash),
			errorCallback
		) {
			hold(
				if (isNull()) null
				else {
					// insight 第三方接口有时候会返回 `-1`
					val blockNumber =
						it!!.safeGet("blockheight").toIntOrNull().orZero()
					if (blockNumber < 0) null
					else blockNumber
				}
			)
		}
	}
}