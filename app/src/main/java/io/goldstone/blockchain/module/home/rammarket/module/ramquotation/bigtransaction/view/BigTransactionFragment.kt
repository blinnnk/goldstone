package io.goldstone.blockchain.module.home.rammarket.module.ramquotation.bigtransaction.view

import com.blinnnk.extension.orEmptyArray
import com.blinnnk.extension.toArrayList
import io.goldstone.blockchain.common.Language.EOSRAMExchangeText
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerFragment
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerView
import io.goldstone.blockchain.module.home.rammarket.module.ramquotation.bigtransaction.presenter.BigTransactionPresenter
import io.goldstone.blockchain.module.home.rammarket.module.ramtrade.model.TradingInfoModel
import org.jetbrains.anko.sdk25.coroutines.onClick

/**
 * @date: 2018/11/5.
 * @author: yanglihai
 * @description:
 */
class BigTransactionFragment : BaseRecyclerFragment<BigTransactionPresenter, TradingInfoModel>() {
	
	override val pageTitle: String = EOSRAMExchangeText.ramExchange
	override val presenter: BigTransactionPresenter = BigTransactionPresenter(this)
	
	override fun setRecyclerViewAdapter(
		recyclerView: BaseRecyclerView,
		asyncData: ArrayList<TradingInfoModel>?
	) {
		recyclerView.adapter = BigTransactionsAdapter(asyncData.orEmptyArray().toArrayList()) {
			onClick {
			
			}
		}
	}
}