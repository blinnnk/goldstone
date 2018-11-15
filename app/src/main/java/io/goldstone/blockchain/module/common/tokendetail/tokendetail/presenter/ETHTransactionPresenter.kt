package io.goldstone.blockchain.module.common.tokendetail.tokendetail.presenter

import android.support.annotation.WorkerThread
import io.goldstone.blockchain.common.error.RequestError
import io.goldstone.blockchain.common.sharedpreference.SharedAddress
import io.goldstone.blockchain.crypto.utils.CryptoUtils
import io.goldstone.blockchain.kernel.commonmodel.TransactionTable
import io.goldstone.blockchain.kernel.network.common.RequisitionUtil
import io.goldstone.blockchain.kernel.network.ethereum.EtherScanApi
import io.goldstone.blockchain.module.home.wallet.transactions.transactionlist.ethereumtransactionlist.model.ETHTransactionModel

/**
 * @date 2018/8/20 2:51 PM
 * @author KaySaith
 */

@WorkerThread
fun TokenDetailPresenter.loadETHChainData(endBlock: Int) {
	updateLocalETHTransactions(endBlock) {
		if (it.isNone()) getETHSeriesData()
		else detailView.showBottomLoading(false)
	}
}

@WorkerThread
fun updateLocalETHTransactions(endBlock: Int, callback: (RequestError) -> Unit) {
	RequisitionUtil.requestUnCryptoData<ETHTransactionModel>(
		EtherScanApi.offsetTransactions(SharedAddress.getCurrentEthereum(), endBlock),
		"result"
	) { transactions, error ->
		if (!transactions.isNullOrEmpty() && error.isNone()) {
			val transactionDao = TransactionTable.dao
			// onConflict InsertAll 利用 RoomDatabase 进行双主键做重复判断 
			// 覆盖或新增到本地 TransactionTable 数据库里
			transactionDao.insertAll(
				transactions.asSequence().map {
					TransactionTable(it)
				}.filterNot {
					it.fromAddress.isEmpty() || it.to.isEmpty()
				}.toList()
			)
			// `Copy` 账单中的 `ETH` 转账账单并标记为 `Fee`
			transactions.asSequence().filter {
				!CryptoUtils.isERC20Transfer(it.input)
			}.map {
				TransactionTable(it).apply { isFee = true }
			}.filterNot {
				it.isReceive
			}.toList().let {
				transactionDao.insertAll(it)
			}
			callback(error)
		} else if (transactions?.isEmpty() == true) {
			callback(RequestError.EmptyResut)
		} else callback(error)
	}
}
