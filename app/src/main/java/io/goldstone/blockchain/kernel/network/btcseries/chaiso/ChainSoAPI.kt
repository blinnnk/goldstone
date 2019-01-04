package io.goldstone.blockchain.kernel.network.btcseries.chaiso

import android.support.annotation.WorkerThread
import com.blinnnk.extension.getTargetChild
import com.blinnnk.extension.isNotNull
import com.blinnnk.extension.orZero
import io.goldstone.blockchain.common.error.RequestError
import io.goldstone.blockchain.kernel.network.common.RequisitionUtil
import org.json.JSONObject


/**
 * @author KaySaith
 * @date  2018/11/09
 */
object ChainSoAPI {
	fun getBalanceFromChainSo(
		address: String,
		@WorkerThread hold: (balance: Double?, error: RequestError) -> Unit
	) {
		RequisitionUtil.requestUnCryptoData<String>(
			ChainSoURL.getBalanceFromChainSo(address),
			listOf(),
			true
		) { result, error ->
			if (result.isNotNull() && error.isNone()) {
				val data = JSONObject(result.firstOrNull())
				val balance = data.getTargetChild("data", "confirmed_balance").toDoubleOrNull().orZero()
				hold(balance, error)
			} else hold(null, error)
		}
	}
}