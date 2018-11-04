package io.goldstone.blockchain.module.home.profile.pincode.presenter

import android.widget.EditText
import com.blinnnk.component.HoneyBaseSwitch
import com.blinnnk.extension.isNull
import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.common.language.CommonText
import io.goldstone.blockchain.common.language.PincodeText
import io.goldstone.blockchain.common.utils.alert
import io.goldstone.blockchain.common.value.Count
import io.goldstone.blockchain.kernel.commonmodel.AppConfigTable
import io.goldstone.blockchain.module.home.profile.pincode.view.PinCodeEditorFragment

/**
 * @date 23/04/2018 2:34 PM
 * @author KaySaith
 */
class PinCodeEditorPresenter(
	override val fragment: PinCodeEditorFragment
) : BasePresenter<PinCodeEditorFragment>() {

	fun setShowPinCodeStatus(status: Boolean, callback: () -> Unit = {}) {
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
}