package io.goldstone.blinnnk.module.home.dapp.dappbrowser.contract

import io.goldstone.blinnnk.module.common.contract.GoldStonePresenter
import io.goldstone.blinnnk.module.common.contract.GoldStoneView


/**
 * @author KaySaith
 * @date  2018/11/29
 */
interface DAppBrowserContract {
	interface GSView : GoldStoneView<GoldStonePresenter> {

	}

	interface GSPresenter : GoldStonePresenter {

	}
}