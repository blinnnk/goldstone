package io.goldstone.blinnnk.module.home.wallet.transactions.transactiondetail.presenter

import android.support.annotation.WorkerThread
import com.blinnnk.extension.isNotNull
import io.goldstone.blinnnk.common.error.RequestError
import io.goldstone.blinnnk.crypto.multichain.ChainID
import io.goldstone.blinnnk.kernel.commontable.BTCSeriesTransactionTable
import io.goldstone.blinnnk.kernel.network.btcseries.insight.InsightApi
import io.goldstone.blinnnk.module.home.wallet.transactions.transactiondetail.model.TransactionSealedModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


/**
 * @author KaySaith
 * @date  2018/11/08
 */

object BTCSeriesTransactionUtils {
	fun getTransaction(
		chainID: ChainID,
		hash: String,
		isReceive: Boolean,
		address: String,
		checkLocal: Boolean,
		@WorkerThread hold: (transition: TransactionSealedModel?, error: RequestError) -> Unit
	) = GlobalScope.launch(Dispatchers.Default) {
		val dao = BTCSeriesTransactionTable.dao
		val transaction =
			dao.getDataByHash(hash, isReceive, false)
		if (transaction.isNotNull() && transaction.blockNumber > 0 && checkLocal)
			hold(TransactionSealedModel(transaction), RequestError.None)
		else InsightApi.getTransactionByHash(chainID, hash, address) { data, error ->
			if (data.isNotNull() && error.isNone()) {
				dao.insert(data)
				hold(TransactionSealedModel(data), error)
			} else hold(null, error)
		}
	}
}