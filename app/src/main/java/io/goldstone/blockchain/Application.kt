package io.goldstone.blockchain

import android.annotation.SuppressLint
import android.app.Application
import com.blinnnk.extension.isFalse
import com.blinnnk.extension.isNull
import com.blinnnk.extension.isTrue
import com.blinnnk.extension.otherwise
import com.tencent.android.tpush.XGPushBaseReceiver
import io.goldstone.blockchain.common.value.CountryCode
import io.goldstone.blockchain.common.value.HoneyLanguage
import io.goldstone.blockchain.crypto.GoldStoneEthCall
import io.goldstone.blockchain.kernel.commonmodel.AppConfigTable
import io.goldstone.blockchain.kernel.database.GoldStoneDataBase
import io.goldstone.blockchain.kernel.network.GoldStoneAPI
import io.goldstone.blockchain.kernel.receiver.XinGePushReceiver
import io.goldstone.blockchain.kernel.receiver.registerDeviceForPush
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable
import io.goldstone.blockchain.module.entrance.starting.presenter.StartingPresenter

@Suppress("DEPRECATION")
/**
 * @date 22/03/2018 3:02 PM
 * @author KaySaith
 */

class GoldStoneApp : Application() {

	@SuppressLint("HardwareIds")
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

		prepareAppConfig { registerDeviceForPush() }

	}

	companion object {

		var currentRate: Double = 1.0
		var currencyCode: String = CountryCode.currentCurrency
		var currentLanguage: Int? = HoneyLanguage.English.code

		/**
		 * Querying the language type of the current account
		 * set and displaying the interface from the database.
		 */
		fun initAppParameters() {
			WalletTable.getCurrentWalletInfo {
				it?.apply {
					initLaunchLanguage(it)
					getCurrencyRate(it)
				}
			}
		}

		private fun initLaunchLanguage(wallet: WalletTable) {
			wallet.isNull() isTrue {
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

		private fun prepareAppConfig(callback: () -> Unit) {
			AppConfigTable.getAppConfig { config ->
				config.isNull().isTrue {
					AppConfigTable.insertAppConfig(callback)
				} otherwise {
					config?.isRegisteredAddresses?.isFalse {
						// 如果之前因为失败原因 `netWork`, `Server` 等注册地址失败, 在这里检测并重新注册
						XinGePushReceiver.registerWalletAddressForPush()
					}
					callback()
				}
			}
		}
	}
}