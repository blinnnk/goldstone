package io.goldstone.blockchain.module.home.quotation.tradermemory.ramrank.presenter

import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.common.utils.LogUtil
import io.goldstone.blockchain.kernel.network.GoldStoneAPI
import io.goldstone.blockchain.module.home.quotation.tradermemory.ramrank.view.RankFragment
import org.jetbrains.anko.runOnUiThread

/**
 * @date: 2018/9/25.
 * @author: yanglihai
 * @description:
 */
class RankPresenter(override val fragment: RankFragment) : BasePresenter<RankFragment>() {
	override fun onFragmentViewCreated() {
		super.onFragmentViewCreated()
		getRank()
	}
	
	private fun getRank() {
		
		GoldStoneAPI.getEOSRAMRank( {
			LogUtil.error("geteosramrank", it)
		}) {
			GoldStoneAPI.context.runOnUiThread {
				fragment.ramRankView.setData(it)
			}
		}
	}
}