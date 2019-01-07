package io.goldstone.blinnnk.module.home.wallet.walletmanagement.walletlist.view

import com.blinnnk.extension.getParentFragment
import io.goldstone.blinnnk.common.base.baserecyclerfragment.BaseRecyclerFragment
import io.goldstone.blinnnk.common.base.baserecyclerfragment.BaseRecyclerView
import io.goldstone.blinnnk.common.language.ProfileText
import io.goldstone.blinnnk.module.home.home.view.MainActivity
import io.goldstone.blinnnk.module.home.profile.profileoverlay.view.ProfileOverlayFragment
import io.goldstone.blinnnk.module.home.wallet.walletmanagement.walletlist.model.WalletListModel
import io.goldstone.blinnnk.module.home.wallet.walletmanagement.walletlist.presenter.WalletListPresenter

/**
 * @date 24/03/2018 8:50 PM
 * @author KaySaith
 */
class WalletListFragment : BaseRecyclerFragment<WalletListPresenter, WalletListModel>() {

	override val pageTitle: String = ProfileText.walletManager
	override val presenter = WalletListPresenter(this)
	override fun setRecyclerViewAdapter(recyclerView: BaseRecyclerView, asyncData: ArrayList<WalletListModel>?) {
		asyncData?.let { it ->
			recyclerView.adapter = WalletListAdapter(it) {
				presenter.switchWallet(it)
			}
		}
	}

	override fun setBackEvent(mainActivity: MainActivity?) {
		getParentFragment<ProfileOverlayFragment> {
			presenter.removeSelfFromActivity()
		}
	}
}