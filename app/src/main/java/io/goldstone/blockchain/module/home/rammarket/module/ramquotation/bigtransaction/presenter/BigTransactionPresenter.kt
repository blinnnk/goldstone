package io.goldstone.blockchain.module.home.rammarket.module.ramquotation.bigtransaction.presenter

import com.blinnnk.extension.toArrayList
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerPresenter
import io.goldstone.blockchain.kernel.network.common.GoldStoneAPI
import io.goldstone.blockchain.module.home.rammarket.module.ramquotation.bigtransaction.contract.BigTransactionContract
import io.goldstone.blockchain.module.home.rammarket.module.ramquotation.bigtransaction.view.BigTransactionFragment
import io.goldstone.blockchain.module.home.rammarket.module.ramtrade.model.TradingInfoModel
import org.jetbrains.anko.*

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
		doAsync {
			GoldStoneAPI.getBigTransactions(1) { data, error ->
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