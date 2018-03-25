package io.goldstone.blockchain.module.home.wallet.walletsettings.walletsettings.view

import android.view.ViewGroup
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.addFragmentAndSetArgument
import io.goldstone.blockchain.common.base.baseoverlayfragment.BaseOverlayFragment
import io.goldstone.blockchain.common.value.ContainerID
import io.goldstone.blockchain.module.home.wallet.walletsettings.walletsettings.presenter.WalletSettingsPresenter
import io.goldstone.blockchain.module.home.wallet.walletsettings.walletsettingslist.view.WalletSettingsListFragment

/**
 * @date 25/03/2018 6:30 PM
 * @author KaySaith
 */

class WalletSettingsFragment : BaseOverlayFragment<WalletSettingsPresenter>() {

  private val header by lazy { WalletSettingsHeader(context!!) }

  override val presenter = WalletSettingsPresenter(this)

  override fun ViewGroup.initView() {
    customHeader = {
      layoutParams.height = 200.uiPX()
      addView(header)
    }

    addFragmentAndSetArgument<WalletSettingsListFragment>(ContainerID.content) {
      // Send Arguments
    }

  }

}