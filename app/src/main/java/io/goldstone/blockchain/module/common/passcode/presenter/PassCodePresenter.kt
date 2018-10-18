package io.goldstone.blockchain.module.common.passcode.presenter

import android.annotation.SuppressLint
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import com.blinnnk.extension.*
import io.goldstone.blockchain.common.base.basefragment.BaseFragment
import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.common.base.baseoverlayfragment.BaseOverlayFragment
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerFragment
import io.goldstone.blockchain.common.language.CommonText
import io.goldstone.blockchain.common.language.PincodeText
import io.goldstone.blockchain.common.utils.alert
import io.goldstone.blockchain.common.utils.getMainActivity
import io.goldstone.blockchain.common.value.Count
import io.goldstone.blockchain.kernel.commonmodel.AppConfigTable
import io.goldstone.blockchain.kernel.commonmodel.AppConfigTable.Companion.setPinCodeStatus
import io.goldstone.blockchain.module.common.passcode.view.PassCodeFragment
import io.goldstone.blockchain.module.entrance.splash.view.SplashActivity
import io.goldstone.blockchain.module.home.home.view.HomeFragment
import io.goldstone.blockchain.module.home.home.view.MainActivity
import io.goldstone.blockchain.module.home.wallet.walletdetail.view.WalletDetailFragment
import org.jetbrains.anko.support.v4.toast

/**
 * @date 23/04/2018 11:04 AM
 * @author KaySaith
 * @rewriteDate 11/09/2018 3:11 PM
 * @reWriter wcx
 * @description 添加指纹解锁相关逻辑和界面
 */
class PassCodePresenter(override val fragment : PassCodeFragment) : BasePresenter<PassCodeFragment>() {

	private var currentFrozenTime = 0L
	private val handler = Handler()
	private var passCode = ""

	fun unlockOrAlert(
		passCode : String,
		action : () -> Unit
	) {
		if(passCode.length >= Count.pinCode) {
			AppConfigTable.getAppConfig { it ->
				val retryTimes = it?.retryTimes.orZero()
				if(fragment.getIsVerifyIdentity()) {
					// 是否再一次输入新密码
					IsEnterYourNewPasswordAgain(passCode)
				} else {
					checkPassCode(passCode) {
						checkPassCodeEvent(
							it,
							retryTimes
						)
					}
				}
			}
		}
		action()
	}

	private fun checkPassCodeEvent(
		it : Boolean,
		retryTime : Int
	) {
		var retryTimes = retryTime
		it isTrue {
			if(retryTimes < Count.retry) resetConfig()
			if(fragment.getIsSetPinCode().orFalse()) {
				fragment.setIsVerifyIdentity(true)
				fragment.setPasswordInputTitles(
					PincodeText.setFourDigitPassword,
					""
				)
				fragment.resetHeaderStyle()
			} else {
				fragment.setIsVerifyIdentity(true)
				fragment.removePassCodeFragment()
			}
		} otherwise {
			retryTimes -= 1
			AppConfigTable.updateRetryTimes(retryTimes)
			if(retryTimes == 0) {
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
				fragment.showFailedAttention(PincodeText.failedAttention(retryTimes))
			}
		}
	}

	private fun IsEnterYourNewPasswordAgain(passCode : String) {
		if(fragment.getIsEnterYourNewPasswordAgain()) {
			checkNewPassword(passCode)
		} else {
			this.passCode = passCode
			fragment.resetHeaderStyle()
			fragment.setPasswordInputTitles(
				PincodeText.resetTheFour_digitPassword,
				PincodeText.ifThePasswordInputIsInconsistentPleaseRe_enter
			)
			fragment.setIsEnterYourNewPasswordAgain(true)
		}
	}

	private fun checkNewPassword(passCode : String) {
		if(this.passCode == passCode) {
			AppConfigTable.updatePinCode(passCode.toInt()) {
				fragment.context?.alert(CommonText.succeed)
				setPinCodeStatus(true) {
					fragment.removePassCodeFragment()
				}
			}
		} else {
			fragment.resetHeaderStyle()
			fragment.toast(PincodeText.ifThePasswordInputIsInconsistentPleaseRe_enter)
		}
	}

	fun isFrozenStatus(callback : (Boolean) -> Unit = {}) {
		AppConfigTable.getAppConfig {
			it?.frozenTime.isNull() isFalse {
				currentFrozenTime = it?.frozenTime.orElse(0L) - System.currentTimeMillis()
				if(currentFrozenTime > 0) {
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

	private val refreshRunnable : Runnable by lazy {
		Runnable {
			currentFrozenTime -= 1000L
			fragment.showFailedAttention(setRemainingFrozenTime(currentFrozenTime))
			if(currentFrozenTime > 0) {
				handler.postDelayed(refreshRunnable,1000L)
			} else {
				resetConfig()
				fragment.recoveryAfterFrezon()
			}
		}
	}

	override fun onFragmentDetach() {
		handler.removeCallbacks(refreshRunnable)

		fun AppCompatActivity.recoverBackEventAfterPinCode() {
			supportFragmentManager.fragments.last()?.apply {
				when(this) {
					is HomeFragment -> {
						(childFragmentManager.fragments.last() as? BaseRecyclerFragment<*,*>)?.apply {
							if(this is WalletDetailFragment) {
								getMainActivity()?.backEvent = null
							} else {
								recoveryBackEvent()
							}
						}
					}

					is BaseFragment<*> -> recoveryBackEvent()
					is BaseRecyclerFragment<*,*> -> recoveryBackEvent()

					is BaseOverlayFragment<*> -> {
						if(!fragment.getIsSetPinCode().orFalse() && !fragment.getIsVerifyIdentity()) {
							this.presenter.removeSelfFromActivity()
						} else {
							childFragmentManager.fragments.last()?.apply {
								when(this) {
									is BaseFragment<*> -> {
										showChildFragment(this)
										recoveryBackEvent()
									}
									is BaseRecyclerFragment<*,*> -> recoveryBackEvent()
								}
							}
						}
					}
				}
			}
		}
		fragment.activity?.apply {
			when(this) {
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
	private fun setRemainingFrozenTime(currentFrozenTime : Long) : String {
		return PincodeText.remainingFrozenTime(currentFrozenTime)
	}

	private fun checkPassCode(
		passCode : String,
		hold : (Boolean) -> Unit
	) {
		if(passCode.length >= Count.pinCode)
		// 从数据库获取本机的 `Passcode`
			AppConfigTable.getAppConfig {
				if(it?.pincode == passCode.toInt()) {
					hold(true)
				} else {
					hold(false)
				}
			}
	}
}
