package io.goldstone.blockchain.module.home.rammarket.module.ramquotation.bigtransaction.view

import android.content.Context
import android.view.Gravity
import android.view.ViewGroup
import android.widget.*
import com.blinnnk.extension.setAlignParentBottom
import com.blinnnk.extension.setAlignParentRight
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.observing
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.utils.TimeUtils
import io.goldstone.blockchain.common.value.*
import io.goldstone.blockchain.module.home.rammarket.module.ramtrade.model.TradingInfoModel
import org.jetbrains.anko.*

/**
 * @date: 2018/11/5.
 * @author: yanglihai
 * @description:
 */
class BigTransactionCell(context: Context): RelativeLayout(context) {
	private lateinit var accountName: TextView
	private lateinit var timing: TextView
	private lateinit var price: TextView
	private lateinit var amount: TextView
	
	var model: TradingInfoModel by observing(TradingInfoModel("", 0.toDouble(), "", 0, 0, 0.toDouble())) {
		accountName.text = model.account
		timing.text = TimeUtils.formatDate(model.time)
		amount.text = model.quantity.toString() + " EOS"
		price.text = "≈ ${model.price} EOS/KB"
		amount.textColor = if (model.type == 0) Spectrum.lightRed else Spectrum.green
	}
	
	init {
		gravity = Gravity.CENTER_VERTICAL
		layoutParams = ViewGroup.LayoutParams(matchParent, 51.uiPX())
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
		}
		
		verticalLayout {
			rightPadding = 7.uiPX()
			gravity = Gravity.END
			layoutParams = RelativeLayout.LayoutParams(wrapContent, matchParent).apply {
				alignParentRight()
			}
			amount = textView {
				textColor = Spectrum.green
				typeface = GoldStoneFont.black(context)
				textSize = fontSize(13)
			}
			price = textView {
				textColor = GrayScale.midGray
				typeface = GoldStoneFont.heavy(context)
				textSize = fontSize(12)
			}
		}
		
		view {
			layoutParams = RelativeLayout.LayoutParams(matchParent, 1.uiPX())
			setAlignParentBottom()
		}
		
	}
	
}






