package io.goldstone.blockchain.module.home.rammarket.module.ramquotation.ramoccupyrank.contract

import io.goldstone.blockchain.module.common.contract.GoldStonePresenter
import io.goldstone.blockchain.module.common.contract.GoldStoneView
import io.goldstone.blockchain.module.home.rammarket.module.ramquotation.ramoccupyrank.model.RAMRankModel

/**
 * @date: 2018-11-20.
 * @author: yangLiHai
 * @description:
 */
interface RAMOccupyRankContract {
	interface GSView: GoldStoneView<GSPresenter>{
		fun updateUI(data: ArrayList<RAMRankModel>)
	}
	interface GSPresenter: GoldStonePresenter
}