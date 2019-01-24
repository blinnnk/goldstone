package io.goldstone.blinnnk.module.home.dapp.dappexplorer.contract

import io.goldstone.blinnnk.module.common.contract.GoldStonePresenter
import io.goldstone.blinnnk.module.common.contract.GoldStoneView
import io.goldstone.blinnnk.module.home.dapp.dappcenter.model.DAPPTable
import io.goldstone.blinnnk.module.home.dapp.dappexplorer.model.DAPPRecentVisitedTable


/**
 * @author KaySaith
 * @date  2018/12/08
 */
interface DAPPExplorerContract {
	interface GSView : GoldStoneView<GoldStonePresenter> {
		fun showHeaderData(data: List<DAPPRecentVisitedTable>)
	}

	interface GSPresenter : GoldStonePresenter {
		fun getSearchResult(condition: String, hold: (List<DAPPTable>) -> Unit)
		fun getRecentVisitedData()
	}
}