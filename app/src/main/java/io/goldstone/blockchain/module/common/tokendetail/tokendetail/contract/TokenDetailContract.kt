package io.goldstone.blockchain.module.common.tokendetail.tokendetail.contract

import android.support.annotation.UiThread
import io.goldstone.blockchain.module.common.contract.GoldStonePresenter
import io.goldstone.blockchain.module.common.contract.GoldStoneView
import io.goldstone.blockchain.module.common.tokendetail.tokendetail.view.TokenDetailAdapter
import io.goldstone.blockchain.module.home.quotation.quotation.model.ChartPoint
import io.goldstone.blockchain.module.home.wallet.transactions.transactionlist.ethereumtransactionlist.model.TransactionListModel


/**
 * @author KaySaith
 * @date  2018/11/11
 */
interface TokenDetailContract {
	interface GSView : GoldStoneView<GoldStonePresenter> {
		var asyncData: ArrayList<TransactionListModel>?
		var currentMenu: String
		fun showLoading(status: Boolean)
		fun showBottomLoading(status: Boolean)
		fun setChartData(data: ArrayList<ChartPoint>)
		fun updateDataChange(data: ArrayList<TransactionListModel>)
		fun notifyDataRangeChanged(start: Int, count: Int)
		fun getDetailAdapter(): TokenDetailAdapter?
		fun removeEmptyView()
		fun setAllMenu()
		fun flipPage()
	}

	interface GSPresenter : GoldStonePresenter {
		fun loadMore()
		fun showOnlyReceiveData()
		fun showOnlyFailedData()
		fun showOnlySentData()
		fun showAllData()
	}
}