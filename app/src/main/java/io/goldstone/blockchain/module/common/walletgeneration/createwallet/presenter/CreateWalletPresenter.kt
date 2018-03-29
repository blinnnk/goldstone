package io.goldstone.blockchain.module.common.walletgeneration.createwallet.presenter

import android.widget.EditText
import com.blinnnk.extension.isTrue
import com.blinnnk.extension.otherwise
import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.common.utils.UnsafeReasons
import io.goldstone.blockchain.common.utils.checkPasswordInRules
import io.goldstone.blockchain.common.value.CreateWalletText
import io.goldstone.blockchain.module.common.walletgeneration.agreementfragment.view.AgreementFragment
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.view.CreateWalletFragment
import io.goldstone.blockchain.module.common.walletgeneration.mnemonicbackup.view.MnemonicBackupFragment
import io.goldstone.blockchain.module.common.walletgeneration.walletgeneration.view.WalletGenerationFragment
import org.jetbrains.anko.support.v4.toast
import org.jetbrains.anko.toast

/**
 * @date 22/03/2018 2:46 AM
 * @author KaySaith
 */

class CreateWalletPresenter(
  override val fragment: CreateWalletFragment
) : BasePresenter<CreateWalletFragment>() {

  fun showAgreementFragment() {
    showTargetFragment<AgreementFragment, WalletGenerationFragment>(
      CreateWalletText.agreement,
      CreateWalletText.mnemonicBackUp
    )
  }

  fun generateWalletWith(passwordInput: EditText, repeatPasswordInput: EditText) {
    passwordInput.text.checkPasswordInRules { safeLevel, reasons ->
      fragment.context?.toast(safeLevel.info)
      if (reasons == UnsafeReasons.None) {
        (passwordInput.text.toString() == repeatPasswordInput.text.toString()).isTrue {
          showMnemonicBackupFragment()
        } otherwise {
          fragment.toast("repeat password must be the same as password")
        }
      } else {
        fragment.toast(reasons.info)
      }
    }
  }

  private fun showMnemonicBackupFragment() {
    showTargetFragment<MnemonicBackupFragment, WalletGenerationFragment>(
      CreateWalletText.mnemonicBackUp,
      CreateWalletText.create
    )
  }

}