package io.goldstone.blinnnk.module.home.wallet.walletsettings.chainaddresses.view

import android.os.Bundle
import android.view.View
import com.blinnnk.extension.getParentFragment
import com.blinnnk.extension.orEmptyArray
import com.blinnnk.util.clickToCopy
import io.goldstone.blinnnk.common.base.baserecyclerfragment.BaseRecyclerFragment
import io.goldstone.blinnnk.common.base.baserecyclerfragment.BaseRecyclerView
import io.goldstone.blinnnk.common.value.ArgumentKey
import io.goldstone.blinnnk.common.value.UMengEvent
import io.goldstone.blinnnk.crypto.multichain.ChainType
import io.goldstone.blinnnk.module.common.walletgeneration.createwallet.model.Bip44Address
import io.goldstone.blinnnk.module.home.home.view.MainActivity
import io.goldstone.blinnnk.module.home.wallet.walletsettings.chainaddresses.presneter.ChainAddressesPresenter
import io.goldstone.blinnnk.module.home.wallet.walletsettings.walletsettings.view.WalletSettingsFragment
import org.jetbrains.anko.sdk27.coroutines.onClick

/**
 * @date 2018/7/16 6:07 PM
 * @author KaySaith
 */
class ChainAddressesFragment
	: BaseRecyclerFragment<ChainAddressesPresenter, Bip44Address>() {

	val coinType by lazy {
		arguments?.getInt(ArgumentKey.coinType)?.let { ChainType(it) }
	}
	override val pageTitle: String get() = coinType?.getSymbol()?.symbol.orEmpty()
	override val presenter = ChainAddressesPresenter(this)

	private var headerView: ChainAddressesHeaderView? = null

	override fun setRecyclerViewAdapter(recyclerView: BaseRecyclerView, asyncData: ArrayList<Bip44Address>?) {
		recyclerView.adapter = ChainAddressesAdapter(
			asyncData.orEmptyArray(),
			{
				headerView = this
			}
		) {
			cell.copyButton.onClick {
				cell.context.clickToCopy(model?.address.orEmpty())
				UMengEvent.add(context, UMengEvent.Click.Common.copyAddress, UMengEvent.Page.allAddressesOfSingleChain)
			}
			cell.moreButton.onClick {
				presenter.showMoreDashboard(model ?: Bip44Address())
				UMengEvent.add(context, UMengEvent.Click.WalletDetail.more, UMengEvent.Page.allAddressesOfSingleChain)
			}
		}
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		asyncData = arrayListOf()
		presenter.setAddAddressEvent()
		presenter.setAddresses()
	}

	override fun setBackEvent(mainActivity: MainActivity?) {
		getParentFragment<WalletSettingsFragment> {
			presenter.popFragmentFrom<ChainAddressesFragment>(false)
		}
	}

	fun setDefaultAddress(address: Bip44Address) {
		headerView?.setDefaultAddress(address) {
			presenter.showMoreDashboard(address, false)
		}
	}
}