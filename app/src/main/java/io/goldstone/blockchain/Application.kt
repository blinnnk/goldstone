package io.goldstone.blockchain

import android.annotation.SuppressLint
import android.app.Application
import com.blinnnk.extension.isNull
import com.google.android.gms.analytics.GoogleAnalytics
import com.google.android.gms.analytics.Tracker
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
}