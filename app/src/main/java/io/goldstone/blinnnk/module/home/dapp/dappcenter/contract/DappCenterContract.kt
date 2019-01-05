package io.goldstone.blinnnk.module.home.dapp.dappcenter.contract

import io.goldstone.blinnnk.module.common.contract.GoldStonePresenter
import io.goldstone.blinnnk.module.common.contract.GoldStoneView
import io.goldstone.blinnnk.module.home.dapp.dappcenter.model.DAPPTable


/**
 * @author KaySaith
 * @date  2018/11/29
 */
interface DAppCenterContract {
	interface GSView : GoldStoneView<GoldStonePresenter> {
		fun showRecommendDAPP(data: ArrayList<DAPPTable>)
		fun showAllDAPP(data: ArrayList<DAPPTable>)
		fun showLatestUsed(data: ArrayList<DAPPTable>)
		fun refreshLatestUsed()
		fun showRecommendedSession(count: Int)
	}

	interface GSPresenter : GoldStonePresenter {
		fun setUsedDAPPs()
	}
}