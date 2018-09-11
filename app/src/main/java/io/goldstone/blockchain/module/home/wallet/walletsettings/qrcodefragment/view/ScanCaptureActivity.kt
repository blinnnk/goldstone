package io.goldstone.blockchain.module.home.wallet.walletsettings.qrcodefragment.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.view.KeyEvent
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.blinnnk.extension.setAlignParentBottom
import com.blinnnk.extension.setCenterInParent
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.CheckPermission
import com.blinnnk.util.PermissionCategory
import com.google.zxing.client.android.Intents
import com.journeyapps.barcodescanner.CaptureManager
import com.journeyapps.barcodescanner.DecoratedBarcodeView
import com.zxing.demo.*
import io.goldstone.blockchain.common.component.button.RoundButton
import io.goldstone.blockchain.common.language.CommonText
import io.goldstone.blockchain.common.value.ScreenSize
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk25.coroutines.onClick

/**
 * @date: 2018/9/11.
 * @author: yanglihai
 * @description:
 */
class ScanCaptureActivity: FragmentActivity() {
	
	private lateinit var captureManager: CaptureManager
	private lateinit var barcodeScannerView: DecoratedBarcodeView
	
	val confirmButton by lazy {
		RoundButton(this).apply {
			text = "选择图片"
			setBlueStyle()
			onClick { choosePicture() }
		}
	}
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		
		relativeLayout {
			barcodeScannerView = DecoratedBarcodeView(this@ScanCaptureActivity)
			captureManager = CaptureManager(this@ScanCaptureActivity, barcodeScannerView)
			captureManager.initializeFromIntent(null, savedInstanceState)
			captureManager.decode ()
			addView(barcodeScannerView)
			relativeLayout {
				addView(confirmButton)
				confirmButton.apply {
					val buttonWidth = ScreenSize.widthWithPadding / 2 - 5.uiPX()
					val buttonHeight = 40.uiPX()
					lparams(buttonWidth, buttonHeight) {
						centerInParent()
					}
				}
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