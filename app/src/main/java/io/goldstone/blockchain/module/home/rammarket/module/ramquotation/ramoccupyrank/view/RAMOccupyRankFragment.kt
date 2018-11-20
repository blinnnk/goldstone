package io.goldstone.blockchain.module.home.rammarket.module.ramquotation.ramoccupyrank.view

import android.os.Bundle
import android.view.View
import com.blinnnk.extension.*
import io.goldstone.blockchain.common.Language.EOSRAMExchangeText
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerFragment
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerView
import io.goldstone.blockchain.common.base.gsfragment.GSRecyclerFragment
import io.goldstone.blockchain.common.utils.ErrorDisplayManager
import io.goldstone.blockchain.module.home.rammarket.module.ramquotation.ramoccupyrank.contract.RAMOccupyRankContract
import io.goldstone.blockchain.module.home.rammarket.module.ramquotation.ramoccupyrank.model.RAMRankModel
import io.goldstone.blockchain.module.home.rammarket.module.ramquotation.ramoccupyrank.presenter.RAMOccupyRankPresenter
import io.goldstone.blockchain.module.home.rammarket.view.RAMMarketDetailFragment
import io.goldstone.blockchain.module.home.rammarket.view.RAMMarketOverlayFragment
import org.jetbrains.anko.sdk27.coroutines.onClick

/**
 * @date: 2018/11/5.
 * @author: yanglihai
 * @description:
 */
class RAMOccupyRankFragment : GSRecyclerFragment<RAMRankModel>(), RAMOccupyRankContract.GSView {
	override fun updateUI(data: ArrayList<RAMRankModel>) {
		setRecyclerViewAdapter(recyclerView, data)
	}
	
	override fun showError(error: Throwable) {
		ErrorDisplayManager(error).show(context)
	}
	
	override val pageTitle: String = EOSRAMExchangeText.ramExchange
	override val presenter: RAMOccupyRankPresenter = RAMOccupyRankPresenter(this)
	
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		presenter.start()
	}
	
	override fun setRecyclerViewAdapter(
		recyclerView: BaseRecyclerView,
		asyncData: ArrayList<RAMRankModel>?
	) {
		recyclerView.setHasFixedSize(true)
		recyclerView.isNestedScrollingEnabled = false
		recyclerView.adapter = RAMOccupyRankAdapter(asyncData.orEmptyArray().toArrayList()) {
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