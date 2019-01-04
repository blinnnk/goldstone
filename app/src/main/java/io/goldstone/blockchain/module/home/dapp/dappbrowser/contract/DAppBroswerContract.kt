package io.goldstone.blockchain.module.home.dapp.dappbrowser.contract

import io.goldstone.blockchain.module.common.contract.GoldStonePresenter
import io.goldstone.blockchain.module.common.contract.GoldStoneView


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