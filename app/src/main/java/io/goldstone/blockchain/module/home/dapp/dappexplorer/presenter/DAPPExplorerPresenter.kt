package io.goldstone.blockchain.module.home.dapp.dappexplorer.presenter

import com.blinnnk.extension.isNotNull
import com.blinnnk.util.load
import com.blinnnk.util.then
import io.goldstone.blockchain.common.thread.launchUI
import io.goldstone.blockchain.common.utils.NetworkUtil
import io.goldstone.blockchain.kernel.network.common.GoldStoneAPI
import io.goldstone.blockchain.module.home.dapp.dappcenter.model.DAPPTable
import io.goldstone.blockchain.module.home.dapp.dappexplorer.contract.DAPPExplorerContract


/**
 * @author KaySaith
 * @date  2018/12/08
 */
class DAPPExplorerPresenter(
	private val view: DAPPExplorerContract.GSView
) : DAPPExplorerContract.GSPresenter {
	override fun start() {

	}

	override fun getSearchResult(condition: String, hold: (result: List<DAPPTable>) -> Unit) {
		if (NetworkUtil.hasNetwork()) GoldStoneAPI.searchDAPP(condition) { data, error ->
			if (data.isNotNull() && error.isNone()) launchUI {
				hold(data)
			} else view.showError(error)
		} else load {
			DAPPTable.dao.getBy(condition)
		} then (hold)
	}
}