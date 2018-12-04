package io.goldstone.blockchain.module.home.rammarket.module.ramprice.view

import android.content.Context
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import com.blinnnk.extension.CustomTargetTextStyle
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.Language.EOSRAMExchangeText
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.value.GrayScale
import io.goldstone.blockchain.common.value.fontSize
import org.jetbrains.anko.*

/**
 * @date: 2018/10/30.
 * @author: yanglihai
 * @description: 展示当前价格
 */
class CurrentPriceView(context: Context) : LinearLayout(context) {
	lateinit var currentPrice: TextView
	lateinit var quoteChangePercent: TextView
	
	init {
		orientation = LinearLayout.VERTICAL
		topPadding = 20.uiPX()
		textView {
			text = EOSRAMExchangeText.currentPrice
			textSize = fontSize(10)
			textColor = GrayScale.midGray
			typeface = GoldStoneFont.heavy(context)
		}
		linearLayout {
			topPadding = 7.uiPX()
			lparams(matchParent, wrapContent)
			currentPrice = textView {
				text = "--"
				textSize = fontSize(26)
				typeface = GoldStoneFont.black(context)
				textColor  = GrayScale.black
				singleLine = true
			}
			textView {
				leftPadding = 10.uiPX()
				text = CustomTargetTextStyle(
					"EOS/KB", "EOS/KB", GrayScale.black, 12.uiPX(), true, false
				)
				textColor = GrayScale.black
			}
			quoteChangePercent = textView {
				gravity = Gravity.END
				textSize = fontSize(20)
				typeface = GoldStoneFont.heavy(context)
				text = ""
				singleLine = true
			}.lparams(matchParent, wrapContent)
		}
	}
}