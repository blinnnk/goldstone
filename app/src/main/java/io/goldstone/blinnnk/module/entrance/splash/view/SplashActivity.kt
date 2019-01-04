package io.goldstone.blinnnk.module.entrance.splash.view

import android.graphics.Color
import android.os.Bundle
import android.support.annotation.WorkerThread
import android.support.v7.app.AppCompatActivity
import android.view.ViewGroup
import android.widget.RelativeLayout
import com.blinnnk.extension.*
import io.goldstone.blinnnk.common.component.GradientType
import io.goldstone.blinnnk.common.component.GradientView
import io.goldstone.blinnnk.common.component.container.SplashContainer
import io.goldstone.blinnnk.common.language.HoneyLanguage
import io.goldstone.blinnnk.common.language.currentLanguage
import io.goldstone.blinnnk.common.sharedpreference.SharedChain
import io.goldstone.blinnnk.common.sharedpreference.SharedValue
import io.goldstone.blinnnk.common.sharedpreference.SharedWallet
import io.goldstone.blinnnk.common.thread.launchUI
import io.goldstone.blinnnk.common.utils.transparentStatus
import io.goldstone.blinnnk.common.value.ContainerID
import io.goldstone.blinnnk.common.value.CountryCode
import io.goldstone.blinnnk.crypto.multichain.*
import io.goldstone.blinnnk.crypto.multichain.node.ChainURL
import io.goldstone.blinnnk.kernel.commontable.AppConfigTable
import io.goldstone.blinnnk.kernel.database.GoldStoneDataBase
import io.goldstone.blinnnk.kernel.receiver.XinGePushReceiver
import io.goldstone.blinnnk.module.common.walletgeneration.createwallet.model.WalletTable
import io.goldstone.blinnnk.module.entrance.splash.presenter.SplashPresenter
import io.goldstone.blinnnk.module.entrance.splash.presenter.SplashPresenter.Companion.updateAccountInformation
import io.goldstone.blinnnk.module.entrance.starting.view.StartingFragment
import io.goldstone.blinnnk.module.home.home.view.MainActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import me.itangqi.waveloadingview.WaveLoadingView


/**
────────────────────────────────────────────────────────────────────────────────────
Copyright (C) 2018 Pʀᴏᴅᴜᴄᴇ Bʏ Vɪsɪᴏɴ Cᴏʀᴇ Cʀᴏᴘ.
────────────────────────────────────────────────────────────────────────────────────
 */
class SplashActivity : AppCompatActivity() {

	var backEvent: Runnable? = null
	private lateinit var container: SplashContainer
	private val presenter = SplashPresenter(this)
	private lateinit var gradientView: GradientView
	private lateinit var waveView: WaveLoadingView

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		// 判断 `SaveInstanceState` 防止旋转屏幕重新创建 `Fragment`
		if (savedInstanceState.isNull()) {
			transparentStatus()
			container = SplashContainer(this)
			setContentView(
				container.apply {
					gradientView = GradientView(context)
					gradientView.setStyle(GradientType.Blue)
					gradientView.into(this)
					initWaveView()
				}
			)
			GlobalScope.launch(Dispatchers.Default) {
				with(presenter) {
					cleanWhenUpdateDatabaseOrElse {
						initSupportCurrencyList(this@SplashActivity)
						initDefaultExchangeData(this@SplashActivity)
						prepareAppConfig {
							recoverySandboxData { hasChanged ->
								if (hasChanged) {
									AppConfigTable.dao.getAppConfig()?.prepareData()
								} else prepareData()
							}
						}
					}
				}
			}
		}
	}

	@WorkerThread
	private fun AppConfigTable.prepareData() {
		SharedValue.updatePincodeDisplayStatus(showPincode)
		SharedWallet.updateCurrencyCode(currencyCode)
		SharedValue.updateJSCode(jsCode)
		// 如果本地的钱包数量不为空那么才开始注册设备
		// 把 `GoldStoneID` 存储到 `SharePreference` 里面
		SharedWallet.updateGoldStoneID(goldStoneID)
		findViewById<RelativeLayout>(ContainerID.splash)?.let { it ->
			val hasStartingFragment =
				supportFragmentManager.fragments.find { it is StartingFragment }.isNotNull()
			if (!hasStartingFragment) launchUI {
				addFragment<StartingFragment>(it.id)
			}
		}
		// Add currency data from local JSON file
		with(presenter) {
			initNodeList(activity) {
				prepareNodeInfo {
					// 初次安装软件关键数据从本地 JSON 生成到数据库,
					// 后续会在网络环境更新为网络数据
					initLaunchLanguage(language)
					initDefaultToken(activity)
					// 检查市场状况
					updateAccountInformation(activity) {
						launchUI {
							jump<MainActivity>()
						}
					}
				}
			}
		}
	}

	override fun onBackPressed() {
		backEvent?.run() ?: super.onBackPressed()
	}

	@WorkerThread
	private fun prepareNodeInfo(callback: () -> Unit) {
		val chainDao = GoldStoneDataBase.database.chainNodeDao()
		val eosKylin = chainDao.getKylinEOSNode()
		val eosJungle = chainDao.getJungleEOSNode()
		val eosMain = chainDao.getMainnetEOSNode()
		val allTestnetChains = chainDao.getUsedTestnet()
		val allMainnetChains = chainDao.getUsedMainnet()
		val localNode =
			if (SharedValue.isTestEnvironment()) allTestnetChains
			else allMainnetChains

		// 存入全部的当前正在使用的 Chain 服务 Notification 的跨主网测试网查询
		SharedChain.updateAllUsedTestnetChains(allTestnetChains)
		SharedChain.updateAllUsedMainnetChains(allMainnetChains)

		localNode.forEach {
			when {
				ChainType(it.chainType).isETH() -> SharedChain.updateCurrentETH(ChainURL(it))
				ChainType(it.chainType).isBTC() -> SharedChain.updateBTCCurrent(ChainURL(it))
				ChainType(it.chainType).isLTC() -> SharedChain.updateLTCCurrent(ChainURL(it))
				ChainType(it.chainType).isBCH() -> SharedChain.updateBCHCurrent(ChainURL(it))
				ChainType(it.chainType).isEOS() -> {
					SharedChain.updateEOSCurrent(ChainURL(it))
					SharedChain.updateKylinEOSTestnet(ChainURL(eosKylin))
					SharedChain.updateJungleEOSTestnet(ChainURL(eosJungle))
					SharedChain.updateEOSMainnet(ChainURL(eosMain))
				}
				ChainType(it.chainType).isETC() -> SharedChain.updateETCCurrent(ChainURL(it))
			}
		}
		callback()
	}

	private fun prepareAppConfig(@WorkerThread callback: AppConfigTable.() -> Unit) {
		GlobalScope.launch(Dispatchers.Default) {
			val config = AppConfigTable.dao.getAppConfig()
			if (config.isNull()) {
				// 如果本地没有配置过 `Config` 你那么首先更新语言为系统语言
				currentLanguage = HoneyLanguage.getCodeBySymbol(CountryCode.currentLanguageSymbol)
				AppConfigTable.insertAppConfig(callback)
			} else {
				// 如果之前因为失败原因 `netWork`, `Server` 等注册地址失败, 在这里检测并重新注册
				if (config.isRegisteredAddresses) {
					val currentWallet = WalletTable.dao.findWhichIsUsing()
					XinGePushReceiver.registerAddressesForPush(currentWallet)
				}
				config.let(callback)
			}
		}
	}

	private fun ViewGroup.initWaveView() {
		waveView = WaveLoadingView(context)
		with(waveView) {
			setShapeType(WaveLoadingView.ShapeType.RECTANGLE)
			progressValue = 35
			waveColor = Color.parseColor("#FF19769D")
			setAnimDuration(30000)
			setAmplitudeRatio(30)
			startAnimation()
		}
		addView(waveView)
	}
}