package io.goldstone.blockchain.module.home.rammarket.module.ramquotation.bigtransaction.presenter

import com.blinnnk.extension.toArrayList
import io.goldstone.blockchain.common.thread.launchUI
import io.goldstone.blockchain.kernel.network.common.GoldStoneAPI
import io.goldstone.blockchain.module.home.rammarket.module.ramquotation.bigtransaction.contract.BigTransactionContract

/**
 * @date: 2018/11/5.
 * @author: yanglihai
 * @description:
 */
class BigTransactionPresenter(private val gsView: BigTransactionContract.GSView)
	: BigTransactionContract.GSPresenter {
	
	override fun start() {
		getBigTransactions()
	}
	
	
	override fun getBigTransactions() {
		GoldStoneAPI.getLargeTransactions(1) { data, error ->
			if (data != null && error.isNone()) {
				launchUI {
					gsView.updateUI(data.toArrayList())
				}
			} else {
				launchUI {
					gsView.showError(error)
				}
			}
		}
	
	}
}