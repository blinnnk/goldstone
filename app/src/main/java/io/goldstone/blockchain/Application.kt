package io.goldstone.blockchain

import android.annotation.SuppressLint
import android.app.Application
import com.blinnnk.extension.isNull
import com.blinnnk.extension.isTrue
import com.blinnnk.extension.otherwise
import com.google.android.gms.analytics.GoogleAnalytics
import com.google.android.gms.analytics.Tracker
import com.instabug.library.Instabug
import com.instabug.library.invocation.InstabugInvocationEvent
import io.goldstone.blockchain.common.utils.LogUtil
import io.goldstone.blockchain.common.utils.NetworkUtil
import io.goldstone.blockchain.common.utils.TinyNumberUtils
import io.goldstone.blockchain.common.value.Config
import io.goldstone.blockchain.common.value.ConfigTag
import io.goldstone.blockchain.common.value.InstaBug
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
		// register the instabug system by config status
		setInstaBugStatus()
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
	
	private fun setInstaBugStatus() {
		NetworkUtil.hasNetwork(this) isTrue {
			GoldStoneAPI.getConfigList(
				{
					LogUtil.error("GetConfigList", it)
				}) {
				it.find {
					it.name == ConfigTag.instaBug
				}?.apply {
					Config.updateInstaBugStatus(switch)
					userInstaBugBySwitch(switch)
				}
			}
		} otherwise {
			userInstaBugBySwitch(Config.getInstaBugStatus())
		}
	}
	
	private fun userInstaBugBySwitch(switch: String) {
		if (TinyNumberUtils.isTrue(switch)) {
			Instabug.Builder(this, InstaBug.key)
				.setInvocationEvent(InstabugInvocationEvent.SHAKE)
				.build()
		}
	}
}