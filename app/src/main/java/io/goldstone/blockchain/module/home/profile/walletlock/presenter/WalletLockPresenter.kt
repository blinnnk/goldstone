package io.goldstone.blockchain.module.home.profile.walletlock.presenter

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Message
import android.os.Parcel
import android.os.Parcelable
import android.widget.EditText
import com.blinnnk.component.HoneyBaseSwitch
import com.blinnnk.extension.getParentFragment
import com.blinnnk.extension.isNull
import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.common.language.CommonText
import io.goldstone.blockchain.common.language.PincodeText
import io.goldstone.blockchain.common.utils.alert
import io.goldstone.blockchain.common.value.Count
import io.goldstone.blockchain.common.value.PasswordRetrievalHandlerMark
import io.goldstone.blockchain.kernel.commonmodel.AppConfigTable
import io.goldstone.blockchain.module.home.profile.profileoverlay.view.ProfileOverlayFragment
import io.goldstone.blockchain.module.home.profile.walletlock.view.WalletLockFragment

/**
 * @date 11/09/2018 3:45 PM
 * @author wcx
 */
class WalletLockPresenter(
	override val fragment: WalletLockFragment
) : BasePresenter<WalletLockFragment>() {

	private val handler: Handler = @SuppressLint("HandlerLeak")
	object : Handler(), Parcelable {
		override fun writeToParcel(
			p0: Parcel?,
			p1: Int
		) {
		}

		override fun describeContents(): Int {
			return 0
		}

		override fun handleMessage(msg: Message?) {
			super.handleMessage(msg)
			when (msg?.what) {
				PasswordRetrievalHandlerMark.setNewPassword -> {
					setShowPinCodeStatus(true)
					fragment.setChangePinCodeVisibility()
					fragment.setPinCodeSingleLineSwitch(true)
				}
				PasswordRetrievalHandlerMark.incompletePassword -> {
					fragment.getParentFragment<ProfileOverlayFragment> {
						presenter.removeSelfFromActivity()
					}
				}
			}
		}
	}

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

	fun setShowFingerprintStatus(
		status: Boolean,
		callback: () -> Unit = {}
	) {
		AppConfigTable.apply {
			setShowFingerprintUnlockStatus(status) {
				callback()
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

	// 跳转至设置数字密码锁界面
	fun setPassCodeFragment() {
	}

	// 跳转至钱包锁校验身份界面
	fun showPassCodeFragment() {
	}
}