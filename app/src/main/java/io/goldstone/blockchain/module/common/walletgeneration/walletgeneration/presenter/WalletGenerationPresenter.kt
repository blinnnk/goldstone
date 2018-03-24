package io.goldstone.blockchain.module.common.walletgeneration.walletgeneration.presenter

import android.support.v4.app.Fragment
import com.blinnnk.extension.findChildFragmentByTag
import com.blinnnk.extension.removeChildFragment
import com.blinnnk.extension.showChildFragment
import io.goldstone.blockchain.common.base.baseoverlayfragment.BaseOverlayPresenter
import io.goldstone.blockchain.common.value.CreateWalletText
import io.goldstone.blockchain.common.value.FragmentTag
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.view.CreateWalletFragment
import io.goldstone.blockchain.module.common.walletgeneration.mnemonicbackup.view.MnemonicBackupFragment
import io.goldstone.blockchain.module.common.walletgeneration.mnemonicconfirmation.view.MnemonicConfirmationFragment
import io.goldstone.blockchain.module.common.walletgeneration.walletgeneration.view.WalletGenerationFragment

/**
 * @date 22/03/2018 9:38 PM
 * @author KaySaith
 */

class WalletGenerationPresenter(
  override val fragment: WalletGenerationFragment
) : BaseOverlayPresenter<WalletGenerationFragment>() {

  override fun backToLastFragment() {
    fragment.apply {
      childFragmentManager.fragments.last().let {
        when (it) {
          is MnemonicBackupFragment -> recoveryHeaderToCreateWalletFrom(it)
          is MnemonicConfirmationFragment -> recoveryHeaderToMnemonicBackup(it)
          else -> {
          }
        }
      }
    }
  }

  // 返回创建键盘的界面
  private fun WalletGenerationFragment.recoveryHeaderToCreateWalletFrom(fragment: Fragment) {
    removeChildFragment(fragment)
    // 恢复上一个 `Fragment` 的显示
    findChildFragmentByTag<CreateWalletFragment>(FragmentTag.walletCreation)?.let {
        showChildFragment(it)
      }
    hasCloseButton = true
    hasBackButton = false
    headerTitle = CreateWalletText.create
  }

  // 返回助记词生成界面
  private fun WalletGenerationFragment.recoveryHeaderToMnemonicBackup(fragment: Fragment) {
    removeChildFragment(fragment)
    // 恢复上一个 `Fragment` 的显示
    findChildFragmentByTag<MnemonicBackupFragment>(FragmentTag.mnemonicBackup)?.let {
      showChildFragment(it)
    }
    headerTitle = CreateWalletText.mnemonicBackUp
  }
}