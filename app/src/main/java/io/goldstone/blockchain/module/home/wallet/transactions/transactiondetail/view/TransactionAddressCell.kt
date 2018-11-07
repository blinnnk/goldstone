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
import com.blinnnk.util.FixTextLength
import io.goldstone.blockchain.R
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.value.GrayScale
import io.goldstone.blockchain.common.value.PaddingSize
import io.goldstone.blockchain.common.value.ScreenSize
import io.goldstone.blockchain.common.value.fontSize
import io.goldstone.blockchain.crypto.eos.EOSValue
import org.jetbrains.anko.imageResource
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.textColor

/**
 * @date 2018/8/5 9:08 PM
 * @author KaySaith
 */
class TransactionAddressCell(context: Context) : RelativeLayout(context) {

	private val addressTextView = TextView(context)
	private val addButton = ImageView(context).apply {
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
		layoutParams = RelativeLayout.LayoutParams(matchParent, 35.uiPX())
		addressTextView.apply {
			gravity = Gravity.CENTER_VERTICAL
			textSize = fontSize(14)
			textColor = GrayScale.black
			typeface = GoldStoneFont.heavy(context)
			layoutParams = LinearLayout.LayoutParams(ScreenSize.widthWithPadding, matchParent)
			setPadding(10.uiPX(), 0, 10.uiPX(), 0)
		}.into(this)
		addButton.into(this)
	}

	fun setAddress(address: String) {
		val fixWidthAddress = if (address.length <= EOSValue.maxNameLength) address
		else object : FixTextLength() {
			override val maxWidth: Float = ScreenSize.widthWithPadding - 90.uiPX() * 1f
			override var text: String = address
			override val textSize: Float = fontSize(12.uiPX())
		}.getFixString(true)
		addressTextView.text = fixWidthAddress
	}

	fun getButton(): ImageView {
		return addButton
	}
}