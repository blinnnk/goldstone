package io.goldstone.blockchain.kernel.network.bitcoin

import com.blinnnk.extension.safeGet
import io.goldstone.blockchain.common.language.ChainText
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
object BTCJsonRPC {
	
	/**
	 * 估算交易在 `nblocks` 个区块开始确认的每千字节的大致费用,
	 * 如果没有足够的交易和区块用来估算则会返回一个负值，-1 表示交易费为 0
	 */
	fun estimatesmartFee(
		chainName: String,
		blocks: Int,
		hold: (Double?) -> Unit
	) {
		RequestBody.create(
			GoldStoneEthCall.contentType,
			ParameterUtil.prepareJsonRPC(
				ChainURL.getCurrentEncryptStatusByNodeName(chainName),
				BitcoinMethod.EstimatesmartFee.method,
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
				ChainText.btcTest
			) {
				if (it.isNotEmpty())
					hold(JSONObject(it).safeGet("feerate").toDoubleOrNull())
				else hold(null)
			}
		}
	}
	
	fun getCurrentBlockHeight(
		isTest: Boolean,
		errorCallback: (Throwable?) -> Unit,
		hold: (Int?) -> Unit
	) {
		RequestBody.create(
			GoldStoneEthCall.contentType,
			ParameterUtil.prepareJsonRPC(
				isTest,
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
				if (isTest) ChainText.btcTest else ChainText.btcMain
			) {
				hold(if (it.isNotEmpty()) it.toInt() else null)
			}
		}
	}
	
	fun sendRawTransaction(
		isTest: Boolean,
		signedMessage: String,
		hold: (String?) -> Unit
	) {
		RequestBody.create(
			GoldStoneEthCall.contentType,
			ParameterUtil.prepareJsonRPC(
				isTest,
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
				if (isTest) ChainText.btcTest else ChainText.btcMain
			) {
				// Return Transaction hash
				hold(if (it.isNotEmpty()) it else null)
			}
		}
	}
}