package io.goldstone.blockchain.module.common.qrcode.view

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.view.KeyEvent
import android.view.WindowManager
import android.widget.*
import com.blinnnk.extension.setAlignParentRight
import com.blinnnk.extension.setCenterInParent
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.CheckPermission
import com.blinnnk.util.PermissionCategory
import com.google.zxing.client.android.Intents
import com.zxing.demo.*
import io.goldstone.blockchain.R
import io.goldstone.blockchain.common.component.button.RoundButton
import io.goldstone.blockchain.common.language.QRText
import io.goldstone.blockchain.common.utils.click
import io.goldstone.blockchain.common.value.ScreenSize
import io.goldstone.blockchain.common.value.fontSize
import io.goldstone.blockchain.module.common.qrcode.QRcodeCaptureManager
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk25.coroutines.onClick

/**
 * @date: 2018/9/11.
 * @author: yanglihai
 * @description:
 */
class ScanCaptureActivity: FragmentActivity() {
	
	private lateinit var captureManager: QRcodeCaptureManager
	private lateinit var barcodeScannerView: DecoratedQRCodeView
	
	val confirmButton by lazy {
		TextView(this).apply {
			text = QRText.selectQRCodeFromAlbum
			textColor = Color.WHITE
			textSize = fontSize(16)
			onClick { choosePicture() }
		}
	}
	
	val close by lazy {
		ImageView(this).apply {
			layoutParams = RelativeLayout.LayoutParams(50.uiPX(), 50.uiPX())
			setAlignParentRight()
			padding = 13.uiPX()
			setImageResource(R.drawable.close_icon)
			setColorFilter(Color.WHITE)
			click { finish() }
		}
	}
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		
		window.setFlags(
			WindowManager.LayoutParams.FLAG_FULLSCREEN,
			WindowManager.LayoutParams.FLAG_FULLSCREEN
		)
		
		relativeLayout {
			barcodeScannerView = DecoratedQRCodeView(this@ScanCaptureActivity)
			captureManager = QRcodeCaptureManager(this@ScanCaptureActivity, barcodeScannerView)
			captureManager.initializeFromIntent(intent, savedInstanceState)
			captureManager.decode ()
			addView(barcodeScannerView)
			addView(close)
			relativeLayout {
				addView(confirmButton.apply {
					lparams {
						centerInParent()
					}
				})
			}.lparams(matchParent, 200.uiPX()) {
				alignParentBottom()
			}
		}
		
	}
	
	private fun choosePicture() {
		object : CheckPermission(this) {
			override var permissionType = PermissionCategory.Write
		}.start {
			val innerIntent = Intent(Intent.ACTION_PICK)
			innerIntent.type = "image/*"
			startActivityForResult(innerIntent, 0)
		}
	}
	
	
	override fun onActivityResult(
		requestCode: Int,
		resultCode: Int,
		data: Intent?
	) {
		if (requestCode == 0 && resultCode == Activity.RESULT_OK) {
			data?.apply {
				val filePath = QRCodeUtil.getFilePathFromCaptureIntent(this)
				filePath?.apply {
					QRCodeUtil.decodeQRCodeImage(this) {
						runOnUiThread {
							intent.putExtra(Intents.Scan.RESULT, it)
							setResult(Activity.RESULT_OK, intent)
							finish()
						}
					}
				}
				
			}
			
		}
	}
	
	override fun onResume() {
		super.onResume()
		captureManager.onResume()
	}
	
	override fun onPause() {
		super.onPause()
		captureManager.onPause()
	}
	
	override fun onDestroy() {
		super.onDestroy()
		captureManager.onDestroy()
	}
	
	override fun onSaveInstanceState(outState: Bundle) {
		super.onSaveInstanceState(outState)
		captureManager.onSaveInstanceState(outState)
	}
	
	override fun onRequestPermissionsResult(
		requestCode: Int,
		permissions: Array<String>,
		grantResults: IntArray
	) {
		captureManager.onRequestPermissionsResult(
			requestCode,
			permissions,
			grantResults
		)
	}
	
	override fun onKeyDown(
		keyCode: Int,
		event: KeyEvent
	): Boolean {
		return barcodeScannerView.onKeyDown(keyCode, event) ||
			super.onKeyDown(keyCode, event
		)
	}
}