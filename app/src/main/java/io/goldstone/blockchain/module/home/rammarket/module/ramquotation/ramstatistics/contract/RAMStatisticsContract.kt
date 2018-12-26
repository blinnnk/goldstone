package io.goldstone.blockchain.module.home.rammarket.module.ramquotation.ramstatistics.contract

import io.goldstone.blockchain.module.common.contract.GoldStonePresenter
import io.goldstone.blockchain.module.common.contract.GoldStoneView

/**
 * @date: 2018-11-20.
 * @author: yangLiHai
 * @description:
 */
interface RAMStatisticsContract {
	interface GSView: GoldStoneView<GSPresenter> {
		fun setGlobalRAMData(availableAmount: Float, totalAmount: Float, percent: Float)
		fun setChainRAMData(ramBalance: String, ramOfEOS: String)
	}
	interface GSPresenter: GoldStonePresenter
}