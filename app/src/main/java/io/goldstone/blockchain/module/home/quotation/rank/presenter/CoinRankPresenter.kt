package io.goldstone.blockchain.module.home.quotation.rank.presenter

import io.goldstone.blockchain.kernel.network.common.GoldStoneAPI
import io.goldstone.blockchain.module.home.quotation.rank.contract.CoinRankContract

/**
 * @date: 2018-12-12.
 * @author: yangLiHai
 * @description:
 */
class CoinRankPresenter(private val gsView: CoinRankContract.GSView
): CoinRankContract.GSPresenter {
	
	private var lastRank = 0
	
	override fun start() {
		getGlobalData()
	
	}
	
	private fun getGlobalData() {
		GoldStoneAPI.getGlobalData { model, error ->
			if (model != null && !error.isNone()) {
				gsView.showHeaderData(model)
			} else {
				gsView.showError(error)
			}
		}
	}
	
	private fun getRankList() {
		GoldStoneAPI.getCoinRank(lastRank) { data, error ->
			
		}
	}
	
	
	override fun loadFirstPage() {
		gsView.showLoadingView(true)
		lastRank = -1
	}
	
	override fun loadMore() {
	
	}
	
}