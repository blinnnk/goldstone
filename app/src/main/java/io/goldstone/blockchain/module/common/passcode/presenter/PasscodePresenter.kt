package io.goldstone.blockchain.module.common.passcode.presenter

import android.annotation.SuppressLint
import android.os.Handler
import android.support.annotation.WorkerThread
import android.support.v7.app.AppCompatActivity
import com.blinnnk.animation.updateAlphaAnimation
import com.blinnnk.extension.orZero
import com.blinnnk.util.load
import com.blinnnk.util.then
import io.goldstone.blockchain.common.base.basefragment.BaseFragment
import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.common.base.baseoverlayfragment.BaseOverlayFragment
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerFragment
import io.goldstone.blockchain.common.base.gsfragment.GSRecyclerFragment
import io.goldstone.blockchain.common.thread.launchUI
import io.goldstone.blockchain.common.utils.getMainActivity
import io.goldstone.blockchain.common.value.Count
import io.goldstone.blockchain.kernel.commontable.AppConfigTable
import io.goldstone.blockchain.module.common.passcode.view.PasscodeFragment
import io.goldstone.blockchain.module.entrance.splash.view.SplashActivity
import io.goldstone.blockchain.module.home.home.view.HomeFragment
import io.goldstone.blockchain.module.home.home.view.MainActivity
import io.goldstone.blockchain.module.home.wallet.walletdetail.view.WalletDetailFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * @date 23/04/2018 11:04 AM
 * @author KaySaith
 */
class PasscodePresenter(override val fragment: PasscodeFragment) : BasePresenter<PasscodeFragment>() {

	private var currentFrozenTime = 0L
	private val handler = Handler()

	/**
	 * @Important
	 * 因为 Pincode 的众多安全判断标记都采用数据库标记而不是相对不安全的 内存 或 SP
	 * 标记, 所以每次解锁都要从数据库获取最新的值. 这里的传入参数不能采用内存来进行
	 * 便利. 必须每次开库检查.
	 */
	@WorkerThread
	fun unlockOrAlert(passcode: String) = GlobalScope.launch(Dispatchers.Default) {
		val config = AppConfigTable.dao.getAppConfig() ?: return@launch
		var retryTimes = config.retryTimes.orZero()
		checkPasscode(config, passcode) { isCorrect ->
			if (isCorrect) {
				if (retryTimes < Count.retry) resetConfig()
				launchUI {
					fragment.removePasscodeFragment()
				}
			} else {
				retryTimes -= 1
				AppConfigTable.dao.updateRetryTimes(retryTimes)
				if (retryTimes == 0) {
					// 如果失败尝试超过 `Count.retry` 次, 那么将会存入冻结时间 `1` 分钟
					val oneMinute = 60L
					load {
						AppConfigTable.dao.updateFrozenTime(oneMinute)
					} then {
						fragment.disableKeyboard(true)
						currentFrozenTime = oneMinute
						refreshRunnable.run()
					}
				} else launchUI {
					fragment.resetHeaderStyle()
					fragment.showFailedAttention("incorrect passcode $retryTimes retry times left")
				}
			}
		}
	}

	fun isFrozenStatus(@WorkerThread callback: (isFrozen: Boolean) -> Unit) {
		AppConfigTable.getAppConfig(Dispatchers.Main) {
			val config = it ?: return@getAppConfig
			if (config.frozenTime > 0) {
				currentFrozenTime = config.frozenTime
				if (currentFrozenTime > 0) {
					refreshRunnable.run()
					callback(true)
				}
			} else {
				resetConfig()
				callback(false)
			}
		}
	}

	private val refreshRunnable: Runnable by lazy {
		Runnable {
			currentFrozenTime -= 1
			fragment.showFailedAttention(setRemainingFrozenTime(currentFrozenTime))
			if (currentFrozenTime > 0) {
				GlobalScope.launch(Dispatchers.Default) {
					AppConfigTable.dao.updateFrozenTime(currentFrozenTime)
				}
				handler.postDelayed(refreshRunnable, 1000L)
			} else {
				resetConfig()
				fragment.disableKeyboard(false)
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
					(childFragmentManager.fragments.last() as? GSRecyclerFragment<*>)?.apply {
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

	@WorkerThread
	private fun resetConfig() = GlobalScope.launch(Dispatchers.Default) {
		AppConfigTable.dao.updateRetryTimes(Count.retry)
		AppConfigTable.dao.updateFrozenTime(0)
		currentFrozenTime = 0L
	}

	@SuppressLint("SetTextI18n")
	private fun setRemainingFrozenTime(currentFrozenTime: Long): String {
		return "you have to wait $currentFrozenTime seconds"
	}

	private fun PasscodeFragment.removePasscodeFragment(callback: () -> Unit = {}) {
		activity?.let {
			container.updateAlphaAnimation(0f) {
				it.supportFragmentManager.beginTransaction().remove(this).commitAllowingStateLoss()
				callback()
			}
		}
	}

	private fun checkPasscode(config: AppConfigTable, passcode: String, hold: (Boolean) -> Unit) {
		// 从数据库获取本机的 `Passcode`
		if (passcode.length >= Count.pinCode) hold(config.pincode == passcode.toInt())
	}
}
