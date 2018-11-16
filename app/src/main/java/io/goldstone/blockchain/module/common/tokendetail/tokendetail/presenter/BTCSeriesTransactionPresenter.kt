package io.goldstone.blockchain.module.common.tokendetail.tokendetail.presenter

import android.support.annotation.WorkerThread
import com.blinnnk.extension.isNotNull
import io.goldstone.blockchain.common.error.RequestError
import io.goldstone.blockchain.common.value.DataValue
import io.goldstone.blockchain.common.value.PageInfo
import io.goldstone.blockchain.crypto.multichain.ChainType
import io.goldstone.blockchain.crypto.multichain.getAddress
import io.goldstone.blockchain.crypto.multichain.isBCH
import io.goldstone.blockchain.kernel.commonmodel.BTCSeriesTransactionTable
import io.goldstone.blockchain.kernel.network.btcseries.insight.InsightApi

/**
 * @date 2018/8/14 4:59 PM
 * @author KaySaith
 */

fun TokenDetailPresenter.loadBTCSeriesData(
	chainType: ChainType,
	localMaxIndex: Int,
	loadNew: Boolean
) {
	val address = chainType.getContract().getAddress()
	InsightApi.getTransactionCount(chainType, !chainType.isBCH(), address) { transactionCount, error ->
		if (transactionCount.isNotNull() && error.isNone()) {
			// 如果本地的最大 Index 与 Count 相同意味着不需要拉取账单
			when {
				loadNew && transactionCount != localMaxIndex -> loadDataFromChain(
					chainType,
					address,
					PageInfo(0, DataValue.pageCount, localMaxIndex, transactionCount),
					true
				) {
					if (it.isNone()) getBTCSeriesData()
					else {
						detailView.showBottomLoading(false)
						detailView.showError(it)
					}
				}
				!loadNew -> loadOldData(chainType, localMaxIndex, transactionCount)
				else -> getBTCSeriesData()
			}
		} else getBTCSeriesData()
	}
}

private fun TokenDetailPresenter.loadOldData(
	chainType: ChainType,
	minIndex: Int,
	totalCount: Int
) {
	val address = chainType.getContract().getAddress()
	loadDataFromChain(
		chainType,
		address,
		PageInfo(
			totalCount - minIndex + 1,
			totalCount - minIndex + DataValue.pageCount + 1,
			minIndex,
			totalCount
		),
		false
	) {
		if (it.isNone()) getBTCSeriesData()
		else {
			detailView.showBottomLoading(false)
			detailView.showError(it)
		}
	}
}

@WorkerThread
private fun loadDataFromChain(
	chainType: ChainType,
	address: String,
	pageInfo: PageInfo,
	loadNew: Boolean,
	callback: (error: RequestError) -> Unit
) {
	// 意味着网络没有更新的数据直接返回
	InsightApi.getTransactions(
		chainType,
		!chainType.isBCH(),
		address,
		pageInfo.from,
		pageInfo.to
	) { transactions, error ->
		// Calculate All Inputs to get transfer value
		// 转换数据格式
		val dataIndex: (index: Int) -> Int = {
			if (pageInfo.maxDataIndex == 0) pageInfo.total
			else pageInfo.maxDataIndex + (it + 1) * if (loadNew) 1 else -1
		}
		if (!transactions.isNullOrEmpty() && error.isNone()) {
			transactions.asSequence().mapIndexed { index, item ->
				BTCSeriesTransactionTable(
					item,
					dataIndex(index),
					address,
					chainType.getContract().symbol,
					false,
					chainType.id
				)
			}.toList().let { all ->
				BTCSeriesTransactionTable.dao.insertAll(all)
				// 同样的账单插入一份燃气费的数据
				BTCSeriesTransactionTable.dao.insertAll(all.filterNot { it.isReceive }.map { it.apply { it.isFee = true } })
				callback(error)
			}
		} else if (transactions?.isEmpty() == true) {
			callback(RequestError.EmptyResut)
		} else callback(error)
	}
}