package io.goldstone.blockchain.module.entrance.splash.presenter

import com.blinnnk.extension.isTrue
import com.blinnnk.extension.jump
import io.goldstone.blockchain.GoldStoneApp
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable
import io.goldstone.blockchain.module.entrance.splash.view.SplashActivity
import io.goldstone.blockchain.module.home.home.view.MainActivity

/**
 * @date 30/03/2018 2:21 AM
 * @author KaySaith
 */

class SplashPresenter(val activity: SplashActivity) {

  fun hasAccountThenLogin() {
    WalletTable.getAll {
      isNotEmpty().isTrue {
        WalletTable.getCurrentWalletInfo {
          it?.apply {
            WalletTable.current =
              it.apply { language = GoldStoneApp.currentLanguage!! }
            activity.jump<MainActivity>()
          }
        }
      }
    }
  }
}