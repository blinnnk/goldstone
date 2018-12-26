package io.goldstone.blockchain.module.home.rammarket.module.ramquotation.bigtransaction.view

import android.annotation.SuppressLint
import android.content.Context
import android.view.Gravity
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import com.blinnnk.uikit.ScreenSize
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.observing
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.utils.TimeUtils
import io.goldstone.blockchain.common.value.*
import io.goldstone.blockchain.crypto.utils.formatCount
import io.goldstone.blockchain.module.home.rammarket.model.RAMMarketPadding
import io.goldstone.blockchain.module.home.rammarket.module.ramtrade.model.TradingInfoModel
import org.jetbrains.anko.*

/**
 * @date: 2018/11/5.
 * @author: yanglihai
 * @description:
 */
@SuppressLint("SetTextI18n")
class BigTransactionCell(context: Context): RelativeLayout(context) {
	private lateinit var accountName: TextView
	private lateinit var timing: TextView
	private lateinit var price: TextView
	private lateinit var amount: TextView
	
	var model: TradingInfoModel by observing(TradingInfoModel()) {
		accountName.text = model.account
		timing.text = TimeUtils.formatYMdHmDate(model.time * 1000)
		amount.text = if (model.type == 0)  "+${model.quantity} EOS" else "-${model.quantity} EOS"
		amount.textColor = if (model.type == 0) Spectrum.green else Spectrum.lightRed
		price.text = "≈ ${model.price.formatCount(4)} EOS/KB"
	}
	
	init {
		layoutParams = ViewGroup.LayoutParams(matchParent, 51.uiPX())
		leftPadding = RAMMarketPadding
		rightPadding = RAMMarketPadding
		
		view {
			layoutParams = RelativeLayout.LayoutParams(matchParent, 1).apply {
				alignParentBottom()
			}
			backgroundColor = GrayScale.lightGray
		}
		verticalLayout {
			leftPadding = 7.uiPX()
			accountName = textView {
				textColor = GrayScale.black
				typeface = GoldStoneFont.heavy(context)
				textSize = fontSize(13)
			}
			timing = textView {
				textColor = GrayScale.midGray
				typeface = GoldStoneFont.heavy(context)
				textSize = fontSize(12)
			}
			
			layoutParams = RelativeLayout.LayoutParams(wrapContent, wrapContent).apply {
				centerVertically()
			}
		}
		
		verticalLayout {
			rightPadding = 7.uiPX()
			layoutParams = RelativeLayout.LayoutParams(ScreenSize.Width / 2, wrapContent).apply {
				alignParentRight()
				centerVertically()
			}
			amount = textView {
				textColor = Spectrum.green
				typeface = GoldStoneFont.black(context)
				textSize = fontSize(13)
				gravity = Gravity.END
			}.lparams(matchParent, wrapContent)
			price = textView {
				textColor = GrayScale.midGray
				typeface = GoldStoneFont.heavy(context)
				textSize = fontSize(12)
				gravity = Gravity.END
			}.lparams(matchParent, wrapContent)
		}
	}
	
}






