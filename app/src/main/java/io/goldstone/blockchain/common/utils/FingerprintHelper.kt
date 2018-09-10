@file:Suppress("DEPRECATION")

package com.example.wangchenxing.myapplication

import android.content.Context
import android.hardware.fingerprint.FingerprintManager
import android.os.Build
import android.os.CancellationSignal
import android.support.annotation.RequiresApi
import android.widget.Toast

/**
 * @date 04/09/2018 3:32 PM
 * @author wcx
 */
@RequiresApi(Build.VERSION_CODES.M)
class FingerprintHelper(private val context: Context) : FingerprintManager.AuthenticationCallback() {
  private var fingerprintManager: FingerprintManager = context.getSystemService(FingerprintManager::class.java)
  private var authenticationCallback: AuthenticationCallback? = null
  private var mCancellationSignal: CancellationSignal? = null

  fun setAuthenticationCallback(authenticationCallback: AuthenticationCallback) {
    this.authenticationCallback = authenticationCallback
  }

  fun startFingerprintUnlock() {
    when (checkFingerprintAvailable()) {
      0 -> Toast.makeText(context, "该设备未录入指纹，请去系统->设置中添加指纹", Toast.LENGTH_SHORT).show()
      1 -> {
        // 可以指纹检测
        authenticate()
      }
      -1 -> Toast.makeText(context, "该设备尚未检测到指纹硬件", Toast.LENGTH_SHORT).show()
    }
  }

  private fun authenticate() {
    if (mCancellationSignal == null) {
      mCancellationSignal = CancellationSignal()
    }
    fingerprintManager.authenticate(
      null,
      mCancellationSignal,
      0,
      this,
      null
    )
  }

  /**
   * @return 0 支持指纹但是没有录入指纹; 1：有可用指纹; -1，手机不支持指纹
   */
  private fun checkFingerprintAvailable(): Int {
    if (!fingerprintManager.isHardwareDetected) {
      return -1
    } else if (!fingerprintManager.hasEnrolledFingerprints()) {
      return 0
    }
    return 1
  }

  fun stopAuthenticate() {
    mCancellationSignal?.cancel()
    mCancellationSignal = null
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