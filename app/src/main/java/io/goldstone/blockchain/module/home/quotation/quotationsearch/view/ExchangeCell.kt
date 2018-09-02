package io.goldstone.blockchain.module.home.quotation.quotationsearch.view

import android.content.Context
import android.widget.*
import com.blinnnk.extension.setAlignParentRight
import com.blinnnk.extension.setCenterInVertical
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.observing
import com.bumptech.glide.Glide
import io.goldstone.blockchain.common.base.basecell.BaseCell
import io.goldstone.blockchain.common.component.button.SquareIcon
import io.goldstone.blockchain.common.utils.*
import io.goldstone.blockchain.common.value.*
import io.goldstone.blockchain.module.home.quotation.quotationsearch.model.ExchangeTable
import org.jetbrains.anko.*

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
	val exchangeIcon by lazy {
		ImageView(context).apply {
			layoutParams = RelativeLayout.LayoutParams(35.uiPX(), 35.uiPX())
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
			layoutParams = RelativeLayout.LayoutParams(50.uiPX(), matchParent)
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
			exchangeIcon.glideRoundImage(iconUrl)
			textView.text = exchangeName
			checkBox.isChecked = isSelected
		}
	}
}