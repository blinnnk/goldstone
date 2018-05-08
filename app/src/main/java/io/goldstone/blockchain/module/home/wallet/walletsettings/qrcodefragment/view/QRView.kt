package io.goldstone.blockchain.module.home.wallet.walletsettings.qrcodefragment.view

import android.content.Context
import android.graphics.Bitmap
import android.widget.ImageView
import android.widget.LinearLayout
import com.blinnnk.extension.into
import com.blinnnk.extension.setMargins
import com.blinnnk.uikit.ScreenSize
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.clickToCopy
import io.goldstone.blockchain.common.component.AttentionTextView
import io.goldstone.blockchain.common.component.RoundButton
import io.goldstone.blockchain.common.utils.click
import io.goldstone.blockchain.common.utils.glideImage
import io.goldstone.blockchain.common.value.CommonText
import org.jetbrains.anko.matchParent

/**
 * @date 2018/5/8 12:23 PM
 * @author KaySaith
 */

class QRView(context: Context) : LinearLayout(context) {

	var saveQRImageEvent: Runnable? = null

	private val address by lazy { AttentionTextView(context) }
	private val qrImage by lazy { ImageView(context) }
	private val saveImageButton by lazy { RoundButton(context) }
	private val copyAddressButton by lazy { RoundButton(context) }

	init {
		orientation = VERTICAL
		layoutParams = LinearLayout.LayoutParams(matchParent, matchParent)
		qrImage.apply {
			val size = (ScreenSize.Width * 0.8).toInt()
			layoutParams = LinearLayout.LayoutParams(size, size).apply {
				leftMargin = (ScreenSize.Width * 0.1).toInt()
			}
			scaleType = ImageView.ScaleType.CENTER_CROP
		}.into(this)

		address.apply {
			setMargins<LinearLayout.LayoutParams> { topMargin = (-15).uiPX() }
		}.into(this)

		copyAddressButton.apply {
			text = CommonText.copyAddress
			setBlueStyle()
			setMargins<LinearLayout.LayoutParams> { topMargin = 10.uiPX() }
		}.click { context.clickToCopy(address.text.toString()) }.into(this)

		saveImageButton.apply {
			text = CommonText.saveToAlbum
			setBlueStyle()
			setMargins<LinearLayout.LayoutParams> {
				topMargin = 10.uiPX()
				bottomMargin = 10.uiPX()
			}
		}.click {
			saveQRImageEvent?.run()
		}.into(this)
	}

	fun setAddressText(address: String) {
		this.address.text = address
	}

	fun setQRImage(bitmap: Bitmap?) {
		qrImage.glideImage(bitmap)
	}

	fun getAddress(): String = address.text.toString()

}