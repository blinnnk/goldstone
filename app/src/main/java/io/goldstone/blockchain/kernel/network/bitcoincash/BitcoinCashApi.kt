package io.goldstone.blockchain.kernel.network.bitcoincash

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
		System.out.println("hello url ${BitcoinCashUrl.getUnspentInfo(address)}")
		BTCSeriesApiUtils.getUnspentListByAddress(BitcoinCashUrl.getUnspentInfo(address), hold)
	}

	fun getTransactions(
		address: String,
		errorCallback: (Throwable) -> Unit,
		hold: (List<JSONObject>) -> Unit
	) {
		BTCSeriesApiUtils.getTransactions(
			BitcoinCashUrl.getTransactions(address),
			errorCallback,
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
			BitcoinCashUrl.getTransactionByHash(hash),
			errorCallback
		) {
			hold(
				if (isNull()) null
				else BTCSeriesTransactionTable(
					it!!,
					address,
					CryptoSymbol.bch,
					false,
					ChainType.BCH.id
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
			BitcoinCashUrl.getTransactionByHash(hash),
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