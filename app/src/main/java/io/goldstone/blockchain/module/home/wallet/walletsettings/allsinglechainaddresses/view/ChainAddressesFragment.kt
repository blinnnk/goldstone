package io.goldstone.blockchain.module.home.wallet.walletsettings.allsinglechainaddresses.view

import com.blinnnk.extension.getParentFragment
import com.blinnnk.extension.orEmptyArray
import com.blinnnk.util.clickToCopy
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerFragment
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerView
import io.goldstone.blockchain.common.language.WalletSettingsText
import io.goldstone.blockchain.common.value.ArgumentKey
import io.goldstone.blockchain.crypto.multichain.ChainType
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.Bip44Address
import io.goldstone.blockchain.module.home.home.view.MainActivity
import io.goldstone.blockchain.module.home.wallet.walletsettings.allsinglechainaddresses.presneter.ChainAddressesPresenter
import io.goldstone.blockchain.module.home.wallet.walletsettings.walletsettings.view.WalletSettingsFragment
import org.jetbrains.anko.sdk27.coroutines.onClick

/**
 * @date 2018/7/16 6:07 PM
 * @author KaySaith
 */
class ChainAddressesFragment
	: BaseRecyclerFragment<ChainAddressesPresenter, Bip44Address>() {

	val coinType by lazy { arguments?.getInt(ArgumentKey.coinType)?.let { ChainType(it) } }
	override val pageTitle: String get() = coinType?.getSymbol()?.symbol.orEmpty()
	override val presenter = ChainAddressesPresenter(this)

	var headerView: ChainAddressesHeaderView? = null

	override fun setRecyclerViewAdapter(recyclerView: BaseRecyclerView, asyncData: ArrayList<Bip44Address>?) {
		recyclerView.adapter = ChainAddressesAdapter(
			asyncData.orEmptyArray(),
			{
				headerView = this
			}
		) {
			cell.copyButton.onClick {
				cell.context.clickToCopy(model?.address.orEmpty())
			}
			cell.moreButton.onClick {
				presenter.showMoreDashboard(cell, model ?: Bip44Address())
			}
		}
	}

	override fun onResume() {
		super.onResume()
		asyncData = arrayListOf()
		presenter.setAddAddressEvent()
		presenter.setAddresses()
	}

	override fun setBackEvent(mainActivity: MainActivity?) {
		getParentFragment<WalletSettingsFragment> {
			headerTitle = WalletSettingsText.viewAddresses
			presenter.popFragmentFrom<ChainAddressesFragment>()
		}
	}

	fun setDefaultAddress(address: Bip44Address) {
		headerView?.setDefaultAddress(address) {
			presenter.showMoreDashboard(this, address, false)
		}
	}
}