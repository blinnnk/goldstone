package io.goldstone.blockchain.module.home.dapp.dapplist.contract

import io.goldstone.blockchain.module.common.contract.GoldStonePresenter
import io.goldstone.blockchain.module.common.contract.GoldStoneView
import io.goldstone.blockchain.module.home.dapp.dappcenter.model.DAPPTable
import io.goldstone.blockchain.module.home.dapp.dapplist.model.DAPPType


/**
 * @author KaySaith
 * @date  2018/12/08
 */
interface DAPPListContract {
	interface GSView : GoldStoneView<GoldStonePresenter> {

	}

	interface GSPresenter : GoldStonePresenter {
		fun getData(type: DAPPType, hold: (List<DAPPTable>) -> Unit)
	}
}