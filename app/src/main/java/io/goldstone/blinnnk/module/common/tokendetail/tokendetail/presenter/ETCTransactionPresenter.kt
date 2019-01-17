package io.goldstone.blinnnk.module.common.tokendetail.tokendetail.presenter

import android.support.annotation.WorkerThread
import com.blinnnk.extension.isNotNull
import io.goldstone.blinnnk.common.error.RequestError
import io.goldstone.blinnnk.common.sharedpreference.SharedAddress
import io.goldstone.blinnnk.common.sharedpreference.SharedChain
import io.goldstone.blinnnk.crypto.multichain.ChainID
import io.goldstone.blinnnk.kernel.commontable.TransactionTable
import io.goldstone.blinnnk.kernel.network.common.GoldStoneAPI
import io.goldstone.blinnnk.module.home.wallet.transactions.transactionlist.ethereumtransactionlist.model.TransactionListModel

/**
 * @date 2018/8/14 5:01 PM
 * @author KaySaith
 */

fun TokenDetailPresenter.loadETCChainData() {
	loadDataFromChain { newData, error ->
		val data = if (!newData.isNullOrEmpty()) newData
		else if (page == 1)  TransactionTable.dao.getByChainIDAndRecordAddress(ChainID.etcMain, SharedAddress.getCurrentETC())
		else null
		data?.let {
			flipPage(it) { }
			if (page == 1) {
				it.map { table ->
					TransactionListModel(table)
				}.generateBalanceList(token.contract) { list ->
					list.updateHeaderData(false)
				}
			}
		}
		page += 1
		if (error.hasError()) {
			detailView.showError(error)
		}
		detailView.showBottomLoading(false)
		detailView.showLoading(false)
	}
}

fun TokenDetailPresenter.loadTestNetETCChainData(blockNumber: Int) {
	loadTestNetDataFromChain(blockNumber) {
		if (it.isNone()) getETHSeriesData()
		else detailView.showLoading(false)
	}
}

@WorkerThread
private fun TokenDetailPresenter.loadDataFromChain(callback: (data: List<TransactionTable>?, error: RequestError) -> Unit) {
	GoldStoneAPI.getMainNetETCTransactions(
		page,
		SharedAddress.getCurrentETC()
	) { newData, error ->
		if (newData.isNotNull() && error.isNone()) {
			val transactionDao = TransactionTable.dao
			var tableList = newData.map { TransactionTable(it) }
			// 计算燃气费的账单
			val feeList = tableList.map {
				TransactionTable(it).apply {
					chainID = it.chainID
					isReceive = it.isReceive
					recordOwnerAddress = it.recordOwnerAddress
					isFee = true
				}
			}.filterNot {
				it.isReceive
			}.asSequence().toList()
			tableList += feeList
			// 如果是第一页的数据，则插入数据库
			if (page == 1) {
				transactionDao.deleteByChainIDAndRecordAddress(SharedChain.getETCCurrent().chainID.id, SharedAddress.getCurrentETC())
				transactionDao.insertAll(tableList)
				transactionDao.getByChainIDAndRecordAddress(SharedChain.getETCCurrent().chainID.id, SharedAddress.getCurrentETC()).let {
					callback(it, error)
				}
			} else {
				callback(tableList.sortedByDescending { it.timeStamp }, error)
			}
		}  else {
			callback(null, error)
		}
	}
}


@WorkerThread
private fun loadTestNetDataFromChain(blockNumber: Int, callback: (error: RequestError) -> Unit) {
	GoldStoneAPI.getETCTransactions(
		SharedChain.getETCCurrent().chainID,
		SharedAddress.getCurrentETC(),
		blockNumber
	) { newData, error ->
		if (!newData.isNullOrEmpty() && error.isNone()) {
			val transactionDao = TransactionTable.dao
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
		} else if (newData?.isEmpty() == true) {
			callback(RequestError.EmptyResut)
		} else callback(error)
	}
}
