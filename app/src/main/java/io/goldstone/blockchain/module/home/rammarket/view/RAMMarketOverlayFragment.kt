package io.goldstone.blockchain.module.home.rammarket.view

import android.view.ViewGroup
import com.blinnnk.util.addFragmentAndSetArgument
import io.goldstone.blockchain.common.Language.EOSRAMExchangeText
import io.goldstone.blockchain.common.base.baseoverlayfragment.BaseOverlayFragment
import io.goldstone.blockchain.common.value.ContainerID
import io.goldstone.blockchain.module.home.rammarket.presenter.RAMMarketPresenter

/**
 * @date: 2018/10/29.
 * @author: yangLiHai
 * @description: 内存交易所的fragment
 */
class RAMMarketOverlayFragment : BaseOverlayFragment<RAMMarketPresenter>() {
	override val presenter: RAMMarketPresenter = RAMMarketPresenter(this)
	
	override fun ViewGroup.initView() {
		headerTitle = EOSRAMExchangeText.ramExchange
		addFragmentAndSetArgument<RAMMarketDetailFragment>(ContainerID.content) { }
	}
	
}