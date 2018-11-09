package io.goldstone.blockchain.module.home.rammarket.module.ramquotation.ramoccupyrank.view

import android.os.Bundle
import com.blinnnk.extension.*
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.Language.EOSRAMExchangeText
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerFragment
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerView
import io.goldstone.blockchain.module.home.rammarket.module.ramquotation.ramoccupyrank.model.RAMRankModel
import io.goldstone.blockchain.module.home.rammarket.module.ramquotation.ramoccupyrank.presenter.RAMOccupyRankPresenter
import io.goldstone.blockchain.module.home.rammarket.module.ramtrade.model.TradingInfoModel
import io.goldstone.blockchain.module.home.rammarket.module.ramtransactionsearch.view.RAMTransactionSearchFragment
import io.goldstone.blockchain.module.home.rammarket.view.RAMMarketDetailFragment
import io.goldstone.blockchain.module.home.rammarket.view.RAMMarketOverlayFragment
import org.jetbrains.anko.leftPadding
import org.jetbrains.anko.rightPadding
import org.jetbrains.anko.sdk27.coroutines.onClick

/**
 * @date: 2018/11/5.
 * @author: yanglihai
 * @description:
 */
class RAMOccupyRankFragment : BaseRecyclerFragment<RAMOccupyRankPresenter, RAMRankModel>() {
	
	override val pageTitle: String = EOSRAMExchangeText.ramExchange
	override val presenter: RAMOccupyRankPresenter = RAMOccupyRankPresenter(this)
	
	override fun setRecyclerViewAdapter(
		recyclerView: BaseRecyclerView,
		asyncData: ArrayList<RAMRankModel>?
	) {
		wrapper.leftPadding = 20.uiPX()
		wrapper.rightPadding = 20.uiPX()
		recyclerView.setHasFixedSize(true)
		recyclerView.isNestedScrollingEnabled = false
		recyclerView.adapter = RAMOccupyRankAdapter(asyncData.orEmptyArray().toArrayList()) {
			onClick {
				getParentFragment<RAMMarketDetailFragment> {
					getParentFragment<RAMMarketOverlayFragment> {
						presenter.showTargetFragment<RAMTransactionSearchFragment>(Bundle().apply { putString("account", model.account) })
						overlayView.header.apply {
							showBackButton(false) { }
							showSearchInput {
								presenter.popFragmentFrom<RAMTransactionSearchFragment>()
							}
						}
						
					}
				}
			}
		}
	}
}