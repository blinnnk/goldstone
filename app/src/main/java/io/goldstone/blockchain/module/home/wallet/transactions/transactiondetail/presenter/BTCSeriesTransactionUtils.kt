package io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.presenter

import android.support.annotation.WorkerThread
import com.blinnnk.extension.isNotNull
import io.goldstone.blockchain.common.error.RequestError
import io.goldstone.blockchain.crypto.multichain.ChainID
import io.goldstone.blockchain.kernel.commonmodel.BTCSeriesTransactionTable
import io.goldstone.blockchain.kernel.database.GoldStoneDataBase
import io.goldstone.blockchain.kernel.network.btcseries.insight.InsightApi
import io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.model.TransactionSealedModel
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
		else InsightApi.getTransactionByHash(chainID, !chainID.isBCH(), hash, address) { data, error ->
			if (data.isNotNull() && error.isNone()) {
				dao.insert(data)
				hold(TransactionSealedModel(data), error)
			} else hold(null, error)
		}
	}
}