package io.goldstone.blockchain.module.home.quotation.quotationsearch.view

import android.content.Context
import android.graphics.Color
import android.widget.*
import com.blinnnk.extension.*
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.observing
import io.goldstone.blockchain.common.base.basecell.BaseCell
import io.goldstone.blockchain.common.utils.*
import io.goldstone.blockchain.common.value.*
import io.goldstone.blockchain.module.home.quotation.quotationsearch.model.ExchangeTable
import org.jetbrains.anko.*
import io.goldstone.blockchain.R

/**
 * @date: 2018/8/29.
 * @author: yanglihai
 * @description:
 */
class ExchangeCell(context: Context) : BaseCell(context) {
	
	val checkBox by lazy { CheckBox(context) }
	val textView by lazy {
		TextView(context).apply {
			textSize = fontSize(14)
			textColor = GrayScale.black
			typeface = GoldStoneFont.heavy(context)
			x += 10.uiPX()
		}
	}
	private val exchangeIcon by lazy {
		ExchangeImageIcon(context).apply {
			layoutParams = RelativeLayout.LayoutParams(35.uiPX(), 35.uiPX())
			backgroundResource = R.drawable.bch_icon
		}
	}
	
	init {
		hasArrow = false
		setHorizontalPadding()
		addView(exchangeIcon.apply {
			setGrayStyle()
		})
		
		addView(textView)
		
		addView(checkBox.apply {
			layoutParams = RelativeLayout.LayoutParams(wrapContent, matchParent)
		})
		
		exchangeIcon.apply {
			setCenterInVertical()
		}
		textView.apply {
			setCenterInVertical()
			x += 40.uiPX()
		}
		
		checkBox.apply {
			setCenterInVertical()
			setAlignParentRight()
		}
		
		setGrayStyle()
	}
	
	var model: ExchangeTable? by observing(null) {
		model?.apply {
			exchangeIcon.glideImage(iconUrl)
			exchangeIcon.addCorner(18.uiPX(), Color.TRANSPARENT)
			textView.text = exchangeName
			checkBox.isChecked = isSelected
		}
	}
}