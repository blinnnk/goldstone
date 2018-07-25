package io.goldstone.blockchain.kernel.network.bitcoin

import com.blinnnk.extension.safeGet
import io.goldstone.blockchain.common.utils.LogUtil
import io.goldstone.blockchain.kernel.network.RequisitionUtil
import io.goldstone.blockchain.kernel.network.bitcoin.model.UnspentModel
import org.json.JSONObject

/**
 * @date 2018/7/19 1:46 AM
 * @author KaySaith
 */
object BitcoinApi {
	
	fun getBalanceByAddress(address: String, hold: (Long) -> Unit) {
		RequisitionUtil.requestUncryptoData<String>(
			BitcoinUrl.getBalance(BitcoinUrl.currentUrl, address),
			address,
			true,
			{
				LogUtil.error("getBitcoinBalance", it)
			}
		) {
			val count = JSONObject(this[0]).safeGet("final_balance").toLongOrNull() ?: 0L
			hold(count)
		}
	}
	
	fun getUnspentListByAddress(address: String, hold: (List<UnspentModel>) -> Unit) {
		RequisitionUtil.requestUncryptoData<UnspentModel>(
			BitcoinUrl.getUnspentInfo(BitcoinUrl.currentUrl, address),
			"unspent_outputs",
			false,
			{
				LogUtil.error("getRawtxByHash", it)
			}
		) {
			hold(if (isNotEmpty()) this else listOf())
		}
	}
}