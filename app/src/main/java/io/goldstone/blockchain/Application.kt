package io.goldstone.blockchain

import android.app.Application
import com.blinnnk.extension.isNull
import com.blinnnk.extension.isTrue
import com.blinnnk.extension.otherwise
import io.goldstone.blockchain.common.value.CountryCode
import io.goldstone.blockchain.common.value.HoneyLanguage
import io.goldstone.blockchain.crypto.GoldStoneEthCall
import io.goldstone.blockchain.kernel.database.GoldStoneDataBase
import io.goldstone.blockchain.kernel.network.GoldStoneAPI
import io.goldstone.blockchain.kernel.receiver.registerDeviceForPush
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable
import io.goldstone.blockchain.module.entrance.starting.presenter.StartingPresenter

@Suppress("DEPRECATION")
/**
 * @date 22/03/2018 3:02 PM
 * @author KaySaith
 */

class GoldStoneApp : Application() {

  override fun onCreate() {
    super.onCreate()
    // create and init database
    GoldStoneDataBase.initDatabase(this)

    // init ethereum utils `Context`
    GoldStoneEthCall.context = this

    // init `Api` context
    GoldStoneAPI.context = this

    // update local `Tokens` info list
    StartingPresenter.updateLocalDefaultTokens(this)

    initAppParameters()

    registerDeviceForPush()
  }

  companion object {

    var currentRate: Double = 1.0
    var currencyCode: String = CountryCode.currentCurrency
    var currentLanguage: Int? = HoneyLanguage.English.code

    fun initAppParameters() {
      // Querying the language type of the current account
      // set and displaying the interface from the database.
      WalletTable.getCurrentWalletInfo {
        it?.apply {
          initLaunchLanguage(it)
          getCurrencyRate(it)
        }
      }
    }

    private fun initLaunchLanguage(wallet: WalletTable) {
      wallet.isNull().isTrue {
        currentLanguage = HoneyLanguage.getLanguageCode(CountryCode.currentLanguage)
      } otherwise {
        currentLanguage = wallet.language
        WalletTable.current = wallet
      }
    }

    // 获取当前的汇率
    private fun getCurrencyRate(wallet: WalletTable) {
      currencyCode = wallet.currencyCode
      GoldStoneAPI.getCurrencyRate(wallet.currencyCode) {
        currentRate = it
      }
    }
  }
}