package io.goldstone.blockchain.module.home.rammarket.module.ramtransactionsearch.contract

import android.support.annotation.UiThread
import io.goldstone.blockchain.module.common.contract.GoldStonePresenter
import io.goldstone.blockchain.module.common.contract.GoldStoneView
import io.goldstone.blockchain.module.home.rammarket.module.ramtrade.model.TradingInfoModel
import java.util.ArrayList

/**
 * @date: 2018-11-20.
 * @author: yangLiHai
 * @description:
 */
interface RAMTransactionSearchContract {
	interface GSView: GoldStoneView<GSPresenter> {
		fun showLoading(status: Boolean)
		fun showBottomLoading(status: Boolean)
		fun notifyUI(isClear: Boolean, newData: ArrayList<TradingInfoModel>)
	}
	interface GSPresenter: GoldStonePresenter {
		fun loadFirstPage()
		fun loadMore()
		fun searchByName(@UiThread callback: () -> Unit)
	}
}