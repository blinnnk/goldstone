package io.goldstone.blockchain.module.home.quotation.tradermemory.tradermemorydetail.present

import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.module.home.quotation.tradermemory.ramtrend.model.RAMMarketHeaderModel
import io.goldstone.blockchain.module.home.quotation.tradermemory.tradermemorydetail.model.RAMMarketModel
import io.goldstone.blockchain.module.home.quotation.tradermemory.tradermemorydetail.view.TraderMemoryDetailFragment

/**
 * @date 18/09/2018 6:36 PM
 * @author wcx
 */
class TraderMemoryDetailPresenter(override val fragment: TraderMemoryDetailFragment)
	: BasePresenter<TraderMemoryDetailFragment>() {

	var ramMarketModel = RAMMarketModel(RAMMarketHeaderModel())
	
	fun sendRAM() {

	}
}