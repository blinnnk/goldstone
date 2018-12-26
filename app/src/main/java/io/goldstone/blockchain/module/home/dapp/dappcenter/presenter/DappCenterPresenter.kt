package io.goldstone.blockchain.module.home.dapp.dappcenter.presenter

import com.blinnnk.extension.toArrayList
import com.blinnnk.util.load
import com.blinnnk.util.then
import io.goldstone.blockchain.common.thread.launchDefault
import io.goldstone.blockchain.common.thread.launchUI
import io.goldstone.blockchain.common.value.DataValue
import io.goldstone.blockchain.module.home.dapp.dappcenter.contract.DAppCenterContract
import io.goldstone.blockchain.module.home.dapp.dappcenter.model.DAPPTable
import io.goldstone.blockchain.module.home.home.presneter.SilentUpdater


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
		getRecommendDappCount()
	}

	override fun setUsedDAPPs() {
		load {
			DAPPTable.dao.getUsed(DataValue.dappPageCount)
		} then {
			dappView.showLatestUsed(it.toArrayList())
		}
	}

	private fun getRecommendDappCount() {
		load { DAPPTable.dao.getRecommendedCount() } then {
			dappView.showRecommendedSession(it)
		}
	}

	private fun setDAPPRecommendData() {
		launchDefault {
			val localData = DAPPTable.dao.getRecommended()
			if (localData.isEmpty()) SilentUpdater.updateRecommendedDAPP {
				load {
					DAPPTable.dao.getRecommended()
				} then {
					dappView.showRecommendDAPP(it.toArrayList())
				}
			} else launchUI {
				dappView.showRecommendDAPP(localData.toArrayList())
			}
		}
	}

	private fun setNewDAPP() {
		launchDefault {
			val localData = DAPPTable.dao.getAll(DataValue.dappPageCount)
			if (localData.isEmpty()) SilentUpdater.updateNewDAPP {
				load {
					DAPPTable.dao.getAll(DataValue.dappPageCount)
				} then {
					dappView.showAllDAPP(it.toArrayList())
				}
			} else launchUI {
				dappView.showAllDAPP(localData.toArrayList())
			}
		}
	}
}