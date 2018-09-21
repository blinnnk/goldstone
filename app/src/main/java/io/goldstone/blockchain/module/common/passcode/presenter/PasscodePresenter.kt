package io.goldstone.blockchain.module.common.passcode.presenter

import android.annotation.SuppressLint
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import com.blinnnk.animation.updateAlphaAnimation
import com.blinnnk.extension.*
import io.goldstone.blockchain.common.base.basefragment.BaseFragment
import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.common.base.baseoverlayfragment.BaseOverlayFragment
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerFragment
import io.goldstone.blockchain.common.utils.getMainActivity
import io.goldstone.blockchain.common.value.Count
import io.goldstone.blockchain.kernel.commonmodel.AppConfigTable
import io.goldstone.blockchain.module.common.passcode.view.PasscodeFragment
import io.goldstone.blockchain.module.entrance.splash.view.SplashActivity
import io.goldstone.blockchain.module.home.home.view.HomeFragment
import io.goldstone.blockchain.module.home.home.view.MainActivity
import io.goldstone.blockchain.module.home.wallet.walletdetail.view.WalletDetailFragment

/**
 * @date 23/04/2018 11:04 AM
 * @author KaySaith
 */
class PasscodePresenter(
	override val fragment: PasscodeFragment
) : BasePresenter<PasscodeFragment>() {

	private var currentFrozenTime = 0L
	private val handler = Handler()

	fun unlockOrAlert(passcode: String, action: () -> Unit) {
		AppConfigTable.getAppConfig { it ->
			var retryTimes = it?.retryTimes.orZero()
			checkPasscode(passcode) {
				it isTrue {
					if (retryTimes < Count.retry) resetConfig()
					fragment.removePasscodeFragment()
				} otherwise {
					retryTimes -= 1
					AppConfigTable.updateRetryTimes(retryTimes)
					if (retryTimes == 0) {
						// 如果失败尝试超过 `Count.retry` 次, 那么将会存入冻结时间 `1` 分钟
						val oneMinute = 60 * 1000L
						AppConfigTable.setFrozenTime(System.currentTimeMillis() + oneMinute) {
							currentFrozenTime = oneMinute
							refreshRunnable.run()
						}
						// 进入冻结状态后恢复重试次数
						resetConfig()
					} else {
						fragment.resetHeaderStyle()
						fragment.showFailedAttention("incorrect passcode $retryTimes retry times left")
					}
				}
			}
			action()
		}
	}

	fun isFrozenStatus(callback: (Boolean) -> Unit = {}) {
		AppConfigTable.getAppConfig {
			it?.frozenTime.isNull() isFalse {
				currentFrozenTime = it?.frozenTime.orElse(0L) - System.currentTimeMillis()
				if (currentFrozenTime > 0) {
					refreshRunnable.run()
					callback(true)
				} else {
					resetConfig()
					callback(false)
				}
			} otherwise {
				resetConfig()
				callback(false)
			}
		}
	}

	private val refreshRunnable: Runnable by lazy {
		Runnable {
			currentFrozenTime -= 1000L
			fragment.showFailedAttention(setRemainingFrozenTime(currentFrozenTime))
			if (currentFrozenTime > 0) {
				handler.postDelayed(refreshRunnable, 1000L)
			} else {
				resetConfig()
				fragment.recoveryAfterFreeze()
			}
		}
	}

	override fun onFragmentDetach() {
		handler.removeCallbacks(refreshRunnable)
	}

	private fun AppCompatActivity.recoverBackEventAfterPinCode() {
		supportFragmentManager.fragments.last()?.apply {
			when (this) {
				is HomeFragment -> {
					(childFragmentManager.fragments.last() as? BaseRecyclerFragment<*, *>)?.apply {
						if (this is WalletDetailFragment) {
							getMainActivity()?.backEvent = null
						} else {
							recoveryBackEvent()
						}
					}
				}

				is BaseFragment<*> -> recoveryBackEvent()
				is BaseRecyclerFragment<*, *> -> recoveryBackEvent()

				is BaseOverlayFragment<*> -> {
					childFragmentManager.fragments.last()?.apply {
						when (this) {
							is BaseFragment<*> -> recoveryBackEvent()
							is BaseRecyclerFragment<*, *> -> recoveryBackEvent()
						}
					}
				}
			}
		}
	}

	override fun onFragmentDestroy() {
		super.onFragmentDestroy()
		fragment.activity?.apply {
			when (this) {
				is SplashActivity -> recoverBackEventAfterPinCode()
				is MainActivity -> recoverBackEventAfterPinCode()
			}
		}
	}

	private fun resetConfig() {
		AppConfigTable.apply {
			updateRetryTimes(Count.retry)
			setFrozenTime(null)
		}
		currentFrozenTime = 0L
	}

	@SuppressLint("SetTextI18n")
	private fun setRemainingFrozenTime(currentFrozenTime: Long): String {
		return "you have to wait ${currentFrozenTime / 1000} seconds"
	}

	private fun PasscodeFragment.removePasscodeFragment(callback: () -> Unit = {}) {
		activity?.let {
			container.updateAlphaAnimation(0f) {
				it.supportFragmentManager.beginTransaction().remove(this).commitAllowingStateLoss()
				callback()
			}
		}
	}

	private fun checkPasscode(passcode: String, hold: (Boolean) -> Unit) {
		if (passcode.length >= Count.pinCode)
		// 从数据库获取本机的 `Passcode`
			AppConfigTable.getAppConfig {
				hold(it?.pincode == passcode.toInt())
			}
	}
}
