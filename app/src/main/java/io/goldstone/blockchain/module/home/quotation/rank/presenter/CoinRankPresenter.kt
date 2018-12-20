package io.goldstone.blockchain.module.home.quotation.rank.presenter

import io.goldstone.blockchain.common.language.HoneyLanguage
import io.goldstone.blockchain.common.language.currentLanguage
import io.goldstone.blockchain.common.thread.launchUI
import io.goldstone.blockchain.kernel.network.common.GoldStoneAPI
import io.goldstone.blockchain.module.home.quotation.rank.contract.CoinRankContract
import java.math.BigDecimal

/**
 * @date: 2018-12-12.
 * @author: yangLiHai
 * @description:
 */
@Suppress("IMPLICIT_CAST_TO_ANY")
class CoinRankPresenter(private val gsView: CoinRankContract.GSView) : CoinRankContract.GSPresenter {
	
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
	
	companion object {
		
		enum class CoinRankUnit(val value: BigDecimal) {
			T(BigDecimal(Math.pow(10.0, 3.0))),
			M(BigDecimal(Math.pow(10.0, 6.0))),
			B(BigDecimal(Math.pow(10.0, 9.0))),
			W(BigDecimal(Math.pow(10.0, 4.0))),
			Y(BigDecimal(Math.pow(10.0, 8.0))),
		}
		
		fun parseVolumeText(text: String): String {
			val volume = BigDecimal(text)
			return if (currentLanguage == HoneyLanguage.English.code
				|| currentLanguage == HoneyLanguage.Russian.code) {
				when {
					volume > CoinRankUnit.B.value -> "${volume.divide(CoinRankUnit.B.value, 1, BigDecimal.ROUND_HALF_UP)}B"
					volume > CoinRankUnit.M.value -> "${volume.divide(CoinRankUnit.M.value, 1, BigDecimal.ROUND_HALF_UP)}M"
					volume > CoinRankUnit.T.value -> "${volume.divide(CoinRankUnit.T.value, 1, BigDecimal.ROUND_HALF_UP)}T"
					else -> volume.setScale(1, BigDecimal.ROUND_HALF_UP).toString()
				}
			} else {
				when {
					volume > CoinRankUnit.Y.value -> "${volume.divide(CoinRankUnit.Y.value, 1, BigDecimal.ROUND_HALF_UP)}亿"
					volume > CoinRankUnit.W.value -> "${volume.divide(CoinRankUnit.W.value, 1, BigDecimal.ROUND_HALF_UP)}万"
					else -> volume.setScale(1, BigDecimal.ROUND_HALF_UP).toString()
				}
			}
			
		}
	}
	
}







