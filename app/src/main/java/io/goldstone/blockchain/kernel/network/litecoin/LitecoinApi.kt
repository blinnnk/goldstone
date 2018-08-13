package io.goldstone.blockchain.kernel.network.litecoin

import com.blinnnk.extension.safeGet
import io.goldstone.blockchain.common.utils.LogUtil
import io.goldstone.blockchain.kernel.network.RequisitionUtil
import io.goldstone.blockchain.kernel.network.bitcoin.BitcoinUrl
import org.json.JSONObject

/**
 * @date 2018/8/13 12:07 PM
 * @author KaySaith
 */

object LitecoinApi {
	fun getBalanceByAddress(address: String, hold: (Double) -> Unit) {
		RequisitionUtil.requestUncryptoData<String>(
			LitecoinUrl.getBalance(address),
			"data",
			true,
			{
				LogUtil.error("getBitcoinBalance", it)
			}
		) {
			val count =
				JSONObject(this[0]).safeGet("confirmed_balance").toDoubleOrNull() ?: 0.0
			hold(count)
		}
	}
}