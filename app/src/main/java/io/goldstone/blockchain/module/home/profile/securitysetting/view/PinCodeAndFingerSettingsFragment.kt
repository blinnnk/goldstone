package io.goldstone.blockchain.module.home.profile.securitysetting.view

import android.content.Intent
import android.support.v4.app.Fragment
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Switch
import com.blinnnk.extension.*
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.base.basefragment.BaseFragment
import io.goldstone.blockchain.common.component.overlay.GoldStoneDialog
import io.goldstone.blockchain.common.component.title.AttentionView
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
import io.goldstone.blockchain.module.home.profile.securitysetting.presenter.PinCodeAndFingerSettingsPresenter
import org.jetbrains.anko.*

/**
 * @date 11/09/2018 3:45 PM
 * @author wcx
 */
class PinCodeAndFingerSettingsFragment: BaseFragment<PinCodeAndFingerSettingsPresenter>() {
	override val pageTitle: String = ProfileText.walletSecurity

	private val changePinCode by lazy { LinearLayout(context) }
	private lateinit var pinCodeSwitch: SecuritySwitchView
	override val presenter = PinCodeAndFingerSettingsPresenter(this)

	override fun AnkoContext<Fragment>.initView() {
		verticalLayout {
			gravity = Gravity.CENTER_HORIZONTAL
			lparams(matchParent, matchParent)
			if(SharedWallet.isPincodeOpened().orFalse() || SharedWallet.isFingerprintUnlockerOpened().orFalse()) {
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
				setSwitchStatus(SharedWallet.isFingerprintUnlockerOpened().orFalse())
				setOnclick { switch ->
					if(getSwitchCheckedStatus()) {
						if(checkIfTheSystemFingerprintExists().isAvailable()) {
							// 系统已设置指纹
							openFingerprintEvent(switch)
						} else {
							setFingerprintTips()
							switch.isChecked = !switch.isChecked
						}
					} else {
						openFingerprintEvent(switch)
					}
				}
				setTitle(FingerprintUnlockText.fingerprintUnlock, "")
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
				if(getSwitchCheckedStatus()) {
					presenter.setPassCodeFragment()
					pinCodeSwitch.setSwitchStatus(false)
				} else {
					changePinCode.visibility = View.GONE
					AppConfigTable.setPinCodeStatus(false) {}
				}
			}
			setTitle(PincodeText.show, "")
		}
		pinCodeSwitch.into(this)

		changePinCode.apply {
			layoutParams = ViewGroup.LayoutParams(
				ScreenSize.widthWithPadding,
				wrapContent
			)
			orientation = LinearLayout.VERTICAL
			textView {
				textSize = fontSize(12)
				typeface = GoldStoneFont.black(context)
				textColor = GrayScale.gray
				text = PincodeText.setPinCode
				layoutParams = LinearLayout.LayoutParams(
					wrapContent,
					wrapContent
				).apply {
					topMargin = 8.uiPX()
				}
			}

			SecuritySwitchView(context).apply {
				setOnclick {
					// 点击后根据更新的数据库情况显示开关状态
					presenter.setPassCodeFragment()
				}

				setTitle(PincodeText.changePinCode, "")
			}.into(this)
		}.into(this)
	}

	private fun setPinCodeSingleLineSwitchStatus(isChecked: Boolean) {
		pinCodeSwitch.setSwitchStatus(isChecked)
	}

	private fun setChangePinCodeVisibility() {
		changePinCode.visibility = View.VISIBLE
	}

	private fun checkIfTheSystemFingerprintExists(): FingerprintAvailableStatus {
		context?.let {
			val fingerprintHelper = FingerprintHelper(it)
			return fingerprintHelper.checkIfTheFingerprintIsAvailable()
		}
		return FingerprintAvailableStatus.HardwareDidNotSupport
	}

	// 点击后根据更新的数据库情况显示指紋解锁开关状态
	private fun openFingerprintEvent(switch: Switch) {
		presenter.setFingerprintStatus(switch.isChecked) {
			switch.isChecked = SharedWallet.isFingerprintUnlockerOpened().orFalse()
			if(!pinCodeSwitch.getSwitchCheckedStatus().orFalse() && switch.isChecked) {
				setPinCodeTips()
			}
		}
	}

	// 设置数字密码弹窗
	private fun setPinCodeTips() {
		context?.let {
			GoldStoneDialog(it).showPinCodeTip {
				presenter.setPassCodeFragment()
			}
		}
	}

	// 设置指纹密码弹窗
	private fun setFingerprintTips() {
		context?.let {
			GoldStoneDialog(it).showFingerPrintTip {
				val intent = Intent("android.settings.SETTINGS")
				intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
				try {
					it.startActivity(intent)
				} catch(e: Exception) {
				}
			}
		}
	}

	override fun onHiddenChanged(hidden: Boolean) {
		if(!hidden) {
			// 设置新密码返回更新状态
			if(SharedWallet.isPincodeOpened().orFalse()) {
				setChangePinCodeVisibility()
				setPinCodeSingleLineSwitchStatus(true)
			}
		}
	}

	override fun setBaseBackEvent(
		activity: MainActivity?,
		parent: Fragment?
	) {
		getParentFragment<ProfileOverlayFragment> {
			presenter.removeSelfFromActivity()
		}
	}
}