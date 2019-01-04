@file:Suppress("DEPRECATION")

package io.goldstone.blockchain.common.utils

import android.content.Context
import android.hardware.fingerprint.FingerprintManager
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat
import android.support.v4.os.CancellationSignal
import com.blinnnk.extension.isNull
import io.goldstone.blockchain.common.error.GoldStoneError
import javax.crypto.Cipher

/**
 * @author KaySaith
 * @date  2018/12/17
 */
@Suppress("DEPRECATION")
class FingerPrintManager(val context: Context) {

	private var cancel: CancellationSignal? = null
	private var handler: Handler? = null
	private var fingerprint: FingerprintManagerCompat? = null
	private var fingerCallback: FingerprintManagerCompat.AuthenticationCallback? = null

	fun observing(callback: (cipher: Cipher?, error: GoldStoneError) -> Unit) {
		if (fingerprint.isNull()) fingerprint = FingerprintManagerCompat.from(context)
		val crypto = null
		val flags = 0
		cancel = CancellationSignal()
		fingerCallback = object : FingerprintManagerCompat.AuthenticationCallback() {
			override fun onAuthenticationError(errMsgId: Int, errString: CharSequence?) {
				super.onAuthenticationError(errMsgId, errString)
				if (errMsgId == FingerprintManager.FINGERPRINT_ERROR_CANCELED) {
					// TODO
				} else {
					callback(null, GoldStoneError(errString?.toString() ?: "error"))
				}
			}

			override fun onAuthenticationFailed() {
				super.onAuthenticationFailed()
				callback(null, GoldStoneError("failed"))
			}

			override fun onAuthenticationHelp(helpMsgId: Int, helpString: CharSequence?) {
				super.onAuthenticationHelp(helpMsgId, helpString)
				callback(null, GoldStoneError(helpString.toString()))
			}

			override fun onAuthenticationSucceeded(result: FingerprintManagerCompat.AuthenticationResult?) {
				super.onAuthenticationSucceeded(result)
				// 这里的 Cipher 在一些机型上会返回 null
				val cipher = result?.cryptoObject?.cipher
				callback(cipher, GoldStoneError.None)
			}
		}
		val handler = object : Handler(Looper.getMainLooper()) {
			override fun handleMessage(msg: Message) {
				when (msg.what) {
					//验证错误
					1 -> handleErrorCode(msg.arg1)
					//验证成功
					2 -> cancel = null
					//验证失败
					3 -> cancel = null
					else -> super.handleMessage(msg)
				}
			}
		}
		fingerprint?.authenticate(crypto, flags, cancel, fingerCallback!!, handler)
	}

	fun checker(): FingerPrintType {
		if (fingerprint.isNull()) fingerprint = FingerprintManagerCompat.from(context)
		return if (fingerprint!!.isHardwareDetected) {
			if (fingerprint!!.hasEnrolledFingerprints()) FingerPrintType.Valid
			else FingerPrintType.NoneFingerprint
		} else FingerPrintType.NoneHardware
	}

	fun removeHandler() {
		handler?.removeCallbacksAndMessages(null)
		cancel = null
		fingerprint = null
		fingerCallback = null
	}

	private fun handleErrorCode(code: Int) {
		when (code) {
			FingerprintManager.FINGERPRINT_ERROR_CANCELED -> {
			}
			FingerprintManager.FINGERPRINT_ERROR_HW_UNAVAILABLE -> {
			}
			FingerprintManager.FINGERPRINT_ERROR_LOCKOUT -> {
			}
			FingerprintManager.FINGERPRINT_ERROR_NO_SPACE -> {
			}
			FingerprintManager.FINGERPRINT_ERROR_TIMEOUT -> {
			}
			FingerprintManager.FINGERPRINT_ERROR_UNABLE_TO_PROCESS -> {
			}
		}
		// todo 指纹传感器不可用，该操作被取消
		// todo 当前设备不可用，请稍后再试
		// todo 由于太多次尝试失败导致被锁，该操作被取消
		// todo 没有足够的存储空间保存这次操作，该操作不能完成
		// todo 操作时间太长，一般为30秒
		// todo 传感器不能处理当前指纹图片
	}

}

enum class FingerPrintType {
	NoneHardware, NoneFingerprint, Valid;

	fun isValid(): Boolean {
		return this == Valid
	}

	fun isUnsupportedDevice(): Boolean {
		return this == NoneHardware
	}
}