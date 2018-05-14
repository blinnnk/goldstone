package io.goldstone.blockchain.module.home.quotation.markettokendetail.view

import android.content.Context
import android.view.Gravity
import android.widget.RelativeLayout
import android.widget.TextView
import com.blinnnk.extension.into
import com.blinnnk.extension.setAlignParentBottom
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.value.GrayScale
import io.goldstone.blockchain.common.value.QuotationText
import io.goldstone.blockchain.common.value.fontSize
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.textColor

/**
 * @date 25/04/2018 9:42 AM
 * @author KaySaith
 */

class TokenInfoView(context: Context) : MarketTokenDetailBaseCell(context) {

	private val contentView = TextView(context).apply {
		gravity = Gravity.TOP
		text = QuotationText.tokenDescriptionPlaceHolder
		textSize = fontSize(12)
		textColor = GrayScale.black
		typeface = GoldStoneFont.medium(context)
		layoutParams = RelativeLayout.LayoutParams(matchParent, 105.uiPX())
	}

	init {
		title.text = QuotationText.tokenDescription
		layoutParams = RelativeLayout.LayoutParams(matchParent, 155.uiPX())
		contentView.into(this)
		contentView.y -= 10.uiPX()
		contentView.setAlignParentBottom()
	}

	fun setTokenDescription(content: String) {
		contentView.text = content
	}

}