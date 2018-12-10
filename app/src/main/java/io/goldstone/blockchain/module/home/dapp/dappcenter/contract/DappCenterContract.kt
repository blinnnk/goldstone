package io.goldstone.blockchain.module.home.dapp.dappcenter.contract

import io.goldstone.blockchain.module.common.contract.GoldStonePresenter
import io.goldstone.blockchain.module.common.contract.GoldStoneView
import io.goldstone.blockchain.module.home.dapp.dappcenter.model.DAPPTable


/**
 * @author KaySaith
 * @date  2018/11/29
 */
interface DAppCenterContract {
	interface GSView : GoldStoneView<GoldStonePresenter> {
		fun showRecommendDAPP(data: ArrayList<DAPPTable>)
		fun showAllDAPP(data: ArrayList<DAPPTable>)
		fun showLatestUsed(data: ArrayList<DAPPTable>)
	}

	interface GSPresenter : GoldStonePresenter {
		fun setUsedDAPPs()
		fun getDAPPUsedStatus(dappID: String, hold: (Boolean) -> Unit)
		fun updateDAPPUsedStatus(dappID: String)
	}
}