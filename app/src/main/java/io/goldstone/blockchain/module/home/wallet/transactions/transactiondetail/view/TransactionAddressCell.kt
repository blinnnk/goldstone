package io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.view

import android.content.Context
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.blinnnk.extension.into
import com.blinnnk.extension.setAlignParentRight
import com.blinnnk.extension.setCenterInVertical
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.R
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.value.GrayScale
import io.goldstone.blockchain.common.value.PaddingSize
import io.goldstone.blockchain.common.value.ScreenSize
import io.goldstone.blockchain.common.value.fontSize
import io.goldstone.blockchain.crypto.bitcoin.BTCUtils
import io.goldstone.blockchain.crypto.utils.CryptoUtils
import org.jetbrains.anko.imageResource
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.textColor

/**
 * @date 2018/8/5 9:08 PM
 * @author KaySaith
 */
class TransactionAddressCell(context: Context) : RelativeLayout(context) {
	
	private val addressTextView = TextView(context)
	private val copyButton = ImageView(context).apply {
		layoutParams = RelativeLayout.LayoutParams(30.uiPX(), 30.uiPX())
		imageResource = R.drawable.add_contact_icon
		setColorFilter(GrayScale.lightGray)
		setAlignParentRight()
		setCenterInVertical()
		x -= PaddingSize.device
		scaleType = ImageView.ScaleType.CENTER_INSIDE
		visibility = View.GONE
	}
	
	init {
		y -= 15.uiPX()
		layoutParams = RelativeLayout.LayoutParams(matchParent, 35.uiPX())
		addressTextView.apply {
			gravity = Gravity.CENTER_VERTICAL
			textSize = fontSize(14)
			textColor = GrayScale.black
			typeface = GoldStoneFont.medium(context)
			layoutParams = LinearLayout.LayoutParams(ScreenSize.widthWithPadding, matchParent)
			x = PaddingSize.device.toFloat()
		}.into(this)
		copyButton.into(this)
	}
	
	fun setAddress(address: String) {
		val scaleCount = if (BTCUtils.isBTCAddress(address)) 14 else 18
		addressTextView.text = CryptoUtils.scaleMiddleAddress(address, scaleCount)
	}
	
	fun getButton(): ImageView {
		return copyButton
	}
}