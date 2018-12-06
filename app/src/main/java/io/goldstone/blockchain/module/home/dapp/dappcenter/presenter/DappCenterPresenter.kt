package io.goldstone.blockchain.module.home.dapp.dappcenter.presenter

import com.blinnnk.extension.toArrayList
import com.blinnnk.util.load
import com.blinnnk.util.then
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
		setLatestUsedDAPP()
	}

	private fun setDAPPRecommendData() {
		load {
			DAPPTable.dao.getRecommended()
		} then {
			dappView.showRecommendDAPP(it.toArrayList())
		}
	}

	private fun setLatestUsedDAPP() {
		// TODO
		load {
			DAPPTable.dao.getAll()
		} then {
			dappView.showLatestUsed(it.toArrayList())
		}
	}

	private fun setNewDAPP() {
		load {
			DAPPTable.dao.getAll()
		} then {
			dappView.showAllDAPP(it.toArrayList())
		}
	}

}