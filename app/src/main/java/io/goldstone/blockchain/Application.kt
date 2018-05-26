package io.goldstone.blockchain

import android.annotation.SuppressLint
import android.app.Application
import com.blinnnk.extension.isFalse
import com.blinnnk.extension.isNull
import com.blinnnk.extension.isTrue
import com.blinnnk.extension.otherwise
import com.google.android.gms.analytics.GoogleAnalytics
import com.google.android.gms.analytics.Tracker
import io.goldstone.blockchain.common.utils.NetworkUtil
import io.goldstone.blockchain.common.value.ChainID
import io.goldstone.blockchain.common.value.CountryCode
import io.goldstone.blockchain.common.value.HoneyLanguage
import io.goldstone.blockchain.crypto.GoldStoneEthCall
import io.goldstone.blockchain.kernel.commonmodel.AppConfigTable
import io.goldstone.blockchain.kernel.commonmodel.SupportCurrencyTable
import io.goldstone.blockchain.kernel.database.GoldStoneDataBase
import io.goldstone.blockchain.kernel.network.GoldStoneAPI
import io.goldstone.blockchain.kernel.receiver.XinGePushReceiver
import io.goldstone.blockchain.kernel.receiver.registerDeviceForPush
import io.goldstone.blockchain.module.entrance.starting.presenter.StartingPresenter
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.model.DefaultTokenTable
import okhttp3.Interceptor

/**
 * @date 22/03/2018 3:02 PM
 * @author KaySaith
 */

class GoldStoneApp : Application() {

	private var sAnalytics: GoogleAnalytics? = null
	private var tracker: Tracker? = null

	@SuppressLint("HardwareIds")
	override fun onCreate() {
		super.onCreate()

		// init google analytics
		sAnalytics = GoogleAnalytics.getInstance(this)

		// create and init database
		GoldStoneDataBase.initDatabase(this)

		// init ethereum utils `Context`
		GoldStoneEthCall.context = this

		// init `Api` context
		GoldStoneAPI.context = this

		// check network to get default toke list
		initDefaultTokenByNetWork()

		// insert support currency list from local json
		initSupportCurrencyList()

		// prepare `config` information
		prepareAppConfig { registerDeviceForPush() }

	}

	/**
	 * Gets the default [Tracker] for this [Application].
	 * @return tracker
	 */
	@Synchronized
	fun getDefaultTracker(): Tracker? {
		if (tracker.isNull()) {
			tracker = sAnalytics?.newTracker(R.xml.global_tracker)
		}
		return tracker
	}

	private fun initDefaultTokenByNetWork() {
		NetworkUtil.hasNetwork(this) isTrue {
			// update local `Tokens` info list
			StartingPresenter.updateLocalDefaultTokens(this)
		} otherwise {
			// if there isn't network init local token list
			DefaultTokenTable.getTokens {
				it.isEmpty() isTrue {
					StartingPresenter.insertLocalTokens(this)
				}
			}
		}
	}

	private fun initSupportCurrencyList() {
		SupportCurrencyTable.getSupportCurrencies {
			it.isEmpty() isTrue {
				StartingPresenter.insertLocalCurrency(this)
			}
		}
	}

	companion object {

		var currentRate: Double = 1.0
		var currencyCode: String = CountryCode.currentCurrency
		var currentLanguage: Int? =
			HoneyLanguage.getLanguageCodeBySymbol(CountryCode.currentLanguageSymbol)
		var currentChain = ChainID.Main.id

		private fun prepareAppConfig(callback: () -> Unit) {
			AppConfigTable.getAppConfig { config ->
				config.isNull() isTrue {
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