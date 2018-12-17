package io.goldstone.blockchain.module.home.quotation.markettokendetail.view

import android.content.Context
import android.view.Gravity
import android.widget.RelativeLayout
import android.widget.TextView
import com.blinnnk.extension.into
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.component.cell.TopBottomLineCell
import io.goldstone.blockchain.common.language.QuotationText
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.value.GrayScale
import io.goldstone.blockchain.common.value.PaddingSize
import io.goldstone.blockchain.common.value.ScreenSize
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
		layoutParams = RelativeLayout.LayoutParams(ScreenSize.widthWithPadding, 105.uiPX())
	}
	
	init {
		setHorizontalPadding(PaddingSize.content.toFloat())
		setTitle(QuotationText.tokenDescription)
		layoutParams = RelativeLayout.LayoutParams(matchParent, 160.uiPX())
		contentView.into(this)
	}
	
	fun setTokenDescription(content: String) {
		// 描述第一位存储了语言码, 如果语言格式不对也要重新拉取数据
		contentView.text = if (content.length > 1) content.substring(1) else return
	}
}