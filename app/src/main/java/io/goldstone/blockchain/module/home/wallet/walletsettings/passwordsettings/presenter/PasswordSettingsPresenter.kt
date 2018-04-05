package io.goldstone.blockchain.module.home.wallet.walletsettings.passwordsettings.presenter

import android.widget.EditText
import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.crypto.updatePassword
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.presenter.CreateWalletPresenter
import io.goldstone.blockchain.module.home.wallet.walletsettings.passwordsettings.view.PasswordSettingsFragment
import org.jetbrains.anko.toast

/**
 * @date 26/03/2018 9:13 PM
 * @author KaySaith
 */

class PasswordSettingsPresenter(
  override val fragment: PasswordSettingsFragment
  ) : BasePresenter<PasswordSettingsFragment>() {

  fun updatePassword(
    oldPasswordInput: EditText,
    newPasswordInput: EditText,
    repeatPasswordInput: EditText
    ) {

    CreateWalletPresenter.checkInputValue(
      "",
      newPasswordInput.text.toString(),
      repeatPasswordInput.text.toString(),
      true
    ) { password, _ ->
      WalletTable.getCurrentWalletInfo {
        it?.apply {
          fragment.context?.updatePassword(
            address,
            oldPasswordInput.text.toString(),
            password
          ) {
            fragment.context?.toast("Revise the password successfully")
          }
        }
      }
    }
  }

}