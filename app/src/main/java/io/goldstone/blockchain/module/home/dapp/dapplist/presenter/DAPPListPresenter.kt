package io.goldstone.blockchain.module.home.dapp.dapplist.presenter

import com.blinnnk.util.load
import com.blinnnk.util.then
import io.goldstone.blockchain.module.home.dapp.dappcenter.model.DAPPTable
import io.goldstone.blockchain.module.home.dapp.dapplist.contract.DAPPListContract
import io.goldstone.blockchain.module.home.dapp.dapplist.model.DAPPType


/**
 * @author KaySaith
 * @date  2018/12/08
 */
class DAPPListPresenter : DAPPListContract.GSPresenter {
	override fun start() {

	}

	override fun getData(type: DAPPType, hold: (List<DAPPTable>) -> Unit) {
		load {
			when (type) {
				DAPPType.New -> DAPPTable.dao.getAll(10)
				DAPPType.Latest -> DAPPTable.dao.getUsed(10)
				else -> throw Throwable("Wrong DAPP Type")
			}
		} then (hold)
	}
}