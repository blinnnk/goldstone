package io.goldstone.blockchain.module.entrance.splash.view

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.blinnnk.extension.addFragment
import com.blinnnk.extension.hideStatusBar
import com.blinnnk.extension.isNull
import io.goldstone.blockchain.GoldStoneApp
import io.goldstone.blockchain.common.component.SplashContainer
import io.goldstone.blockchain.common.utils.LogUtil
import io.goldstone.blockchain.common.utils.alert
import io.goldstone.blockchain.common.value.AlertText
import io.goldstone.blockchain.kernel.commonmodel.AppConfigTable
import io.goldstone.blockchain.kernel.network.GoldStoneAPI
import io.goldstone.blockchain.module.entrance.splash.presenter.SplashPresenter
import io.goldstone.blockchain.module.entrance.starting.view.StartingFragment
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.runOnUiThread

/**
─────────────────────────────────────────────────────────────
─██████████████─██████████████─██████─────────████████████───
─██░░░░░░░░░░██─██░░░░░░░░░░██─██░░██─────────██░░░░░░░░████─
─██░░██████████─██░░██████░░██─██░░██─────────██░░████░░░░██─
─██░░██─────────██░░██──██░░██─██░░██─────────██░░██──██░░██─
─██░░██─────────██░░██──██░░██─██░░██─────────██░░██──██░░██─
─██░░██──██████─██░░██──██░░██─██░░██─────────██░░██──██░░██─
─██░░██──██░░██─██░░██──██░░██─██░░██─────────██░░██──██░░██─
─██░░██──██░░██─██░░██──██░░██─██░░██─────────██░░██──██░░██─
─██░░██████░░██─██░░██████░░██─██░░██████████─██░░████░░░░██─
─██░░░░░░░░░░██─██░░░░░░░░░░██─██░░░░░░░░░░██─██░░░░░░░░████─
─██████████████─██████████████─██████████████─████████████───
─────────────────────────────────────────────────────────────
────────────────────────────────────────────────────────────────────────────────────
─██████████████─██████████████─██████████████─██████──────────██████─██████████████─
─██░░░░░░░░░░██─██░░░░░░░░░░██─██░░░░░░░░░░██─██░░██████████──██░░██─██░░░░░░░░░░██─
─██░░██████████─██████░░██████─██░░██████░░██─██░░░░░░░░░░██──██░░██─██░░██████████─
─██░░██─────────────██░░██─────██░░██──██░░██─██░░██████░░██──██░░██─██░░██─────────
─██░░██████████─────██░░██─────██░░██──██░░██─██░░██──██░░██──██░░██─██░░██████████─
─██░░░░░░░░░░██─────██░░██─────██░░██──██░░██─██░░██──██░░██──██░░██─██░░░░░░░░░░██─
─██████████░░██─────██░░██─────██░░██──██░░██─██░░██──██░░██──██░░██─██░░██████████─
─────────██░░██─────██░░██─────██░░██──██░░██─██░░██──██░░██████░░██─██░░██─────────
─██████████░░██─────██░░██─────██░░██████░░██─██░░██──██░░░░░░░░░░██─██░░██████████─
─██░░░░░░░░░░██─────██░░██─────██░░░░░░░░░░██─██░░██──██████████░░██─██░░░░░░░░░░██─
─██████████████─────██████─────██████████████─██████──────────██████─██████████████─
────────────────────────────────────────────────────────────────────────────────────
Copyright (C) 2018 Pʀᴏᴅᴜᴄᴇ Bʏ Vɪsɪᴏɴ Cᴏʀᴇ Cʀᴏᴘ.
────────────────────────────────────────────────────────────────────────────────────
 */

class SplashActivity : AppCompatActivity() {

	var backEvent: Runnable? = null
	private val container by lazy { SplashContainer(this) }
	private val presenter = SplashPresenter(this)

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		hideStatusBar()

		container.apply {
			savedInstanceState.isNull {
				// 判断 `SaveInstanceState` 防止旋转屏幕重新创建 `Fragment`
				addFragment<StartingFragment>(container.id)
			}
		}.let {
			setContentView(it)
		}

		AppConfigTable.getAppConfig {
			it?.apply {
				initLaunchLanguage(language)
				setCurrentChainID(chainID)
				// 打印必要数据在 `Debug` 的时候
				LogUtil.debug(this.javaClass.simpleName, "Config: $this")
				getCurrencyRate(this) {
					// check network to get default toke list
					// insert support currency list from local json
					presenter.initSupportCurrencyList {
						presenter.initDefaultTokenByNetWork {
							presenter.hasAccountThenLogin()
						}
					}
				}
			}
		}
	}

	override fun onBackPressed() {
		if (backEvent.isNull()) {
			super.onBackPressed()
		} else {
			backEvent?.run()
		}
	}

	/**
	 * Querying the language type of the current account
	 * set and displaying the interface from the database.
	 */
	private fun initLaunchLanguage(code: Int) {
		GoldStoneApp.currentLanguage = code
	}

	private fun setCurrentChainID(id: String) {
		GoldStoneApp.currentChain = id
	}

	// 获取当前的汇率
	private fun getCurrencyRate(
		config: AppConfigTable,
		callback: () -> Unit
	) {
		doAsync {
			GoldStoneApp.currencyCode = config.currencyCode
			GoldStoneAPI.getCurrencyRate(config.currencyCode, {
				GoldStoneAPI.context.runOnUiThread {
					callback()
					this@SplashActivity.alert(AlertText.getRateFromServerError)
				}
			}) {
				GoldStoneApp.currentRate = it
				GoldStoneAPI.context.runOnUiThread { callback() }
			}
		}
	}

}