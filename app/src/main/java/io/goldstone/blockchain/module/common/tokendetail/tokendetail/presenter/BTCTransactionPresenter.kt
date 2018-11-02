package io.goldstone.blockchain.module.common.tokendetail.tokendetail.presenter

import io.goldstone.blockchain.common.language.LoadingText
import io.goldstone.blockchain.common.utils.AddressUtils
import io.goldstone.blockchain.crypto.multichain.ChainType
import io.goldstone.blockchain.crypto.multichain.CoinSymbol
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
	BitcoinApi.getTransactionCount(address) { transactionCount, error ->
		if (transactionCount != null && error.isNone()) loadTransactionsFromChain(
			address,
			localMaxIndex,
			// TODO 第三方 `Insight` 限制一次请求数量, 暂时这样, 下个版本做分页拉取(当前版本1.4.2)
			if (transactionCount > 50) 50 else transactionCount
		) {
			fragment.context?.runOnUiThread {
				fragment.removeLoadingView()
			}
			loadDataFromDatabaseOrElse()
		}
	}
}

private fun loadTransactionsFromChain(
	address: String,
	localDataMaxIndex: Int,
	transactionCount: Int,
	callback: (hasData: Boolean) -> Unit
) {
	val pageInfo = BTCSeriesApiUtils.getPageInfo(transactionCount, localDataMaxIndex)
	// 意味着网络没有更新的数据直接返回
	if (pageInfo.to == 0) {
		callback(false)
		return
	}
	BitcoinApi.getBTCTransactions(
		address,
		pageInfo.from,
		pageInfo.to
	) { transactions, error ->
		// Calculate All Inputs to get transfer value
		if (transactions != null && error.isNone())
			callback(transactions.asSequence().mapIndexed { index, item ->
				// 转换数据格式
				BTCSeriesTransactionTable(
					item,
					pageInfo.maxDataIndex + index + 1,
					address,
					CoinSymbol.pureBTCSymbol,
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
			}.toList().isNotEmpty())
		else callback(false)
	}
}