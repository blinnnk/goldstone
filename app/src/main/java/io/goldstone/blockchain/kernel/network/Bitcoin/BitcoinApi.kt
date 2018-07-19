package io.goldstone.blockchain.kernel.network.bitcoin

import com.blinnnk.extension.orZero
import com.blinnnk.extension.safeGet
import io.goldstone.blockchain.common.utils.LogUtil
import io.goldstone.blockchain.kernel.network.RequisitionUtil
import org.json.JSONObject

/**
 * @date 2018/7/19 1:46 AM
 * @author KaySaith
 */
object BitcoinApi {
	
	fun getBalanceByAddress(address: String, hold: (Double) -> Unit) {
		RequisitionUtil.requestUncryptoData<String>(
			BitcoinUrl.getBalance(BitcoinUrl.currentUrl, address),
			address,
			true,
			{
				LogUtil.error("getBitcoinBalance", it)
			}
		) {
			val count = JSONObject(this[0]).safeGet("final_balance").toDoubleOrNull().orZero()
			hold(count)
		}
	}
}