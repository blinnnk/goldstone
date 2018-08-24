package io.goldstone.blockchain.module.home.wallet.walletsettings.qrcodefragment.presenter

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.support.v4.app.Fragment
import android.support.v4.content.FileProvider
import com.blinnnk.extension.timeUpThen
import com.blinnnk.uikit.ScreenSize
import com.blinnnk.util.CheckPermission
import com.blinnnk.util.PermissionCategory
import com.google.zxing.BarcodeFormat
import com.google.zxing.integration.android.IntentIntegrator
import com.journeyapps.barcodescanner.BarcodeEncoder
import com.journeyapps.barcodescanner.CaptureActivity
import io.goldstone.blockchain.BuildConfig
import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.common.language.QRText
import io.goldstone.blockchain.common.value.ArgumentKey
import io.goldstone.blockchain.kernel.network.GoldStoneAPI
import io.goldstone.blockchain.module.home.profile.contacts.contractinput.model.ContactModel
import io.goldstone.blockchain.module.home.wallet.walletsettings.qrcodefragment.view.QRCodeFragment
import org.jetbrains.anko.support.v4.toast
import java.io.File
import java.io.FileOutputStream
import java.util.*

/**
 * @date 26/03/2018 11:07 PM
 * @author KaySaith
 */
open class QRCodePresenter(
	override val fragment: QRCodeFragment
) : BasePresenter<QRCodeFragment>() {

	val addressModel by lazy {
		fragment.arguments?.getSerializable(ArgumentKey.addressModel) as? ContactModel
	}

	override fun onFragmentViewCreated() {
		addressModel?.let {
			fragment.setAddressText(it.address)
			fragment.setQRImage(generateQRCode(it.address))
		}
	}

	companion object {

		fun generateQRCode(content: String): Bitmap? {
			return try {
				val size = (ScreenSize.Width * 0.8).toInt()
				val barcodeEncoder = BarcodeEncoder()
				barcodeEncoder.encodeBitmap(content, BarcodeFormat.QR_CODE, size, size)
			} catch (error: Exception) {
				println(error)
				null
			}
		}

		fun saveQRCodeImageToAlbum(
			content: String,
			fragment: Fragment,
			hold: (Uri) -> Unit = {}
		) {
			object : CheckPermission(fragment.activity) {
				override var permissionType = PermissionCategory.Write
			}.start {
				val size = (ScreenSize.Width * 0.8).toInt()
				val barcodeEncoder = BarcodeEncoder()
				val bitmap = barcodeEncoder.encodeBitmap(content, BarcodeFormat.QR_CODE, size, size)
				// 防止生成 bitmap 有一定概率太慢, 这里延时保存一下
				300L timeUpThen { saveImage(bitmap, fragment, hold) }
				fragment.toast(QRText.savedAttention)
			}
		}

		fun shareQRImage(
			fragment: Fragment,
			content: String
		) {
			saveQRCodeImageToAlbum(content, fragment) {
				val intent = Intent(Intent.ACTION_SEND)
				intent.type = "image/*"
				intent.putExtra(Intent.EXTRA_STREAM, it)
				fragment.activity?.startActivity(Intent.createChooser(intent, QRText.shareQRTitle))
			}
		}

		fun scanQRCode(fragment: Fragment) {
			val integrator = IntentIntegrator.forSupportFragment(fragment)
			integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
			integrator.setOrientationLocked(false)
			integrator.captureActivity = CaptureActivity::class.java
			integrator.setCameraId(0)
			integrator.setPrompt(QRText.screenText)
			integrator.setBeepEnabled(true)
			integrator.setBarcodeImageEnabled(true)
			integrator.initiateScan()
		}

		private fun saveImage(
			bitmap: Bitmap,
			fragment: Fragment,
			hold: (Uri) -> Unit = {}
		) {
			val root = Environment.getExternalStorageDirectory()
			val myDirector = File(root.absolutePath + "/DCIM/Camera/")
			myDirector.mkdirs()
			val generator = Random()
			var n = 10000
			n = generator.nextInt(n)
			val fileName = "Image-$n.jpg"
			val file = File(myDirector, fileName)
			if (file.exists()) file.delete()
			val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
			val contentUri = Uri.fromFile(file)
			mediaScanIntent.data = contentUri
			fragment.context?.sendBroadcast(mediaScanIntent)
			try {
				val out = FileOutputStream(file)
				bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
				out.flush()
				out.close()
			} catch (e: Exception) {
				e.printStackTrace()
			}
			val providerURI = FileProvider.getUriForFile(
				GoldStoneAPI.context, BuildConfig.APPLICATION_ID + ".provider", file
			)
			hold(providerURI)
		}
	}
}