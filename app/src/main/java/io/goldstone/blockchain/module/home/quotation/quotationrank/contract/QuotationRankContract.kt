package io.goldstone.blockchain.module.home.quotation.quotationrank.contract

import io.goldstone.blockchain.module.common.contract.GoldStonePresenter
import io.goldstone.blockchain.module.common.contract.GoldStoneView
import io.goldstone.blockchain.module.home.quotation.quotationrank.model.QuotationGlobalModel
import io.goldstone.blockchain.module.home.quotation.quotationrank.model.QuotationRankTable


/**
 * @author KaySaith
 * @date  2019/01/02
 */
interface QuotationRankContract {
	interface GSView : GoldStoneView<GoldStonePresenter> {
		fun showLoadingView(status: Boolean)
		fun showBottomLoading(isShow: Boolean)
		fun showHeaderData(model: QuotationGlobalModel)
		fun updateData(newData: List<QuotationRankTable>)
	}
	
	interface GSPresenter : GoldStonePresenter {
		fun loadFirstPage()
		fun loadMore()
	}
}