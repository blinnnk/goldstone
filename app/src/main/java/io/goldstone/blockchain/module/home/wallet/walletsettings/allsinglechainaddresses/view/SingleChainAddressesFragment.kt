package io.goldstone.blockchain.module.home.wallet.walletsettings.allsinglechainaddresses.view

import com.blinnnk.extension.orEmptyArray
import io.goldstone.blockchain.common.base.BaseRecyclerView
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerFragment
import io.goldstone.blockchain.module.home.wallet.walletsettings.allsinglechainaddresses.presneter.SingleChainAddressesPresenter

/**
 * @date 2018/7/16 6:07 PM
 * @author KaySaith
 */
class SingleChainAddressesFragment
	: BaseRecyclerFragment<SingleChainAddressesPresenter, Pair<String, String>>() {
	
	override val presenter = SingleChainAddressesPresenter(this)
	
	override fun setRecyclerViewAdapter(
		recyclerView: BaseRecyclerView,
		asyncData: ArrayList<Pair<String, String>>?
	) {
		recyclerView.adapter = ChainAddressesAdapter(asyncData.orEmptyArray())
	}
}