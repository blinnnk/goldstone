package io.goldstone.blockchain.module.home.rammarket.module.ramquotation.bigtransaction.contract

import io.goldstone.blockchain.module.common.contract.GoldStonePresenter
import io.goldstone.blockchain.module.common.contract.GoldStoneView
import io.goldstone.blockchain.module.home.rammarket.module.ramtrade.model.TradingInfoModel

/**
 * @date: 2018-11-20.
 * @author: yangLiHai
 * @description:
 */
class BigTransactionContract{
	interface GSView: GoldStoneView<GSPresenter> {
		fun updateUI(data: ArrayList<TradingInfoModel>)
	}
	
	interface GSPresenter: GoldStonePresenter {
		fun getBigTransactions()
	}

}