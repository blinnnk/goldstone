package io.goldstone.blockchain.module.home.wallet.walletsettings.walletsettingslist.view

import com.blinnnk.extension.orEmptyArray
import com.blinnnk.extension.preventDuplicateClicks
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.base.BaseRecyclerView
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerFragment
import io.goldstone.blockchain.common.value.WalletSettingsText
import io.goldstone.blockchain.module.home.wallet.walletsettings.walletsettingslist.model.WalletSettingsListModel
import io.goldstone.blockchain.module.home.wallet.walletsettings.walletsettingslist.presenter.WalletSettingsListPresenter
import org.jetbrains.anko.sdk25.coroutines.onClick

/**
 * @date 25/03/2018 10:15 PM
 * @author KaySaith
 */

class WalletSettingsListFragment :
	BaseRecyclerFragment<WalletSettingsListPresenter, WalletSettingsListModel>() {

	override val presenter = WalletSettingsListPresenter(this)

	override fun setRecyclerViewAdapter(
		recyclerView: BaseRecyclerView,
		asyncData: ArrayList<WalletSettingsListModel>?
	) {
		recyclerView.adapter = WalletSettingsListAdapter(asyncData.orEmptyArray()) {
			// 余额的 `Cell` 不显示箭头
			if(model.title == WalletSettingsText.balance) {
				hasArrow = false
			}
			onClick {
				if (model.title == WalletSettingsText.delete) {
					presenter.deleteWallet()
				} else {
					presenter.showTargetFragment(model.title)
				}
				preventDuplicateClicks()
			}
		}
	}

	override fun setSlideUpWithCellHeight() = 50.uiPX()


}