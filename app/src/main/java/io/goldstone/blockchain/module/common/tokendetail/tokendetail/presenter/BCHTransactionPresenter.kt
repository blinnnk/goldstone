package io.goldstone.blockchain.module.common.tokendetail.tokendetail.presenter

import io.goldstone.blockchain.common.utils.AddressUtils
import io.goldstone.blockchain.crypto.multichain.ChainType
import io.goldstone.blockchain.crypto.multichain.CoinSymbol
import io.goldstone.blockchain.kernel.commonmodel.BTCSeriesTransactionTable
import io.goldstone.blockchain.kernel.database.GoldStoneDataBase
import io.goldstone.blockchain.kernel.network.BTCSeriesApiUtils
import io.goldstone.blockchain.kernel.network.bitcoincash.BitcoinCashApi
import org.jetbrains.anko.runOnUiThread

/**
 * @date 2018/8/15 3:02 PM
 * @author KaySaith
 */

fun TokenDetailPresenter.loadBCHChainData(localDataMaxIndex: Int) {
	fragment.showLoadingView()
	val address = AddressUtils.getCurrentBCHAddress()
	BitcoinCashApi.getTransactionCount(address) { transactionCount, error ->
		if (transactionCount == null || error.hasError()) return@getTransactionCount
		loadBCHTransactionsFromChain(
			address,
			localDataMaxIndex,
			transactionCount
		) {
			fragment.context?.runOnUiThread {
				fragment.removeLoadingView()
			}
			loadDataFromDatabaseOrElse()
		}
	}
}

private fun loadBCHTransactionsFromChain(
	address: String,
	localDataMaxIndex: Int,
	transactionCount: Int,
	callback: (hasData: Boolean) -> Unit
) {
	val pageInfo = BTCSeriesApiUtils.getPageInfo(transactionCount, localDataMaxIndex)
	// 意味着网络没有更新的数据直接返回
	if (pageInfo.to == 0) {
		callback(false)
	} else BitcoinCashApi.getTransactions(address, pageInfo.from, pageInfo.to) { transactions, error ->
		// Calculate All Inputs to get transfer value
		// 转换数据格式
		if (transactions != null && error.isNone()) transactions.asSequence().mapIndexed { index, item ->
			BTCSeriesTransactionTable(
				item,
				pageInfo.maxDataIndex + index + 1,
				address,
				CoinSymbol.bch,
				false,
				ChainType.BCH.id
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