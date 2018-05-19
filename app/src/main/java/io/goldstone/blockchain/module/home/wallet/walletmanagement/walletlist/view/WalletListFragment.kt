package io.goldstone.blockchain.module.home.wallet.walletmanagement.walletlist.view

import com.blinnnk.extension.getParentFragment
import com.blinnnk.extension.preventDuplicateClicks
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.base.BaseRecyclerView
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerFragment
import io.goldstone.blockchain.module.home.home.view.MainActivity
import io.goldstone.blockchain.module.home.wallet.walletmanagement.walletlist.model.WalletListModel
import io.goldstone.blockchain.module.home.wallet.walletmanagement.walletlist.presenter.WalletListPresenter
import io.goldstone.blockchain.module.home.wallet.walletmanagement.walletmanagement.view.WalletManagementFragment
import org.jetbrains.anko.sdk25.coroutines.onClick

/**
 * @date 24/03/2018 8:50 PM
 * @author KaySaith
 */

class WalletListFragment : BaseRecyclerFragment<WalletListPresenter, WalletListModel>() {

	override val presenter = WalletListPresenter(this)

	override fun setRecyclerViewAdapter(
		recyclerView: BaseRecyclerView,
		asyncData: ArrayList<WalletListModel>?
	) {
		asyncData?.let {
			recyclerView.adapter = WalletListAdapter(it) {
				onClick {
					presenter.switchWallet(model.address)
					preventDuplicateClicks()
				}
			}
		}
	}

	override fun setSlideUpWithCellHeight() =
		75.uiPX()

	override fun onHiddenChanged(hidden: Boolean) {
		super.onHiddenChanged(hidden)
		if (!hidden) {
			getParentFragment<WalletManagementFragment> {
				overlayView.header.showAddButton(true) {
					presenter.showWalletAddingMethodFragment()
				}
			}
		}
	}

	override fun setBackEvent(mainActivity: MainActivity?) {
		super.setBackEvent(mainActivity)
		mainActivity?.backEvent = null
	}

}