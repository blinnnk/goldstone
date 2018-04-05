package io.goldstone.blockchain.module.home.wallet.walletsettings.qrcodefragment.presenter

import android.widget.ImageView
import com.blinnnk.uikit.ScreenSize
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder
import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.common.utils.glideImage
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable
import io.goldstone.blockchain.module.home.wallet.walletsettings.qrcodefragment.view.QRCodeFragment

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
    }
  }

  private fun getCurrentWalletAddress(hold: String.() -> Unit) {
    WalletTable.getCurrentWalletInfo {
      it?.apply { hold(address) }
    }
  }

  fun generateQRCode(imageView: ImageView) {
    try {
      getCurrentWalletAddress {
        val size = (ScreenSize.Width * 0.8).toInt()
        val barcodeEncoder = BarcodeEncoder()
        val bitmap = barcodeEncoder.encodeBitmap(this, BarcodeFormat.QR_CODE, size, size)
        imageView.glideImage(bitmap)
      }
    } catch (error: Exception) {
      System.out.println(error)
    }

  }

}