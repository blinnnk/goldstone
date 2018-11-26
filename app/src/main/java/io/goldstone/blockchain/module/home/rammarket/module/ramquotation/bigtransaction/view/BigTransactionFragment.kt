package io.goldstone.blockchain.module.home.rammarket.module.ramquotation.bigtransaction.view

import android.os.Bundle
import android.view.View
import com.blinnnk.extension.*
import io.goldstone.blockchain.common.Language.EOSRAMExchangeText
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerFragment
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerView
import io.goldstone.blockchain.common.base.gsfragment.GSRecyclerFragment
import io.goldstone.blockchain.common.utils.ErrorDisplayManager
import io.goldstone.blockchain.module.home.rammarket.module.ramquotation.bigtransaction.contract.BigTransactionContract
import io.goldstone.blockchain.module.home.rammarket.module.ramquotation.bigtransaction.presenter.BigTransactionPresenter
import io.goldstone.blockchain.module.home.rammarket.module.ramtrade.model.TradingInfoModel
import io.goldstone.blockchain.module.home.rammarket.view.RAMMarketDetailFragment
import io.goldstone.blockchain.module.home.rammarket.view.RAMMarketOverlayFragment
import org.jetbrains.anko.sdk27.coroutines.onClick

/**
 * @date: 2018/11/5.
 * @author: yanglihai
 * @description:
 */
class BigTransactionFragment: GSRecyclerFragment<TradingInfoModel>(), BigTransactionContract.GSView {
	override fun updateUI(data: ArrayList<TradingInfoModel>) {
		setRecyclerViewAdapter(recyclerView, data)
	}
	
	override fun showError(error: Throwable) {
		ErrorDisplayManager(error).show(context)
	}
	
	override val pageTitle: String = EOSRAMExchangeText.ramExchange
	override val presenter: BigTransactionPresenter = BigTransactionPresenter(this)
	
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		presenter.start()
	}
	
	override fun setRecyclerViewAdapter(
		recyclerView: BaseRecyclerView,
		asyncData: ArrayList<TradingInfoModel>?
	) {
		// 把滑动交给父view
		recyclerView.setHasFixedSize(true)
		recyclerView.isNestedScrollingEnabled = false
		
		recyclerView.adapter = BigTransactionsAdapter(asyncData.orEmptyArray().toArrayList()) {
			onClick {
				getParentFragment<RAMMarketDetailFragment> {
					getParentFragment<RAMMarketOverlayFragment> {
						presenter.showTransactionHistoryFragment(model.account)
					}
				}
			}
		}
	}
}