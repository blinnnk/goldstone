package io.goldstone.blockchain.module.home.quotation.rank.contract

import io.goldstone.blockchain.module.common.contract.GoldStonePresenter
import io.goldstone.blockchain.module.common.contract.GoldStoneView
import io.goldstone.blockchain.module.home.quotation.rank.model.CoinGlobalModel
import io.goldstone.blockchain.module.home.quotation.rank.model.CoinRankModel

/**
 * @date: 2018-12-12.
 * @author: yangLiHai
 * @description:
 */
interface CoinRankContract {
	interface GSView: GoldStoneView<GSPresenter> {
		fun showHeaderData(model: CoinGlobalModel)
		fun showListData(data: List<CoinRankModel>)
	}
	interface GSPresenter: GoldStonePresenter {
	}
	
}