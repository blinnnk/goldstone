package io.goldstone.blockchain.kernel.network.bitcoin

import com.blinnnk.extension.isNull
import com.blinnnk.extension.orZero
import com.blinnnk.extension.safeGet
import io.goldstone.blockchain.common.utils.LogUtil
import io.goldstone.blockchain.kernel.network.ChainURL
import io.goldstone.blockchain.kernel.network.GoldStoneEthCall
import io.goldstone.blockchain.kernel.network.ParameterUtil
import io.goldstone.blockchain.kernel.network.RequisitionUtil
import io.goldstone.blockchain.kernel.network.bitcoin.value.BitcoinMethod
import okhttp3.RequestBody
import org.json.JSONObject

/**
 * @date 2018/7/23 7:43 PM
 * @author KaySaith
 */
object BTCSeriesJsonRPC {

	/**
	 * 估算交易在 `nblocks` 个区块开始确认的每千字节的大致费用,
	 * 如果没有足够的交易和区块用来估算则会返回一个负值，-1 表示交易费为 0
	 */
	fun estimatesmartFee(
		chainName: String,
		blocks: Int,
		isSmartFee: Boolean = true,
		hold: (Double?) -> Unit
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
				{ error, reason ->
					LogUtil.error("estimatesmartFee $reason", error)
				},
				chainName
			) {
				if (it.isNotEmpty()) {
					val fee =
						if (!isSmartFee) it.toDoubleOrNull()
						else JSONObject(it).safeGet("feerate").toDoubleOrNull()
					hold(fee)
				} else hold(null)
			}
		}
	}

	fun getCurrentBlockHeight(
		chainName: String,
		errorCallback: (Throwable?) -> Unit,
		hold: (Int?) -> Unit
	) {
		RequestBody.create(
			GoldStoneEthCall.contentType,
			ParameterUtil.prepareJsonRPC(
				ChainURL.getCurrentEncryptStatusByNodeName(chainName),
				BitcoinMethod.Getblockcount.method,
				1,
				false,
				false,
				null
			)
		).let { it ->
			RequisitionUtil.callChainBy(
				it,
				{ error, reason ->
					errorCallback(error)
					LogUtil.error("getblockHeight $reason", error)
				},
				chainName
			) {
				hold(if (it.isNotEmpty()) it.toInt() else null)
			}
		}
	}

	fun sendRawTransaction(
		chainName: String,
		signedMessage: String,
		hold: (String?) -> Unit
	) {
		RequestBody.create(
			GoldStoneEthCall.contentType,
			ParameterUtil.prepareJsonRPC(
				ChainURL.getCurrentEncryptStatusByNodeName(chainName),
				BitcoinMethod.SendRawtTansaction.method,
				1,
				false,
				false,
				signedMessage,
				true // anyone can pay by this signed message
			)
		).let { it ->
			RequisitionUtil.callChainBy(
				it,
				{ error, reason ->
					LogUtil.error("SendRawtTansaction $reason", error)
				},
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
		errorCallback: (Throwable?, String?) -> Unit,
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