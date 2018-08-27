package io.goldstone.blockchain.module.common.tokendetail.tokendetail.presenter

import io.goldstone.blockchain.common.language.LoadingText
import io.goldstone.blockchain.common.utils.AddressUtils
import io.goldstone.blockchain.common.utils.LogUtil
import io.goldstone.blockchain.crypto.ChainType
import io.goldstone.blockchain.crypto.CryptoSymbol
import io.goldstone.blockchain.kernel.commonmodel.BTCSeriesTransactionTable
import io.goldstone.blockchain.kernel.network.BTCSeriesApiUtils
import io.goldstone.blockchain.kernel.network.bitcoin.BitcoinApi
import io.goldstone.blockchain.module.home.wallet.transactions.transactionlist.ethereumtransactionlist.model.TransactionListModel
import org.jetbrains.anko.runOnUiThread

/**
 * @date 2018/8/14 4:59 PM
 * @author KaySaith
 */

fun TokenDetailPresenter.loadBTCChainData(localMaxIndex: Int) {
	fragment.showLoadingView(LoadingText.transactionData)
	val address = AddressUtils.getCurrentBTCAddress()
	BitcoinApi.getTransactionsCount(
		address,
		{
			LogUtil.error("loadBTCChainData", it)
		}
	) { transactionCount ->
		loadTransactionsFromChain(
			address,
			localMaxIndex,
			transactionCount,
			{
				fragment.removeLoadingView()
				LogUtil.error("loadBTCChainData", it)
			}
		) {
			fragment.context?.runOnUiThread {
				fragment.removeLoadingView()
			}
			loadDataFromDatabaseOrElse { _, _ -> }
		}
	}
}

private fun loadTransactionsFromChain(
	address: String,
	localDataMaxIndex: Int,
	transactionCount: Int,
	errorCallback: (Throwable) -> Unit,
	successCallback: (hasData: Boolean) -> Unit
) {
	val pageInfo = BTCSeriesApiUtils.getPageInfo(transactionCount, localDataMaxIndex)
	// 意味着网络没有更新的数据直接返回
	if (pageInfo.to == 0) {
		successCallback(false)
		return
	}
	BitcoinApi.getBTCTransactions(
		address,
		pageInfo.from,
		pageInfo.to,
		errorCallback
	) { transactions ->
		// Calculate All Inputs to get transfer value
		successCallback(transactions.mapIndexed { index, item ->
			// 转换数据格式
			BTCSeriesTransactionTable(
				item,
				pageInfo.maxDataIndex - index,
				address,
				CryptoSymbol.pureBTCSymbol,
				false,
				ChainType.BTC.id
			)
		}.map {
			// 插入转账数据到数据库
			BTCSeriesTransactionTable.preventRepeatedInsert(it.hash, false, it)
			// 同样的账单插入一份燃气费的数据
			if (!it.isReceive) {
				BTCSeriesTransactionTable.preventRepeatedInsert(it.hash, true, it.apply { isFee = true })
			}
			TransactionListModel(it)
		}.isNotEmpty())
	}
}