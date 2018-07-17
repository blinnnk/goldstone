package io.goldstone.blockchain.module.home.wallet.walletsettings.allsinglechainaddresses.view

import android.os.Bundle
import android.view.View
import com.blinnnk.extension.orEmptyArray
import com.blinnnk.extension.orZero
import com.blinnnk.util.clickToCopy
import io.goldstone.blockchain.common.base.BaseRecyclerView
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerFragment
import io.goldstone.blockchain.common.value.ArgumentKey
import io.goldstone.blockchain.module.home.wallet.walletsettings.allsinglechainaddresses.presneter.ChainAddressesPresenter
import org.jetbrains.anko.sdk25.coroutines.onClick

/**
 * @date 2018/7/16 6:07 PM
 * @author KaySaith
 */
class ChainAddressesFragment
	: BaseRecyclerFragment<ChainAddressesPresenter, Pair<String, String>>() {
	
	val coinType by lazy {
		arguments?.getInt(ArgumentKey.coinType)
	}
	override val presenter = ChainAddressesPresenter(this)
	
	override fun setRecyclerViewAdapter(
		recyclerView: BaseRecyclerView,
		asyncData: ArrayList<Pair<String, String>>?
	) {
		recyclerView.adapter = ChainAddressesAdapter(asyncData.orEmptyArray()) {
			cell.copyButton.onClick {
				cell.context.clickToCopy(model.first)
			}
			cell.moreButton.onClick {
				presenter.showMoreDashboard(cell, model.first, coinType.orZero())
			}
		}
	}
	
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		presenter.updateAddAddressEvent()
	}
}