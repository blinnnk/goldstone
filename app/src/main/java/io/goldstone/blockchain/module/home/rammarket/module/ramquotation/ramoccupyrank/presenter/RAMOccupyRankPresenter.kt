package io.goldstone.blockchain.module.home.rammarket.module.ramquotation.ramoccupyrank.presenter

import com.blinnnk.extension.toArrayList
import io.goldstone.blockchain.common.thread.launchUI
import io.goldstone.blockchain.kernel.network.common.GoldStoneAPI
import io.goldstone.blockchain.module.home.rammarket.module.ramquotation.ramoccupyrank.contract.RAMOccupyRankContract

/**
 * @date: 2018/11/5.
 * @author: yanglihai
 * @description:
 */
class RAMOccupyRankPresenter(private val gsView: RAMOccupyRankContract.GSView)
	:RAMOccupyRankContract.GSPresenter {
	
	override fun start() {
		getBigTransactions()
		
	}
	
	override fun getBigTransactions() {
		GoldStoneAPI.getRAMOccupyRank { data, error ->
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