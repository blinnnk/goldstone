package io.goldstone.blockchain.module.common.passcode.presenter

import com.blinnnk.animation.updateAlphaAnimation
import com.blinnnk.extension.isTrue
import com.blinnnk.extension.otherwise
import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.common.utils.alert
import io.goldstone.blockchain.common.value.Count
import io.goldstone.blockchain.kernel.commonmodel.AppConfigTable
import io.goldstone.blockchain.module.common.passcode.view.PasscodeFragment

/**
 * @date 23/04/2018 11:04 AM
 * @author KaySaith
 */

class PasscodePresenter(
  override val fragment: PasscodeFragment
  ) : BasePresenter<PasscodeFragment>() {

  private var retryTimes = 5

  fun unlockOrAlert(passcode: String, action: () -> Unit) {
    if (retryTimes <= 0) {
      fragment.context?.alert("Your have entered too many times please wait a momnet")
      return
    }
    checkPasscode(passcode) {
      it isTrue {
        // ToDo 在数据库存入超过次数的时间戳下次进行比对, 多次输错进行锁定
        fragment.removePasscodeFragment()

      } otherwise {
        retryTimes -= 1
        fragment.showFailedAttention(retryTimes)
        fragment.context?.alert("Wrong Passcode Please Retry")
      }
    }
    action()
  }

  private fun PasscodeFragment.removePasscodeFragment(callback: () -> Unit = {}) {
    activity?.let {
      container.updateAlphaAnimation(0f) {
        it.supportFragmentManager.beginTransaction().remove(this).commit()
        callback()
      }
    }
  }

  private fun checkPasscode(passcode: String, hold: (Boolean) -> Unit) {
    if (passcode.length >= Count.pinCode)
    // 从数据库获取本机的 `Passcode`
    AppConfigTable.getAppConfig {
      if (it.pincode == passcode.toInt()) {
        hold(true)
      } else {
        hold(false)
      }
    }
  }
}