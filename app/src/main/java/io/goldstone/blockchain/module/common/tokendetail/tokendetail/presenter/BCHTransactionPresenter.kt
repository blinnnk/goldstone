package io.goldstone.blockchain.module.common.tokendetail.tokendetail.presenter

import com.blinnnk.extension.isNull
import io.goldstone.blockchain.common.language.LoadingText
import io.goldstone.blockchain.common.utils.LogUtil
import io.goldstone.blockchain.common.value.Config
import io.goldstone.blockchain.crypto.ChainType
import io.goldstone.blockchain.crypto.CryptoSymbol
import io.goldstone.blockchain.kernel.commonmodel.BTCSeriesTransactionTable
import io.goldstone.blockchain.kernel.network.bitcoincash.BitcoinCashApi
import io.goldstone.blockchain.module.home.wallet.transactions.transactionlist.ethereumtransactionlist.model.TransactionListModel
import org.jetbrains.anko.runOnUiThread

/**
 * @date 2018/8/15 3:02 PM
 * @author KaySaith
 */

fun TokenDetailPresenter.loadBCHChainData() {
	fragment.showLoadingView(LoadingText.transactionData)
	loadBCHTransactionsFromChain(
		arrayListOf(),
		{
			fragment.removeLoadingView()
			LogUtil.error("loadBCHChainData", it)
		}
	) {
		fragment.context?.runOnUiThread {
			fragment.removeLoadingView()
		}
		// TODO 判断数据
		loadDataFromDatabaseOrElse()
	}
}

private fun loadBCHTransactionsFromChain(
	localData: List<BTCSeriesTransactionTable>,
	errorCallback: (Throwable) -> Unit,
	successCallback: (hasData: Boolean) -> Unit
) {
	val address =
		if (Config.isTestEnvironment())
			Config.getCurrentBTCSeriesTestAddress()
		else Config.getCurrentBCHAddress()
	BitcoinCashApi.getTransactions(
		address,
		errorCallback
	) { transactions ->
		// Calculate All Inputs to get transfer value
		successCallback(transactions.map {
			// 转换数据格式
			BTCSeriesTransactionTable(
				it,
				address,
				CryptoSymbol.bch,
				false,
				ChainType.BCH.id
			)
		}.filterNot { chainData ->
			// 去除翻页机制导致的不可避免的重复数据
			val localTransaction =
				localData.find { it.hash.equals(chainData.hash, true) }
			// 本地的数据更新网络数据, 因为本地可能有  `Pending` 拼接的数据, 所以重复的都首先更新网络
			!localTransaction?.apply {
				BTCSeriesTransactionTable.updateLocalDataByHash(
					hash,
					this,
					false,
					false
				)
			}.isNull()
		}.map {
			// 插入转账数据到数据库
			BTCSeriesTransactionTable
				.preventRepeatedInsert(it.hash, false, it)
			// 同样的账单插入一份燃气费的数据
			if (!it.isReceive) {
				BTCSeriesTransactionTable
					.preventRepeatedInsert(
						it.hash,
						true,
						it.apply { isFee = true }
					)
			}
			TransactionListModel(it)
		}.isNotEmpty())
	}
}