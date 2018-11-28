package io.goldstone.blockchain

import android.annotation.SuppressLint
import android.app.Application
import com.blinnnk.extension.isNull
import com.google.android.gms.analytics.GoogleAnalytics
import com.google.android.gms.analytics.Tracker
import io.goldstone.blockchain.kernel.database.GoldStoneDataBase
import io.goldstone.blockchain.kernel.network.common.GoldStoneAPI
import io.goldstone.blockchain.kernel.network.ethereum.ETHJsonRPC

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
		// Init google analytics
		sAnalytics = GoogleAnalytics.getInstance(this)
		// Create and init database
		GoldStoneDataBase.initDatabase(applicationContext)
		// Init ethereum utils `Context`
		ETHJsonRPC.context = this
		// Init `Api` context
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
		@JvmField
		var hasShownMobileAlert = false
		@JvmField
		var hasSilentUpdated = false
	}
}