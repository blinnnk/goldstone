package io.goldstone.blockchain.module.home.profile.securitysetting.view

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.*
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.blinnnk.extension.*
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.R
import io.goldstone.blockchain.common.base.gsfragment.GSFragment
import io.goldstone.blockchain.common.component.overlay.GoldStoneDialog
import io.goldstone.blockchain.common.component.title.AttentionView
import io.goldstone.blockchain.common.component.title.TwoLineTitles
import io.goldstone.blockchain.common.language.FingerprintUnlockText
import io.goldstone.blockchain.common.language.PincodeText
import io.goldstone.blockchain.common.language.ProfileText
import io.goldstone.blockchain.common.sharedpreference.SharedWallet
import io.goldstone.blockchain.common.utils.FingerprintAvailableStatus
import io.goldstone.blockchain.common.utils.FingerprintHelper
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.value.GrayScale
import io.goldstone.blockchain.common.value.ScreenSize
import io.goldstone.blockchain.common.value.Spectrum
import io.goldstone.blockchain.common.value.fontSize
import io.goldstone.blockchain.kernel.commonmodel.AppConfigTable
import io.goldstone.blockchain.module.home.home.view.MainActivity
import io.goldstone.blockchain.module.home.profile.profileoverlay.view.ProfileOverlayFragment
import io.goldstone.blockchain.module.home.profile.securitysetting.contract.PinCodeAndFingerContract
import io.goldstone.blockchain.module.home.profile.securitysetting.presenter.PinCodeAndFingerSettingsPresenter
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.support.v4.UI

/**
 * @date 11/09/2018 3:45 PM
 * @author wcx
 */
class PinCodeAndFingerSettingsFragment: GSFragment(),PinCodeAndFingerContract.GSView {
	
	override fun showError(error: Throwable) {
	
	}
	override val pageTitle: String = ProfileText.walletSecurity

	private val changePinCode by lazy { LinearLayout(context) }
	private lateinit var pinCodeSwitch: SecuritySwitchView
	override val presenter = PinCodeAndFingerSettingsPresenter(this)
	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		return UI {
			initView()
		}.view
	}
	private fun AnkoContext<Fragment>.initView() {
		verticalLayout {
			gravity = Gravity.CENTER_HORIZONTAL
			lparams(matchParent, matchParent)
			if(SharedWallet.isPincodeOpened() || SharedWallet.isFingerprintUnlockerOpened()) {
				presenter.showVerifyPinCodeFragment()
			}

			AttentionView(context).apply {
				text = FingerprintUnlockText.attention
				textSize = fontSize(13)
				textColor = Spectrum.white
				setMargins<LinearLayout.LayoutParams> {
					topMargin = 20.uiPX()
				}
			}.into(this)

			initSwitchCell()
		}
	}

	private fun ViewGroup.initSwitchCell() {
		if(!checkIfTheSystemFingerprintExists().hardwareIsUnsupported()) {
			// 指纹解锁
			SecuritySwitchView(context).apply {
				setSwitchStatus(SharedWallet.isFingerprintUnlockerOpened())
				setOnclick {
					notifyFingerStatus()
				}
				setTitle(FingerprintUnlockText.fingerprintUnlock, "sub title")
			}.into(this)
		}
		
		pinCodeSwitch = SecuritySwitchView(context).apply {
			setSwitchStatus(SharedWallet.isPincodeOpened())
			if(SharedWallet.isPincodeOpened()) {
				changePinCode.visibility = View.VISIBLE
			} else {
				changePinCode.visibility = View.GONE
			}
			setOnclick {
				// 点击后跳转到PinCode编辑界面
				notifyPincodeStatus()
			}
			setTitle(PincodeText.show, "sub title")
		}
		pinCodeSwitch.into(this)

		changePinCode.apply {
			layoutParams = ViewGroup.LayoutParams(ScreenSize.widthWithPadding, wrapContent)
			orientation = LinearLayout.VERTICAL
			textView {
				textSize = fontSize(12)
				typeface = GoldStoneFont.black(context)
				textColor = GrayScale.gray
				text = PincodeText.setPinCode
				layoutParams = LinearLayout.LayoutParams(wrapContent, wrapContent).apply {
					topMargin = 8.uiPX()
				}
			}
			
			relativeLayout {
				layoutParams = RelativeLayout.LayoutParams(matchParent, 50.uiPX())
				leftPadding = 20.uiPX()
				TwoLineTitles(context).apply {
					centerInVertical()
					setBlackTitles()
					title.text = PincodeText.changePinCode
					subtitle.text = "sub title"
				}.into(this)
				
				imageView {
					layoutParams = RelativeLayout.LayoutParams(24.uiPX(), 24.uiPX())
					centerInVertical()
					alignParentRight()
					imageResource = R.drawable.arrow_icon
					setColorFilter(GrayScale.lightGray)
				}
				
				onClick {
					// 点击后根据更新的数据库情况显示开关状态
					presenter.setPassCodeFragment()
				}
			}
		}.into(this)
	}

	private fun setPinCodeSingleLineSwitchStatus(isChecked: Boolean) {
		pinCodeSwitch.setSwitchStatus(isChecked)
	}

	private fun setChangePinCodeVisibility() {
		changePinCode.visibility = View.VISIBLE
	}

	private fun checkIfTheSystemFingerprintExists(): FingerprintAvailableStatus {
		return FingerprintHelper.checkIfTheFingerprintIsAvailable()
	}
	
	override fun SecuritySwitchView.notifyFingerStatus() {
		if(getSwitchCheckedStatus()) {
			if(checkIfTheSystemFingerprintExists().isAvailable()) {
				// 系统已设置指纹
				openFingerprintEvent()
			} else {
				setFingerprintTips()
				setSwitchStatus(false)
			}
		} else {
			openFingerprintEvent()
		}
	}
	
	override fun SecuritySwitchView.notifyPincodeStatus() {
		if(getSwitchCheckedStatus()) {
			presenter.setPassCodeFragment()
			pinCodeSwitch.setSwitchStatus(false)
		} else {
			changePinCode.visibility = View.GONE
			AppConfigTable.setPinCodeStatus(false) {}
		}
	}

	// 点击后根据更新的数据库情况显示指紋解锁开关状态
	private fun SecuritySwitchView.openFingerprintEvent() {
		presenter.updateFingerStatus(getSwitchCheckedStatus()) {
			setSwitchStatus(SharedWallet.isFingerprintUnlockerOpened())
			if(!pinCodeSwitch.getSwitchCheckedStatus() && getSwitchCheckedStatus()) {
				setPinCodeTips()
			}
		}
	}

	// 设置数字密码弹窗
	override fun setPinCodeTips() {
		context?.let {
			GoldStoneDialog(it).showPinCodeTip {
				presenter.setPassCodeFragment()
			}
		}
	}

	// 设置指纹密码弹窗
	override fun setFingerprintTips() {
		context?.let {
			GoldStoneDialog(it).showFingerPrintTip {
				presenter.goToSettingFinger()
			}
		}
	}

	override fun onHiddenChanged(hidden: Boolean) {
		if(!hidden) {
			// 设置新密码返回更新状态
			if(SharedWallet.isPincodeOpened()) {
				setChangePinCodeVisibility()
				setPinCodeSingleLineSwitchStatus(true)
			}
		}
	}

	override fun setBaseBackEvent(activity: MainActivity?, parent: Fragment?) {
		getParentFragment<ProfileOverlayFragment> {
			presenter.removeSelfFromActivity()
		}
	}
}