package io.goldstone.blockchain.module.common.qrcode

import android.Manifest
import android.annotation.TargetApi
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Bitmap
import android.os.*
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.Surface
import android.view.WindowManager
import com.blinnnk.extension.isNull
import com.google.zxing.ResultMetadataType
import com.google.zxing.ResultPoint
import com.google.zxing.client.android.*
import com.journeyapps.barcodescanner.*
import io.goldstone.blockchain.module.common.qrcode.view.DecoratedQRCodeView
import java.io.*

/**
 * @date: 2018/9/13.
 * @author: yanglihai
 * @description:
 */
class QRcodeCaptureManager(
	private val activity: Activity,
	private val barcodeView: DecoratedQRCodeView
) {
	
	private var cameraPermissionReqCode = 250
	
	private var orientationLock = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
	private val savedOrientationLock = "SAVED_ORIENTATION_LOCK"
	private var returnBarcodeImagePath = false
	private var destroyed = false
	private var inactivityTimer: InactivityTimer
	private var beepManager = BeepManager(activity)
	private val handler = Handler()
	private var finishWhenClosed = false
	
	private val callback = object : BarcodeCallback {
		override fun barcodeResult(result: BarcodeResult) {
			barcodeView.pause()
			beepManager.playBeepSoundAndVibrate()
			handler.post { returnResult(result) }
		}
		
		override fun possibleResultPoints(resultPoints: List<ResultPoint>) {}
	}
	
	private val stateListener = object : CameraPreview.StateListener {
		override fun previewSized() { }
		override fun previewStarted() { }
		override fun previewStopped() { }
		override fun cameraError(error: Exception) {
			displayFrameworkBugMessageAndExit()
		}
		
		override fun cameraClosed() {
			if (finishWhenClosed) {
				Log.d(CaptureManager::class.java.simpleName, "Camera closed; finishing activity")
				finish()
			}
		}
	}
	
	init {
		barcodeView.barcodeView.addStateListener(stateListener)
		inactivityTimer = InactivityTimer(activity,
			Runnable {
				Log.d(CaptureManager::class.java.simpleName, "Finishing due to inactivity")
				finish()
			}
		)
	}
	
	/**
	 * Perform initialization, according to preferences set in the intent.
	 *
	 * @param intent the intent containing the scanning preferences
	 * @param savedInstanceState saved state, containing orientation lock
	 */
	fun initializeFromIntent(
		intent: Intent?,
		savedInstanceState: Bundle?
	) {
		val window = activity.window
		window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
		
		savedInstanceState?.apply {
			// If the screen was locked and unlocked again, we may start in a different orientation
			// (even one not allowed by the manifest). In this case we restore the orientation we were
			// previously locked to.
			orientationLock = getInt(savedOrientationLock, ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
		}
		
		intent?.apply {
			// Only lock the orientation if it's not locked to something else yet
			val orientationLocked = getBooleanExtra(Intents.Scan.ORIENTATION_LOCKED, true)
			if (orientationLocked) {
				lockOrientation()
			}
			if (Intents.Scan.ACTION == action) {
				barcodeView.initializeFromIntent(this)
			}
			
			if (!getBooleanExtra(Intents.Scan.BEEP_ENABLED, true)) {
				beepManager.isBeepEnabled = false
			}
			
			if (hasExtra(Intents.Scan.TIMEOUT)) {
				handler.postDelayed(
					{ returnResultTimeout() },
					getLongExtra(Intents.Scan.TIMEOUT, 0L)
				)
			}
			
			if (getBooleanExtra(Intents.Scan.BARCODE_IMAGE_ENABLED, false)) {
				returnBarcodeImagePath = true
			}
		}
		
	}
	
	/**
	 * Lock display to current orientation.
	 */
	private fun lockOrientation() {
		// Only get the orientation if it's not locked to one yet.
		if (this.orientationLock == ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED) {
			// Adapted from http://stackoverflow.com/a/14565436
			val display = activity.windowManager.defaultDisplay
			val rotation = display.rotation
			val baseOrientation = activity.resources.configuration.orientation
			var orientation = 0
			if (baseOrientation == Configuration.ORIENTATION_LANDSCAPE) {
				orientation = if (rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_90) {
					ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
				} else {
					ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE
				}
			} else if (baseOrientation == Configuration.ORIENTATION_PORTRAIT) {
				orientation = if (rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_270) {
					ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
				} else {
					ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT
				}
			}
			this.orientationLock = orientation
		}
		activity.requestedOrientation = this.orientationLock
	}
	
	/**
	 * Start decoding.
	 */
	fun decode() {
		barcodeView.decodeSingle(callback)
	}
	
	/**
	 * Call from Activity#onResume().
	 */
	fun onResume() {
		if (Build.VERSION.SDK_INT >= 23) {
			openCameraWithPermission()
		} else {
			barcodeView.resume()
		}
		inactivityTimer.start()
	}
	
	private var askedPermission = false
	
	@TargetApi(23)
	private fun openCameraWithPermission() {
		if (ContextCompat.checkSelfPermission(
				this.activity,
				Manifest.permission.CAMERA
			) == PackageManager.PERMISSION_GRANTED
		) {
			barcodeView.resume()
		} else if (!askedPermission) {
			ActivityCompat.requestPermissions(
				this.activity,
				arrayOf(Manifest.permission.CAMERA),
				cameraPermissionReqCode
			)
			askedPermission = true
		} else {
			// Wait for permission result
		}
	}
	
	/**
	 * Call from Activity#onRequestPermissionsResult
	 * @param requestCode The request code passed in [android.support.v4.app.ActivityCompat.requestPermissions].
	 * @param permissions The requested permissions.
	 * @param grantResults The grant results for the corresponding permissions
	 * which is either [android.content.pm.PackageManager.PERMISSION_GRANTED]
	 * or [android.content.pm.PackageManager.PERMISSION_DENIED]. Never null.
	 */
	fun onRequestPermissionsResult(
		requestCode: Int,
		permissions: Array<String>,
		grantResults: IntArray
	) {
		Log.e(QRcodeCaptureManager::class.java.simpleName, permissions.toString())
		if (requestCode == cameraPermissionReqCode) {
			if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				// permission was granted
				barcodeView.resume()
			} else {
				// TODO: display better error message.
				displayFrameworkBugMessageAndExit()
			}
		}
	}
	
	/**
	 * Call from Activity#onPause().
	 */
	fun onPause() {
		inactivityTimer.cancel()
		barcodeView.pauseAndWait()
	}
	
	/**
	 * Call from Activity#onDestroy().
	 */
	fun onDestroy() {
		destroyed = true
		inactivityTimer.cancel()
		handler.removeCallbacksAndMessages(null)
	}
	
	/**
	 * Call from Activity#onSaveInstanceState().
	 */
	fun onSaveInstanceState(outState: Bundle) {
		outState.putInt(savedOrientationLock, this.orientationLock)
	}
	
	/**
	 * Create a intent to return as the Activity result.
	 *
	 * @param rawResult the BarcodeResult, must not be null.
	 * @param barcodeImagePath a path to an exported file of the Barcode Image, can be null.
	 * @return the Intent
	 */
	private fun resultIntent(
		rawResult: BarcodeResult,
		barcodeImagePath: String?
	): Intent {
		val intent = Intent(Intents.Scan.ACTION)
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT)
		intent.putExtra(Intents.Scan.RESULT, rawResult.toString())
		intent.putExtra(Intents.Scan.RESULT_FORMAT, rawResult.barcodeFormat.toString())
		val rawBytes = rawResult.rawBytes
		if (!rawBytes.isNull() && rawBytes.isNotEmpty()) {
			intent.putExtra(Intents.Scan.RESULT_BYTES, rawBytes)
		}
		val metadata = rawResult.resultMetadata
		if (metadata != null) {
			if (metadata.containsKey(ResultMetadataType.UPC_EAN_EXTENSION)) {
				intent.putExtra(Intents.Scan.RESULT_UPC_EAN_EXTENSION,
					metadata[ResultMetadataType.UPC_EAN_EXTENSION].toString())
			}
			val orientation = metadata[ResultMetadataType.ORIENTATION] as? Number
			orientation?.apply {
				intent.putExtra(Intents.Scan.RESULT_ORIENTATION, toInt())
			}
			val ecLevel = metadata[ResultMetadataType.ERROR_CORRECTION_LEVEL] as? String
			ecLevel?.apply {
				intent.putExtra(
					Intents.Scan.RESULT_ERROR_CORRECTION_LEVEL,
					this
				)
			}
			(metadata[ResultMetadataType.BYTE_SEGMENTS] as? Iterable<*>)?.apply {
				forEachIndexed { index, byteSegment ->
					(byteSegment as? ByteArray)?.apply {
						intent.putExtra(
							Intents.Scan.RESULT_BYTE_SEGMENTS_PREFIX + index,
							this
						)
					}
				}
				
			}
			
		}
		barcodeImagePath?.apply {
			intent.putExtra(
				Intents.Scan.RESULT_BARCODE_IMAGE_PATH,
				this
			)
		}
		return intent
	}
	
	/**
	 * Save the barcode image to a temporary file stored in the application's cache, and return its path.
	 * Only does so if returnBarcodeImagePath is enabled.
	 *
	 * @param rawResult the BarcodeResult, must not be null
	 * @return the path or null
	 */
	private fun getBarcodeImagePath(rawResult: BarcodeResult): String? {
		var barcodeImagePath: String? = null
		if (returnBarcodeImagePath) {
			val bmp = rawResult.bitmap
			try {
				val bitmapFile = File.createTempFile(
					"barcodeimage",
					".jpg",
					activity.cacheDir
				)
				val outputStream = FileOutputStream(bitmapFile)
				bmp.compress(
					Bitmap.CompressFormat.JPEG,
					100,
					outputStream
				)
				outputStream.close()
				barcodeImagePath = bitmapFile.absolutePath
			} catch (e: IOException) {
				Log.w(CaptureManager::class.java.simpleName,
					"Unable to create temporary file and store bitmap! $e")
			}
			
		}
		return barcodeImagePath
	}
	
	private fun finish() {
		activity.finish()
	}
	
	private fun closeAndFinish() {
		if (barcodeView.barcodeView.isCameraClosed) {
			finish()
		} else {
			finishWhenClosed = true
		}
		
		barcodeView.pause()
		inactivityTimer.cancel()
	}
	
	private fun returnResultTimeout() {
		val intent = Intent(Intents.Scan.ACTION)
		intent.putExtra(Intents.Scan.TIMEOUT, true)
		activity.setResult(Activity.RESULT_CANCELED, intent)
		closeAndFinish()
	}
	
	private fun returnResult(rawResult: BarcodeResult) {
		val intent = resultIntent(rawResult, getBarcodeImagePath(rawResult))
		activity.setResult(Activity.RESULT_OK, intent)
		closeAndFinish()
	}
	
	private fun displayFrameworkBugMessageAndExit() {
		if (activity.isFinishing || this.destroyed || finishWhenClosed) {
			return
		}
		val builder = AlertDialog.Builder(activity)
		builder.setTitle(activity.getString(R.string.zxing_app_name))
		builder.setMessage(activity.getString(R.string.zxing_msg_camera_framework_bug))
		builder.setPositiveButton(
			R.string.zxing_button_ok
		) { _, _ -> finish() }
		builder.setOnCancelListener { finish() }
		builder.show()
	}
}