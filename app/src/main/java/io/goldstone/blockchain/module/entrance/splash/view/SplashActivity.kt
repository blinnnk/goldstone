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
import io.goldstone.blockchain.common.language.currentLanguage
import io.goldstone.blockchain.common.utils.LogUtil
import io.goldstone.blockchain.common.utils.NetworkUtil
import io.goldstone.blockchain.common.utils.transparentStatus
import io.goldstone.blockchain.common.value.*
import io.goldstone.blockchain.crypto.utils.getObjectMD5HexString
import io.goldstone.blockchain.kernel.commonmodel.AppConfigTable
import io.goldstone.blockchain.kernel.network.GoldStoneAPI
import io.goldstone.blockchain.kernel.receiver.XinGePushReceiver
import io.goldstone.blockchain.kernel.receiver.registerDeviceForPush
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable
import io.goldstone.blockchain.module.entrance.splash.presenter.SplashPresenter
import io.goldstone.blockchain.module.entrance.starting.presenter.StartingPresenter.Companion.updateShareContentFromServer
import io.goldstone.blockchain.module.entrance.starting.view.StartingFragment
import me.itangqi.waveloadingview.WaveLoadingView
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
	private val gradientView by lazy {
		GradientView(this).apply { setStyle(GradientType.Blue) }
	}
	private val waveView by lazy { WaveLoadingView(this) }

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		// 判断 `SaveInstanceState` 防止旋转屏幕重新创建 `Fragment`
		savedInstanceState.isNull {
			transparentStatus()
			setContentView(container.apply {
				gradientView.into(this)
				initWaveView()
			})
		}
	}

	override fun onStart() {
		super.onStart()
		presenter.cleanWhenUpdateDatabaseOrElse {
			runOnUiThread { prepareData() }
		}
	}

	private fun prepareYingYongBaoInReviewStatus(callback: () -> Unit) {
		// 如果不是 `YingYongBao` 渠道跳过
		if (!currentChannel.value.equals(ApkChannel.Tencent.value, true)) {
			Config.updateYingYongBaoInReviewStatus(false)
			callback()
			return
		}
		// 没有网络直接返回
		if (!NetworkUtil.hasNetwork(this)) {
			callback()
			return
		}
		// 从服务器获取配置状态
		GoldStoneAPI.getConfigList(
			{
				callback()
				LogUtil.error("prepareStatusForYingYongBao", it)
			}
		) { serverConfigs ->
			val isInReview = serverConfigs.find {
				it.name.equals("inReview", true)
			}?.switch?.toIntOrNull() == 1
			if (isInReview) {
				Config.updateYingYongBaoInReviewStatus(true)
			} else {
				Config.updateYingYongBaoInReviewStatus(false)
			}
			callback()
		}
	}

	private fun prepareData() {
		prepareAppConfig config@{
			// 如果本地的钱包数量不为空那么才开始注册设备
			WalletTable.getAll {
				if (isNotEmpty()) {
					registerDeviceForPush()
					// 把 `GoldStoneID` 存储到 `Sharepreference` 里面
					Config.updateGoldStoneID(goldStoneID)
				}
			}
			initLaunchLanguage(language)
			findViewById<RelativeLayout>(ContainerID.splash)?.let { it ->
				supportFragmentManager.fragments.find {
					it is StartingFragment
				}.isNull {
					addFragment<StartingFragment>(it.id)
				}
			}
			// 错开动画时间再执行数据请求
			Duration.wave timeUpThen {
				// Add currency data from local JSON file
				presenter.apply {
					initSupportCurrencyList {
						doAsync {
							// 更新分享的文案内容
							updateShareContentFromServer()
							// 更新用户条款如果有必要
							updateAgreement()
							// Insert support currency list from local json
							updateCurrencyRateFromServer(this@config)
						}
						// Check network to get default toke list
						initDefaultTokenByNetWork {
							prepareYingYongBaoInReviewStatus {
								hasAccountThenLogin()
							}
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

	private fun updateAgreement() {
		AppConfigTable.getAppConfig { it ->
			it?.apply {
				val md5 = terms.getObjectMD5HexString()
				GoldStoneAPI.getTerms(md5, {
					LogUtil.error("updateAgreement", it)
				}) {
					if (it.isNotEmpty()) {
						AppConfigTable.updateTerms(it)
					}
				}
			}
		}
	}

	private fun prepareAppConfig(callback: AppConfigTable.() -> Unit) {
		AppConfigTable.getAppConfig { config ->
			config.isNull() isTrue {
				AppConfigTable.insertAppConfig {
					AppConfigTable.getAppConfig {
						it?.apply {
							callback(this)
						}
					}
				}
			} otherwise {
				config?.isRegisteredAddresses?.isFalse {
					/**
					 * 如果之前因为失败原因 `netWork`, `Server` 等注册地址失败, 在这里
					 * 检测并重新注册
					 */
					XinGePushReceiver.registerAddressesForPush()
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
		currentLanguage = code
		Config.updateCurrentLanguageCode(code)
	}

	private fun ViewGroup.initWaveView() {
		waveView.apply {
			setShapeType(WaveLoadingView.ShapeType.RECTANGLE)
			progressValue = 35
			waveColor = Color.parseColor("#FF19769D")
			setAnimDuration(30000)
			setAmplitudeRatio(30)
			startAnimation()
		}.into(this)
	}
}