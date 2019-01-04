package io.goldstone.blockchain.module.common.tokendetail.tokendetail.presenter

import android.support.annotation.WorkerThread
import com.blinnnk.extension.hasValue
import io.goldstone.blockchain.common.error.RequestError
import io.goldstone.blockchain.common.sharedpreference.SharedAddress
import io.goldstone.blockchain.crypto.multichain.getAddress
import io.goldstone.blockchain.crypto.multichain.isETH
import io.goldstone.blockchain.crypto.utils.CryptoUtils
import io.goldstone.blockchain.kernel.commontable.TransactionTable
import io.goldstone.blockchain.kernel.network.common.RequisitionUtil
import io.goldstone.blockchain.kernel.network.ethereum.EtherScanApi
import io.goldstone.blockchain.module.home.wallet.transactions.transactionlist.ethereumtransactionlist.model.ETHTransactionModel
import io.goldstone.blockchain.module.home.wallet.transactions.transactionlist.ethereumtransactionlist.model.TransactionListModel

/**
 * @date 2018/8/20 2:51 PM
 * @author KaySaith
 */

@WorkerThread
fun TokenDetailPresenter.loadETHChainData(endBlock: Int) {
	updateLocalETHTransactions(endBlock) {
		if (it.isNone()) getETHSeriesData()
		else {
			if (it.isEmptyResult()) detailView.showLoading(false)
			detailView.showBottomLoading(false)
		}
	}
}

@WorkerThread
private fun updateLocalETHTransactions(endBlock: Int, callback: (RequestError) -> Unit) {
	RequisitionUtil.requestUnCryptoData<ETHTransactionModel>(
		EtherScanApi.offsetTransactions(SharedAddress.getCurrentEthereum(), endBlock),
		listOf("result")
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

@WorkerThread
fun TokenDetailPresenter.getETHSeriesData() {
	val address = token.contract.getAddress()
	val dao = TransactionTable.dao
	val endBlock = try {
		// 场景: 1 用户设定筛选条件,  2 超快速翻页
		// 会出现数组线程不安全, 这里通过 `Try Catch` 捕捉, 并容错
		if (detailView.asyncData.isNullOrEmpty()) {
			getMaxBlockNumber()
		} else detailView.asyncData?.minBy { it.blockNumber }?.blockNumber!! - 1
	} catch (error: Exception) {
		return
	}
	if (endBlock.hasValue()) {
		val transactions =
			if (token.contract.isETH()) dao.getETHAndAllFee(
				address,
				token.contract.contract,
				endBlock,
				token.chainID
			) else dao.getDataWithFee(
				address,
				token.contract.contract,
				token.chainID,
				endBlock
			)
		when {
			transactions.isNotEmpty() -> {
				if (detailView.asyncData?.isEmpty() == true) {
					transactions.map {
						TransactionListModel(it)
					}.generateBalanceList(token.contract) {
						it.updateHeaderData(false)
					}
				}
				flipPage(transactions) {
					detailView.showBottomLoading(false)
					detailView.showLoading(false)
				}
			}
			else -> when {
				token.contract.isETH() -> loadETHChainData(endBlock)
				else -> detailView.showBottomLoading(false)
			}
		}
	} else {
		detailView.showLoading(false)
		detailView.showBottomLoading(false)
	}
}

