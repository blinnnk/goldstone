package io.goldstone.blockchain.module.common.tokendetail.tokendetail.presenter

import io.goldstone.blockchain.common.language.LoadingText
import io.goldstone.blockchain.module.home.wallet.transactions.transactionlist.classictransactionlist.presenter.ClassicTransactionListPresenter

/**
 * @date 2018/8/14 5:01 PM
 * @author KaySaith
 */

fun TokenDetailPresenter.loadETCChainData() {
	fragment.showLoadingView(LoadingText.transactionData)
	ClassicTransactionListPresenter.getETCTransactionsFromChain(arrayListOf()) {
		fragment.removeLoadingView()
		loadDataFromDatabaseOrElse()
	}
}