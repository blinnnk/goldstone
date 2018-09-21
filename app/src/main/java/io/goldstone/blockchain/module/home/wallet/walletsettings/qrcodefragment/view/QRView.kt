package io.goldstone.blockchain.module.home.wallet.walletsettings.qrcodefragment.view

import android.content.Context
import android.graphics.Bitmap
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.blinnnk.extension.into
import com.blinnnk.extension.setMargins
import com.blinnnk.uikit.ScreenSize
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.clickToCopy
import io.goldstone.blockchain.common.component.title.AttentionTextView
import io.goldstone.blockchain.common.component.button.RoundButton
import io.goldstone.blockchain.common.language.CommonText
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.utils.click
import io.goldstone.blockchain.common.utils.glideImage
import io.goldstone.blockchain.common.value.Spectrum
import io.goldstone.blockchain.common.value.fontSize
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.textColor
import org.jetbrains.anko.verticalLayout

/**
 * @date 2018/5/8 12:23 PM
 * @author KaySaith
 */

class QRView(context: Context) : LinearLayout(context) {

	var saveQRImageEvent: Runnable? = null
	var shareEvent: Runnable? = null
	var convertEvent: Runnable? = null

	private val address by lazy { AttentionTextView(context) }
	private val qrImage by lazy { ImageView(context) }
	private val saveImageButton by lazy { RoundButton(context) }
	private val shareButton by lazy { RoundButton(context) }
	private val copyAddressButton by lazy { RoundButton(context) }
	private var buttonsLayout: LinearLayout
	private val convertToLegacyButton by lazy { TextView(context) }

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
			isCenter()
			setMargins<LinearLayout.LayoutParams> { topMargin = (-15).uiPX() }
		}.into(this)

		convertToLegacyButton.apply {
			textSize = fontSize(12)
			layoutParams = LinearLayout.LayoutParams(matchParent, 30.uiPX())
			gravity = Gravity.CENTER
			textColor = Spectrum.blue
			typeface = GoldStoneFont.heavy(context)
			visibility = View.GONE
		}.click {
			convertEvent?.run()
		}.into(this)

		buttonsLayout = verticalLayout {
			gravity = Gravity.CENTER_HORIZONTAL
			lparams(matchParent, matchParent)
		}
	}

	fun showFormattedButton(status: Boolean) {
		convertToLegacyButton.visibility = if (status) View.VISIBLE else View.GONE
		convertToLegacyButton.text = "Convert BCH Address Formatted"
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