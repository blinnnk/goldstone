package io.goldstone.blockchain.module.common.walletgeneration.createwallet.presenter

import com.blinnnk.extension.hideChildFragment
import com.blinnnk.util.addFragmentAndSetArgument
import com.blinnnk.util.getParentFragment
import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.common.value.ContainerID
import io.goldstone.blockchain.common.value.CreateWalletText
import io.goldstone.blockchain.common.value.FragmentTag
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

  fun showMnemonicBackupFragment() {
    fragment.getParentFragment<WalletGenerationFragment>()?.apply {
      // 隐藏当前 `Fragment` 节省内存
      hideChildFragment(fragment)
      addFragmentAndSetArgument<MnemonicBackupFragment>(ContainerID.content, FragmentTag.mnemonicBackup) {
        // Send Argument Parameters
      }
      hasBackButton = true
      hasCloseButton = false
      headerTitle = CreateWalletText.mnemonicBackUp
    }
  }

}