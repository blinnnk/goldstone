package io.goldstone.blockchain.module.home.dapp.dappcenter.presenter

import com.blinnnk.extension.toArrayList
import com.blinnnk.util.load
import com.blinnnk.util.then
import io.goldstone.blockchain.common.value.DataValue
import io.goldstone.blockchain.module.home.dapp.dappcenter.contract.DAppCenterContract
import io.goldstone.blockchain.module.home.dapp.dappcenter.model.DAPPTable


/**
 * @author KaySaith
 * @date  2018/11/29
 */
class DAppCenterPresenter(
	private val dappView: DAppCenterContract.GSView
) : DAppCenterContract.GSPresenter {

	override fun start() {
		setDAPPRecommendData()
		setNewDAPP()
		setUsedDAPPs()
	}

	private fun setDAPPRecommendData() {
		load {
			DAPPTable.dao.getRecommended()
		} then {
			dappView.showRecommendDAPP(it.toArrayList())
		}
	}

	override fun setUsedDAPPs() {
		load {
			DAPPTable.dao.getUsed(DataValue.dappPageCount)
		} then {
			dappView.showLatestUsed(it.toArrayList())
		}
	}

	private fun setNewDAPP() {
		load {
			DAPPTable.dao.getAll(DataValue.dappPageCount)
		} then {
			dappView.showAllDAPP(it.toArrayList())
		}
	}

}