package io.goldstone.blockchain.module.home.wallet.walletsettings.walletsettings.presenter

import android.view.View
import com.blinnnk.animation.updateHeightAnimation
import com.blinnnk.extension.*
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.replaceFragmentAndSetArgument
import io.goldstone.blockchain.common.base.baseoverlayfragment.BaseOverlayPresenter
import io.goldstone.blockchain.common.value.ContainerID
import io.goldstone.blockchain.common.value.WalletSettingsText
import io.goldstone.blockchain.module.home.wallet.walletsettings.passwordsettings.view.PasswordSettingsFragment
import io.goldstone.blockchain.module.home.wallet.walletsettings.qrcodefragment.view.QRCodeFragment
import io.goldstone.blockchain.module.home.wallet.walletsettings.walletnameeditor.view.WalletNameEditorFragment
import io.goldstone.blockchain.module.home.wallet.walletsettings.walletsettings.view.WalletSettingsFragment
import io.goldstone.blockchain.module.home.wallet.walletsettings.walletsettings.view.WalletSettingsHeader
import io.goldstone.blockchain.module.home.wallet.walletsettings.walletsettingslist.view.WalletSettingsListFragment

/**
 * @date 25/03/2018 6:30 PM
 * @author KaySaith
 */

class WalletSettingsPresenter(
  override val fragment: WalletSettingsFragment
) : BaseOverlayPresenter<WalletSettingsFragment>() {

  fun showTargetFragmentByTitle(title: String) {
    when (title) {
      WalletSettingsText.passwordSettings -> showPasswordSettingsFragment()
      WalletSettingsText.walletNameSettings -> showWalletNameEditorFragment()
      WalletSettingsText.checkQRCode -> showQRCodeFragment()
      else -> showWalletSettingListFragment()
    }
  }

  private fun showWalletSettingListFragment() {
    fragment.apply {

      customHeader = {
        layoutParams.height = 200.uiPX()
        header.isNull().isTrue {
          header = WalletSettingsHeader(context)
          addView(header)
        } otherwise {
          overlayView.header.showBackButton(false)
          header?.visibility = View.VISIBLE
        }
      }

      replaceFragmentAndSetArgument<WalletSettingsListFragment>(ContainerID.content) {
        // Send Arguments
      }
    }
  }

  private fun showQRCodeFragment() {
    fragment.apply {
      setNormalHeaderWithHeight(fragment.context?.getRealScreenHeight().orZero())
      replaceFragmentAndSetArgument<QRCodeFragment>(ContainerID.content) {
        // Send Arguments
      }
    }
  }

  private fun showWalletNameEditorFragment() {
    fragment.apply {
      setNormalHeaderWithHeight(300.uiPX())
      replaceFragmentAndSetArgument<WalletNameEditorFragment>(ContainerID.content) {
        // Send Arguments
      }
    }
  }

  private fun showPasswordSettingsFragment() {
    fragment.apply {
      setNormalHeaderWithHeight(420.uiPX())
      replaceFragmentAndSetArgument<PasswordSettingsFragment>(ContainerID.content) {
        // Send Arguments
      }
    }
  }

  private fun WalletSettingsFragment.setNormalHeaderWithHeight(contentHeight: Int) {
    recoveryOverlayHeader()
    header?.visibility = View.GONE
    overlayView.apply {
      header.showBackButton(true) { showWalletSettingListFragment() }
      contentLayout.updateHeightAnimation(contentHeight)
    }
  }

}