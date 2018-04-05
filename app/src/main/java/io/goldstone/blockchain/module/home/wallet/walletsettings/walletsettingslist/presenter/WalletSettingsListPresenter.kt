package io.goldstone.blockchain.module.home.wallet.walletsettings.walletsettingslist.presenter

import com.blinnnk.util.getParentFragment
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerPresenter
import io.goldstone.blockchain.common.value.SymbolText
import io.goldstone.blockchain.common.value.WalletSettingsText
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable
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

  override fun updateData(asyncData: ArrayList<WalletSettingsListModel>?) {
    WalletTable.getCurrentWalletInfo {
      val balanceText = it?.balance.toString() + SymbolText.usd
      fragment.asyncData = arrayListOf(
        WalletSettingsListModel(WalletSettingsText.checkQRCode),
        WalletSettingsListModel(WalletSettingsText.balance, balanceText),
        WalletSettingsListModel(WalletSettingsText.walletName, it?.name.orEmpty()),
        WalletSettingsListModel(WalletSettingsText.hint, "······"),
        WalletSettingsListModel(WalletSettingsText.passwordSettings),
        WalletSettingsListModel(WalletSettingsText.exportPrivateKey),
        WalletSettingsListModel(WalletSettingsText.exportKeystore),
        WalletSettingsListModel(WalletSettingsText.delete)
      )
    }
  }

  fun showTargetFragment(title: String) {
    fragment.getParentFragment<WalletSettingsFragment>()?.apply {
      headerTitle = title
      presenter.showTargetFragmentByTitle(title)
    }
  }

}