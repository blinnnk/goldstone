package io.goldstone.blockchain.module.home.dapp.dappcenter.presenter

import android.support.annotation.UiThread
import com.blinnnk.extension.hasValue
import com.blinnnk.extension.toArrayList
import com.blinnnk.util.load
import com.blinnnk.util.then
import io.goldstone.blockchain.common.sharedpreference.SharedWallet
import io.goldstone.blockchain.kernel.commontable.FavoriteTable
import io.goldstone.blockchain.kernel.commontable.value.TableType
import io.goldstone.blockchain.module.home.dapp.dappcenter.contract.DAppCenterContract
import io.goldstone.blockchain.module.home.dapp.dappcenter.model.DAPPTable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


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
			DAPPTable.dao.getUsed()
		} then {
			dappView.showLatestUsed(it.toArrayList())
		}
	}

	override fun getDAPPUsedStatus(dappID: String, @UiThread hold: (Boolean) -> Unit) {
		load {
			FavoriteTable.dao.getDataCount(dappID, TableType.DAPP).hasValue()
		} then {
			hold(it)
		}
	}

	override fun updateDAPPUsedStatus(dappID: String) {
		GlobalScope.launch(Dispatchers.Default) {
			FavoriteTable.dao.insert(
				FavoriteTable(
					SharedWallet.getCurrentWalletID(),
					TableType.DAPP,
					dappID,
					"${System.currentTimeMillis()}"
				)
			)
			setUsedDAPPs()
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