package io.goldstone.blockchain.module.home.wallet.walletsettings.qrcodefragment.view

import android.content.Context
import android.graphics.Bitmap
import android.view.Gravity
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
import org.jetbrains.anko.verticalLayout

/**
 * @date 2018/5/8 12:23 PM
 * @author KaySaith
 */

class QRView(context: Context) : LinearLayout(context) {

	var saveQRImageEvent: Runnable? = null
	var shareEvent: Runnable? = null

	private val address by lazy { AttentionTextView(context) }
	private val qrImage by lazy { ImageView(context) }
	private val saveImageButton by lazy { RoundButton(context) }
	private val shareButton by lazy { RoundButton(context) }
	private val copyAddressButton by lazy { RoundButton(context) }
	private var buttonsLayout: LinearLayout

	init {
		orientation = VERTICAL
		gravity = Gravity.CENTER_HORIZONTAL
		layoutParams = LinearLayout.LayoutParams(matchParent, matchParent)
		qrImage.apply {
			val size = (ScreenSize.Width * 0.8).toInt()
			layoutParams = LinearLayout.LayoutParams(size, size)
			scaleType = ImageView.ScaleType.CENTER_CROP
		}.into(this)

		address.apply {
			setMargins<LinearLayout.LayoutParams> { topMargin = (-15).uiPX() }
		}.into(this)

		buttonsLayout = verticalLayout {
			gravity = Gravity.CENTER_HORIZONTAL
			lparams(matchParent, matchParent)
		}
	}

	fun setAddressText(address: String) {
		this.address.text = address
	}

	fun setQRImage(bitmap: Bitmap?) {
		qrImage.glideImage(bitmap)
	}

	fun getAddress(): String = address.text.toString()

	private fun showCopyButtons() {
		copyAddressButton.apply {
			text = CommonText.copyAddress
			setBlueStyle()
			setMargins<LinearLayout.LayoutParams> { topMargin = 10.uiPX() }
		}.click { context.clickToCopy(address.text.toString()) }.into(buttonsLayout)
	}

	fun showSaveAndShareButtons() {
		saveImageButton.apply {
			text = CommonText.saveToAlbum
			setBlueStyle()
			setMargins<LinearLayout.LayoutParams> {
				topMargin = 10.uiPX()
			}
		}.click {
			saveQRImageEvent?.run()
		}.into(buttonsLayout)

		shareButton.apply {
			text = CommonText.shareQRImage
			setBlueStyle()
			setMargins<LinearLayout.LayoutParams> {
				topMargin = 10.uiPX()
			}
		}.click {
			shareEvent?.run()
		}.into(buttonsLayout)
	}

	fun showAllButtons() {
		showCopyButtons()
		showSaveAndShareButtons()
	}

}