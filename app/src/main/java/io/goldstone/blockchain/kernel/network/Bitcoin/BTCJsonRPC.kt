package io.goldstone.blockchain.kernel.network.bitcoin

import android.support.annotation.WorkerThread
import com.blinnnk.extension.isNull
import com.blinnnk.extension.safeGet
import io.goldstone.blockchain.common.error.RequestError
import io.goldstone.blockchain.common.utils.LogUtil
import io.goldstone.blockchain.kernel.network.ChainURL
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
		chainName: String,
		blocks: Int,
		isSmartFee: Boolean,
		errorCallback: (RequestError) -> Unit,
		@WorkerThread hold: (Double?) -> Unit
	) {
		val method =
			if (isSmartFee) BitcoinMethod.EstimatesmartFee.method
			else BitcoinMethod.EstimateFee.method
		RequestBody.create(
			GoldStoneEthCall.contentType,
			ParameterUtil.prepareJsonRPC(
				ChainURL.getCurrentEncryptStatusByNodeName(chainName),
				method,
				1,
				false,
				false,
				blocks
			)
		).let { it ->
			RequisitionUtil.callChainBy(
				it,
				errorCallback,
				chainName
			) {
				if (it.isNotEmpty()) {
					val fee = if (isSmartFee) JSONObject(it).safeGet("feerate").toDoubleOrNull()
					else it.toDoubleOrNull()
					hold(fee)
				} else hold(null)
			}
		}
	}

	fun sendRawTransaction(
		chainName: String,
		signedMessage: String,
		errorCallback: (RequestError) -> Unit,
		@WorkerThread hold: (String?) -> Unit
	) {
		RequestBody.create(
			GoldStoneEthCall.contentType,
			ParameterUtil.prepareJsonRPC(
				ChainURL.getCurrentEncryptStatusByNodeName(chainName),
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
				errorCallback,
				chainName
			) {
				// Return Transaction hash
				hold(if (it.isNotEmpty()) it else null)
			}
		}
	}

	fun getConfirmations(
		chainName: String,
		txID: String,
		errorCallback: (RequestError) -> Unit,
		hold: (Int?) -> Unit
	) {
		RequestBody.create(
			GoldStoneEthCall.contentType,
			ParameterUtil.prepareJsonRPC(
				ChainURL.getCurrentEncryptStatusByNodeName(chainName),
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
				errorCallback,
				chainName
			) {
				val confirmations = try {
					JSONObject(it).safeGet("confirmations").toIntOrNull()
				} catch (error: Exception) {
					LogUtil.error("getConfirmations", error)
					null
				}
				// Return Transaction hash
				hold(if (confirmations.isNull()) null else confirmations)
			}
		}
	}
}