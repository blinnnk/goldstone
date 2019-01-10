package io.goldstone.blinnnk

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import com.umeng.commonsdk.UMConfigure
import io.goldstone.blinnnk.common.value.GoldStoneCryptoKey
import io.goldstone.blinnnk.common.value.OS
import io.goldstone.blinnnk.common.value.currentChannel
import io.goldstone.blinnnk.kernel.database.GoldStoneDataBase

/**
 * @date 22/03/2018 3:02 PM
 * @author KaySaith
 */
class GoldStoneApp : Application() {

	@SuppressLint("HardwareIds")
	override fun onCreate() {
		super.onCreate()
		// Create and init database
		GoldStoneDataBase.initDatabase(applicationContext)
		// Init `Api` context
		appContext = this
		UMConfigure.init(
			this,
			GoldStoneCryptoKey.umengKey,
			currentChannel.value,
			OS.android,
			"pushKey"
		)
	}

	companion object {
		lateinit var appContext: Context
		@JvmField
		var hasShownMobileAlert = false
	}
}