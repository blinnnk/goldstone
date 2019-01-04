package io.goldstone.blockchain.module.home.dapp.dappexplorer.contract

import io.goldstone.blockchain.module.common.contract.GoldStonePresenter
import io.goldstone.blockchain.module.common.contract.GoldStoneView
import io.goldstone.blockchain.module.home.dapp.dappcenter.model.DAPPTable


/**
 * @author KaySaith
 * @date  2018/12/08
 */
interface DAPPExplorerContract {
	interface GSView : GoldStoneView<GoldStonePresenter> {

	}

	interface GSPresenter : GoldStonePresenter {
		fun getSearchResult(condition: String, hold: (List<DAPPTable>) -> Unit)
	}
}