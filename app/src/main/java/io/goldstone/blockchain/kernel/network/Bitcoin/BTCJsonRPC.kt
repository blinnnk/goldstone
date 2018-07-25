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
	 * 计算制定 `Block Number` 的数字来查询燃气费的使用的价格平均
	 */
	fun estimatesmartFee(
		chainName: String,
		targetBlock: Int,
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
				targetBlock // Target block number
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