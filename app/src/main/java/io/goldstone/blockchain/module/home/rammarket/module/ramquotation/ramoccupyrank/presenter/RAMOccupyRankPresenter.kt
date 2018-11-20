package io.goldstone.blockchain.module.home.rammarket.module.ramquotation.ramoccupyrank.presenter

import com.blinnnk.extension.toArrayList
import io.goldstone.blockchain.kernel.network.common.GoldStoneAPI
import io.goldstone.blockchain.module.home.rammarket.module.ramquotation.ramoccupyrank.contract.RAMOccupyRankContract
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.runOnUiThread

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
	
	private fun getBigTransactions() {
		doAsync {
			GoldStoneAPI.getRAMOccupyRank { data, error ->
				if (data != null && error.isNone()) {
					GoldStoneAPI.context.runOnUiThread {
						gsView.updateUI(data.toArrayList())
					}
				} else {
					GoldStoneAPI.context.runOnUiThread {
						gsView.showError(error)
					}
				}
			}
		}
	}
}