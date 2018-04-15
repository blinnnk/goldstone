package io.goldstone.blockchain.module.home.wallet.walletsettings.qrcodefragment.presenter

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import com.blinnnk.uikit.ScreenSize
import com.blinnnk.util.PermissionCategory
import com.blinnnk.util.checkPermissionThen
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder
import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.common.utils.glideImage
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable
import io.goldstone.blockchain.module.home.wallet.walletsettings.qrcodefragment.view.QRCodeFragment
import org.jetbrains.anko.support.v4.toast
import java.io.File
import java.io.FileOutputStream
import java.util.*

/**
 * @date 26/03/2018 11:07 PM
 * @author KaySaith
 */

class QRCodePresenter(
  override val fragment: QRCodeFragment
  ) : BasePresenter<QRCodeFragment>() {

  override fun onFragmentViewCreated() {
    getCurrentWalletAddress {
      fragment.address.text = this
      generateQRCode(this)
    }
  }

  private fun getCurrentWalletAddress(hold: String.() -> Unit) {
    WalletTable.getCurrentWalletInfo {
      hold(it!!.address)
    }
  }

  private fun generateQRCode(address: String) {
    try {
      val size = (ScreenSize.Width * 0.8).toInt()
      val barcodeEncoder = BarcodeEncoder()
      val bitmap = barcodeEncoder.encodeBitmap(address, BarcodeFormat.QR_CODE, size, size)
      fragment.qrImage.glideImage(bitmap)
    } catch (error: Exception) {
      println(error)
    }
  }

  fun saveQRCodeImageToAlbum(address: String) {
    fragment.activity?.checkPermissionThen(PermissionCategory.Write) {
      val size = (ScreenSize.Width * 0.8).toInt()
      val barcodeEncoder = BarcodeEncoder()
      val bitmap = barcodeEncoder.encodeBitmap(address, BarcodeFormat.QR_CODE, size, size)
      saveImage(bitmap)
      fragment.toast("QR code image has saved to album")
    }
  }

  private fun saveImage(bitmap: Bitmap) {
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

  }

}