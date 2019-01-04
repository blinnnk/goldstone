package io.goldstone.blockchain.module.home.profile.pincode.view

import android.support.v4.app.Fragment
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Switch
import com.blinnnk.extension.*
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.base.basefragment.BaseFragment
import io.goldstone.blockchain.common.component.button.RoundButton
import io.goldstone.blockchain.common.component.button.roundButton
import io.goldstone.blockchain.common.component.edittext.RoundInput
import io.goldstone.blockchain.common.component.edittext.roundInput
import io.goldstone.blockchain.common.language.CommonText
import io.goldstone.blockchain.common.language.PincodeText
import io.goldstone.blockchain.common.language.ProfileText
import io.goldstone.blockchain.common.sharedpreference.SharedValue
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.utils.click
import io.goldstone.blockchain.common.value.*
import io.goldstone.blockchain.common.value.ScreenSize
import io.goldstone.blockchain.module.common.passcode.view.PasscodeFragment
import io.goldstone.blockchain.module.home.home.view.MainActivity
import io.goldstone.blockchain.module.home.profile.pincode.presenter.PinCodeEditorPresenter
import io.goldstone.blockchain.module.home.profile.profileoverlay.view.ProfileOverlayFragment
import org.jetbrains.anko.*

/**
 * @date 23/04/2018 2:34 PM
 * @author KaySaith
 */
class PinCodeEditorFragment : BaseFragment<PinCodeEditorPresenter>() {

	override val pageTitle: String = ProfileText.pinCode
	lateinit var confirmButton: RoundButton
	private lateinit var newPinCode: RoundInput
	private lateinit var repeatPinCode:RoundInput
	private lateinit var switch: Switch
	override val presenter = PinCodeEditorPresenter(this)

	override fun AnkoContext<Fragment>.initView() {
		verticalLayout {
			gravity = Gravity.CENTER_HORIZONTAL
			lparams(matchParent, matchParent)

			if (SharedValue.getPincodeDisplayStatus())
				PasscodeFragment.show(this@PinCodeEditorFragment)

			initSwitchCell()

			textView {
				text = PincodeText.description
				textSize = fontSize(15)
				textColor = GrayScale.midGray
				typeface = GoldStoneFont.medium(context)
				gravity = Gravity.CENTER
				layoutParams = LinearLayout.LayoutParams(matchParent, 30.uiPX())
				y += 20.uiPX()
			}

			newPinCode = roundInput {
				title = PincodeText.pincode
				setPinCodeInput()
				horizontalPaddingSize = PaddingSize.gsCard
			}
			newPinCode.setMargins<LinearLayout.LayoutParams> {
				topMargin = 40.uiPX()
			}

			repeatPinCode = roundInput {
				horizontalPaddingSize = PaddingSize.gsCard
				title = PincodeText.repeat
				setPinCodeInput()
			}
			repeatPinCode.setMargins<LinearLayout.LayoutParams> {
				topMargin = 10.uiPX()
			}

			confirmButton = roundButton {
				text = CommonText.confirm
				setBlueStyle(15.uiPX())
			}.click {
				presenter.resetPinCode(newPinCode, repeatPinCode, switch)
			}
		}
	}

	private fun ViewGroup.initSwitchCell() {
		relativeLayout {
			lparams {
				width = ScreenSize.widthWithPadding
				height = 65.uiPX()
			}

			textView(PincodeText.show).apply {
				textSize = fontSize(16)
				textColor = GrayScale.midGray
				typeface = GoldStoneFont.heavy(context)
				gravity = Gravity.CENTER_VERTICAL
				lparams(matchParent, matchParent)
			}

			switch = switch {
				isDefaultStyle(Spectrum.blue)
				isChecked = SharedValue.getPincodeDisplayStatus()
			}.click { switch ->
				// 点击后根据更新的数据库情况显示开关状态
				presenter.setPinCodeDisplayStatus(switch.isChecked) {
					switch.isChecked = it
				}
			}
			switch.alignParentRight()
			switch.centerInVertical()
			// 分割线
			View(context).apply {
				lparams {
					width = matchParent
					height = BorderSize.default.toInt()
					alignParentBottom()
				}
				backgroundColor = GrayScale.lightGray
			}.into(this)
		}
	}

	override fun setBaseBackEvent(activity: MainActivity?, parent: Fragment?) {
		getParentFragment<ProfileOverlayFragment> {
			presenter.removeSelfFromActivity()
		}
	}
}