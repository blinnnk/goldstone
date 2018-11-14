package io.goldstone.blockchain.kernel.network.btcseries.blockinfo

import android.support.annotation.WorkerThread
import com.blinnnk.extension.safeGet
import io.goldstone.blockchain.common.error.RequestError
import io.goldstone.blockchain.crypto.multichain.Amount
import io.goldstone.blockchain.kernel.network.bitcoin.model.BlockInfoUnspentModel
import io.goldstone.blockchain.kernel.network.common.RequisitionUtil
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
		RequisitionUtil.requestUnCryptoData<String>(api, address, true) { data, error ->
			if (data != null && error.isNone()) {
				hold(Amount(JSONObject(data.firstOrNull()).safeGet("final_balance").toLong()), error)
			} else hold(null, error)
		}
	}

	// `Insight` 接口挂掉的时候向 `BlockInfo` 发起请求
	fun getUnspentListByAddressFromBlockInfo(
		api: String,
		@WorkerThread hold: (unspents: List<BlockInfoUnspentModel>?, error: RequestError) -> Unit
	) {
		RequisitionUtil.requestUnCryptoData(api, "unspent_outputs", false, hold)
	}
}