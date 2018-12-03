package io.goldstone.blockchain.module.home.dapp.dappcenter.contract

import io.goldstone.blockchain.module.common.contract.GoldStonePresenter
import io.goldstone.blockchain.module.common.contract.GoldStoneView
import io.goldstone.blockchain.module.home.dapp.dappcenter.model.DAPPModel


/**
 * @author KaySaith
 * @date  2018/11/29
 */
interface DAppCenterContract {
	interface GSView : GoldStoneView<GoldStonePresenter> {
		fun showRecommendDAPP(data: ArrayList<DAPPModel>)
		fun showNewDAPP(data: ArrayList<DAPPModel>)
		fun showLatestUsed(data: ArrayList<DAPPModel>)
	}

	interface GSPresenter : GoldStonePresenter {

	}
}