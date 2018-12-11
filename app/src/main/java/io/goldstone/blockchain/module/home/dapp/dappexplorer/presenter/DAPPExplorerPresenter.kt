package io.goldstone.blockchain.module.home.dapp.dappexplorer.presenter

import com.blinnnk.util.load
import com.blinnnk.util.then
import io.goldstone.blockchain.common.utils.NetworkUtil
import io.goldstone.blockchain.module.home.dapp.dappcenter.model.DAPPTable
import io.goldstone.blockchain.module.home.dapp.dappexplorer.contract.DAPPExplorerContract


/**
 * @author KaySaith
 * @date  2018/12/08
 */
class DAPPExplorerPresenter : DAPPExplorerContract.GSPresenter {
	override fun start() {

	}

	override fun getSearchResult(condition: String, hold: (List<DAPPTable>) -> Unit) {
		if (NetworkUtil.hasNetwork()) {
			load {
				DAPPTable.dao.getBy(condition)
			} then {
				hold(it)
			}
		} else {
			load {
				DAPPTable.dao.getBy(condition)
			} then (hold)
		}
	}
}