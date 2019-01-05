package io.goldstone.blinnnk.kernel.network.bitcoin

import android.support.annotation.WorkerThread
import com.blinnnk.extension.safeGet
import io.goldstone.blinnnk.common.error.RequestError
import io.goldstone.blinnnk.crypto.multichain.node.ChainURL
import io.goldstone.blinnnk.kernel.network.ParameterUtil
import io.goldstone.blinnnk.kernel.network.bitcoin.value.BitcoinMethod
import io.goldstone.blinnnk.kernel.network.common.RequisitionUtil
import io.goldstone.blinnnk.kernel.network.ethereum.ETHJsonRPC
import okhttp3.RequestBody
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
	fun estimatesmartFee(
		chainURL: ChainURL,
		blocks: Int,
		isSmartFee: Boolean,
		@WorkerThread hold: (fee: Double?, error: RequestError) -> Unit
	) {
		val method =
			if (isSmartFee) BitcoinMethod.EstimatesmartFee.method
			else BitcoinMethod.EstimateFee.method
		RequestBody.create(
			ETHJsonRPC.contentType,
			ParameterUtil.prepareJsonRPC(
				chainURL.isEncrypt,
				method,
				1,
				false,
				false,
				blocks
			)
		).let { it ->
			RequisitionUtil.callChainBy(it, chainURL) { result, error ->
				if (result != null && error.isNone()) {
					val fee =
						if (isSmartFee) JSONObject(result).safeGet("feerate").toDoubleOrNull()
						else result.toDoubleOrNull()
					hold(fee, error)
				} else hold(null, error)
			}
		}
	}

	fun sendRawTransaction(
		chainURL: ChainURL,
		signedMessage: String,
		@WorkerThread hold: (hash: String?, error: RequestError) -> Unit
	) {
		RequestBody.create(
			ETHJsonRPC.contentType,
			ParameterUtil.prepareJsonRPC(
				chainURL.isEncrypt,
				BitcoinMethod.SendRawTansaction.method,
				1,
				false,
				false,
				signedMessage,
				true // anyone can pay by this signed message
			)
		).let { requestBody ->
			RequisitionUtil.callChainBy(
				requestBody,
				chainURL,
				hold
			)
		}
	}

	fun getConfirmations(
		chainURL: ChainURL,
		txID: String,
		hold: (confirmCount: Int?, error: RequestError) -> Unit
	) {
		RequestBody.create(
			ETHJsonRPC.contentType,
			ParameterUtil.prepareJsonRPC(
				chainURL.isEncrypt,
				BitcoinMethod.GetRawTransaction.method,
				null,
				false,
				false,
				txID,
				1
			)
		).let { it ->
			RequisitionUtil.callChainBy(it, chainURL) { result, error ->
				if (result != null && error.isNone()) {
					val confirmations = JSONObject(result).safeGet("confirmations").toIntOrNull()
					hold(confirmations, error)
				} else hold(null, error)
			}
		}
	}

	fun getBlockCount(
		chainURL: ChainURL,
		hold: (blockCount: Int?, error: RequestError) -> Unit
	) {
		RequestBody.create(
			ETHJsonRPC.contentType,
			ParameterUtil.prepareJsonRPC(
				chainURL.isEncrypt,
				BitcoinMethod.Getblockcount.method,
				null,
				false,
				false,
				null
			)
		).let { it ->
			RequisitionUtil.callChainBy(it, chainURL) { result, error ->
				hold(result?.toLong()?.toInt(), error)
			}
		}
	}
}