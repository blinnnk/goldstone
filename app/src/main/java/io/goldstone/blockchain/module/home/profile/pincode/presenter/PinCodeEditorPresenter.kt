package io.goldstone.blockchain.module.home.profile.pincode.presenter

import android.widget.EditText
import com.blinnnk.animation.updateHeightAnimation
import com.blinnnk.component.HoneyBaseSwitch
import com.blinnnk.extension.addFragmentAndSetArguments
import com.blinnnk.extension.getParentFragment
import com.blinnnk.extension.isNull
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.common.utils.alert
import io.goldstone.blockchain.common.value.CommonText
import io.goldstone.blockchain.common.value.ContainerID
import io.goldstone.blockchain.common.value.Count
import io.goldstone.blockchain.common.value.PincodeText
import io.goldstone.blockchain.kernel.commonmodel.AppConfigTable
import io.goldstone.blockchain.module.common.passcode.view.PasscodeFragment
import io.goldstone.blockchain.module.home.profile.pincode.view.PinCodeEditorFragment
import io.goldstone.blockchain.module.home.profile.profileoverlay.view.ProfileOverlayFragment

/**
 * @date 23/04/2018 2:34 PM
 * @author KaySaith
 */
class PinCodeEditorPresenter(
	override val fragment: PinCodeEditorFragment
) : BasePresenter<PinCodeEditorFragment>() {
	
	fun setShowPinCodeStatus(
		status: Boolean,
		callback: () -> Unit = {}
	) {
		AppConfigTable.apply {
			getAppConfig {
				if (it?.pincode.isNull()) {
					fragment.context?.alert(PincodeText.turnOnAttention)
					callback()
					return@getAppConfig
				}
				setShowPinCodeStatus(status) {
					callback()
				}
			}
		}
	}
	
	fun resetPinCode(
		newPinCode: EditText,
		repeatPinCode: EditText,
		switch: HoneyBaseSwitch
	) {
		if (newPinCode.text.isEmpty()) {
			fragment.context?.alert(PincodeText.countAlert)
			return
		}
		
		if (newPinCode.text.length > Count.pinCode || repeatPinCode.text.length > Count.pinCode) {
			fragment.context?.alert(PincodeText.countAlert)
			return
		}
		
		if (newPinCode.text.toString() != repeatPinCode.text.toString()) {
			fragment.context?.alert(PincodeText.verifyAlert)
			return
		}
		
		AppConfigTable.updatePinCode(newPinCode.text.toString().toInt()) {
			fragment.context?.alert(CommonText.succeed)
			setShowPinCodeStatus(true)
			switch.isChecked = true
		}
	}
	
	override fun onFragmentViewCreated() {
		super.onFragmentViewCreated()
		fragment.getParentFragment<ProfileOverlayFragment> {
			val height = 380.uiPX()
			overlayView.contentLayout.updateHeightAnimation(height, height, 0)
		}
	}
	
	fun showPinCodeFragment() {
		fragment.activity?.addFragmentAndSetArguments<PasscodeFragment>(ContainerID.main) {
			//
		}
	}
}