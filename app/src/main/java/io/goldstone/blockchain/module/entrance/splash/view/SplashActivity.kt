package io.goldstone.blockchain.module.entrance.splash.view

import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.ViewGroup
import android.widget.RelativeLayout
import com.blinnnk.extension.*
import io.goldstone.blockchain.common.component.GradientType
import io.goldstone.blockchain.common.component.GradientView
import io.goldstone.blockchain.common.component.SplashContainer
import io.goldstone.blockchain.common.value.Config
import io.goldstone.blockchain.common.value.Duration
import io.goldstone.blockchain.kernel.commonmodel.AppConfigTable
import io.goldstone.blockchain.kernel.receiver.XinGePushReceiver
import io.goldstone.blockchain.kernel.receiver.registerDeviceForPush
import io.goldstone.blockchain.module.entrance.splash.presenter.SplashPresenter
import io.goldstone.blockchain.module.entrance.starting.view.StartingFragment
import me.itangqi.waveloadingview.WaveLoadingView
import org.jetbrains.anko.matchParent

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
	private val gradientView by lazy {
		GradientView(this).apply { setStyle(GradientType.Blue) }
	}
	private val waveView by lazy { WaveLoadingView(this) }
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		
		hideStatusBar()
		
		prepareAppConfig {
			application.registerDeviceForPush()
			
			initLaunchLanguage(language)
			setCurrentChainID(chainID)
			container.apply {
				gradientView.into(this)
				initWaveView()
				// 判断 `SaveInstanceState` 防止旋转屏幕重新创建 `Fragment`
				savedInstanceState.isNull {
					addFragment<StartingFragment>(container.id)
				}
			}.let {
				setContentView(it)
			}
			// 错开动画时间再执行数据请求
			Duration.wave timeUpThen {
				// Add currency data from local JSON file
				presenter.apply {
					initSupportCurrencyList {
						// insert support currency list from local json
						updateCurrencyRateFromServer(this@prepareAppConfig)
						// check network to get default toke list
						initDefaultTokenByNetWork {
							hasAccountThenLogin()
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
					/**
					 * 如果之前因为失败原因 `netWork`, `Server` 等注册地址失败, 在这里
					 * 检测并重新注册
					 */
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
		Config.updateCurrentLanguageCode(code)
	}
	
	private fun setCurrentChainID(id: String) {
		Config.updateCurrentChain(id)
	}
	
	private fun ViewGroup.initWaveView() {
		waveView.apply {
			layoutParams = RelativeLayout.LayoutParams(matchParent, matchParent)
			setShapeType(WaveLoadingView.ShapeType.RECTANGLE)
			progressValue = 35
			waveColor = Color.parseColor("#FF19769D")
			setAnimDuration(30000)
			setAmplitudeRatio(30)
			startAnimation()
		}.into(this)
	}
}