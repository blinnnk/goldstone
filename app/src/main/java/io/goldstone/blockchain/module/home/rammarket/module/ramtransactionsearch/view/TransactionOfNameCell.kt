package io.goldstone.blockchain.module.home.rammarket.module.ramtransactionsearch.view

import android.content.Context
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import com.blinnnk.extension.setMargins
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.observing
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.utils.TimeUtils
import io.goldstone.blockchain.common.value.*
import io.goldstone.blockchain.module.home.rammarket.module.ramtrade.model.TradingInfoModel
import org.jetbrains.anko.*

/**
 * @date: 2018/11/8.
 * @author: yangLiHai
 * @description:
 */
class TransactionOfNameCell(context: Context): RelativeLayout(context) {
	private lateinit var accountName: TextView
	private lateinit var timing: TextView
	private lateinit var amount: TextView
	
	var model: TradingInfoModel by observing(TradingInfoModel()) {
		accountName.text = model.account
		timing.text = TimeUtils.formatYMdHmDate(model.time * 1000)
		amount.text = if (model.type == 0)  "+${model.quantity} EOS" else "-${model.quantity} EOS"
		amount.textColor = if (model.type == 0) Spectrum.green else Spectrum.lightRed
	}
	
	init {
		layoutParams = ViewGroup.LayoutParams(matchParent, 44.uiPX())
		leftPadding = 20.uiPX()
		rightPadding = 20.uiPX()
		
		view {
			layoutParams = RelativeLayout.LayoutParams(matchParent, 1).apply {
				alignParentBottom()
			}
			backgroundColor = GrayScale.lightGray
		}
		
		timing = textView {
			textColor = GrayScale.gray
			typeface = GoldStoneFont.book(context)
			textSize = fontSize(12)
			layoutParams = RelativeLayout.LayoutParams(wrapContent, wrapContent).apply {
				centerVertically()
			}
		}
		
		accountName = textView {
			textColor = GrayScale.black
			typeface = GoldStoneFont.black(context)
			textSize = fontSize(13)
			layoutParams = RelativeLayout.LayoutParams(wrapContent, wrapContent).apply {
				centerVertically()
			}
			setMargins<RelativeLayout.LayoutParams> { leftMargin = 135.uiPX() }
		}
		
		amount = textView {
			textColor = Spectrum.green
			typeface = GoldStoneFont.black(context)
			textSize = fontSize(13)
			layoutParams = RelativeLayout.LayoutParams(wrapContent, wrapContent).apply {
				centerVertically()
				alignParentRight()
			}
		}
	}
	
}






