package io.goldstone.blockchain.kernel.network.bitcoin

import com.blinnnk.extension.safeGet
import io.goldstone.blockchain.common.utils.LogUtil
import io.goldstone.blockchain.common.value.ChainText
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
		).let {
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
}