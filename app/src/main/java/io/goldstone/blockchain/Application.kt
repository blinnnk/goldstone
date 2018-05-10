package io.goldstone.blockchain

import android.annotation.SuppressLint
import android.app.Application
import com.blinnnk.extension.isFalse
import com.blinnnk.extension.isNull
import com.blinnnk.extension.isTrue
import com.blinnnk.extension.otherwise
import io.goldstone.blockchain.common.utils.NetworkUtil
import io.goldstone.blockchain.common.value.CountryCode
import io.goldstone.blockchain.common.value.HoneyLanguage
import io.goldstone.blockchain.crypto.GoldStoneEthCall
import io.goldstone.blockchain.kernel.commonmodel.AppConfigTable
import io.goldstone.blockchain.kernel.database.GoldStoneDataBase
import io.goldstone.blockchain.kernel.network.GoldStoneAPI
import io.goldstone.blockchain.kernel.receiver.XinGePushReceiver
import io.goldstone.blockchain.kernel.receiver.registerDeviceForPush
import io.goldstone.blockchain.module.entrance.starting.presenter.StartingPresenter
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.model.DefaultTokenTable

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

		NetworkUtil.hasNetwork(this) isTrue {
			// update local `Tokens` info list
			StartingPresenter.updateLocalDefaultTokens(this)
		} otherwise {
			/** 没有网络且本地数据为空的时候插入本地事先准备好的 `Token Json` */
			DefaultTokenTable.getTokens {
				it.isEmpty() isTrue {
					StartingPresenter.insertLocalTokens(this)
				}
			}
		}

		prepareAppConfig { registerDeviceForPush() }

		initAppParameters()

	}

	companion object {

		var currentRate: Double = 1.0
		var currencyCode: String = CountryCode.currentCurrency
		var currentLanguage: Int? = HoneyLanguage.getLanguageCodeBySymbol(CountryCode.currentLanguageSymbol)

		/**
		 * Querying the language type of the current account
		 * set and displaying the interface from the database.
		 */
		private fun initAppParameters() {
			AppConfigTable.getAppConfig {
				initLaunchLanguage(it?.language)
				it?.let {
					getCurrencyRate(it)
				}
			}
		}

		private fun initLaunchLanguage(code: Int?) {
			currentLanguage = code ?: currentLanguage
		}

		// 获取当前的汇率
		private fun getCurrencyRate(config: AppConfigTable) {
			currencyCode = config.currencyCode
			GoldStoneAPI.getCurrencyRate(config.currencyCode) {
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