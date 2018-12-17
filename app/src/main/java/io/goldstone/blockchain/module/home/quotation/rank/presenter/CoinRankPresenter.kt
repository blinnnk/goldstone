package io.goldstone.blockchain.module.home.quotation.rank.presenter

import io.goldstone.blockchain.common.thread.launchUI
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
		loadFirstPage()
	}
	
	private fun getGlobalData() {
		GoldStoneAPI.getGlobalData { model, error ->
			launchUI {
				if (model != null && error.isNone()) {
					gsView.showHeaderData(model)
				} else {
					gsView.showError(error)
				}
			}
		}
	}
	
	private fun getNextPage(callback: () -> Unit) {
		GoldStoneAPI.getCoinRank(lastRank) { data, error ->
			launchUI {
				if (data != null && error.isNone()) {
					gsView.showListData(lastRank == 0, data)
					if (data.isNotEmpty()) {
						lastRank = data[data.lastIndex].rank
					}
				} else {
					gsView.showError(error)
				}
				callback()
			}
			
			
		}
	}
	
	
	override fun loadFirstPage() {
		gsView.showLoadingView(true)
		lastRank = 0
		getNextPage {
			gsView.showLoadingView(false)
		}
	}
	
	override fun loadMore() {
		gsView.showBottomLoading(true)
		getNextPage {
			gsView.showBottomLoading(false)
		}
	}
	
}