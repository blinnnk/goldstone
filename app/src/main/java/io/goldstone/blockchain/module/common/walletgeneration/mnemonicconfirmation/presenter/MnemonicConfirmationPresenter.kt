package io.goldstone.blockchain.module.common.walletgeneration.mnemonicconfirmation.presenter

import com.blinnnk.extension.isTrue
import com.blinnnk.extension.jump
import com.blinnnk.extension.otherwise
import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.module.common.walletgeneration.mnemonicconfirmation.view.MnemonicConfirmationFragment
import io.goldstone.blockchain.module.entrance.splash.view.SplashActivity
import org.jetbrains.anko.toast

/**
 * @date 22/03/2018 11:40 PM
 * @author KaySaith
 */

class MnemonicConfirmationPresenter(
  override val fragment: MnemonicConfirmationFragment
  ) : BasePresenter<MnemonicConfirmationFragment>() {

  fun clickConfirmationButton(correct: String, current: String) {
    compareMnemonicCode(correct, current).isTrue {
      goToSplashActivity()
    } otherwise {
      fragment.context?.toast("incorrect mnemonic please re-enter")
    }
  }

  private fun compareMnemonicCode(correct: String, current: String) = correct == current

  private fun goToSplashActivity() {
    fragment.activity?.jump<SplashActivity>()
  }


}