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
		val fingerprintAvailableStatus = checkIfTheFingerprintIsAvailable()
		if (fingerprintAvailableStatus.isAvailable()) {
			// 可以指纹检测
			authenticate()
			hold(WalletSecurityError.None)
		} else {
			getCheckedFingerprintTips(fingerprintAvailableStatus) {
				hold(it)
			}
		}
	}

	private fun getCheckedFingerprintTips(
		fingerprintAvailableStatus: FingerprintAvailableStatus,
		@UiThread hold: (error: WalletSecurityError) -> Unit
	) {
		if (fingerprintAvailableStatus.isUnregistered()) {
			hold(WalletSecurityError.UnregisteredFingerprint)
		} else {
			hold(WalletSecurityError.HardwareDoesNotSupportFingerprints)
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

	fun checkIfTheFingerprintIsAvailable(): FingerprintAvailableStatus {
		if (fingerprintManager.isNull()) {
			fingerprintManager = context.getSystemService(FingerprintManager::class.java)
		}
		if (!fingerprintManager.isHardwareDetected) {
			return FingerprintAvailableStatus.HardwareDoesNotSupportFingerprints
		} else if (!fingerprintManager.hasEnrolledFingerprints()) {
			return FingerprintAvailableStatus.NoFingerprintSaveRecord
		}
		return FingerprintAvailableStatus.Available
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
enum class FingerprintAvailableStatus(val status: Int) {
	Available(1),
	NoFingerprintSaveRecord(0),
	HardwareDoesNotSupportFingerprints(-1);

	fun isAvailable(): Boolean {
		return status == Available.status
	}

	fun isUnregistered(): Boolean {
		return status == NoFingerprintSaveRecord.status
	}

	fun hardwareIsUnsupported(): Boolean {
		return status == HardwareDoesNotSupportFingerprints.status
	}
}