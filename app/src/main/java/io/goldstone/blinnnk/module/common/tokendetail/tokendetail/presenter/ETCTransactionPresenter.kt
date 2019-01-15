package io.goldstone.blinnnk.module.common.tokendetail.tokendetail.presenter

import android.support.annotation.WorkerThread
import com.blinnnk.extension.toArrayList
import io.goldstone.blinnnk.common.error.RequestError
import io.goldstone.blinnnk.common.sharedpreference.SharedAddress
import io.goldstone.blinnnk.common.sharedpreference.SharedChain
import io.goldstone.blinnnk.crypto.multichain.getAddress
import io.goldstone.blinnnk.kernel.commontable.TransactionTable
import io.goldstone.blinnnk.kernel.network.common.GoldStoneAPI
import io.goldstone.blinnnk.module.home.wallet.transactions.transactionlist.ethereumtransactionlist.model.TransactionListModel

/**
 * @date 2018/8/14 5:01 PM
 * @author KaySaith
 */

fun TokenDetailPresenter.loadETCChainData() {
	loadDataFromChain(page) { data, error ->
		if (!data.isNullOrEmpty()) {
			flipPage(data) { }
			if (page == 1) {
				data.map {
					TransactionListModel(it)
				}.generateBalanceList(token.contract) {
					it.updateHeaderData(false)
				}
			}
			if (data.size >= 10) page++
		}
		if (!error.isNone()) {
			detailView.showError(error)
		}
		detailView.showBottomLoading(false)
		detailView.showLoading(false)
	}
}

@WorkerThread
private fun loadDataFromChain(page: Int, callback: (data: List<TransactionTable>?, error: RequestError) -> Unit) {
	GoldStoneAPI.getETCTransactions(
		page,
		SharedAddress.getCurrentETC()
	) { newData, error ->
		if (!newData.isNullOrEmpty() && error.isNone()) {
			val transactionDao = TransactionTable.dao
			var tableList = newData.map { TransactionTable(it) }
			// 计算燃气费的账单
			val feeList = tableList.map {
				TransactionTable(it).apply { chainID = it.chainID }
			}.filterNot {
				it.isReceive
			}.map {
				it.apply { isFee = true }
			}.asSequence().toList()
			tableList += feeList
			if (page == 1) {
				transactionDao.deleteByChainIDAndRecordAddress(SharedChain.getETCCurrent().chainID.id, SharedAddress.getCurrentETC())
				transactionDao.insertAll(tableList)
			}
			callback(tableList.sortedByDescending { it.timeStamp }, error)
		} else if (page == 1) {
			TransactionTable.dao.getByChainIDAndRecordAddress(SharedChain.getETCCurrent().chainID.id, SharedAddress.getCurrentETC()).let {
				callback(it, error)
			}
		} else {
			callback(null, error)
		}
	}
}
