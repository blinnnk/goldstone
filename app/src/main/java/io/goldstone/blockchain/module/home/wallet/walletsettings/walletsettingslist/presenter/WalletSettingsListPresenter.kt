package io.goldstone.blockchain.module.home.wallet.walletsettings.walletsettingslist.presenter

import com.blinnnk.util.getParentFragment
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerPresenter
import io.goldstone.blockchain.common.value.ArgumentKey
import io.goldstone.blockchain.module.home.wallet.walletsettings.walletsettings.view.WalletSettingsFragment
import io.goldstone.blockchain.module.home.wallet.walletsettings.walletsettingslist.model.WalletSettingsListModel
import io.goldstone.blockchain.module.home.wallet.walletsettings.walletsettingslist.view.WalletSettingsListFragment

/**
 * @date 25/03/2018 10:15 PM
 * @author KaySaith
 */

class  WalletSettingsListPresenter(
  override val fragment: WalletSettingsListFragment
) : BaseRecyclerPresenter<WalletSettingsListFragment, WalletSettingsListModel>() {

  fun showTargetFragment(title: String) {
    fragment.getParentFragment<WalletSettingsFragment>()?.apply {
      headerTitle = title
      presenter.showTargetFragmentByTitle(title)
    }
  }

}