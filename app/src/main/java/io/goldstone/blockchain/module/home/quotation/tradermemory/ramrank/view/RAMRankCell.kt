package io.goldstone.blockchain.module.home.quotation.tradermemory.ramrank.view

import android.content.Context
import android.view.Gravity
import android.widget.RelativeLayout
import android.widget.TextView
import com.blinnnk.extension.setAlignParentRight
import com.blinnnk.extension.setMargins
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.observing
import io.goldstone.blockchain.common.base.basecell.BaseCell
import io.goldstone.blockchain.common.value.GrayScale
import io.goldstone.blockchain.common.value.fontSize
import io.goldstone.blockchain.module.home.quotation.tradermemory.ramtrend.model.EOSRAMRankModel
import org.jetbrains.anko.*

/**
 * @date: 2018/9/25.
 * @author: yanglihai
 * @description:
 */
class RAMRankCell(context: Context): BaseCell(context) {
	
	val maxHeight = 50.uiPX()
	
	var model: EOSRAMRankModel by observing(EOSRAMRankModel()) {
		rankIndex.text = model.rank
		accountName.text = model.account
		ramAmount.text = model.ram
		percent.text = model.percent
		
	}
	val rankIndex: TextView = textView {
		textColor = GrayScale.black
		textSize = fontSize(14)
		text = "1"
		layoutParams = RelativeLayout.LayoutParams(maxHeight, matchParent)
		gravity = Gravity.CENTER_VERTICAL
	}
	val accountName: TextView = textView {
		textColor = GrayScale.black
		textSize = fontSize(14)
		x += 50.uiPX()
		layoutParams = RelativeLayout.LayoutParams(wrapContent, matchParent)
		gravity = Gravity.CENTER_VERTICAL
	}
	val ramAmount = textView {
		textColor = GrayScale.black
		textSize = fontSize(14)
		layoutParams = RelativeLayout.LayoutParams(wrapContent, matchParent)
		gravity = Gravity.CENTER_VERTICAL
		setAlignParentRight()
		setMargins<RelativeLayout.LayoutParams> {
			rightMargin = 70.uiPX()
		}
	}
	val percent = textView {
		textColor = GrayScale.black
		textSize = fontSize(14)
		layoutParams = RelativeLayout.LayoutParams(wrapContent, matchParent)
		gravity = Gravity.CENTER_VERTICAL
		setAlignParentRight()
	}
	
	init {
	  layoutParams = LayoutParams(matchParent, maxHeight)
		leftPadding = 10.uiPX()
		rightPadding = 20.uiPX()
	}
	
}