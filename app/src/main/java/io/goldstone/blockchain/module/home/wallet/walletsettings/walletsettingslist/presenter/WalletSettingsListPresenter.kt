package io.goldstone.blockchain.module.home.wallet.walletsettings.walletsettingslist.presenter

import android.support.v4.app.Fragment
import com.blinnnk.extension.isFalse
import com.blinnnk.extension.jump
import com.blinnnk.util.getParentFragment
import io.goldstone.blockchain.GoldStoneApp
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerPresenter
import io.goldstone.blockchain.common.utils.alert
import io.goldstone.blockchain.common.utils.getMainActivity
import io.goldstone.blockchain.common.utils.showAlertView
import io.goldstone.blockchain.common.value.WalletSettingsText
import io.goldstone.blockchain.crypto.deleteAccount
import io.goldstone.blockchain.crypto.formatCurrency
import io.goldstone.blockchain.kernel.commonmodel.MyTokenTable
import io.goldstone.blockchain.kernel.commonmodel.TransactionTable
import io.goldstone.blockchain.kernel.receiver.XinGePushReceiver
import io.goldstone.blockchain.module.common.tokendetail.tokendetail.model.TokenBalanceTable
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable
import io.goldstone.blockchain.module.entrance.splash.view.SplashActivity
import io.goldstone.blockchain.module.home.wallet.walletsettings.walletsettings.view.WalletSettingsFragment
import io.goldstone.blockchain.module.home.wallet.walletsettings.walletsettingslist.model.WalletSettingsListModel
import io.goldstone.blockchain.module.home.wallet.walletsettings.walletsettingslist.view.WalletSettingsListFragment

/**
 * @date 25/03/2018 10:15 PM
 * @author KaySaith
 */

class WalletSettingsListPresenter(
  override val fragment: WalletSettingsListFragment
) : BaseRecyclerPresenter<WalletSettingsListFragment, WalletSettingsListModel>() {

  override fun updateData() {
    val balanceText = WalletTable.current.balance?.formatCurrency() + " (${GoldStoneApp.currencyCode})"
    fragment.asyncData = arrayListOf(
      WalletSettingsListModel(WalletSettingsText.checkQRCode),
      WalletSettingsListModel(WalletSettingsText.balance, balanceText),
      WalletSettingsListModel(WalletSettingsText.walletName, WalletTable.current.name),
      WalletSettingsListModel(WalletSettingsText.hint, "······"),
      WalletSettingsListModel(WalletSettingsText.passwordSettings),
      WalletSettingsListModel(WalletSettingsText.exportPrivateKey),
      WalletSettingsListModel(WalletSettingsText.exportKeystore),
      WalletSettingsListModel(WalletSettingsText.delete)
    )
  }

  fun showTargetFragment(title: String) {
    fragment.getParentFragment<WalletSettingsFragment>()?.apply {
      headerTitle = title
      presenter.showTargetFragmentByTitle(title)
    }
  }

  /** 分别从数据库和 `Keystore` 文件内删除掉用户钱包的所有数据 */
  fun deleteWallet() {
    fragment.context?.showAlertView(
      WalletSettingsText.deleteInfoTitle,
      WalletSettingsText.deleteInfoSubtitle,
      !WalletTable.current.isWatchOnly
    ) {
      deleteWalletData(it?.text.toString())
    }
  }

  private fun deleteWalletData(password: String) {
    fragment.getMainActivity()?.showLoadingView()
    // get current wallet address
    WalletTable.current.apply {
      if (isWatchOnly) {
        deleteWatchOnlyWallet(address)
      } else {
        fragment.deleteRoutineWallet(address, password)
      }
    }
  }

  private fun Fragment.deleteRoutineWallet(address: String, password: String) {
    // delete `keystore` file
    context?.deleteAccount(address, password) {
      it.isFalse {
        fragment.context?.alert("Wrong Password")
        getMainActivity()?.removeLoadingView()
        return@deleteAccount
      }
      // delete all records of this `address` in `myTokenTable`
      MyTokenTable.deleteByAddress(address) {
        TransactionTable.deleteByAddress(address) {
          TokenBalanceTable.deleteByAddress(address) {
            // delete wallet record in `walletTable`
            WalletTable.deleteCurrentWallet {
              // 删除 `push` 监听包地址不再监听用户删除的钱包地址
              XinGePushReceiver.registerWalletAddressForPush()
              activity?.jump<SplashActivity>()
            }
          }
        }
      }
    }
  }

  private fun deleteWatchOnlyWallet(address: String) {
    MyTokenTable.deleteByAddress(address) {
      TransactionTable.deleteByAddress(address) {
        TokenBalanceTable.deleteByAddress(address) {
          WalletTable.deleteCurrentWallet {
            fragment.getMainActivity()?.removeLoadingView()
            fragment.activity?.jump<SplashActivity>()
          }
        }
      }
    }
  }

}