package io.goldstone.blockchain.module.home.rammarket.module.ramquotation.ramoccupyrank.view

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.blinnnk.extension.*
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.Language.EOSRAMExchangeText
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.value.GrayScale
import io.goldstone.blockchain.common.value.fontSize
import org.jetbrains.anko.*

/**
 * @date: 2018/11/5.
 * @author: yanglihai
 * @description:
 */
class RAMOccupyRankHeaderView(context: Context): RelativeLayout(context) {
	
	init {
		layoutParams = ViewGroup.LayoutParams(matchParent, 51.uiPX())
		gravity = Gravity.CENTER_VERTICAL
		backgroundColor = Color.parseColor("#05000000")
		
	  textView {
			text = EOSRAMExchangeText.transactionAccount
			leftPadding = 7.uiPX()
			textColor = GrayScale.midGray
			typeface = GoldStoneFont.book(context)
			textSize = fontSize(11)
			layoutParams = RelativeLayout.LayoutParams(wrapContent, wrapContent).apply {
				centerVertically()
			}
		}
		textView {
			text = EOSRAMExchangeText.transactionAmount
			rightPadding = 7.uiPX()
			textColor = GrayScale.midGray
			typeface = GoldStoneFont.book(context)
			textSize = fontSize(11)
			layoutParams = RelativeLayout.LayoutParams(wrapContent, wrapContent).apply {
				centerVertically()
				alignParentRight()
			}
		}
		
	}
}