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
import io.goldstone.blockchain.kernel.commonmodel.AppConfigTable
import io.goldstone.blockchain.kernel.commonmodel.SupportCurrencyTable
import io.goldstone.blockchain.kernel.database.GoldStoneDataBase
import io.goldstone.blockchain.kernel.network.GoldStoneAPI
import io.goldstone.blockchain.kernel.network.GoldStoneEthCall
import io.goldstone.blockchain.kernel.receiver.XinGePushReceiver
import io.goldstone.blockchain.kernel.receiver.registerDeviceForPush
import io.goldstone.blockchain.module.entrance.starting.presenter.StartingPresenter
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.model.DefaultTokenTable

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
		GoldStoneDataBase.initDatabase(applicationContext)
		// init ethereum utils `Context`
		GoldStoneEthCall.context = this
		// init `Api` context
		GoldStoneAPI.context = this
		// prepare `config` information
		prepareAppConfig { registerDeviceForPush() }
	}
	
	/**
	 * Gets the default [Tracker] for this [Application].
	 */
	@Synchronized
	fun getDefaultTracker(): Tracker? {
		if (tracker.isNull()) {
			tracker = sAnalytics?.newTracker(R.xml.global_tracker)
		}
		return tracker
	}
	
	companion object {
		
		var currentRate: Double = 1.0
		var currencyCode: String = CountryCode.currentCurrency
		var currentLanguage: Int? = HoneyLanguage.getLanguageCodeBySymbol(CountryCode.currentLanguageSymbol)
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