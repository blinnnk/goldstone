package io.goldstone.blockchain.module.home.profile.hint.presenter

import android.widget.EditText
import com.blinnnk.extension.isTrue
import com.blinnnk.extension.otherwise
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.common.utils.alert
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable
import io.goldstone.blockchain.module.home.profile.hint.view.HintFragment

/**
 * @date 24/04/2018 10:54 AM
 * @author KaySaith
 */

class HintPresenter(
  override val fragment: HintFragment
  ) : BasePresenter<HintFragment>() {
  fun updateHint(hintInput: EditText) {
    hintInput.text?.toString()?.let {
      it.isNotEmpty() isTrue {
        WalletTable.updateHint(it) {
          fragment.context?.alert("Modify Succeed")
        }
      } otherwise {
        fragment.context?.alert("It is empty please enter some word")
      }
    }
  }

  override fun onFragmentViewCreated() {
    super.onFragmentViewCreated()
    // 初始化高度
    updateHeight(250.uiPX())
  }
}