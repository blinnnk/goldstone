package io.goldstone.blockchain.module.home.rammarket.module.ramtransactionsearch.view

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.ViewGroup
import android.widget.*
import com.blinnnk.extension.*
import com.blinnnk.uikit.ScreenSize
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.observing
import io.goldstone.blockchain.common.Language.EOSRAMExchangeText
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.utils.TimeUtils
import io.goldstone.blockchain.common.value.*
import io.goldstone.blockchain.crypto.utils.formatCount
import io.goldstone.blockchain.module.home.rammarket.module.ramtrade.model.TradingInfoModel
import org.jetbrains.anko.*

/**
 * @date: 2018/11/8.
 * @author: yangLiHai
 * @description:
 */
class TransactionOfNameHeaderView(context: Context): RelativeLayout(context) {
	private val accountName: TextView
	private val timing: TextView
	private val amount: TextView
	
	init {
		layoutParams = ViewGroup.LayoutParams(matchParent, 38.uiPX())
		backgroundColor = Color.parseColor("#05000000")
		
		view {
			backgroundColor = GrayScale.midGray
			layoutParams = RelativeLayout.LayoutParams(matchParent, 1).apply {
				alignParentBottom()
			}
		}
		
		timing = textView {
			textColor = GrayScale.midGray
			typeface = GoldStoneFont.book(context)
			textSize = fontSize(11)
			text = EOSRAMExchangeText.timing
			leftPadding = 20.uiPX()
			layoutParams = RelativeLayout.LayoutParams(wrapContent, wrapContent).apply {
				centerVertically()
			}
		}
		
		accountName = textView {
			textColor = GrayScale.midGray
			typeface = GoldStoneFont.book(context)
			textSize = fontSize(11)
			text = EOSRAMExchangeText.user
			layoutParams = RelativeLayout.LayoutParams(wrapContent, wrapContent).apply {
				centerVertically()
			}
			setMargins<RelativeLayout.LayoutParams> { leftMargin = 170.uiPX() }
		}
		
		amount = textView {
			textColor = GrayScale.midGray
			typeface = GoldStoneFont.book(context)
			textSize = fontSize(11)
			rightPadding = 20.uiPX()
			text = EOSRAMExchangeText.tradingAmount
			layoutParams = RelativeLayout.LayoutParams(wrapContent, wrapContent).apply {
				centerVertically()
				alignParentRight()
			}
		}
	}
	
}






