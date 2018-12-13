package io.goldstone.blockchain.module.home.quotation.rank.presenter

import io.goldstone.blockchain.kernel.network.common.GoldStoneAPI
import io.goldstone.blockchain.module.home.quotation.rank.contract.CoinRankContract

/**
 * @date: 2018-12-12.
 * @author: yangLiHai
 * @description:
 */
class CoinRankPresenter: CoinRankContract.GSPresenter {
	override fun start() {
		getGlobalData()
	
	}
	
	private fun getGlobalData() {
		GoldStoneAPI.getGlobalData { model, error ->
		
		}
	}
	
}