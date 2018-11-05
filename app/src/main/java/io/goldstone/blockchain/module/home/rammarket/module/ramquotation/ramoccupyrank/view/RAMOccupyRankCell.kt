package io.goldstone.blockchain.module.home.rammarket.module.ramquotation.ramoccupyrank.view

import android.content.Context
import android.view.Gravity
import android.view.ViewGroup
import android.widget.*
import com.blinnnk.extension.*
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.observing
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.utils.TimeUtils
import io.goldstone.blockchain.common.value.*
import io.goldstone.blockchain.module.home.rammarket.module.ramquotation.ramoccupyrank.model.RAMRankModel
import org.jetbrains.anko.*

/**
 * @date: 2018/11/5.
 * @author: yanglihai
 * @description:
 */
class RAMOccupyRankCell(context: Context): RelativeLayout(context) {
	private val rank = textView {
		leftPadding = 7.uiPX()
		textColor = GrayScale.midGray
		textSize = fontSize(12)
		typeface = GoldStoneFont.black(context)
	}
	private lateinit var accountName: TextView
	private lateinit var price: TextView
	private lateinit var amount: TextView
	private lateinit var percent: TextView
	
	var model: RAMRankModel by observing(RAMRankModel("", "", "" ,"", "", 0, 0.toDouble())) {
		rank.text = model.rank.toString()
		accountName.text = model.account
	}
	
	init {
		layoutParams = ViewGroup.LayoutParams(matchParent, 51.uiPX())
		verticalLayout {
			leftPadding = 30.uiPX()
			accountName = textView {
				textColor = GrayScale.black
				typeface = GoldStoneFont.heavy(context)
				textSize = fontSize(13)
			}
			price = textView {
				textColor = GrayScale.midGray
				typeface = GoldStoneFont.heavy(context)
				textSize = fontSize(12)
			}
		}
		
		verticalLayout {
			rightPadding = 7.uiPX()
			layoutParams = RelativeLayout.LayoutParams(wrapContent, matchParent).apply {
				alignParentRight()
			}
			amount = textView {
				textColor = Spectrum.green
				typeface = GoldStoneFont.black(context)
				textSize = fontSize(13)
			}
			percent = textView {
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






