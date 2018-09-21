package io.goldstone.blockchain.module.home.quotation.tradermemory.tradermemorydetail.present

import io.goldstone.blockchain.R
import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.module.home.quotation.tradermemory.ramtrend.model.RAMMarketHeaderModel
import io.goldstone.blockchain.module.home.quotation.tradermemory.tradermemorydetail.model.RAMMarketModel
import io.goldstone.blockchain.common.language.ChainText
import io.goldstone.blockchain.kernel.commonmodel.AppConfigTable
import io.goldstone.blockchain.module.home.profile.chain.chainselection.model.ChainSelectionModel
import io.goldstone.blockchain.module.home.quotation.tradermemory.tradermemorydetail.view.TraderMemoryDetailFragment
import org.jetbrains.anko.toast

/**
 * @date 18/09/2018 6:36 PM
 * @author wcx
 */
class TraderMemoryDetailPresenter(override val fragment: TraderMemoryDetailFragment)
	: BasePresenter<TraderMemoryDetailFragment>() {

	fun merchandiseRAM() {

	}

	fun getIsMainnet() {}

	var ramMarketModel = RAMMarketModel(RAMMarketHeaderModel())

}