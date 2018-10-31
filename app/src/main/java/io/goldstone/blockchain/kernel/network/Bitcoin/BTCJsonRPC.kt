package io.goldstone.blockchain.kernel.network.bitcoin

import android.support.annotation.WorkerThread
import com.blinnnk.extension.isNull
import com.blinnnk.extension.safeGet
import io.goldstone.blockchain.common.error.RequestError
import io.goldstone.blockchain.crypto.multichain.node.ChainURL
import io.goldstone.blockchain.kernel.network.ParameterUtil
import io.goldstone.blockchain.kernel.network.bitcoin.value.BitcoinMethod
import io.goldstone.blockchain.kernel.network.common.RequisitionUtil
import io.goldstone.blockchain.kernel.network.ethereum.GoldStoneEthCall
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
			GoldStoneEthCall.contentType,
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
				val fee =
					if (isSmartFee) JSONObject(result!!).safeGet("feerate").toDoubleOrNull()
					else result!!.toDoubleOrNull()
				if (!result.isNull() && error.isNone()) hold(fee, error)
				else hold(null, error)
			}
		}
	}

	fun sendRawTransaction(
		chainURL: ChainURL,
		signedMessage: String,
		@WorkerThread hold: (hash: String?, error: RequestError) -> Unit
	) {
		RequestBody.create(
			GoldStoneEthCall.contentType,
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
			GoldStoneEthCall.contentType,
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
			RequisitionUtil.callChainBy(
				it,
				chainURL
			) { result, error ->
				val confirmations = JSONObject(result).safeGet("confirmations").toIntOrNull()
				// Return Transaction hash
				hold(confirmations, error)
			}
		}
	}
}