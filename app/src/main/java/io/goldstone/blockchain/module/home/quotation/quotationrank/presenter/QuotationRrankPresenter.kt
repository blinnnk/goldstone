package io.goldstone.blockchain.module.home.quotation.quotationrank.presenter

import io.goldstone.blockchain.module.home.quotation.quotationrank.contract.QuotationRankContract
import io.goldstone.blockchain.module.home.quotation.quotationrank.model.QuotationRankModel


/**
 * @author KaySaith
 * @date  2019/01/02
 */
class QuotationRankPresenter(
	private val view: QuotationRankContract.GSView
) : QuotationRankContract.GSPresenter {
	override fun start() {
		setMarketInfo()
		setInitData()
	}

	override fun loadMore() {
		// TODO
	}

	private fun setInitData() {
		// 从数据库获取数据
		val data  =
			listOf<QuotationRankModel>()
		view.updateAdapterDataSet(data)
	}

	private fun setMarketInfo() {
		// 大盘指数相关
		view.showMarketInfo()
	}
}