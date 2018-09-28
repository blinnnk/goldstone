package io.goldstone.blockchain.module.home.wallet.walletsettings.allsinglechainaddresses.view

import android.os.Bundle
import android.view.View
import com.blinnnk.extension.getParentFragment
import com.blinnnk.extension.orEmptyArray
import com.blinnnk.util.clickToCopy
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerFragment
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerView
import io.goldstone.blockchain.common.language.WalletSettingsText
import io.goldstone.blockchain.common.value.ArgumentKey
import io.goldstone.blockchain.crypto.multichain.ChainType
import io.goldstone.blockchain.module.home.home.view.MainActivity
import io.goldstone.blockchain.module.home.wallet.walletsettings.allsinglechainaddresses.presneter.ChainAddressesPresenter
import io.goldstone.blockchain.module.home.wallet.walletsettings.walletsettings.view.WalletSettingsFragment
import org.jetbrains.anko.sdk25.coroutines.onClick

/**
 * @date 2018/7/16 6:07 PM
 * @author KaySaith
 */
class ChainAddressesFragment
	: BaseRecyclerFragment<ChainAddressesPresenter, Pair<String, String>>() {

	val coinType by lazy { arguments?.getInt(ArgumentKey.coinType)?.let { ChainType(it) } }
	override val pageTitle: String get() = coinType?.getSymbol()?.symbol.orEmpty()
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
				coinType?.apply {
					presenter.showMoreDashboard(cell, model.first, this)
				}
			}
		}
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		presenter.updateAddAddressEvent()
	}

	override fun setBackEvent(mainActivity: MainActivity?) {
		getParentFragment<WalletSettingsFragment> {
			headerTitle = WalletSettingsText.viewAddresses
			presenter.popFragmentFrom<ChainAddressesFragment>()
		}
	}
}