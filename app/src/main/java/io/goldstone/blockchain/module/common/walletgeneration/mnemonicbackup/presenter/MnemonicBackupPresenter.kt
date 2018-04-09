package io.goldstone.blockchain.module.common.walletgeneration.mnemonicbackup.presenter

import android.os.Bundle
import com.blinnnk.util.getParentFragment
import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.common.value.CreateWalletText
import io.goldstone.blockchain.module.common.walletgeneration.mnemonicbackup.view.MnemonicBackupFragment
import io.goldstone.blockchain.module.common.walletgeneration.mnemonicconfirmation.view.MnemonicConfirmationFragment
import io.goldstone.blockchain.module.common.walletgeneration.walletgeneration.view.WalletGenerationFragment
import org.jetbrains.anko.sdk25.coroutines.onClick

/**
 * @date 22/03/2018 9:32 PM
 * @author KaySaith
 */

class MnemonicBackupPresenter(
  override val fragment: MnemonicBackupFragment
  ) : BasePresenter<MnemonicBackupFragment>() {

  fun goToMnemonicConfirmation(arguments: Bundle) {
    showTargetFragment<MnemonicConfirmationFragment, WalletGenerationFragment>(
      CreateWalletText.mnemonicConfirmation,
      CreateWalletText.mnemonicBackUp,
      arguments
    )
  }

  override fun onFragmentShowFromHidden() {
    super.onFragmentShowFromHidden()
    fragment.getParentFragment<WalletGenerationFragment>()?.apply {
      overlayView.header.backButton.onClick {
        headerTitle = CreateWalletText.create
        presenter.popFragmentFrom<MnemonicBackupFragment>()
        setContentHeight()
      }
    }
  }

}
