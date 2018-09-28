package io.goldstone.blockchain.module.home.profile.walletsecurity.view

import android.content.Intent
import android.os.Build
import android.support.v4.app.Fragment
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.blinnnk.component.HoneyBaseSwitch
import com.blinnnk.extension.getParentFragment
import com.blinnnk.extension.into
import com.blinnnk.extension.orFalse
import com.blinnnk.extension.setMargins
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.R
import io.goldstone.blockchain.common.base.basefragment.BaseFragment
import io.goldstone.blockchain.common.component.SingleLineSwitch
import io.goldstone.blockchain.common.component.overlay.GoldStoneDialog
import io.goldstone.blockchain.common.component.title.AttentionView
import io.goldstone.blockchain.common.language.FingerprintUnlockText
import io.goldstone.blockchain.common.language.PincodeText
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
import io.goldstone.blockchain.module.home.profile.walletsecurity.presenter.WalletSecuritySettingsPresenter
import org.jetbrains.anko.*


/**
 * @date 11/09/2018 3:45 PM
 * @author wcx
 */
class WalletSecuritySettingsFragment : BaseFragment<WalletSecuritySettingsPresenter>() {
	override val pageTitle: String = ""

	private val changePinCode by lazy { LinearLayout(context) }
	private var pinCodeSingleLineSwitch: SingleLineSwitch? = null
	override val presenter = WalletSecuritySettingsPresenter(this)

	override fun AnkoContext<Fragment>.initView() {
		verticalLayout {
			gravity = Gravity.CENTER_HORIZONTAL
			lparams(
				matchParent,
				matchParent
			)
			AppConfigTable.getAppConfig {
				if (it?.pincodeIsOpened.orFalse() || it?.fingerprintUnlockerIsOpened.orFalse()) {
					presenter.showPassCodeFragment()
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
	}

	private fun ViewGroup.initSwitchCell() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !checkIfTheSystemFingerprintExists().isHardwareDoesNotSupportFingerprints()) {
			// 指纹解锁
			SingleLineSwitch(
				context,
				true
			).apply {
				AppConfigTable.getAppConfig { config ->
					setSwitch(config?.fingerprintUnlockerIsOpened.orFalse())
				}
				setOnclick { switch ->
					if (switch.isChecked) {
						if (checkIfTheSystemFingerprintExists().isAvailable()) {
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
				setContent(FingerprintUnlockText.fingerprintUnlock)
			}.into(this)
		}

		pinCodeSingleLineSwitch = SingleLineSwitch(
			context,
			true
		).apply {
			AppConfigTable.getAppConfig { config ->
				setSwitch(config?.pincodeIsOpened.orFalse())
				if (config?.pincodeIsOpened.orFalse()) {
					changePinCode.visibility = View.VISIBLE
				} else {
					changePinCode.visibility = View.GONE
				}
			}
			setOnclick {
				// 点击后跳转到PinCode编辑界面
				val switchChecked = pinCodeSingleLineSwitch?.getSwitchChecked()
				if (switchChecked.orFalse()) {
					presenter.setPassCodeFragment()
					pinCodeSingleLineSwitch?.setSwitch(!switchChecked.orFalse())
				} else {
					changePinCode.visibility = View.GONE
					AppConfigTable.setPinCodeStatus(false) {}
				}
			}
			setContent(PincodeText.show)
		}
		pinCodeSingleLineSwitch?.into(this)

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

			SingleLineSwitch(
				context,
				false
			).apply {
				setOnclick {
					// 点击后根据更新的数据库情况显示开关状态
					presenter.setPassCodeFragment()
				}

				setContent(PincodeText.changePinCode)
			}.into(this)
		}.into(this)
	}

	fun setPinCodeSingleLineSwitch(isChecked: Boolean) {
		pinCodeSingleLineSwitch?.setSwitch(isChecked)
	}

	fun setChangePinCodeVisibility() {
		changePinCode.visibility = View.VISIBLE
	}

	private fun checkIfTheSystemFingerprintExists(): FingerprintAvailableStatus {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			context?.let {
				val fingerprintHelper = FingerprintHelper(it)
				return fingerprintHelper.checkIfTheFingerprintIsAvailable()
			}
		}
		return FingerprintAvailableStatus.HardwareDoesNotSupportFingerprints
	}

	// 点击后根据更新的数据库情况显示指紋解锁开关状态
	private fun openFingerprintEvent(switch: HoneyBaseSwitch) {
		presenter.setFingerprintStatus(switch.isChecked) {
			AppConfigTable.getAppConfig {
				switch.isChecked = it?.fingerprintUnlockerIsOpened.orFalse()
				if (!pinCodeSingleLineSwitch?.getSwitchChecked().orFalse() && switch.isChecked) {
					setPinCodeTips()
				}
			}
		}
	}

	// 设置数字密码弹窗
	private fun setPinCodeTips() {
		context?.let {
			GoldStoneDialog.show(it) {
				showButtons(PincodeText.goToSetPinCode) {
					presenter.setPassCodeFragment()
					GoldStoneDialog.remove(context)
				}
				setImage(R.drawable.network_browken_banner)
				setContent(
					FingerprintUnlockText.fingerprintIsOn,
					FingerprintUnlockText.fingerprintOpeningPrompt
				)
			}
		}
	}

	// 设置指纹密码弹窗
	private fun setFingerprintTips() {
		context?.let {
			GoldStoneDialog.show(it) {
				showButtons(FingerprintUnlockText.goToSetFingerprint) {
					val intent = Intent("android.settings.SETTINGS")
					intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
					try {
						context.startActivity(intent)
					} catch (e: Exception) {
					}
					GoldStoneDialog.remove(context)
				}
				setImage(R.drawable.network_browken_banner)
				setContent(
					FingerprintUnlockText.yourDeviceHasNotSetAFingerprintYet,
					FingerprintUnlockText.fingerprintNotSetPrompt
				)
			}
		}
	}

	override fun onHiddenChanged(hidden: Boolean) {
		if (!hidden) {
			AppConfigTable.getAppConfig {
				// 设置新密码返回更新状态
				if (it?.pincodeIsOpened.orFalse()) {
					setChangePinCodeVisibility()
					setPinCodeSingleLineSwitch(true)
				}
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