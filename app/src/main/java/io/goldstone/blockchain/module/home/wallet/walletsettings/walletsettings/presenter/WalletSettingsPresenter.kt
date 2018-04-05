package io.goldstone.blockchain.module.home.wallet.walletsettings.walletsettings.presenter

import android.view.View
import com.blinnnk.animation.updateHeightAnimation
import com.blinnnk.extension.*
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.replaceFragmentAndSetArgument
import io.goldstone.blockchain.R
import io.goldstone.blockchain.common.base.baseoverlayfragment.BaseOverlayPresenter
import io.goldstone.blockchain.common.utils.glideImage
import io.goldstone.blockchain.common.value.ContainerID
import io.goldstone.blockchain.common.value.WalletSettingsText
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable
import io.goldstone.blockchain.module.home.wallet.walletsettings.keystoreexport.view.KeystoreExportFragment
import io.goldstone.blockchain.module.home.wallet.walletsettings.passwordsettings.view.PasswordSettingsFragment
import io.goldstone.blockchain.module.home.wallet.walletsettings.privatekeyexport.view.PrivateKeyExportFragment
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

  override fun onFragmentViewCreated() {
    showCurrentWalletInfo()
  }

  fun showTargetFragmentByTitle(title: String) {
    when (title) {
      WalletSettingsText.passwordSettings -> showPasswordSettingsFragment()
      WalletSettingsText.walletNameSettings -> showWalletNameEditorFragment()
      WalletSettingsText.exportPrivateKey -> showPrivateKeyExportFragment()
      WalletSettingsText.exportKeystore -> showKeystoreExportFragment()
      WalletSettingsText.checkQRCode -> showQRCodeFragment()
      "" -> showWalletSettingListFragment()
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
          overlayView.header.apply {
            showBackButton(false)
            showCloseButton(true)
          }
          header?.visibility = View.VISIBLE
        }
      }

      replaceFragmentAndSetArgument<WalletSettingsListFragment>(ContainerID.content) {
        // Send Arguments
      }
    }
  }

  private fun showPrivateKeyExportFragment() {
    fragment.apply {
      setNormalHeaderWithHeight(fragment.context?.getRealScreenHeight().orZero())
      replaceFragmentAndSetArgument<PrivateKeyExportFragment>(ContainerID.content) {
        // Send Arguments
      }
    }
  }

  private fun showKeystoreExportFragment() {
    fragment.apply {
      setNormalHeaderWithHeight(fragment.context?.getRealScreenHeight().orZero())
      replaceFragmentAndSetArgument<KeystoreExportFragment>(ContainerID.content) {
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
      header.showCloseButton(false)
      contentLayout.updateHeightAnimation(contentHeight)
    }
  }

  private fun showCurrentWalletInfo() {
    WalletTable.getCurrentWalletInfo {
      it?.apply {
        fragment.header?.apply {
          walletInfo.apply {
            title.text = name
            subtitle.text = address
          }
          avatarImage.glideImage(R.drawable.avatar)
        }
      }
    }
  }

}