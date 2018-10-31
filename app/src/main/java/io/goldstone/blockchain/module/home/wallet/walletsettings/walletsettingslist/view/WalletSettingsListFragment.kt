package io.goldstone.blockchain.module.home.wallet.walletsettings.walletsettingslist.view

import com.blinnnk.extension.orEmptyArray
import com.blinnnk.extension.preventDuplicateClicks
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerFragment
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerView
import io.goldstone.blockchain.common.language.WalletSettingsText
import io.goldstone.blockchain.module.home.home.view.MainActivity
import io.goldstone.blockchain.module.home.wallet.walletsettings.walletsettingslist.model.WalletSettingsListModel
import io.goldstone.blockchain.module.home.wallet.walletsettings.walletsettingslist.presenter.WalletSettingsListPresenter
import org.jetbrains.anko.sdk27.coroutines.onClick

/**
 * @date 25/03/2018 10:15 PM
 * @author KaySaith
 */
class WalletSettingsListFragment :
	BaseRecyclerFragment<WalletSettingsListPresenter, WalletSettingsListModel>() {

	override val pageTitle: String = "Wallet Settings"
	override val presenter = WalletSettingsListPresenter(this)
	override fun setRecyclerViewAdapter(
		recyclerView: BaseRecyclerView, asyncData: ArrayList<WalletSettingsListModel>?
	) {
		recyclerView.adapter = WalletSettingsListAdapter(asyncData.orEmptyArray()) {
			if (model.title.equals(WalletSettingsText.balance, true)) {
				hasArrow = false
			}
			onClick {
				presenter.showTargetFragment(model.title)
				preventDuplicateClicks()
			}
		}
	}

	override fun setBackEvent(mainActivity: MainActivity?) {
		super.setBackEvent(mainActivity)
		mainActivity?.backEvent = null
	}
}