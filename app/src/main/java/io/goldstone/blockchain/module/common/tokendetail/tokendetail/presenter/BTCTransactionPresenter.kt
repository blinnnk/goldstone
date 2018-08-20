package io.goldstone.blockchain.module.common.tokendetail.tokendetail.presenter

import io.goldstone.blockchain.common.language.LoadingText
import io.goldstone.blockchain.module.home.wallet.transactions.transactionlist.bitcointransactionlist.bitcointransactionlistPresenter.BitcoinTransactionListPresenter
import org.jetbrains.anko.runOnUiThread

/**
 * @date 2018/8/14 4:59 PM
 * @author KaySaith
 */

fun TokenDetailPresenter.loadBTCChainData() {
	fragment.showLoadingView(LoadingText.transactionData)
	BitcoinTransactionListPresenter.loadTransactionsFromChain(
		arrayListOf(),
		{
			fragment.removeLoadingView()
			// TODO ERROR Alert
		}
	) {
		fragment.context?.runOnUiThread { fragment.removeLoadingView() }
		// TODO 判断数据
		loadDataFromDatabaseOrElse()
	}
}