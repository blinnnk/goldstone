package io.goldstone.blockchain.module.common.walletgeneration.createwallet.presenter

import android.os.Bundle
import android.widget.EditText
import com.blinnnk.extension.isTrue
import com.blinnnk.extension.otherwise
import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.common.utils.UnsafeReasons
import io.goldstone.blockchain.common.utils.checkPasswordInRules
import io.goldstone.blockchain.common.value.ArgumentKey
import io.goldstone.blockchain.common.value.CreateWalletText
import io.goldstone.blockchain.crypto.generateWallet
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

  fun generateWalletWith(passwordInput: EditText, repeatPasswordInput: EditText, isAgree: Boolean) {
    fragment.context?.apply {
      passwordInput.text.checkPasswordInRules { _, reasons ->
        if (reasons == UnsafeReasons.None) {
          (passwordInput.text.toString() == repeatPasswordInput.text.toString()).isTrue {
            isAgree.isTrue {
              generateWallet(passwordInput.text.toString()) { mnemonicCode, address ->
                val arguments = Bundle().apply {
                  putString(ArgumentKey.mnemonicCode, mnemonicCode)
                  putString(ArgumentKey.walletAddress, address)
                }
                showMnemonicBackupFragment(arguments)
              }
            } otherwise {
              toast(CreateWalletText.agreeRemind)
            }
          } otherwise {
            toast(CreateWalletText.repeatPasswordRemind)
          }
        } else {
          toast(reasons.info)
        }
      }
    }
  }

  private fun showMnemonicBackupFragment(arguments: Bundle) {
    showTargetFragment<MnemonicBackupFragment, WalletGenerationFragment>(
      CreateWalletText.mnemonicBackUp,
      CreateWalletText.create,
      arguments
    )
  }

}