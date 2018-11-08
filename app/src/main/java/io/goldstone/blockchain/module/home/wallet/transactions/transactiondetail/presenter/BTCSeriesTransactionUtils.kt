package io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.presenter

import android.support.annotation.WorkerThread
import io.goldstone.blockchain.common.error.RequestError
import io.goldstone.blockchain.kernel.database.GoldStoneDataBase
import io.goldstone.blockchain.kernel.network.bitcoin.BitcoinApi
import io.goldstone.blockchain.kernel.network.litecoin.LitecoinApi
import io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.model.TransactionSealedModel
import org.jetbrains.anko.doAsync


/**
 * @author KaySaith
 * @date  2018/11/08
 */
object BTCSeriesTransactionUtils {

	fun getTransaction(
		hash: String,
		isReceive: Boolean,
		address: String,
		headerUrl: String,
		checkLocal: Boolean,
		@WorkerThread hold: (data: TransactionSealedModel?, error: RequestError) -> Unit
	) {
		doAsync {
			val transactionDao =
				GoldStoneDataBase.database.btcSeriesTransactionDao()
			val transaction =
				transactionDao.getDataByHash(hash, isReceive, false)
			if (transaction != null && transaction.blockNumber > 0 && checkLocal)
				hold(TransactionSealedModel(transaction), RequestError.None)
			else BitcoinApi.getTransactionByHash(hash, address, headerUrl) { data, error ->
				if (data != null && error.isNone()) {
					transactionDao.insert(data)
					hold(TransactionSealedModel(data), error)
				} else hold(null, error)
			}
		}
	}
}

object LTCTransactionUtils {
	fun getTransaction(
		hash: String,
		isReceive: Boolean,
		address: String,
		headerUrl: String,
		checkLocal: Boolean,
		@WorkerThread hold: (transition: TransactionSealedModel?, error: RequestError) -> Unit
	) {
		doAsync {
			val transactionDao =
				GoldStoneDataBase.database.btcSeriesTransactionDao()
			val transaction =
				transactionDao.getDataByHash(hash, isReceive, false)
			if (transaction != null && transaction.blockNumber > 0 && checkLocal)
				hold(TransactionSealedModel(transaction), RequestError.None)
			else LitecoinApi.getTransactionByHash(hash, address, headerUrl) { data, error ->
				if (data != null && error.isNone()) {
					transactionDao.insert(data)
					hold(TransactionSealedModel(data), error)
				} else hold(null, error)
			}
		}
	}
}