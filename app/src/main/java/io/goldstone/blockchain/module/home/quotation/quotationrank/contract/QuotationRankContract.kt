package io.goldstone.blockchain.module.home.quotation.quotationrank.contract

import io.goldstone.blockchain.module.common.contract.GoldStonePresenter
import io.goldstone.blockchain.module.common.contract.GoldStoneView
import io.goldstone.blockchain.module.home.quotation.quotationrank.model.QuotationRankModel


/**
 * @author KaySaith
 * @date  2019/01/02
 */
interface QuotationRankContract {
	interface GSView : GoldStoneView<GoldStonePresenter> {
		fun showMarketInfo()
		fun updateAdapterDataSet(data: List<QuotationRankModel>)
	}

	interface GSPresenter : GoldStonePresenter {
		fun loadMore()
	}
}