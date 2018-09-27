@file:Suppress("DEPRECATION")

package io.goldstone.blockchain.common.utils

import android.content.Context
import android.hardware.fingerprint.FingerprintManager
import android.os.Build
import android.os.CancellationSignal
import android.support.annotation.RequiresApi
import android.support.annotation.UiThread
import com.blinnnk.extension.isNull
import io.goldstone.blockchain.common.error.WalletSecurityError

/**
 * @date 04/09/2018 3:32 PM
 * @author wcx
 */
@RequiresApi(Build.VERSION_CODES.M)
class FingerprintHelper(private val context: Context) : FingerprintManager.AuthenticationCallback() {
	private var fingerprintManager: FingerprintManager = context.getSystemService(FingerprintManager::class.java)
	private var authenticationCallback: AuthenticationCallback? = null
	private var cancellationSignal: CancellationSignal? = null

	fun setAuthenticationCallback(authenticationCallback: AuthenticationCallback) {
		this.authenticationCallback = authenticationCallback
	}

	fun startFingerprintUnlock(@UiThread hold: (error: WalletSecurityError) -> Unit) {
		val checkFingerprintAvailable = checkFingerprintAvailable()
		if (checkFingerprintAvailable == CheckFingerprintAvailable.Normal.status) {
			// 可以指纹检测
			authenticate()
			hold(WalletSecurityError.None)
		} else {
			getCheckedFingerprintTips(checkFingerprintAvailable) {
				hold(it)
			}
		}
	}

	private fun getCheckedFingerprintTips(
		checkFingerprintAvailable: Int,
		@UiThread hold: (error: WalletSecurityError) -> Unit
	) {
		when (checkFingerprintAvailable) {
			CheckFingerprintAvailable.TheDeviceIsNotFingerprinted.status ->
				hold(WalletSecurityError.TheDeviceIsNotFingerprinted)
			CheckFingerprintAvailable.TheDeviceHasNotDetectedTheFingerprintHardware.status ->
				hold(WalletSecurityError.TheDeviceHasNotDetectedTheFingerprintHardware)
		}
	}

	private fun authenticate() {
		if (cancellationSignal == null) {
			cancellationSignal = CancellationSignal()
		}
		fingerprintManager.authenticate(
			null,
			cancellationSignal,
			0,
			this,
			null
		)
	}

	fun checkFingerprintAvailable(): Int {
		if (fingerprintManager.isNull()) {
			fingerprintManager = context.getSystemService(FingerprintManager::class.java)
		}
		if (!fingerprintManager.isHardwareDetected) {
			return CheckFingerprintAvailable.TheDeviceHasNotDetectedTheFingerprintHardware.status
		} else if (!fingerprintManager.hasEnrolledFingerprints()) {
			return CheckFingerprintAvailable.TheDeviceIsNotFingerprinted.status
		}
		return CheckFingerprintAvailable.Normal.status
	}

	fun stopAuthenticate() {
		cancellationSignal?.cancel()
		cancellationSignal = null
		authenticationCallback = null
	}

	override fun onAuthenticationSucceeded(result: FingerprintManager.AuthenticationResult) {
		if (authenticationCallback == null) {
			return
		}
		authenticationCallback?.onAuthenticationSucceeded(result.toString())
	}

	override fun onAuthenticationError(
		errorCode: Int,
		errString: CharSequence
	) {
		if (authenticationCallback != null) {
			authenticationCallback?.onAuthenticationFail(
				errorCode,
				errString
			)
		}
	}

	override fun onAuthenticationHelp(
		helpCode: Int,
		helpString: CharSequence
	) {
		authenticationCallback?.onAuthenticationHelp(
			helpCode,
			helpString
		)
	}

	override fun onAuthenticationFailed() {
		authenticationCallback?.onAuthenticationFailed()
	}

	interface AuthenticationCallback {
		fun onAuthenticationSucceeded(value: String)
		fun onAuthenticationFail(
			errorCode: Int,
			errString: CharSequence
		)

		fun onAuthenticationFailed()
		fun onAuthenticationHelp(
			helpCode: Int,
			helpString: CharSequence
		)
	}
}

/**
 *  0 支持指纹但是没有录入指纹; 1：有可用指纹; -1，手机不支持指纹
 */
enum class CheckFingerprintAvailable(val status: Int) {
	Normal(1),
	TheDeviceIsNotFingerprinted(0),
	TheDeviceHasNotDetectedTheFingerprintHardware(-1)
}