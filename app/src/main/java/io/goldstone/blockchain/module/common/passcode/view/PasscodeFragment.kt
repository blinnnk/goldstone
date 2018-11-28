package io.goldstone.blockchain.module.common.passcode.view

import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.blinnnk.animation.updateAlphaAnimation
import com.blinnnk.extension.*
import com.blinnnk.uikit.ScreenSize
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.SoftKeyboard
import io.goldstone.blockchain.R
import io.goldstone.blockchain.common.base.basefragment.BaseFragment
import io.goldstone.blockchain.common.component.GradientType
import io.goldstone.blockchain.common.component.GradientView
import io.goldstone.blockchain.common.component.overlay.GoldStoneDialog
import io.goldstone.blockchain.common.error.WalletSecurityError
import io.goldstone.blockchain.common.language.FingerprintUnlockText
import io.goldstone.blockchain.common.language.PincodeText
import io.goldstone.blockchain.common.sharedpreference.SharedWallet
import io.goldstone.blockchain.common.utils.FingerprintHelper
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.value.*
import io.goldstone.blockchain.kernel.commonmodel.AppConfigTable
import io.goldstone.blockchain.module.common.passcode.presenter.PasscodePresenter
import io.goldstone.blockchain.module.home.home.view.MainActivity
import io.goldstone.blockchain.module.home.profile.profileoverlay.view.ProfileOverlayFragment
import kotlinx.coroutines.Dispatchers
import org.jetbrains.anko.*

/**
 * @date 23/04/2018 11:04 AM
 * @author KaySaith
 * @rewriteDate 11/09/2018 3:11 PM
 * @reWriter wcx
 * @description 添加指纹解锁相关逻辑和界面
 */
class PasscodeFragment: BaseFragment<PasscodePresenter>() {
	override val pageTitle: String = PincodeText.setTheDigitalLock
	private val isPinCodeSetting by lazy { arguments?.getBoolean(ArgumentKey.setPinCode) }
	private val disableTheBackButtonToExit by lazy { arguments?.getBoolean(ArgumentKey.disableTheBackButtonToExit) }
	lateinit var container: RelativeLayout
	private val keyboard by lazy { NumberKeyboard(context!!) }
	private val passcodeInput by lazy { PasscodeInput(context!!) }
	private var failedAttention: TextView? = null
	private var isPinCode = false
	private var isTwoVerificationMethods = false
	private var isVerifyIdentity = false
	private var isEnterYourNewPasswordAgain = false
	
	private val fingerprintHelper by lazy { FingerprintHelper(context!!) }
	override val presenter = PasscodePresenter(this)
	
	override fun AnkoContext<Fragment>.initView() {
		setParentBackButton()
		container = relativeLayout {
			isClickable = true
			lparams(matchParent, matchParent)
			GradientView(context).apply {
				if(isPinCodeSetting.orFalse()) {
					setStyle(GradientType.BlueGreen)
				} else {
					setStyle(GradientType.Blue)
				}
				lparams(matchParent, matchParent)
			}.into(this)
			
			passcodeInput.apply {
				if(isPinCodeSetting.orFalse()) {
					y += ScreenSize.Height * 0.07f
					AppConfigTable.getAppConfig(Dispatchers.Main) { it ->
						if(it?.pincode.isNull()) {
							isVerifyIdentity = true
							setPasswordInputTitles(
								PincodeText.setFourDigitPassword,
								""
							)
						} else {
							setPasswordInputTitles(
								PincodeText.needToVerifyYourIdentity,
								PincodeText.pleaseEnterYourCurrentNumericPassword
							)
						}
					}
				} else {
					determineTheInterfaceMode {
						y += if(isTwoVerificationMethods) {
							ScreenSize.Height * 0.072f
						} else {
							ScreenSize.Height * 0.11f
						}
					}
				}
			}.into(this)
			
			if(!isPinCodeSetting.orFalse()) {
				determineTheInterfaceMode {
					initializePasswordInput()
					if(isTwoVerificationMethods) {
						val switchVerificationMethodLinearLayout = LinearLayout(context)
						switchVerificationMethodLinearLayout.into(this)
						switchVerificationMethodLinearLayout.apply {
							orientation = LinearLayout.VERTICAL
							layoutParams = RelativeLayout.LayoutParams(wrapContent, wrapContent)
							y += ScreenSize.Height * 0.27f
							centerInHorizontal()
							gravity = Gravity.CENTER_HORIZONTAL
							
							imageView {
								layoutParams = LinearLayout.LayoutParams(42.uiPX(), 42.uiPX())
								setImageDrawable(ContextCompat.getDrawable(
									context,
									R.drawable.fingerprint_recognition
								))
								setColorFilter(Spectrum.white)
								alpha = 0.3f
							}
						}
					} else if(!isPinCode) {
						loadFingerprintImage(this)
					}
				}
			}
			
			keyboard.into(this)
			keyboard.apply {
				layoutParams = RelativeLayout.LayoutParams(wrapContent, wrapContent)
				centerInHorizontal()
				alignParentBottom()
				y -= ScreenSize.Height * 0.113f
				setKeyboardClickEventByFrozenStatus()
			}
		}
	}
	
	private fun setParentBackButton() {
		if(isPinCodeSetting.orFalse()) {
			getParentFragment<ProfileOverlayFragment> {
				getContainer().header.showCloseButton(false) { }
				getContainer().header.showBackButton(true) {
					if(isEnterYourNewPasswordAgain) {
						isEnterYourNewPasswordAgain = false
						setPasswordInputTitles(
							PincodeText.setFourDigitPassword,
							""
						)
					} else {
						presenter.removeSelfFromActivity()
					}
				}
			}
		}
	}
	
	private fun loadFingerprintImage(container: @AnkoViewDslMarker _RelativeLayout) {
		val switchVerificationMethodLinearLayout = LinearLayout(context)
		switchVerificationMethodLinearLayout.into(container)
		switchVerificationMethodLinearLayout.apply {
			orientation = LinearLayout.VERTICAL
			layoutParams = RelativeLayout.LayoutParams(wrapContent, wrapContent)
			y += ScreenSize.Height * 0.27f
			centerInHorizontal()
			gravity = Gravity.CENTER_HORIZONTAL
			
			imageView {
				if(isTwoVerificationMethods) {
					layoutParams = LinearLayout.LayoutParams(42.uiPX(), 42.uiPX())
					setColorFilter(Spectrum.white)
					alpha = 0.3f
				} else if(!isPinCode) {
					layoutParams = LinearLayout.LayoutParams(140.uiPX(), 140.uiPX())
					setColorFilter(Spectrum.white)
					alpha = 0.3f
				}
				setImageDrawable(ContextCompat.getDrawable(
					context,
					R.drawable.fingerprint_recognition
				))
				setColorFilter(Spectrum.white)
				alpha = 0.3f
			}
		}
	}
	
	private fun determineTheInterfaceMode(callback: () -> Unit) {
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			SharedWallet.isPincodeOpened().orFalse().isTrue {
				SharedWallet.isFingerprintUnlockerOpened().orFalse().isTrue {
					isTwoVerificationMethods = true
				}
			}.otherwise {
				SharedWallet.isFingerprintUnlockerOpened().orFalse().isTrue {
					isPinCode = true
				}
			}
			callback()
		}
	}
	
	private fun initializePasswordInput() {
		if(isTwoVerificationMethods) {
			startFingerprintUnlock()
			setPasswordInputTitles(
				PincodeText.needToVerifyYourIdentity,
				PincodeText.pleaseEnterYourCurrentNumericPassword
			)
		} else {
			if(isPinCode) {
				isPinCode = false
				keyboard.visibility = View.GONE
				passcodeInput.setTitles(
					FingerprintUnlockText.checkFingerprint,
					"",
					false
				)
				startFingerprintUnlock()
			} else {
				fingerprintHelper.stopAuthenticate()
				recoveryAfterFrezon()
				isPinCode = true
				keyboard.visibility = View.VISIBLE
				setPasswordInputTitles(
					PincodeText.needToVerifyYourIdentity,
					PincodeText.pleaseEnterYourCurrentNumericPassword
				)
			}
		}
	}
	
	private fun startFingerprintUnlock() {
		fingerprintHelper.stopAuthenticate()
		fingerprintHelper.setAuthenticationCallback(object: FingerprintHelper.AuthenticationCallback {
			override fun onAuthenticationSucceeded(value: String) {
				isVerifyIdentity = true
				recoveryAfterFrezon()
				removePassCodeFragment()
			}
			
			override fun onAuthenticationFail(
				errorCode: Int,
				errString: CharSequence
			) {
			}
			
			override fun onAuthenticationFailed() {
				showFailedAttention(FingerprintUnlockText.tryAgain)
				startFingerprintUnlock()
			}
			
			override fun onAuthenticationHelp(
				helpCode: Int,
				helpString: CharSequence
			) {
			}
		})
		fingerprintHelper.startFingerprintUnlock() {
			if(it.content != WalletSecurityError.None.content) {
				context?.toast(it.content)
			}
		}
	}
	
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		activity?.apply { SoftKeyboard.hide(this) }
	}
	
	fun resetHeaderStyle() {
		keyboard.resetCode()
		passcodeInput.swipe()
	}
	
	fun showFailedAttention(content: String) {
		failedAttention.isNull() isFalse {
			failedAttention?.text = content
		} otherwise {
			failedAttention = TextView(context).apply {
				y += 30.uiPX()
				layoutParams = RelativeLayout.LayoutParams(
					matchParent,
					20.uiPX()
				)
				textSize = fontSize(12)
				textColor = Spectrum.red
				typeface = GoldStoneFont.medium(context)
				text = content
				gravity = Gravity.CENTER_HORIZONTAL
			}
			failedAttention?.into(container)
		}
	}
	
	fun recoveryAfterFrezon() {
		failedAttention?.let { container.removeView(it) }
		keyboard.setKeyboardClickEventByFrozenStatus()
		failedAttention = null
		resetHeaderStyle()
	}
	
	// 检查是否处于冻结状态
	private fun NumberKeyboard.setKeyboardClickEventByFrozenStatus() {
		presenter.isFrozenStatus { isFrozen ->
			checkCode = Runnable {
				if (!isFrozen) {
					disableKeyboard(false)
					if (getEnteredCode().length == Count.pinCode) presenter.unlockOrAlert(getEnteredCode())
					if (getEnteredCode().isEmpty()) passcodeInput.recoveryStyle()
					else passcodeInput.setEnteredStyle(getEnteredCode().lastIndex)
				}
			}
		}
	}
	
	fun removePassCodeFragment(callback: () -> Unit = {}) {
		activity?.let {
			container.updateAlphaAnimation(0f) {
				if(isPinCodeSetting.orFalse()) {
					this.getParentFragment<ProfileOverlayFragment> {
						this.presenter.removeSelfFromActivity()
					}
				} else {
					it.supportFragmentManager.beginTransaction().remove(this).commitAllowingStateLoss()
				}
				callback()
				fingerprintHelper.stopAuthenticate()
			}
		}
	}
	
	fun setPasswordInputTitles(
		title: String,
		subtitle: String
	) {
		passcodeInput.setTitles(
			title,
			subtitle,
			true
		)
	}
	
	fun getIsPinCodeSetting(): Boolean? {
		return isPinCodeSetting
	}
	
	fun setIsVerifyIdentity(isVerifyIdentity: Boolean) {
		this.isVerifyIdentity = isVerifyIdentity
	}
	
	fun getIsVerifyIdentity(): Boolean {
		return isVerifyIdentity
	}
	
	fun setIsEnterYourNewPasswordAgain(isEnterYourNewPasswordAgain: Boolean) {
		this.isEnterYourNewPasswordAgain = isEnterYourNewPasswordAgain
	}
	
	fun getIsEnterYourNewPasswordAgain(): Boolean {
		return isEnterYourNewPasswordAgain
	}
	
	private fun backEvent() {
		if(!disableTheBackButtonToExit.orFalse()) {
			removePassCodeFragment()
		} else {
			showBackToHomeConfirmationDialog()
		}
	}
	fun disableKeyboard(status: Boolean) {
		keyboard.disableKeyboard(status)
	}
	fun recoveryAfterFreeze() {
		failedAttention?.let {
			container.removeView(it)
		}
		keyboard.setKeyboardClickEventByFrozenStatus()
		failedAttention = null
		resetHeaderStyle()
	}
	private fun showBackToHomeConfirmationDialog() {
		context?.apply {
			GoldStoneDialog(this).showBackHomeDialog {
				activity?.finish()
			}
		}
	}
	
	override fun setBaseBackEvent(
		activity: MainActivity?,
		parent: Fragment?
	) {
		if(isPinCodeSetting.orFalse() || isPinCode) {
			val enteredCode = keyboard.getEnteredCode()
			if(enteredCode.isNotEmpty()) {
				keyboard.setEnteredCode(enteredCode.substring(
					0,
					enteredCode.lastIndex
				))
			} else {
				backEvent()
			}
		} else {
			backEvent()
		}
	}
	
	companion object {
		fun show(fragment: Fragment) {
			if (fragment.activity?.supportFragmentManager?.findFragmentByTag(FragmentTag.pinCode).isNull())
				fragment.activity?.addFragment<PasscodeFragment>(ContainerID.main, FragmentTag.pinCode)
		}
	}
}