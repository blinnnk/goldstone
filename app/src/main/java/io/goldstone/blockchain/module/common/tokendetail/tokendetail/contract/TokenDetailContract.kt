package io.goldstone.blockchain.module.common.tokendetail.tokendetail.contract

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
		fun showLoading(status: Boolean)
		fun showBottomLoading(status: Boolean)
		fun setChartData(data: ArrayList<ChartPoint>)
		fun notifyDataRangeChanged(start: Int, count: Int)
		fun getDetailAdapter(): TokenDetailAdapter?
		fun removeEmptyView()
		fun flipPage()
		fun filterData(data: List<TransactionListModel>?): List<TransactionListModel>
		fun showFilterLoadMoreAttention(dataSize: Int)
	}

	interface GSPresenter : GoldStonePresenter {
		fun loadMore()
		fun refreshData()
	}
}