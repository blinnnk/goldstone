package io.goldstone.blockchain.module.home.rammarket.module.ramquotation.ramoccupyrank.view

import android.content.Context
import android.view.Gravity
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import com.blinnnk.extension.toDoubleOrZero
import com.blinnnk.uikit.ScreenSize
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.observing
import io.goldstone.blockchain.common.sharedpreference.SharedValue
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.value.GrayScale
import io.goldstone.blockchain.common.value.fontSize
import io.goldstone.blockchain.crypto.utils.formatCount
import io.goldstone.blockchain.module.home.rammarket.model.RAMMarketPadding
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
		layoutParams = RelativeLayout.LayoutParams(wrapContent, matchParent)
		gravity = Gravity.CENTER
	}
	private lateinit var accountName: TextView
	private lateinit var eosAmount: TextView
	private lateinit var ramAmount: TextView
	private lateinit var percent: TextView
	
	var model: RAMRankModel by observing(RAMRankModel()) {
		rank.text = model.rank.toString()
		accountName.text = model.account
		ramAmount.text = model.ram
		percent.text = model.percent
		eosAmount.text = "≈  ${getEOSOfRAM(model.ram)}  EOS"
	}
	
	private fun getEOSOfRAM(ram: String): String {
		val unitRAM =  when {
			ram.contains("GB") -> {
				ram.replace("GB", "").trim().toDoubleOrZero() * 2014 * 1024
			}
			ram.contains("MB") -> {
				ram.replace("MB", "").trim().toDoubleOrZero() * 2014
			}
			ram.contains("KB") -> {
				ram.replace("KB", "").trim().toDoubleOrZero()
			}
			else -> 0.0
		}
		
		return (SharedValue.getRAMUnitPrice() * unitRAM).formatCount(4)
		
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
			leftPadding = 25.uiPX()
			accountName = textView {
				textColor = GrayScale.black
				typeface = GoldStoneFont.heavy(context)
				textSize = fontSize(13)
			}
			eosAmount = textView {
				textColor = GrayScale.midGray
				typeface = GoldStoneFont.heavy(context)
				textSize = fontSize(12)
			}
			
			layoutParams = RelativeLayout.LayoutParams(wrapContent, wrapContent).apply {
				centerVertically()
				SharedValue.getRAMUnitPrice()
			}
		}
		
		verticalLayout {
			rightPadding = 7.uiPX()
			layoutParams = RelativeLayout.LayoutParams(ScreenSize.Width / 2, wrapContent).apply {
				alignParentRight()
				centerVertically()
			}
			ramAmount = textView {
				textColor = GrayScale.black
				typeface = GoldStoneFont.black(context)
				textSize = fontSize(13)
				gravity = Gravity.END
			}.lparams(matchParent, wrapContent)
			percent = textView {
				textColor = GrayScale.midGray
				typeface = GoldStoneFont.heavy(context)
				textSize = fontSize(12)
				gravity = Gravity.END
			}.lparams(matchParent, wrapContent)
			
		}
		
		
	}
	
}






