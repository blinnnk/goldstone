package io.goldstone.blockchain.module.home.wallet.walletsettings.walletsettingslist.view

import android.os.Bundle
import android.view.View
import com.blinnnk.extension.orEmptyArray
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.base.BaseRecyclerView
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerFragment
import io.goldstone.blockchain.common.value.SymbolText
import io.goldstone.blockchain.module.home.wallet.walletsettings.walletsettingslist.model.WalletSettingsListModel
import io.goldstone.blockchain.module.home.wallet.walletsettings.walletsettingslist.presenter.WalletSettingsListPresenter

/**
 * @date 25/03/2018 10:15 PM
 * @author KaySaith
 */

class WalletSettingsListFragment : BaseRecyclerFragment<WalletSettingsListPresenter, WalletSettingsListModel>() {

  override val presenter = WalletSettingsListPresenter(this)

  override fun setRecyclerViewAdapter(recyclerView: BaseRecyclerView, asyncData: ArrayList<WalletSettingsListModel>?) {
    recyclerView.adapter = WalletSettingsListAdapter(asyncData.orEmptyArray())
  }

  override fun setSlideUpWithCellHeight() = 50.uiPX()

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    asyncData = arrayListOf(
      WalletSettingsListModel("Check QR Code", ""),
      WalletSettingsListModel("Balance", "192.35 ${SymbolText.usd}"),
      WalletSettingsListModel("Wallet Name", "Kaysaith"),
      WalletSettingsListModel("Password Hint", "······"),
      WalletSettingsListModel("Change Password"),
      WalletSettingsListModel("Export Private Key"),
      WalletSettingsListModel("Export Keystore")
    )

  }

}