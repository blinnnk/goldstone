package io.goldstone.blockchain.module.common.createwallet.presenter

import com.blinnnk.util.addFragment
import com.blinnnk.util.getParentFragment
import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.common.utils.hideChildFragment
import io.goldstone.blockchain.common.value.ContainerID
import io.goldstone.blockchain.common.value.CreateWalletText
import io.goldstone.blockchain.common.value.FragmentTag
import io.goldstone.blockchain.module.common.createwallet.view.CreateWalletFragment
import io.goldstone.blockchain.module.common.mnemonicbackup.view.MnemonicBackupFragment
import io.goldstone.blockchain.module.common.walletgeneration.view.WalletGenerationFragment

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
      addFragment<MnemonicBackupFragment>(ContainerID.content, FragmentTag.mnemonicBackup)
      hasBackButton = true
      hasCloseButton = false
      headerTitle = CreateWalletText.mnemonicBackUp
    }
  }

}