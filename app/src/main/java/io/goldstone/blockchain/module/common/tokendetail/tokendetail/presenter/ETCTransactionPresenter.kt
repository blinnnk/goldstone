package io.goldstone.blockchain.module.common.tokendetail.tokendetail.presenter

import android.support.annotation.WorkerThread
import io.goldstone.blockchain.common.error.RequestError
import io.goldstone.blockchain.common.sharedpreference.SharedAddress
import io.goldstone.blockchain.common.sharedpreference.SharedChain
import io.goldstone.blockchain.kernel.commonmodel.TransactionTable
import io.goldstone.blockchain.kernel.database.GoldStoneDataBase
import io.goldstone.blockchain.kernel.network.common.GoldStoneAPI

/**
 * @date 2018/8/14 5:01 PM
 * @author KaySaith
 */

fun TokenDetailPresenter.loadETCChainData(blockNumber: Int) {
	loadDataFromChain(blockNumber) {
		loadLocalData()
	}
}

@WorkerThread
private fun loadDataFromChain(blockNumber: Int, callback: (error: RequestError) -> Unit) {
	GoldStoneAPI.getETCTransactions(
		SharedChain.getETCCurrent().chainID,
		SharedAddress.getCurrentETC(),
		"$blockNumber"
	) { newData, error ->
		if (newData?.isNotEmpty() == true && error.isNone()) {
			val transactionDao =
				GoldStoneDataBase.database.transactionDao()
			// 插入普通账单
			transactionDao.insertAll(newData.map { TransactionTable(it) })
			// 插入燃气费的账单
			transactionDao.insertAll(
				newData.asSequence().map {
					TransactionTable(it)
				}.filterNot {
					it.isReceive
				}.map {
					it.apply { isFee = true }
				}.toList()
			)
			callback(error)
		} else callback(error)
	}
}