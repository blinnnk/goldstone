@file:Suppress("DEPRECATION")

package io.goldstone.blockchain.common.utils

import android.content.Context
import android.hardware.fingerprint.FingerprintManager
import android.os.Build
import android.os.CancellationSignal
import android.support.annotation.RequiresApi
import com.blinnnk.extension.isNull
import io.goldstone.blockchain.common.language.FingerprintUnlockText
import org.jetbrains.anko.toast

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

	fun startFingerprintUnlock() {
		when (checkFingerprintAvailable()) {
			0 -> context.toast(FingerprintUnlockText.theDeviceIsNotFingerprinted)
			1 -> {
				// 可以指纹检测
				authenticate()
			}
			-1 -> context.toast(FingerprintUnlockText.theDeviceHasNotDetectedTheFingerprintHardware)
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

	/**
	 * @return 0 支持指纹但是没有录入指纹; 1：有可用指纹; -1，手机不支持指纹
	 */
	fun checkFingerprintAvailable(): Int {
		if (fingerprintManager.isNull()) {
			fingerprintManager = context.getSystemService(FingerprintManager::class.java)
		}
		if (!fingerprintManager.isHardwareDetected) {
			return -1
		} else if (!fingerprintManager.hasEnrolledFingerprints()) {
			return 0
		}
		return 1
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