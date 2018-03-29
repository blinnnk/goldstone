package io.goldstone.blockchain.module.common.walletgeneration.mnemonicconfirmation.presenter

import com.blinnnk.extension.jump
import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.module.common.walletgeneration.mnemonicconfirmation.view.MnemonicConfirmationFragment
import io.goldstone.blockchain.module.entrance.splash.view.SplashActivity
import io.goldstone.blockchain.module.home.home.view.MainActivity

/**
 * @date 22/03/2018 11:40 PM
 * @author KaySaith
 */

class MnemonicConfirmationPresenter(
  override val fragment: MnemonicConfirmationFragment
  ) : BasePresenter<MnemonicConfirmationFragment>() {

  fun clickConfirmationButton() {
    if (fragment.activity is SplashActivity) goToMainActivity()
  }

  private fun goToMainActivity() {
    fragment.activity?.jump<MainActivity>()
  }

}