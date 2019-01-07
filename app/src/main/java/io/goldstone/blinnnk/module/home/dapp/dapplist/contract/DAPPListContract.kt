package io.goldstone.blinnnk.module.home.dapp.dapplist.contract

import android.support.annotation.WorkerThread
import io.goldstone.blinnnk.module.common.contract.GoldStonePresenter
import io.goldstone.blinnnk.module.common.contract.GoldStoneView
import io.goldstone.blinnnk.module.home.dapp.dappcenter.model.DAPPTable
import io.goldstone.blinnnk.module.home.dapp.dapplist.model.DAPPType


/**
 * @author KaySaith
 * @date  2018/12/08
 */
interface DAPPListContract {
	interface GSView : GoldStoneView<GoldStonePresenter> {

	}

	interface GSPresenter : GoldStonePresenter {
		fun getData(type: DAPPType, hold: (List<DAPPTable>) -> Unit)
		fun loadMore(pageIndex: Int, dataType: DAPPType, @WorkerThread hold: (List<DAPPTable>) -> Unit)
	}
}