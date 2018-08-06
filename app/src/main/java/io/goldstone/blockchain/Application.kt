package io.goldstone.blockchain

import android.annotation.SuppressLint
import android.app.Application
import com.blinnnk.extension.isNull
import com.google.android.gms.analytics.GoogleAnalytics
import com.google.android.gms.analytics.Tracker
import io.goldstone.blockchain.kernel.database.GoldStoneDataBase
import io.goldstone.blockchain.kernel.network.GoldStoneAPI
import io.goldstone.blockchain.kernel.network.GoldStoneEthCall
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable
import java.io.File

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
		// 检查是否需要清理本地的 `KeyStore File`
		cleanKeyStoreFileWhenUpdateDatabase()

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

	// 因为密钥都存储在本地的 `Keystore File` 文件里面, 当升级数据库 `FallBack` 数据的情况下
	// 需要也同时清理本地的 `Keystore File`
	private fun cleanKeyStoreFileWhenUpdateDatabase() {
		WalletTable.getAll {
			if (isEmpty()) {
				cleanKeyStoreFile(filesDir)
			}
		}
	}

	private fun cleanKeyStoreFile(dir: File): Boolean {
		if (dir.isDirectory) {
			val children = dir.list()
			for (index in children.indices) {
				val success = cleanKeyStoreFile(File(dir, children[index]))
				if (!success) {
					return false
				}
			}
		}
		// The directory is now empty so delete it
		return dir.delete()
	}
}