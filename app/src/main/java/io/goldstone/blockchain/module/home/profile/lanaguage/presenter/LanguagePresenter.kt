package io.goldstone.blockchain.module.home.profile.lanaguage.presenter

import com.blinnnk.extension.reboot
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerPresenter
import io.goldstone.blockchain.common.value.HoneyLanguage
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable
import io.goldstone.blockchain.module.entrance.splash.view.SplashActivity
import io.goldstone.blockchain.module.home.profile.lanaguage.model.LanguageModel
import io.goldstone.blockchain.module.home.profile.lanaguage.view.LanguageFragment
import org.jetbrains.anko.alert
import org.jetbrains.anko.noButton
import org.jetbrains.anko.yesButton

@Suppress("DEPRECATION")
/**
 * @date 26/03/2018 6:42 PM
 * @author KaySaith
 */

class LanguagePresenter(
  override val fragment: LanguageFragment
  ) : BaseRecyclerPresenter<LanguageFragment, LanguageModel>() {

  fun setLanguage(language: String, hold: Boolean.() -> Unit) {
    fragment.context?.apply {
      alert(
        "Once you selected it, application will be rebooted and just wait several seconds.",
        "Are You Sure To Switch Language Settings?") {
        yesButton {
          updateData(language)
          hold(true)
        }
        noButton {
          hold(false)
        }
      }.show()
    }
  }

  private fun updateData(language: String) {
    val code = when (language) {
      HoneyLanguage.English.language -> HoneyLanguage.English.code
      HoneyLanguage.Chinese.language -> HoneyLanguage.Chinese.code
      HoneyLanguage.Japanese.language -> HoneyLanguage.Japanese.code
      else -> HoneyLanguage.English.code
    }

    WalletTable.updateLanguage(code) {
      fragment.reboot<SplashActivity>()
    }
  }

}