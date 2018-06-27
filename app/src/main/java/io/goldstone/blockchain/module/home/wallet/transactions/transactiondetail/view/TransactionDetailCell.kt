package io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.blinnnk.extension.into
import com.blinnnk.extension.setAlignParentRight
import com.blinnnk.extension.setCenterInVertical
import com.blinnnk.extension.setUnderline
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.observing
import io.goldstone.blockchain.R
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.value.*
import io.goldstone.blockchain.common.value.ScreenSize
import io.goldstone.blockchain.crypto.utils.CryptoUtils
import io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.model.TransactionDetailModel
import org.jetbrains.anko.*

/**
 * @date 27/03/2018 3:27 AM
 * @author KaySaith
 */
open class TransactionDetailCell(context: Context) : RelativeLayout(context) {
	
	var model: TransactionDetailModel by observing(TransactionDetailModel()) {
		description.text = model.description
		info.text =
			if (model.info.isEmpty()) {
				if (model.description.equals(TransactionText.memo, true)) {
					TransactionText.noMemo
				} else {
					"Waiting ..."
				}
			} else {
				if (
					model.description.equals(CommonText.from, true)
					|| model.description.equals(CommonText.to, true)
				) {
					CryptoUtils.scaleMiddleAddress(model.info, 18)
				} else {
					model.info
				}
			}
	}
	private val description = TextView(context)
	private val info = TextView(context)
	private val copyButton = ImageView(context).apply {
		layoutParams = RelativeLayout.LayoutParams(30.uiPX(), 30.uiPX())
		imageResource = R.drawable.add_contact_icon
		setColorFilter(GrayScale.lightGray)
		x -= PaddingSize.device
		y += 8.uiPX()
		scaleType = ImageView.ScaleType.CENTER_INSIDE
		visibility = View.GONE
	}
	
	init {
		this.setWillNotDraw(false)
		
		layoutParams = RelativeLayout.LayoutParams(matchParent, TransactionSize.cellHeight)
		
		verticalLayout {
			x = PaddingSize.device.toFloat()
			description.apply {
				textSize = fontSize(12)
				textColor = GrayScale.midGray
				typeface = GoldStoneFont.book(context)
				layoutParams = LinearLayout.LayoutParams(ScreenSize.widthWithPadding, 17.uiPX()).apply {
					topMargin = 15.uiPX()
				}
			}.into(this)
			
			info.apply {
				textSize = fontSize(14)
				textColor = GrayScale.black
				typeface = GoldStoneFont.medium(context)
				layoutParams = LinearLayout.LayoutParams(ScreenSize.widthWithPadding, wrapContent)
				y += 2.uiPX()
			}.into(this)
		}.let {
			setCenterInVertical()
		}
		
		this.addView(copyButton)
		copyButton.setAlignParentRight()
		copyButton.setCenterInVertical()
	}
	
	fun showAddContactButton(hold: ImageView.() -> Unit) {
		copyButton.visibility = View.VISIBLE
		hold(copyButton)
	}
	
	private val paint = Paint().apply {
		isAntiAlias = true
		color = GrayScale.lightGray
		style = Paint.Style.FILL
	}
	
	override fun onDraw(canvas: Canvas?) {
		super.onDraw(canvas)
		
		canvas?.drawLine(
			PaddingSize.device.toFloat(),
			height - BorderSize.default,
			(width - PaddingSize.device).toFloat(),
			height - BorderSize.default,
			paint
		)
	}
	
	fun setContentColor(color: Int) {
		info.textColor = color
		info.text =
			if (info.text.length > 130) (info.text.substring(0, 130) + "...").setUnderline()
			else info.text.toString().setUnderline()
	}
}



















