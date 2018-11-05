package io.goldstone.blockchain.module.home.rammarket.module.ramquotation.ramoccupyrank.view

import com.blinnnk.extension.orEmptyArray
import com.blinnnk.extension.toArrayList
import io.goldstone.blockchain.common.Language.EOSRAMExchangeText
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerFragment
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerView
import io.goldstone.blockchain.module.home.rammarket.module.ramquotation.ramoccupyrank.model.RAMRankModel
import io.goldstone.blockchain.module.home.rammarket.module.ramquotation.ramoccupyrank.presenter.RAMOccupyRankPresenter
import io.goldstone.blockchain.module.home.rammarket.module.ramtrade.model.TradingInfoModel
import org.jetbrains.anko.sdk25.coroutines.onClick

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
		recyclerView.adapter = RAMOccupyRankAdapter(asyncData.orEmptyArray().toArrayList()) {
			onClick {
			
			}
		}
	}
}