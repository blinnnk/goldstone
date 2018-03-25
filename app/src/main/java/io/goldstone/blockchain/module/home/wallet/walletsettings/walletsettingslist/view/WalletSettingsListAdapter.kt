package io.goldstone.blockchain.module.home.wallet.walletsettings.walletsettingslist.view

import android.content.Context
import com.blinnnk.base.HoneyBaseAdapter
import io.goldstone.blockchain.common.value.WalletSettingsText
import io.goldstone.blockchain.module.home.wallet.walletsettings.walletsettingslist.model.WalletSettingsListModel

/**
 * @date 25/03/2018 10:16 PM
 * @author KaySaith
 */

class WalletSettingsListAdapter(
  override val dataSet: ArrayList<WalletSettingsListModel>
  ) : HoneyBaseAdapter<WalletSettingsListModel, WalletSettingsListCell>() {

  override fun generateCell(context: Context) = WalletSettingsListCell(context)

  override fun WalletSettingsListCell.bindCell(data: WalletSettingsListModel, position: Int) {
    model = data
  }


}