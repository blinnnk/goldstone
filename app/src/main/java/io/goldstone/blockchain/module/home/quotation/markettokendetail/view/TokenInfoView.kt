package io.goldstone.blockchain.module.home.quotation.markettokendetail.view

import android.content.Context
import android.view.Gravity
import android.widget.RelativeLayout
import android.widget.TextView
import com.blinnnk.extension.into
import com.blinnnk.extension.setAlignParentBottom
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.component.TopBottomLineCell
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

class TokenInfoView(context: Context) : TopBottomLineCell(context) {

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
		layoutParams = RelativeLayout.LayoutParams(matchParent, 145.uiPX())
		contentView.into(this)
		contentView.y -= 10.uiPX()
		contentView.setAlignParentBottom()
	}

	fun setTokenDescription(content: String) {
		// 描述第一位存储了语言码, 如果语言格式不对也要重新拉取数据
		contentView.text = content.substring(1)
	}

}