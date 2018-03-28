package io.goldstone.blockchain.module.common.walletgeneration.createwallet.presenter

import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.common.value.CreateWalletText
import io.goldstone.blockchain.module.common.walletgeneration.agreementfragment.view.AgreementFragment
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.view.CreateWalletFragment
import io.goldstone.blockchain.module.common.walletgeneration.mnemonicbackup.view.MnemonicBackupFragment
import io.goldstone.blockchain.module.common.walletgeneration.walletgeneration.view.WalletGenerationFragment

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

  fun showMnemonicBackupFragment() {
    showTargetFragment<MnemonicBackupFragment, WalletGenerationFragment>(
      CreateWalletText.mnemonicBackUp,
      CreateWalletText.create
    )
  }

}