package io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.contract

import io.goldstone.blockchain.module.common.contract.GoldStonePresenter
import io.goldstone.blockchain.module.common.contract.GoldStoneView
import io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.model.TransactionDetailModel
import io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.model.TransactionHeaderModel
import io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.model.TransactionProgressModel


/**
 * @author KaySaith
 * @date  2018/11/07
 */
interface TransactionDetailContract {
	interface GSView : GoldStoneView<GoldStonePresenter> {
		fun showLoading(status: Boolean)
		fun showProgress(model: TransactionProgressModel?)
		fun showHeaderData(model: TransactionHeaderModel)
		fun showMemo(memo: TransactionDetailModel)
		fun showTransactionAddresses(vararg model: TransactionDetailModel)
		fun showTransactionInformation(vararg info: TransactionDetailModel)
		fun showErrorAlert(error: Throwable)
		fun showContactEditor(address: String, symbol: String)
		fun updateTokenDetailList()
	}

	interface GSPresenter : GoldStonePresenter {
		fun removeObserver()
	}
}