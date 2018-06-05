package io.goldstone.blockchain

import android.annotation.SuppressLint
import android.app.Application
import com.blinnnk.extension.isNull
import com.blinnnk.util.getDoubleFromSharedPreferences
import com.blinnnk.util.getIntFromSharedPreferences
import com.blinnnk.util.getStringFromSharedPreferences
import com.blinnnk.util.saveDataToSharedPreferences
import com.google.android.gms.analytics.GoogleAnalytics
import com.google.android.gms.analytics.Tracker
import io.goldstone.blockchain.common.value.SharesPreference
import io.goldstone.blockchain.kernel.database.GoldStoneDataBase
import io.goldstone.blockchain.kernel.network.GoldStoneAPI
import io.goldstone.blockchain.kernel.network.GoldStoneEthCall

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
		
		fun getCurrentLanguage(): Int =
			GoldStoneAPI.context.getIntFromSharedPreferences(SharesPreference.currentLanguage)
		
		fun updateCurrentLanguage(languageCode: Int) =
			GoldStoneAPI.context.saveDataToSharedPreferences(
				SharesPreference.currentLanguage,
				languageCode
			)
		
		fun getCurrentChain(): String =
			GoldStoneAPI.context.getStringFromSharedPreferences(SharesPreference.currentChain)
		
		fun updateCurrentChain(chainID: String) =
			GoldStoneAPI.context.saveDataToSharedPreferences(SharesPreference.currentChain, chainID)
		
		fun getCurrencyCode(): String =
			GoldStoneAPI.context.getStringFromSharedPreferences(SharesPreference.currencyCode)
		
		fun updateCurrencyCode(code: String) =
			GoldStoneAPI.context.saveDataToSharedPreferences(SharesPreference.currencyCode, code)
		
		fun getCurrentRate(): Double =
			GoldStoneAPI.context.getDoubleFromSharedPreferences(SharesPreference.rate)
		
		fun updateCurrentRate(rate: Double) =
			GoldStoneAPI.context.saveDataToSharedPreferences(SharesPreference.rate, rate.toFloat())
		
		fun getWalletCount(): Int =
			GoldStoneAPI.context.getIntFromSharedPreferences(SharesPreference.walletCount)
		
		fun updateWalletCount(count: Int) =
			GoldStoneAPI.context.saveDataToSharedPreferences(SharesPreference.walletCount, count)
		
		fun getMaxWalletID(): Int =
			GoldStoneAPI.context.getIntFromSharedPreferences(SharesPreference.maxWalletID)
		
		fun updateMaxWalletID(id: Int) =
			GoldStoneAPI.context.saveDataToSharedPreferences(SharesPreference.maxWalletID, id)
	}
}