package io.goldstone.blockchain.module.home.rammarket.module.ramquotation.ramstatistics.presenter

import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.kernel.network.eos.EOSAPI
import io.goldstone.blockchain.module.home.rammarket.module.ramquotation.ramstatistics.view.RAMStatisticsFragment

/**
 * @date: 2018/11/6.
 * @author: yanglihai
 * @description:
 */
class RAMStatisticsPresenter(override val fragment: RAMStatisticsFragment)
	: BasePresenter<RAMStatisticsFragment>() {
	
	override fun onFragmentViewCreated() {
		super.onFragmentViewCreated()
		
		
	}
	
	private fun getGlobalRAMData() {
		EOSAPI.getGlobalInformation { globalModel, requestError ->
			if (globalModel != null && requestError.isNone()) {
			
			} else {
			
			}
		}
	}
	
	
	
}