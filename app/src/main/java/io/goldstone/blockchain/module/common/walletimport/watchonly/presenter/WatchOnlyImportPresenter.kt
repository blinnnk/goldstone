package io.goldstone.blockchain.module.common.walletimport.watchonly.presenter

import android.widget.EditText
import com.blinnnk.extension.isTrue
import com.blinnnk.extension.jump
import com.blinnnk.extension.otherwise
import com.blinnnk.util.coroutinesTask
import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.presenter.CreateWalletPresenter
import io.goldstone.blockchain.module.common.walletimport.watchonly.view.WatchOnlyImportFragment
import io.goldstone.blockchain.module.entrance.splash.view.SplashActivity
import io.goldstone.blockchain.module.home.home.view.MainActivity
import org.jetbrains.anko.alert
import org.jetbrains.anko.appcompat.v7.Appcompat
import org.web3j.crypto.WalletUtils

/**
 * @date 23/03/2018 2:16 AM
 * @author KaySaith
 */

class WatchOnlyImportPresenter(
  override val fragment: WatchOnlyImportFragment
) : BasePresenter<WatchOnlyImportFragment>() {

  fun importWatchOnlyWallet(addressInput: EditText, nameInput: EditText) {
    val address = addressInput.text.toString()
    WalletUtils.isValidAddress(address).isTrue {
      val name =
        if  (nameInput.text.toString().isEmpty()) "Wallet"
        else nameInput.text.toString()
      coroutinesTask({
        WalletTable.insert(WalletTable(0, name, address, true, true))
        CreateWalletPresenter.generateMyTokenInfo(address)
      }) {
        fragment.activity?.jump<SplashActivity>()
      }
    } otherwise {
      fragment.context?.alert(Appcompat, "Address Content Is Incorrect")?.show()
    }

  }
}