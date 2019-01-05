package io.goldstone.blinnnk.kernel.network.btcseries.blockinfo

import android.support.annotation.WorkerThread
import com.blinnnk.extension.isNotNull
import com.blinnnk.extension.safeGet
import io.goldstone.blinnnk.common.error.RequestError
import io.goldstone.blinnnk.crypto.multichain.Amount
import io.goldstone.blinnnk.kernel.network.bitcoin.model.BlockInfoUnspentModel
import io.goldstone.blinnnk.kernel.network.common.RequisitionUtil
import org.json.JSONObject


/**
 * @author KaySaith
 * @date  2018/11/09
 */
object BlockInfoAPI {
	// `Insight` 不稳定的时候用 `BlockChainInfo` 做备份
	fun getBalanceFromBlockInfo(
		api: String,
		address: String,
		@WorkerThread hold: (balance: Amount<Long>?, error: RequestError) -> Unit
	) {
		RequisitionUtil.requestUnCryptoData<String>(api, listOf(address), true) { data, error ->
			if (data.isNotNull() && error.isNone()) {
				hold(Amount(JSONObject(data.firstOrNull()).safeGet("final_balance").toLong()), error)
			} else hold(null, error)
		}
	}

	// `Insight` 接口挂掉的时候向 `BlockInfo` 发起请求
	fun getUnspentListByAddressFromBlockInfo(
		api: String,
		@WorkerThread hold: (unspents: List<BlockInfoUnspentModel>?, error: RequestError) -> Unit
	) {
		RequisitionUtil.requestUnCryptoData(api, listOf("unspent_outputs"), false, hold)
	}
}