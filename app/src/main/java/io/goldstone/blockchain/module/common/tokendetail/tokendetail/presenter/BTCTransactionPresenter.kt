package io.goldstone.blockchain.module.common.tokendetail.tokendetail.presenter

import android.support.annotation.WorkerThread
import io.goldstone.blockchain.common.utils.AddressUtils
import io.goldstone.blockchain.crypto.multichain.ChainType
import io.goldstone.blockchain.crypto.multichain.isBCH
import io.goldstone.blockchain.kernel.commonmodel.BTCSeriesTransactionTable
import io.goldstone.blockchain.kernel.database.GoldStoneDataBase
import io.goldstone.blockchain.kernel.network.btcseries.insight.InsightApi

/**
 * @date 2018/8/14 4:59 PM
 * @author KaySaith
 */

fun TokenDetailPresenter.loadBTCSeriesData(chainType: ChainType, localMaxIndex: Int) {
	val address = AddressUtils.getCurrentBTCAddress()
	InsightApi.getTransactionCount(chainType, !chainType.isBCH(), address) { transactionCount, error ->
		if (transactionCount != null && error.isNone()) loadTransactionsFromChain(
			chainType,
			address,
			localMaxIndex,
			// TODO 第三方 `Insight` 限制一次请求数量, 暂时这样, 下个版本做分页拉取(当前版本1.4.2)
			if (transactionCount > 50) 50 else transactionCount
		) {
			loadLocalData()
		}
	}
}

@WorkerThread
private fun loadTransactionsFromChain(
	chainType: ChainType,
	address: String,
	localDataMaxIndex: Int,
	transactionCount: Int,
	callback: (hasData: Boolean) -> Unit
) {
	val pageInfo = InsightApi.getPageInfo(transactionCount, localDataMaxIndex)
	// 意味着网络没有更新的数据直接返回
	if (pageInfo.to == 0) {
		callback(false)
	} else InsightApi.getTransactions(
		chainType,
		!chainType.isBCH(),
		address,
		pageInfo.from,
		pageInfo.to
	) { transactions, error ->
		// Calculate All Inputs to get transfer value
		// 转换数据格式
		if (transactions != null && error.isNone()) transactions.asSequence().mapIndexed { index, item ->
			BTCSeriesTransactionTable(
				item,
				pageInfo.maxDataIndex + index + 1,
				address,
				chainType.getContract().symbol,
				false,
				chainType.id
			)
		}.toList().let { all ->
			val transactionDao =
				GoldStoneDataBase.database.btcSeriesTransactionDao()
			transactionDao.insertAll(all)
			// 同样的账单插入一份燃气费的数据
			transactionDao.insertAll(all.filterNot { it.isReceive }.map { it.apply { it.isFee = true } })
			callback(all.isNotEmpty())
		}
	}
}