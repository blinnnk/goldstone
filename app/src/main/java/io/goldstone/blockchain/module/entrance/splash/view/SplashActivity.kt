package io.goldstone.blockchain.module.entrance.splash.view

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.blinnnk.extension.*
import io.goldstone.blockchain.GoldStoneApp
import io.goldstone.blockchain.common.component.SplashContainer
import io.goldstone.blockchain.common.utils.LogUtil
import io.goldstone.blockchain.kernel.commonmodel.AppConfigTable
import io.goldstone.blockchain.kernel.commonmodel.SupportCurrencyTable
import io.goldstone.blockchain.kernel.network.GoldStoneAPI
import io.goldstone.blockchain.kernel.receiver.XinGePushReceiver
import io.goldstone.blockchain.module.entrance.splash.presenter.SplashPresenter
import io.goldstone.blockchain.module.entrance.starting.view.StartingFragment
import org.jetbrains.anko.doAsync

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
		
		prepareAppConfig {
			initLaunchLanguage(language)
			setCurrentChainID(chainID)
			
			container.apply {
				// 判断 `SaveInstanceState` 防止旋转屏幕重新创建 `Fragment`
				savedInstanceState.isNull {
					addFragment<StartingFragment>(container.id)
				}
			}.let {
				setContentView(it)
			}
			// 打印必要数据在 `Debug` 的时候
			LogUtil.debug(this.javaClass.simpleName, "Config: $this")
			// Add currency data from local JSON file
			presenter.initSupportCurrencyList {
				// insert support currency list from local json
				updateCurrencyRateFromServer(this)
				// check network to get default toke list
				presenter.initDefaultTokenByNetWork {
					presenter.hasAccountThenLogin()
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
	
	private fun prepareAppConfig(callback: AppConfigTable.() -> Unit) {
		AppConfigTable.getAppConfig { config ->
			config.isNull() isTrue {
				AppConfigTable.insertAppConfig {
					AppConfigTable.getAppConfig {
						it?.let(callback)
					}
				}
			} otherwise {
				config?.isRegisteredAddresses?.isFalse {
					// 如果之前因为失败原因 `netWork`, `Server` 等注册地址失败, 在这里检测并重新注册
					XinGePushReceiver.registerWalletAddressForPush()
				}
				config?.let(callback)
			}
		}
	}
	
	/**
	 * Querying the language type of the current account
	 * set and displaying the interface from the database.
	 */
	private fun initLaunchLanguage(code: Int) {
		GoldStoneApp.updateCurrentLanguage(code)
	}
	
	private fun setCurrentChainID(id: String) {
		GoldStoneApp.updateCurrentChain(id)
	}
	
	// 获取当前的汇率
	private fun updateCurrencyRateFromServer(
		config: AppConfigTable
	) {
		doAsync {
			GoldStoneApp.updateCurrencyCode(config.currencyCode)
			GoldStoneAPI.getCurrencyRate(config.currencyCode, {
				LogUtil.error("Request of get currency rate has error")
			}) {
				// 更新内存中的值
				GoldStoneApp.updateCurrentRate(it)
				// 更新数据库的值
				SupportCurrencyTable.updateUsedRateValue(it)
			}
		}
	}
}