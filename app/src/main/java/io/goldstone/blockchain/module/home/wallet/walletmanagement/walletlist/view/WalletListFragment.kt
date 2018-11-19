package io.goldstone.blockchain.module.home.wallet.walletmanagement.walletlist.view

import com.blinnnk.extension.getParentFragment
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerFragment
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerView
import io.goldstone.blockchain.common.language.ProfileText
import io.goldstone.blockchain.common.utils.isEmptyThen
import io.goldstone.blockchain.module.home.home.view.MainActivity
import io.goldstone.blockchain.module.home.profile.profileoverlay.view.ProfileOverlayFragment
import io.goldstone.blockchain.module.home.wallet.walletmanagement.walletlist.model.WalletListModel
import io.goldstone.blockchain.module.home.wallet.walletmanagement.walletlist.presenter.WalletListPresenter

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
				setClickEvent {
					presenter.switchWallet(model.address)
				}
			}
		}
	}

	override fun setBackEvent(mainActivity: MainActivity?) {
		getParentFragment<ProfileOverlayFragment> {
			presenter.removeSelfFromActivity()
		}
	}
}