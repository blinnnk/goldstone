package io.goldstone.blinnnk.kernel.network.bitcoin

import android.support.annotation.WorkerThread
import com.blinnnk.extension.isNotNull
import com.blinnnk.extension.safeGet
import io.goldstone.blinnnk.common.error.RequestError
import io.goldstone.blinnnk.crypto.multichain.node.ChainURL
import io.goldstone.blinnnk.kernel.network.btcseries.insight.InsightApi
import org.json.JSONObject

/**
 * @date 2018/7/23 7:43 PM
 * @author KaySaith
 */
object BTCSeriesJsonRPC {

	/**
	 * 估算交易在 `n blocks` 个区块开始确认的每千字节的大致费用,
	 * 如果没有足够的交易和区块用来估算则会返回一个负值，-1 表示交易费为 0
	 */
	fun estimateSmartFee(
		chainURL: ChainURL,
		blocks: Int,
		@WorkerThread hold: (fee: Double?, error: RequestError) -> Unit
	) {
		InsightApi.getEstimateFee(
			chainURL.chainType,
			blocks,
			hold
		)
	}

	fun sendRawTransaction(
		chainURL: ChainURL,
		signedMessage: String,
		@WorkerThread hold: (hash: String?, error: RequestError) -> Unit
	) {
		InsightApi.sendRAWTransaction(
			chainURL.chainType,
			signedMessage
		) { result, error ->
			if (result.isNotNull() && error.isNone()) {
				hold(
					if (result.contains("result")) JSONObject(JSONObject(result).safeGet("txid")).safeGet("result")
					else JSONObject(result).safeGet("txid"),
				error
				)
			} else (hold(null, error))
		}
	}

	fun getConfirmations(
		chainURL: ChainURL,
		txID: String,
		hold: (confirmCount: Int?, error: RequestError) -> Unit
	) {
		InsightApi.getTransactionJSONByHash(
			chainURL.chainID,
			txID
		) { json, error ->
			try {
				hold(JSONObject(json).safeGet("confirmations").toIntOrNull(), error)
			} catch (error: Exception) {
				hold(null, RequestError(error.message.orEmpty()))
			}
		}
	}

	fun getBlockCount(
		chainURL: ChainURL,
		hold: (blockCount: Int?, error: RequestError) -> Unit
	) {
		InsightApi.getBlockCount(
			chainURL.chainType,
			hold
		)
	}
}