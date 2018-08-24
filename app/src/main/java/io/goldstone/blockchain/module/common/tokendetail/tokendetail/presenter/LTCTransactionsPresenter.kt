package io.goldstone.blockchain.module.common.tokendetail.tokendetail.presenter

import io.goldstone.blockchain.common.language.LoadingText
import io.goldstone.blockchain.common.utils.AddressUtils
import io.goldstone.blockchain.common.utils.LogUtil
import io.goldstone.blockchain.crypto.ChainType
import io.goldstone.blockchain.crypto.CryptoSymbol
import io.goldstone.blockchain.kernel.commonmodel.BTCSeriesTransactionTable
import io.goldstone.blockchain.kernel.network.BTCSeriesApiUtils
import io.goldstone.blockchain.kernel.network.litecoin.LitecoinApi
import io.goldstone.blockchain.module.home.wallet.transactions.transactionlist.ethereumtransactionlist.model.TransactionListModel
import org.jetbrains.anko.runOnUiThread

/**
 * @date 2018/8/14 12:13 PM
 * @author KaySaith
 */

fun TokenDetailPresenter.loadLTCChainData() {
	fragment.showLoadingView(LoadingText.transactionData)
	val address = AddressUtils.getCurrentLTCAddress()
	LitecoinApi.getTransactionsCount(
		address,
		{
			LogUtil.error("loadLTCChainData", it)
		}
	) { transactionCount ->
		loadLitecoinTransactionsFromChain(
			address,
			0,
			transactionCount,
			{
				fragment.removeLoadingView()
				LogUtil.error("loadLTCChainData", it)
			}
		) {
			fragment.context?.runOnUiThread {
				fragment.removeLoadingView()
			}
			loadDataFromDatabaseOrElse()
		}
	}
}

private fun loadLitecoinTransactionsFromChain(
	address: String,
	localDataMaxIndex: Int,
	transactionCount: Int,
	errorCallback: (Throwable) -> Unit,
	successCallback: (hasData: Boolean) -> Unit
) {
	val pageInfo = BTCSeriesApiUtils.getPageInfo(transactionCount, localDataMaxIndex)
	LitecoinApi.getTransactions(
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
				CryptoSymbol.ltc,
				false,
				ChainType.LTC.id
			)
		}.map {
			// 插入转账数据到数据库
			BTCSeriesTransactionTable.preventRepeatedInsert(it.hash, false, it)
			// 同样的账单插入一份燃气费的数据
			if (!it.isReceive) {
				BTCSeriesTransactionTable.preventRepeatedInsert(
					it.hash,
					true,
					it.apply { isFee = true }
				)
			}
			TransactionListModel(it)
		}.isNotEmpty())
	}
}