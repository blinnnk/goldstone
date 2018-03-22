package io.goldstone.blockchain.module.common.walletgeneration.mnemonicbackup.presenter

import com.blinnnk.util.addFragment
import com.blinnnk.util.getParentFragment
import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.common.utils.hideChildFragment
import io.goldstone.blockchain.common.value.ContainerID
import io.goldstone.blockchain.common.value.CreateWalletText
import io.goldstone.blockchain.module.common.walletgeneration.mnemonicbackup.view.MnemonicBackupFragment
import io.goldstone.blockchain.module.common.walletgeneration.mnemonicconfirmation.view.MnemonicConfirmationFragment
import io.goldstone.blockchain.module.common.walletgeneration.walletgeneration.view.WalletGenerationFragment

/**
 * @date 22/03/2018 9:32 PM
 * @author KaySaith
 */

class MnemonicBackupPresenter(
  override val fragment: MnemonicBackupFragment
  ) : BasePresenter<MnemonicBackupFragment>() {

  fun goToMnemonicConfirmation() {
    fragment.getParentFragment<WalletGenerationFragment>()?.apply {
      hideChildFragment(fragment)
      addFragment<MnemonicConfirmationFragment>(ContainerID.content)
      headerTitle = CreateWalletText.mnemonicConfirmation
    }
  }

}
