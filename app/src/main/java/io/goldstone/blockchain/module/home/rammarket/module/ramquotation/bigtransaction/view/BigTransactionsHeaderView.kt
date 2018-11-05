package io.goldstone.blockchain.module.home.rammarket.module.ramquotation.bigtransaction.view

import android.content.Context
import android.view.Gravity
import android.view.ViewGroup
import android.widget.RelativeLayout
import com.blinnnk.extension.*
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.Language.EOSRAMExchangeText
import org.jetbrains.anko.*

/**
 * @date: 2018/11/5.
 * @author: yanglihai
 * @description:
 */
class BigTransactionsHeaderView(context: Context): RelativeLayout(context) {
	
	init {
		layoutParams = ViewGroup.LayoutParams(matchParent, 51.uiPX())
		gravity = Gravity.CENTER_VERTICAL
		
	  textView {
			text = EOSRAMExchangeText.transactionAccount
			leftPadding = 7.uiPX()
		}
		textView {
			text = EOSRAMExchangeText.transactionAmount
			leftPadding = 7.uiPX()
			layoutParams = RelativeLayout.LayoutParams(wrapContent, matchParent).apply {
				alignParentRight()
			}
		}
		
		view {
			layoutParams = RelativeLayout.LayoutParams(matchParent, 1.uiPX())
			setAlignParentBottom()
		}
		
	}
}