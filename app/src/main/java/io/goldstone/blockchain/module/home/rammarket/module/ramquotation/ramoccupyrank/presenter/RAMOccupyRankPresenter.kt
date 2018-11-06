package io.goldstone.blockchain.module.home.rammarket.module.ramquotation.ramoccupyrank.presenter

import com.blinnnk.extension.toArrayList
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerPresenter
import io.goldstone.blockchain.kernel.network.common.GoldStoneAPI
import io.goldstone.blockchain.module.home.rammarket.module.ramquotation.ramoccupyrank.model.RAMRankModel
import io.goldstone.blockchain.module.home.rammarket.module.ramquotation.ramoccupyrank.view.RAMOccupyRankFragment
import org.jetbrains.anko.*

/**
 * @date: 2018/11/5.
 * @author: yanglihai
 * @description:
 */
class RAMOccupyRankPresenter(override val fragment: RAMOccupyRankFragment)
	: BaseRecyclerPresenter<RAMOccupyRankFragment, RAMRankModel>() {
	
	override fun updateData() {
		super.updateData()
		getBigTransactions()
	}
	
	private fun getBigTransactions() {
		doAsync {
			GoldStoneAPI.getRAMOccupyRank { data, error ->
				if (data != null && error.isNone()) {
					GoldStoneAPI.context.runOnUiThread {
						fragment.setRecyclerViewAdapter(fragment.recyclerView, data.toArrayList())
					}
				} else {
					GoldStoneAPI.context.runOnUiThread {
						fragment.context?.alert(error.message)
					}
				}
			}
		}
	}
}