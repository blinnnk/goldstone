package io.goldstone.blockchain.module.home.wallet.walletsettings.walletsettingslist.presenter

import android.text.InputType
import android.widget.EditText
import com.blinnnk.extension.jump
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.getParentFragment
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerPresenter
import io.goldstone.blockchain.common.value.CommonText
import io.goldstone.blockchain.common.value.Spectrum
import io.goldstone.blockchain.common.value.SymbolText
import io.goldstone.blockchain.common.value.WalletSettingsText
import io.goldstone.blockchain.crypto.deleteAccount
import io.goldstone.blockchain.kernel.commonmodel.MyTokenTable
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable
import io.goldstone.blockchain.module.entrance.splash.view.SplashActivity
import io.goldstone.blockchain.module.home.wallet.walletsettings.walletsettings.view.WalletSettingsFragment
import io.goldstone.blockchain.module.home.wallet.walletsettings.walletsettingslist.model.WalletSettingsListModel
import io.goldstone.blockchain.module.home.wallet.walletsettings.walletsettingslist.view.WalletSettingsListFragment
import org.jetbrains.anko.*

/**
 * @date 25/03/2018 10:15 PM
 * @author KaySaith
 */

class  WalletSettingsListPresenter(
  override val fragment: WalletSettingsListFragment
) : BaseRecyclerPresenter<WalletSettingsListFragment, WalletSettingsListModel>() {

  override fun updateData(asyncData: ArrayList<WalletSettingsListModel>?) {
    WalletTable.getCurrentWalletInfo {
      val balanceText = it.balance.toString() + SymbolText.usd
      fragment.asyncData = arrayListOf(
        WalletSettingsListModel(WalletSettingsText.checkQRCode),
        WalletSettingsListModel(WalletSettingsText.balance, balanceText),
        WalletSettingsListModel(WalletSettingsText.walletName, it.name),
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

  /** 分别从数据库和 `Keystore` 文件内删除掉用户钱包的所有数据 */
  fun deleteWallet() {

    var input: EditText? = null

    fragment.context?.apply {
      alert(
        WalletSettingsText.deleteInfoSubtitle,
        WalletSettingsText.deleteInfoTitle) {

        customView {
          verticalLayout {
            lparams {
              padding = 20.uiPX()
            }

            input = editText {
              inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
              hint = CommonText.enterPassword
              hintTextColor = Spectrum.opacity1White
            }
          }
        }
        yesButton {
          deleteWalletData(input?.text.toString())
        }
        noButton {  }
      }.show()
    }
  }

  private fun deleteWalletData(password: String) {
    // get current wallet address
    WalletTable.getCurrentWalletInfo {
      // delete `keystore` file
      fragment.context?.deleteAccount(it.address, password) {
        // delete all records of this `address` in `myTokenTable`
        MyTokenTable.deleteByAddress(it.address) {
          // delete wallet record in `walletTable`
          WalletTable.deleteCurrentWallet {
            fragment.activity?.jump<SplashActivity>()
          }
        }
      }
    }
  }

}